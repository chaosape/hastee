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
package net.sf.hastee.ui.labeling;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.Group;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

import com.google.inject.Inject;

/**
 * Provides labels for a EObjects.
 * 
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class STLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public STLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	public String image(Declaration decl) {
		return "template.gif";
	}

	public String text(Declaration decl) {
		return decl.getName();
	}

	public String text(Group group) {
		String[] segments = group.eResource().getURI().segments();
		return segments[segments.length - 1];
	}

}
