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

import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;

/**
 * This class maps tokens to highlighting styles.
 * 
 * @author Herve Yviquel
 * 
 */
public class STAntlrTokenToAttributeIdMapper extends
		DefaultAntlrTokenToAttributeIdMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if ("'<<'".equals(tokenName)) {
			return STHighlightingConfiguration.SEPARATORS;
		} else if ("'>>'".equals(tokenName)) {
			return STHighlightingConfiguration.SEPARATORS;
		} else if ("'::='".equals(tokenName)) {
			return STHighlightingConfiguration.SEPARATORS;
		}

		return super.calculateId(tokenName, tokenType);
	}

}
