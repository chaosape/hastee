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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.StFactory;
import net.sf.hastee.st.StPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.linking.impl.DefaultLinkingService;
import org.eclipse.xtext.nodemodel.INode;

/**
 * This class defines a linking service for built-in ST functions and implicit
 * attributes.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class StLinkingService extends DefaultLinkingService {

	private Map<String, Declaration> declarations;

	private Resource stubsResource = null;

	/**
	 * Creates a new ST linking service which creates builtin functions.
	 */
	public StLinkingService() {
		declarations = new HashMap<String, Declaration>();

		addDeclaration("first");
		addDeclaration("last");
		addDeclaration("rest");
		addDeclaration("trunc");
		addDeclaration("strip");
		addDeclaration("length");
		addDeclaration("i0");
		addDeclaration("i");
	}

	/**
	 * Adds a new declaration to the built-in declarations map.
	 * 
	 * @param name
	 *            declaration name
	 */
	private void addDeclaration(String name) {
		Declaration decl = StFactory.eINSTANCE.createDeclaration();
		decl.setName(name);
		declarations.put(name, decl);
	}

	/**
	 * Returns a singleton if <code>name</code> is a builtin declaration, and an
	 * empty list otherwise.
	 * 
	 * @param context
	 *            the context in which a function is referenced.
	 * @param name
	 *            declaration name
	 * @return a list
	 */
	private List<EObject> getBuiltinDeclaration(EObject context, String name) {
		Declaration declaration = declarations.get(name);
		if (declaration != null) {
			Resource res = getBuiltinResource();
			res.getContents().add(declaration);

			return Collections.singletonList((EObject) declaration);
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the resource for built-in declarations, creating it if necessary.
	 * 
	 * @return the resource for built-in declarations
	 */
	private Resource getBuiltinResource() {
		if (stubsResource != null) {
			return stubsResource;
		}

		URI stubURI = URI.createPlatformPluginURI(
				"/net.sf.hastee/src/net/sf/hastee/Builtin.java", true);

		ResourceSet set = new ResourceSetImpl();
		stubsResource = set.getResource(stubURI, false);
		if (stubsResource == null) {
			stubsResource = set.createResource(stubURI);
		}

		return stubsResource;
	}

	@Override
	public List<EObject> getLinkedObjects(EObject context, EReference ref,
			INode node) {
		List<EObject> result = super.getLinkedObjects(context, ref, node);
		if (result != null && !result.isEmpty()) {
			return result;
		}

		final EClass requiredType = ref.getEReferenceType();
		final String s = getCrossRefNodeAsString(node);
		if (requiredType != null && s != null) {
			if (StPackage.Literals.DECLARATION.isSuperTypeOf(requiredType)) {
				return getBuiltinDeclaration(context, s);
			}
		}

		return Collections.emptyList();
	}

}
