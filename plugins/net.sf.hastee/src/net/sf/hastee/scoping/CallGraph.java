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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

/**
 * This class defines methods to compute the call graph of each template.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class CallGraph {

	private static final Map<Group, CallGraph> map = new WeakHashMap<Group, CallGraph>();

	/**
	 * Returns the call graph associated with the given group. The call graph is
	 * constructed on-the-fly.
	 * 
	 * @param group
	 *            a group
	 * @return the call graph associated with the given group
	 */
	public static CallGraph getCallGraph(Group group) {
		CallGraph cg = map.get(group);
		if (cg == null) {
			cg = new CallGraph();
			cg.build(group);
			map.put(group, cg);
		}
		return cg;
	}

	private Map<TemplateDeclaration, Set<TemplateDeclaration>> calleesMap;

	private Map<TemplateDeclaration, Set<TemplateDeclaration>> callersMap;

	private Deque<TemplateDeclaration> callStack;

	/**
	 * Creates a new empty call graph.
	 */
	public CallGraph() {
		calleesMap = new HashMap<TemplateDeclaration, Set<TemplateDeclaration>>();
		callersMap = new HashMap<TemplateDeclaration, Set<TemplateDeclaration>>();
	}

	/**
	 * Adds an edge from <code>source</code> to <code>target</code> to the given
	 * <code>map</code>.
	 * 
	 * @param map
	 *            a map from source to a set of targets
	 * @param source
	 *            source template
	 * @param target
	 *            target template
	 */
	private void add(Map<TemplateDeclaration, Set<TemplateDeclaration>> map,
			TemplateDeclaration source, TemplateDeclaration target) {
		Set<TemplateDeclaration> set = map.get(source);
		if (set == null) {
			set = new HashSet<TemplateDeclaration>();
			map.put(source, set);
		}

		set.add(target);
	}

	/**
	 * Builds the call graph for the given group.
	 * 
	 * @param group
	 *            a group
	 */
	private void build(Group group) {
		for (Declaration topDecl : group.getMembers()) {
			EObject contents = topDecl.getContents();
			if (contents instanceof TemplateDeclaration) {
				TemplateDeclaration tmplDecl = (TemplateDeclaration) contents;
				callStack = new ArrayDeque<TemplateDeclaration>();
				visit(tmplDecl);
			}
		}

		for (Declaration topDecl : group.getMembers()) {
			EObject contents = topDecl.getContents();
			if (contents instanceof TemplateDeclaration) {
				TemplateDeclaration tmplDecl = (TemplateDeclaration) contents;
				if (getCaller(tmplDecl) == null) {
					// got a potential top
					callStack = new ArrayDeque<TemplateDeclaration>();
					removeCycles(tmplDecl);
				}
			}
		}
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
	 * This method visits this call graph and removes cycles.
	 */
	private void removeCycles(TemplateDeclaration caller) {
		callStack.push(caller);
		Set<TemplateDeclaration> callees = calleesMap.get(caller);
		if (callees != null) {
			Iterator<TemplateDeclaration> it = callees.iterator();
			while (it.hasNext()) {
				TemplateDeclaration callee = it.next();
				if (callStack.contains(callee)) {
					it.remove();
					Set<TemplateDeclaration> callers = callersMap.get(callee);
					callers.remove(caller);
				} else {
					removeCycles(callee);
				}
			}
		}
		callStack.pop();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<TemplateDeclaration, Set<TemplateDeclaration>> entry : callersMap
				.entrySet()) {
			Declaration callee = (Declaration) entry.getKey().eContainer();
			builder.append(callee.getName());
			builder.append(" <- ");
			for (TemplateDeclaration caller : entry.getValue()) {
				builder.append((Declaration) caller.eContainer());
				builder.append(" ");
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	/**
	 * Visits the given template declaration and adds edges to this call graph.
	 * 
	 * @param caller
	 *            a template declaration that may call other templates
	 */
	private void visit(TemplateDeclaration caller) {
		if (callStack.contains(caller)) {
			return;
		}
		callStack.push(caller);

		TreeIterator<EObject> it = caller.eAllContents();
		while (it.hasNext()) {
			EObject eObject = it.next();
			if (eObject instanceof ExprReference) {
				ExprReference exprRef = (ExprReference) eObject;
				Declaration decl = exprRef.getTarget();
				EObject contents = decl.getContents();
				if (contents instanceof TemplateDeclaration) {
					TemplateDeclaration callee = (TemplateDeclaration) contents;
					add(calleesMap, caller, callee);
					add(callersMap, callee, caller);

					visit(callee);
				}
			}
		}

		callStack.pop();
	}

}
