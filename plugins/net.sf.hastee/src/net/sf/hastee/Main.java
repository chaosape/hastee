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
package net.sf.hastee;

import java.io.File;
import java.util.List;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.DictionaryDeclaration;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.Pair;
import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* Injector injector = */
		new STStandaloneSetup().createInjectorAndDoEMFRegistration();

		String path = args[0];

		XtextResourceSet resourceSet = new XtextResourceSet();
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL,
				Boolean.TRUE);

		URI uri = URI.createFileURI(new File(path).getAbsolutePath());
		Resource resource = resourceSet.getResource(uri, true);
		Group group = (Group) resource.getContents().get(0);

		// contains linking errors
		List<Diagnostic> errors = resource.getErrors();
		if (!errors.isEmpty()) {
			for (Diagnostic error : errors) {
				System.err.println(error);
			}
		}

		for (Declaration member : group.getMembers()) {
			System.out.print(member.getName());
			EObject contents = member.getContents();
			if (contents instanceof TemplateDeclaration) {
				TemplateDeclaration decl = (TemplateDeclaration) contents;
				System.out.println("(" + decl.getAttributes() + ") ::= <<");
				System.out.println(decl.getDefinition().getTemplate());
				System.out.println(">>");
			} else if (member instanceof DictionaryDeclaration) {
				DictionaryDeclaration decl = (DictionaryDeclaration) contents;
				System.out.println("[");
				for (Pair pair : decl.getPairs()) {
					System.out.println(pair.getKey().getElements() + " = "
							+ pair.getDefinition().getTemplate());
				}
				System.out.println("]");
			}
		}
	}

}
