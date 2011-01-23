package net.sf.stui.linking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.stui.st.Attribute;
import net.sf.stui.st.ExprTemplate;
import net.sf.stui.st.Group;
import net.sf.stui.st.NamedTemplate;

import org.eclipse.xtext.EcoreUtil2;

public class ImplicitAttributeSolver {

	private Map<NamedTemplate, Map<String, Attribute>> attrMap;

	public ImplicitAttributeSolver() {

	}

	public Map<NamedTemplate, Map<String, Attribute>> buildAttributeMap(
			Group group) {
		attrMap = new HashMap<NamedTemplate, Map<String, Attribute>>();
		for (NamedTemplate template : group.getTemplates()) {
			Set<NamedTemplate> visitedSet = new HashSet<NamedTemplate>();
			Map<String, Attribute> attributes = new HashMap<String, Attribute>();
			visitTemplate(visitedSet, attributes, template);
		}

		return attrMap;
	}

	private void visitTemplate(Set<NamedTemplate> visitedSet,
			Map<String, Attribute> attributes, NamedTemplate template) {
		visitedSet.add(template);
		for (Attribute attribute : template.getArguments()) {
			attributes.put(attribute.get_name(), attribute);
		}
		attrMap.put(template, attributes);

		List<ExprTemplate> templates = EcoreUtil2.getAllContentsOfType(
				template, ExprTemplate.class);
		for (ExprTemplate calledTemplate : templates) {
			if (!visitedSet.contains(calledTemplate.getTemplate())) {
				Set<NamedTemplate> newVisitedSet = new HashSet<NamedTemplate>(
						visitedSet);
				Map<String, Attribute> newMap = new HashMap<String, Attribute>(
						attributes);
				visitTemplate(newVisitedSet, newMap,
						calledTemplate.getTemplate());
			}
		}
	}

}
