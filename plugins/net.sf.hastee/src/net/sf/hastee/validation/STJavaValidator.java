package net.sf.hastee.validation;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.ExprReference;
import net.sf.hastee.st.ExprTemplateArgs;
import net.sf.hastee.st.StPackage;
import net.sf.hastee.st.TemplateDeclaration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.Check;

public class STJavaValidator extends AbstractSTJavaValidator {

	@Check
	public void checkTemplateReference(ExprReference ref) {
		Declaration decl = ref.getTarget();
		EObject contents = decl.getContents();
		if (contents instanceof TemplateDeclaration) {
			TemplateDeclaration tmplDecl = (TemplateDeclaration) contents;
			int expected = tmplDecl.getAttributes().size();

			ExprTemplateArgs args = ref.getArgs();
			int actual = (args == null) ? 0 : args.getArgs().size();
			if (expected != actual) {
				error("Number of arguments mismatch: expected " + expected
						+ " arguments, got " + actual + " arguments", ref,
						StPackage.Literals.EXPR_REFERENCE__TARGET, -1);
			}
		}
	}

}
