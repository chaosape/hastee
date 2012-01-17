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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

/**
 * This class defines the highlighting configuration for the ST editor.
 * 
 * @author Herve Yviquel
 * @author Matthieu Wipliez
 */
public class STHighlightingConfiguration extends
		DefaultHighlightingConfiguration {

	public static final String EXPRESSION = "Expression";

	public static final String SEPARATORS = "Separators";

	public static final String TEMPLATE_NAME = "TemplateName";

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(TEMPLATE_NAME, "Template name",
				templateNameTextStyle());
		acceptor.acceptDefaultHighlighting(SEPARATORS, "Template separators",
				separatorsTextStyle());
		acceptor.acceptDefaultHighlighting(EXPRESSION, "Template expression",
				expressionTextStyle());
	}

	public TextStyle expressionTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	public TextStyle separatorsTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 255));
		return textStyle;
	}

	public TextStyle templateNameTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(255, 128, 0));
		return textStyle;
	}

}
