/*
 * generated by Xtext
 */
package net.sf.hastee.scoping;

import net.sf.hastee.st.Attribute;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.TemplateDef;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

import com.google.common.base.Function;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 * 
 */
public class STScopeProvider extends AbstractDeclarativeScopeProvider {

	private IScope getScopeOfArguments(Iterable<Attribute> elements) {
		return getScopeOfArguments(elements, IScope.NULLSCOPE);
	}

	private IScope getScopeOfArguments(Iterable<Attribute> elements,
			IScope outer) {
		IScope scope = Scopes.scopeFor(elements,
				new Function<Attribute, String>() {
					public String apply(Attribute attribute) {
						return attribute.get_name();
					}
				}, outer);
		return scope;
	}

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
	public IScope scope_Arg_attribute(ExprReference expr, EReference reference) {
		EObject body = expr.getObjRef().getBody();
		if (body instanceof TemplateDef) {
			TemplateDef template = (TemplateDef) body;
			return getScopeOfArguments(template.getArguments().getArguments());
		} else {
			return IScope.NULLSCOPE;
		}
	}

	public IScope scope_ExprAttribute_attribute(TemplateDef template,
			EReference reference) {
		return getScopeOfArguments(template.getArguments().getArguments(),
				getScope(template.eContainer(), reference));
	}

}
