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
package net.sf.hastee.naming;

import net.sf.hastee.st.Declaration;
import net.sf.hastee.st.Option;
import net.sf.hastee.st.Property;
import net.sf.hastee.st.TemplateAnonymous;
import net.sf.hastee.st.TopDeclaration;

import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

/**
 * This class is a qualified name provider.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class STNameProvider extends DefaultDeclarativeQualifiedNameProvider {

	public QualifiedName qualifiedName(Declaration decl) {
		if (decl.eContainer() instanceof TopDeclaration) {
			return getConverter().toQualifiedName(decl.getName());
		}

		// for declarations inside TemplateDeclaration
		// will make the provider use the "name" field and prepend it with the
		// container's name
		return null;
	}

	public QualifiedName qualifiedName(Option option) {
		// name is private
		return null;
	}

	public QualifiedName qualifiedName(Property property) {
		// name is private
		return null;
	}

	public QualifiedName qualifiedName(TemplateAnonymous tmpl) {
		return getConverter().toQualifiedName(tmpl.toString());
	}

	public QualifiedName qualifiedName(TopDeclaration topDecl) {
		return getFullyQualifiedName(topDecl.getDecl());
	}

}
