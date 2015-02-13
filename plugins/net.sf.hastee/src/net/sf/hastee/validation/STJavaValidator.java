package net.sf.hastee.validation;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.ExprMap;
import net.sf.hastee.st.ExprNoComma;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.ExprTemplateArgs;
import net.sf.hastee.st.StPackage;
import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;

public class STJavaValidator extends AbstractSTJavaValidator {

	@Check
	public void checkTemplateReference(ExprReference ref) {
		Declaration decl = ref.getTarget();
		EObject contents = decl.getContents();
		if (contents instanceof TemplateDeclaration) {
			TemplateDeclaration tmplDecl = (TemplateDeclaration) contents;
			int maxExpected = tmplDecl.getAttributes().size();
			int minExpected = maxExpected;
			for (Declaration attr : tmplDecl.getAttributes()) {
				if (attr.getContents() != null) {
					minExpected--;
				}
			}

			ExprTemplateArgs args = ref.getArgs();
			int actual = (args == null) ? 0 : args.getArgs().size();

			ExprMap exprMap = EcoreUtil2.getContainerOfType(ref, ExprMap.class);
			if (exprMap != null && exprMap.getTemplate() == ref) {
				actual += exprMap.getMembers().size();
			} else {
				ExprNoComma enc = EcoreUtil2.getContainerOfType(ref, ExprNoComma.class);
				actual += enc.getMap() != null && enc.getMap() == ref? 1 : 0;
			}

			if (maxExpected < actual || minExpected > actual) {
				error("Number of arguments mismatch: expected between "
						+ minExpected + " and " + maxExpected
						+ " arguments, got " + actual + " arguments", ref,
						StPackage.Literals.EXPR_REFERENCE__TARGET, -1);
			}
		}
	}

}
