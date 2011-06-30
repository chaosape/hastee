/*
 * Copyright (c) 2011, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * This file is part of Hastee.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.hastee.linking;

import java.util.HashMap;
import java.util.Map;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateDef;
import net.sf.hastee.st.TopDeclaration;

public class ImplicitAttributeSolver {

	private Map<TemplateDef, Map<String, Declaration>> attrMap;

	public ImplicitAttributeSolver() {

	}

	public Map<TemplateDef, Map<String, Declaration>> buildAttributeMap(
			Group group) {
		attrMap = new HashMap<TemplateDef, Map<String, Declaration>>();
		for (TopDeclaration obj : group.getMembers()) {
			/*if (obj.getBody() instanceof TemplateDef) {
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
			}*/
		}

		return attrMap;
	}

//	private void visitTemplate(Set<TemplateDef> visitedSet,
//			Map<String, Attribute> attributes, TemplateDef template) {
//		visitedSet.add(template);
//		for (Attribute attribute : template.getArguments()) {
//			attributes.put(attribute.get_name(), attribute);
//		}
//		attrMap.put(template, attributes);
//
//		List<TemplateDef> defs = EcoreUtil2.getAllContentsOfType(template,
//				TemplateDef.class);
//		for (TemplateDef innerTemplateDef : defs) {
//			if (!visitedSet.contains(innerTemplateDef)) {
//				Set<TemplateDef> newVisitedSet = new HashSet<TemplateDef>(
//						visitedSet);
//				Map<String, Attribute> newMap = new HashMap<String, Attribute>(
//						attributes);
//				visitTemplate(newVisitedSet, newMap, innerTemplateDef);
//			}
//		}
//
//		List<ExprReference> refs = EcoreUtil2.getAllContentsOfType(template,
//				ExprReference.class);
//		for (ExprReference ref : refs) {
//			NamedObject refObj = ref.getObjRef();
//			if (refObj.getBody() instanceof TemplateDef) {
//				TemplateDef innerTemplate = (TemplateDef) refObj.getBody();
//				if (!visitedSet.contains(innerTemplate)) {
//					Set<TemplateDef> newVisitedSet = new HashSet<TemplateDef>(
//							visitedSet);
//					Map<String, Attribute> newMap = new HashMap<String, Attribute>(
//							attributes);
//
//					visitTemplate(newVisitedSet, newMap, innerTemplate);
//				}
//			}
//		}
//	}

}
