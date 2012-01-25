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
package net.sf.hastee.ui;

import net.sf.hastee.CustomSTLexer;
import net.sf.hastee.ui.editor.STAntlrTokenToAttributeIdMapper;
import net.sf.hastee.ui.editor.STHighlightingConfiguration;
import net.sf.hastee.ui.editor.STSemanticHighlightingCalculator;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.parser.antlr.Lexer;
import org.eclipse.xtext.ui.LexerUIBindings;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class STUiModule extends net.sf.hastee.ui.AbstractSTUiModule {

	public STUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
		return STAntlrTokenToAttributeIdMapper.class;
	}

	public Class<? extends IHighlightingConfiguration> bindILexicalHighlightingConfiguration() {
		return STHighlightingConfiguration.class;
	}

	public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
		return STSemanticHighlightingCalculator.class;
	}

	@Override
	public void configureHighlightingLexer(Binder binder) {
		binder.bind(Lexer.class)
				.annotatedWith(Names.named(LexerUIBindings.HIGHLIGHTING))
				.to(CustomSTLexer.class);
	}

}
