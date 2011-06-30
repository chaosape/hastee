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

import net.sf.hastee.Util;
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

/**
 * This class defines an ImportUriResolver for ST.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class STImportUriResolver extends ImportUriResolver {

	@Override
	public String apply(EObject from) {
		if (from instanceof Import) {
			Import importObj = (Import) from;
			String path = Util.toString(importObj.getUri());
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
