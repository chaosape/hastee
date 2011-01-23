package net.sf.hastee;

import java.io.File;
import java.util.List;

import net.sf.hastee.st.Group;
import net.sf.hastee.st.NamedTemplate;

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

		for (NamedTemplate template : group.getTemplates()) {
			System.out.print(template.getName() + '(');
			System.out.println(") ::= <<");

			System.out.println(">>");
		}
	}

}
