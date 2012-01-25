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
package net.sf.hastee;

import net.sf.hastee.linking.StLinkingService;
import net.sf.hastee.naming.STNameProvider;
import net.sf.hastee.scoping.STImportUriResolver;

import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.antlr.Lexer;
import org.eclipse.xtext.parser.antlr.LexerBindings;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * This class defines several custom services.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class STRuntimeModule extends net.sf.hastee.AbstractSTRuntimeModule {

	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return StLinkingService.class;
	}

	public Class<? extends ImportUriResolver> bindImportUriResolver() {
		return STImportUriResolver.class;
	}

	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return STNameProvider.class;
	}

	@Override
	public void configureRuntimeLexer(Binder binder) {
		binder.bind(Lexer.class)
				.annotatedWith(Names.named(LexerBindings.RUNTIME))
				.to(CustomSTLexer.class);
	}

}
