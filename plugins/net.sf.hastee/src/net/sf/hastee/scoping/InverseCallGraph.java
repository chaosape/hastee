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
package net.sf.hastee.scoping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.DeclarationBody;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateDeclaration;
import net.sf.hastee.st.TopDeclaration;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

/**
 * This class defines methods to compute the call graph of each template.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class InverseCallGraph {

	private static final Map<Group, InverseCallGraph> map = new WeakHashMap<Group, InverseCallGraph>();

	/**
	 * Returns the inverse call graph associated with the given group. The call
	 * graph is constructed on-the-fly.
	 * 
	 * @param group
	 *            a group
	 * @return the inverse call graph associated with the given group
	 */
	public static InverseCallGraph getICG(Group group) {
		InverseCallGraph icg = map.get(group);
		if (icg == null) {
			icg = new InverseCallGraph();
			icg.build(group);
			map.put(group, icg);
		}
		return icg;
	}

	private Stack<TemplateDeclaration> callStack;

	private Map<TemplateDeclaration, Set<TemplateDeclaration>> callersMap;

	/**
	 * Creates a new empty inverse call graph.
	 */
	public InverseCallGraph() {
		callersMap = new HashMap<TemplateDeclaration, Set<TemplateDeclaration>>();
	}

	/**
	 * Builds the inverse call graph for the given group.
	 * 
	 * @param group
	 *            a group
	 */
	public void build(Group group) {
		for (TopDeclaration topDecl : group.getMembers()) {
			DeclarationBody body = topDecl.getBody();
			if (body instanceof TemplateDeclaration) {
				TemplateDeclaration tmplDecl = (TemplateDeclaration) body;
				callStack = new Stack<TemplateDeclaration>();
				visit(tmplDecl);
			}
		}

		removeCycles();
	}

	/**
	 * Returns the caller of the given callee.
	 * 
	 * @param callee
	 *            a template declaration
	 * @return the caller of the given callee, or <code>null</code> if the given
	 *         template is never called
	 */
	public TemplateDeclaration getCaller(TemplateDeclaration callee) {
		Set<TemplateDeclaration> callers = callersMap.get(callee);
		if (callers == null || callers.isEmpty()) {
			return null;
		}
		return callers.iterator().next();
	}

	/**
	 * This method visits this inverse call graph and removes any simple cycle
	 * that may exist.
	 */
	private void removeCycles() {
		for (Entry<TemplateDeclaration, Set<TemplateDeclaration>> entry : callersMap
				.entrySet()) {
			TemplateDeclaration callee = entry.getKey();
			Set<TemplateDeclaration> callers = entry.getValue();
			if (callers == null) {
				continue;
			}
			Iterator<TemplateDeclaration> it = callers.iterator();
			while (it.hasNext()) {
				TemplateDeclaration caller = it.next();
				Set<TemplateDeclaration> callerCallers = callersMap.get(caller);
				if (callerCallers != null && callerCallers.contains(callee)) {
					it.remove();
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<TemplateDeclaration, Set<TemplateDeclaration>> entry : callersMap
				.entrySet()) {
			Declaration callee = ((TopDeclaration) entry.getKey().eContainer())
					.getDecl();
			builder.append(callee.getName());
			builder.append(" <- ");
			for (TemplateDeclaration caller : entry.getValue()) {
				builder.append(((TopDeclaration) caller.eContainer()).getDecl()
						.getName());
				builder.append(" ");
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	/**
	 * Builds the inverse call graph for the given template declaration.
	 * 
	 * @param caller
	 *            a template declaration that may call other templates
	 */
	public void visit(TemplateDeclaration caller) {
		if (callStack.contains(caller)) {
			return;
		}
		callStack.push(caller);

		TreeIterator<EObject> it = caller.eAllContents();
		while (it.hasNext()) {
			EObject eObject = it.next();
			if (eObject instanceof ExprReference) {
				ExprReference exprRef = (ExprReference) eObject;
				Declaration decl = exprRef.getObjRef();
				EObject cter = decl.eContainer();
				if (cter instanceof TopDeclaration) {
					TopDeclaration topDecl = (TopDeclaration) cter;
					DeclarationBody body = topDecl.getBody();
					if (body instanceof TemplateDeclaration) {
						TemplateDeclaration callee = (TemplateDeclaration) body;
						Set<TemplateDeclaration> callers = callersMap
								.get(callee);
						if (callers == null) {
							callers = new HashSet<TemplateDeclaration>();
							callersMap.put(callee, callers);
						}

						callers.add(caller);
						visit(callee);
					}
				}
			}
		}

		callStack.pop();
	}

}
