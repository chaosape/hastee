package net.sf.hastee.linking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hastee.st.DictPair;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateDef;

import org.eclipse.xtext.EcoreUtil2;

public class ImplicitAttributeSolver {

	private Map<TemplateDef, Map<String, Attribute>> attrMap;

	public ImplicitAttributeSolver() {

	}

	public Map<TemplateDef, Map<String, Attribute>> buildAttributeMap(
			Group group) {
		attrMap = new HashMap<TemplateDef, Map<String, Attribute>>();
		for (NamedObject obj : group.getMembers()) {
			if (obj.getBody() instanceof TemplateDef) {
				TemplateDef template = (TemplateDef) obj.getBody();
				Set<TemplateDef> visitedSet = new HashSet<TemplateDef>();
				Map<String, Attribute> attributes = new HashMap<String, Attribute>();
				visitTemplate(visitedSet, attributes, template);
			} else if (obj.getBody() instanceof Dictionary) {
				Dictionary dict = (Dictionary) obj.getBody();
				for (DictPair pair : dict.getPairs()) {
					TemplateDef template = pair.getTemplate();
					Set<TemplateDef> visitedSet = new HashSet<TemplateDef>();
					Map<String, Attribute> attributes = new HashMap<String, Attribute>();
					visitTemplate(visitedSet, attributes, template);
				}
			}
		}

		return attrMap;
	}

	private void visitTemplate(Set<TemplateDef> visitedSet,
			Map<String, Attribute> attributes, TemplateDef template) {
		visitedSet.add(template);
		for (Attribute attribute : template.getArguments()) {
			attributes.put(attribute.get_name(), attribute);
		}
		attrMap.put(template, attributes);

		List<TemplateDef> defs = EcoreUtil2.getAllContentsOfType(template,
				TemplateDef.class);
		for (TemplateDef innerTemplateDef : defs) {
			if (!visitedSet.contains(innerTemplateDef)) {
				Set<TemplateDef> newVisitedSet = new HashSet<TemplateDef>(
						visitedSet);
				Map<String, Attribute> newMap = new HashMap<String, Attribute>(
						attributes);
				visitTemplate(newVisitedSet, newMap, innerTemplateDef);
			}
		}

		List<ExprReference> refs = EcoreUtil2.getAllContentsOfType(template,
				ExprReference.class);
		for (ExprReference ref : refs) {
			NamedObject refObj = ref.getObjRef();
			if (refObj.getBody() instanceof TemplateDef) {
				TemplateDef innerTemplate = (TemplateDef) refObj.getBody();
				if (!visitedSet.contains(innerTemplate)) {
					Set<TemplateDef> newVisitedSet = new HashSet<TemplateDef>(
							visitedSet);
					Map<String, Attribute> newMap = new HashMap<String, Attribute>(
							attributes);

					visitTemplate(newVisitedSet, newMap, innerTemplate);
				}
			}
		}
	}

}
