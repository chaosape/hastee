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

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateAnonymous;
import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

/**
 * This class defines a scope provider for ST.
 * 
 * @author Matthieu Wipliez
 */
public class STScopeProvider extends AbstractDeclarativeScopeProvider {

	public IScope scope_Arg_attribute(ExprReference expr, EReference reference) {
		Declaration declaration = expr.getTarget();
		EObject contents = declaration.getContents();
		if (contents instanceof TemplateDeclaration) {
			TemplateDeclaration tmplDecl = (TemplateDeclaration) contents;
			IScope scope = Scopes.scopeFor(tmplDecl.getAttributes());
			return scope;
		}
		return IScope.NULLSCOPE;
	}

	public IScope scope_ExprAttribute_attribute(TemplateAnonymous decl,
			EReference reference) {
		IScope scope = Scopes.scopeFor(decl.getArguments(),
				getScope(decl.eContainer(), reference));
		return scope;
	}

	public IScope scope_ExprAttribute_attribute(TemplateDeclaration decl,
			EReference reference) {
		Group group = EcoreUtil2.getContainerOfType(decl, Group.class);
		CallGraph icg = CallGraph.getCallGraph(group);
		EObject caller = icg.getCaller(decl);
		IScope outer = (caller == null) ? IScope.NULLSCOPE : getScope(caller,
				reference);
		IScope scope = Scopes.scopeFor(decl.getAttributes(), outer);
		return scope;
	}

}
