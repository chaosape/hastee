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

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.DictionaryDeclaration;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.StPackage;
import net.sf.hastee.st.TemplateAnonymous;
import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScope;

import com.google.common.base.Function;

/**
 * This class defines methods to compute the call graph of each template.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class CallGraph {

	private static final Map<Group, WeakReference<CallGraph>> map = new WeakHashMap<Group, WeakReference<CallGraph>>();

	/**
	 * Returns the call graph associated with the given group. The call graph is
	 * constructed on-the-fly.
	 * 
	 * @param group
	 *            a group
	 * @return the call graph associated with the given group
	 */
	public static CallGraph getCallGraph(Group group,
			IGlobalScopeProvider scopeProvider) {
		// at most one access on the map
		// because Xtext spawns one build thread + one validation thread
		synchronized (map) {
			WeakReference<CallGraph> ref = map.get(group);
			if (ref != null) {
				CallGraph cg = ref.get();
				if (cg != null) {
					return cg;
				}
			}

			CallGraph cg = new CallGraph(scopeProvider);
			cg.build(group);
			ref = new WeakReference<CallGraph>(cg);
			map.put(group, ref);

			return cg;
		}
	}

	private Map<EObject, Set<EObject>> calleesMap;

	private Map<EObject, Set<EObject>> callersMap;

	private Deque<EObject> callStack;

	/**
	 * used to store the overridden definition.
	 */
	private Map<String, Declaration> overrideMap;

	private Map<EObject, Integer> rankMap;

	private IGlobalScopeProvider scopeProvider;

	/**
	 * Creates a new empty call graph.
	 */
	public CallGraph(IGlobalScopeProvider scopeProvider) {
		calleesMap = new HashMap<EObject, Set<EObject>>();
		callersMap = new HashMap<EObject, Set<EObject>>();
		overrideMap = new HashMap<String, Declaration>();
		rankMap = new HashMap<EObject, Integer>();
		this.scopeProvider = scopeProvider;
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
	private void add(Map<EObject, Set<EObject>> map, EObject source,
			EObject target) {
		Set<EObject> set = map.get(source);
		if (set == null) {
			set = new HashSet<EObject>();
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
		// fill override map
		for (Declaration topDecl : group.getMembers()) {
			overrideMap.put(topDecl.getName(), topDecl);
		}

		// build (inverse) Call Graph
		visitTemplateDeclarations(group,
				new Function<TemplateDeclaration, Object>() {

					@Override
					public Object apply(TemplateDeclaration from) {
						callStack = new ArrayDeque<EObject>();
						visit(from);
						return null;
					}
				});

		// remove cycles
		visitTemplateDeclarations(group,
				new Function<TemplateDeclaration, Object>() {

					@Override
					public Object apply(TemplateDeclaration from) {
						if (getCaller(from) == null) {
							// got a potential top
							callStack = new ArrayDeque<EObject>();
							removeCycles(from);
						}
						return null;
					}
				});
	}

	/**
	 * Returns the caller of the given callee.
	 * 
	 * @param callee
	 *            a template declaration
	 * @return the caller of the given callee, or <code>null</code> if the given
	 *         template is never called
	 */
	public EObject getCaller(EObject callee) {
		List<EObject> list = getList(callersMap, callee);
		int size = list.size();
		if (size == 0) {
			return null;
		} else {
			// quick and dirty, but "works"
			return list.get(size - 1);
		}
	}

	/**
	 * Returns the list of objects associated with a given object, sorted by
	 * position in the document.
	 * 
	 * @param map
	 *            a map
	 * @param source
	 *            source object
	 * @return a list
	 */
	private List<EObject> getList(Map<EObject, Set<EObject>> map, EObject source) {
		Set<EObject> set = map.get(source);
		if (set == null) {
			return Collections.emptyList();
		}

		// sort by position in the document
		List<EObject> list = new ArrayList<EObject>(set);
		Collections.sort(list, new Comparator<EObject>() {

			@Override
			public int compare(EObject o1, EObject o2) {
				Integer r1 = rankMap.get(o1);
				Integer r2 = rankMap.get(o2);
				if (r1 != null && r2 != null) {
					return r1.compareTo(r2);
				} else {
					return 0;
				}
			}

		});
		return list;
	}

	/**
	 * This method visits this call graph and removes cycles.
	 */
	private void removeCycles(EObject caller) {
		callStack.push(caller);
		List<EObject> callees = getList(calleesMap, caller);
		Iterator<EObject> it = callees.iterator();
		while (it.hasNext()) {
			EObject callee = it.next();
			if (callStack.contains(callee)) {
				it.remove();
				Set<EObject> callers = callersMap.get(callee);
				callers.remove(caller);
			} else {
				removeCycles(callee);
			}
		}
		callStack.pop();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<EObject, Set<EObject>> entry : callersMap.entrySet()) {
			builder.append(toString(entry.getKey()));
			builder.append(" <- ");
			Set<EObject> callers = entry.getValue();
			if (callers != null) {
				for (EObject caller : callers) {
					builder.append(toString(caller));
					builder.append(" ");
				}
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	private String toString(EObject eObject) {
		if (eObject instanceof TemplateDeclaration) {
			Declaration decl = EcoreUtil2.getContainerOfType(eObject,
					Declaration.class);
			return decl.getName();
		} else {
			return "{}";
		}
	}

	/**
	 * Visits the given template declaration and adds edges to this call graph.
	 * 
	 * @param caller
	 *            a template declaration that may call other templates
	 */
	private void visit(EObject caller) {
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
				EObject callee = decl.getContents();
				if (callee instanceof TemplateDeclaration) {
					if (overrideMap.containsKey(decl.getName())) {
						decl = overrideMap.get(decl.getName());
						callee = decl.getContents();
					}

					add(calleesMap, caller, callee);
					add(callersMap, callee, caller);

					visit(callee);
				} else if (callee instanceof DictionaryDeclaration) {
					System.err.println("dict");
				}
			} else if (eObject instanceof TemplateAnonymous) {
				TemplateAnonymous callee = (TemplateAnonymous) eObject;
				add(calleesMap, caller, callee);
				add(callersMap, callee, caller);

				visit(callee);

				// do not visit children of this anonymous template, otherwise
				// this messes up the call graph by introducing redundant call
				// dependencies
				it.prune();
			}
		}

		callStack.pop();
	}

	/**
	 * Visits all elements provided by the global scope provider (so, outside of
	 * the group), and then all elements of the group.
	 * 
	 * @param group
	 *            the group
	 * @param function
	 *            the function to apply
	 */
	private void visitTemplateDeclarations(Group group,
			Function<TemplateDeclaration, Object> function) {
		EReference reference = StPackage.eINSTANCE.getGroup_Members();
		IScope scope = scopeProvider.getScope(group.eResource(), reference,
				null);
		int rank = 0;
		for (IEObjectDescription desc : scope.getAllElements()) {
			if (desc.getEClass() == StPackage.eINSTANCE.getDeclaration()) {
				EObject proxy = desc.getEObjectOrProxy();
				EObject eObj = EcoreUtil.resolve(proxy, group);
				Declaration decl = (Declaration) eObj;
				EObject contents = decl.getContents();
				if (contents instanceof TemplateDeclaration) {
					rankMap.put(contents, rank++);
					function.apply((TemplateDeclaration) contents);
				}
			}
		}

		for (Declaration decl : group.getMembers()) {
			EObject contents = decl.getContents();
			if (contents instanceof TemplateDeclaration) {
				rankMap.put(contents, rank++);
				function.apply((TemplateDeclaration) contents);
			}
		}
	}

}
