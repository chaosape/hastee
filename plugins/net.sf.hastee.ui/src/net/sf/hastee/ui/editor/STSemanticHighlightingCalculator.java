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
package net.sf.hastee.ui.editor;

import static net.sf.hastee.ui.editor.STHighlightingConfiguration.TEMPLATE_NAME;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findActualNodeFor;
import net.sf.hastee.st.TopDeclaration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

/**
 * This class defines a simple highlighting calculator for rule declarations.
 * Initially copied from org.eclipse.xtext.xtext.ui.editor.syntaxcoloring.
 * SemanticHighlightingCalculator.
 * 
 * @author Matthieu Wipliez
 * @author Herve Yviquel
 */
public class STSemanticHighlightingCalculator implements
		ISemanticHighlightingCalculator {

	@Override
	public void provideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor) {
		if (resource == null || resource.getParseResult() == null) {
			return;
		}

		INode root = resource.getParseResult().getRootNode();
		for (INode node : root.getAsTreeIterable()) {
			EObject eObject = node.getSemanticElement();
			if (eObject instanceof TopDeclaration) {
				TopDeclaration topDecl = (TopDeclaration) eObject;

				INode decl = findActualNodeFor(topDecl.getDecl());
				acceptor.addPosition(decl.getOffset(), decl.getLength(),
						TEMPLATE_NAME);
			}
		}
	}

}
