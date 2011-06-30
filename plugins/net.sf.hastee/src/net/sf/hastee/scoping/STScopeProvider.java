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

import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

/**
 * This class defines a scope provider for ST.
 * 
 * @author Matthieu Wipliez
 */
public class STScopeProvider extends AbstractDeclarativeScopeProvider {

	/*
	 * private IScope getScopeOfArguments(Iterable<Attribute> elements) { return
	 * getScopeOfArguments(elements, IScope.NULLSCOPE); }
	 * 
	 * private IScope getScopeOfArguments(Iterable<Attribute> elements, IScope
	 * outer) { IScope scope = Scopes.scopeFor(elements, new Function<Attribute,
	 * QualifiedName>() { public QualifiedName apply(Attribute attribute) {
	 * return QualifiedName.create(attribute.get_name()); } }, outer); return
	 * scope; }
	 */

	/**
	 * Returns the scope for looking up an attribute in an argument when
	 * referencing a template.
	 * 
	 * @param expr
	 *            template call expression
	 * @param reference
	 *            a reference
	 * @return a scope
	 */
	/*
	 * public IScope scope_Arg_attribute(ExprReference expr, EReference
	 * reference) { EObject body = expr.getObjRef().getBody(); if (body
	 * instanceof TemplateDef) { TemplateDef template = (TemplateDef) body;
	 * return getScopeOfArguments(template.getArguments()); } else { return
	 * IScope.NULLSCOPE; } }
	 */

	public IScope scope_TopDeclaration(TemplateDeclaration decl,
			EReference reference) {
		IScope scope = Scopes.scopeFor(decl.getAttributes());
		return scope;
	}

}
