package net.sf.hastee.validation;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.ExprMap;
import net.sf.hastee.st.ExprNoComma;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.ExprTemplateArgs;
import net.sf.hastee.st.StPackage;
import net.sf.hastee.st.TemplateDeclaration;
import net.sf.hastee.util.EcoreHelper;

import org.eclipse.emf.ecore.EObject;
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

			ExprMap exprMap = EcoreHelper
					.getContainerOfType(ref, ExprMap.class);
			if (exprMap != null && exprMap.getTemplate() == ref) {
				actual += exprMap.getMembers().size();
			} else {
				actual += EcoreHelper
						.getContainerOfType(ref, ExprNoComma.class) != null ? 1
						: 0;
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
