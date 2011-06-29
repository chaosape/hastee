package net.sf.hastee;

import java.io.File;
import java.util.List;

import net.sf.hastee.st.DeclarationBody;
import net.sf.hastee.st.DictPair;
import net.sf.hastee.st.DictionaryDeclaration;
import net.sf.hastee.st.Group;
import net.sf.hastee.st.TemplateDeclaration;
import net.sf.hastee.st.TopDeclaration;

import org.eclipse.emf.common.util.URI;
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

		for (TopDeclaration member : group.getMembers()) {
			System.out.print(member.getDecl().getName());
			DeclarationBody body = member.getBody();
			if (body instanceof TemplateDeclaration) {
				TemplateDeclaration decl = (TemplateDeclaration) body;
				System.out.println("(" + decl.getAttributes() + ") ::= <<");
				System.out.println(decl.getDefinition().getTemplate());
				System.out.println(">>");
			} else if (member instanceof DictionaryDeclaration) {
				DictionaryDeclaration decl = (DictionaryDeclaration) body;
				System.out.println("[");
				for (DictPair pair : decl.getPairs()) {
					System.out.println(pair.getKey().getContents() + " = "
							+ pair.getDefinition().getTemplate());
				}
				System.out.println("]");
			}
		}
	}

}
