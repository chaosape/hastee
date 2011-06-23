/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.sf.hastee.ui.editor;

import java.util.Iterator;
import java.util.List;

import net.sf.hastee.st.NamedObject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.XtextPackage;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.LeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
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

	private void highlightNode(INode node, String id,
			IHighlightedPositionAcceptor acceptor) {
		if (node == null)
			return;
		if (node instanceof LeafNode) {
			acceptor.addPosition(node.getOffset(), node.getLength(), id);
		} else {
			for (ILeafNode leaf : node.getLeafNodes()) {
				if (!leaf.isHidden()) {
					acceptor.addPosition(leaf.getOffset(), leaf.getLength(), id);
				}
			}
		}
	}

	public void provideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor) {
		if (resource == null)
			return;
		Iterator<EObject> iter = EcoreUtil.getAllContents(resource, true);
		while (iter.hasNext()) {
			EObject current = iter.next();
			if (current instanceof NamedObject) {
				List<INode> nodes = NodeModelUtils.findNodesForFeature(current,
						XtextPackage.Literals.ABSTRACT_RULE__NAME);
				if (!nodes.isEmpty()) {
					highlightNode(nodes.get(0),
							STHighlightingConfiguration.TEMPLATE_NAME, acceptor);
				}
			}
		}
	}

}
