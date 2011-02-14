package net.sf.hastee.scoping;

import net.sf.hastee.st.Import;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

public class STImportUriResolver extends ImportUriResolver {

	@Override
	public String apply(EObject from) {
		if (from instanceof Import) {
			Import importObj = (Import) from;
			StringBuilder builder = new StringBuilder();
			for (String token : importObj.getUri().getTokens()) {
				builder.append(token);
			}

			String path = builder.toString();
			return getURI(from.eResource(), path);
		}

		return null;
	}

	private String getURI(Resource resource, String path) {
		String uri = resource.getURI().toPlatformString(true);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile file = workspace.getRoot().getFile(new Path(uri));
		IProject project = file.getProject();

		IJavaProject javaProject = JavaCore.create(project);
		if (!javaProject.exists()) {
			return null;
		}

		// iterate over package roots
		try {
			for (IPackageFragmentRoot root : javaProject
					.getPackageFragmentRoots()) {
				IResource res = root.getCorrespondingResource();
				if (res != null && res.getType() == IResource.FOLDER) {
					IFolder folder = (IFolder) res;
					IFile importFile = folder.getFile(path);
					if (importFile.exists()) {
						URI fileUri = URI.createPlatformResourceURI(importFile
								.getFullPath().toString(), true);
						return fileUri.toString();
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return null;
	}

}
