/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
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
