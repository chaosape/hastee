package net.sf.hastee.linking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hastee.st.Attribute;
import net.sf.hastee.st.Dictionary;
import net.sf.hastee.st.ExprTemplate;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.GroupMember;
import net.sf.hastee.st.TemplateNamed;

import org.eclipse.xtext.EcoreUtil2;

public class ImplicitAttributeSolver {

	private Map<TemplateNamed, Map<String, Attribute>> attrMap;

	public ImplicitAttributeSolver() {

	}

	public Map<TemplateNamed, Map<String, Attribute>> buildAttributeMap(
			Group group) {
		attrMap = new HashMap<TemplateNamed, Map<String, Attribute>>();
		for (GroupMember member : group.getMembers()) {
			if (member instanceof TemplateNamed) {
				TemplateNamed template = (TemplateNamed) member;
				Set<TemplateNamed> visitedSet = new HashSet<TemplateNamed>();
				Map<String, Attribute> attributes = new HashMap<String, Attribute>();
				visitTemplate(visitedSet, attributes, template);
			} else if (member instanceof Dictionary) {
				// Dictionary dict = (Dictionary) member;
				// for (DictPair pair : dict.getPairs()) {
				// TemplateNamed template = pair.getTemplate();
				// Set<TemplateNamed> visitedSet = new HashSet<TemplateNamed>();
				// Map<String, Attribute> attributes = new HashMap<String,
				// Attribute>();
				// visitTemplate(visitedSet, attributes, template);
				// }
			}
		}

		return attrMap;
	}

	private void visitTemplate(Set<TemplateNamed> visitedSet,
			Map<String, Attribute> attributes, TemplateNamed template) {
		visitedSet.add(template);
		for (Attribute attribute : template.getArguments()) {
			attributes.put(attribute.get_name(), attribute);
		}
		attrMap.put(template, attributes);

		List<ExprTemplate> templates = EcoreUtil2.getAllContentsOfType(
				template, ExprTemplate.class);
		for (ExprTemplate calledTemplate : templates) {
			if (!visitedSet.contains(calledTemplate.getTemplate())) {
				Set<TemplateNamed> newVisitedSet = new HashSet<TemplateNamed>(
						visitedSet);
				Map<String, Attribute> newMap = new HashMap<String, Attribute>(
						attributes);
				visitTemplate(newVisitedSet, newMap,
						calledTemplate.getTemplate());
			}
		}
	}

}
