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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.xtext.parser.antlr.Lexer;

/**
 * This class defines a hand-written lexer for ST that allows custom separators.
 * Some methods are derived from work copyrighted by Terrence Parr.
 * 
 * <p>
 * The idea of this lexer is to combine three different lexers depending on the
 * context. Three contexts are handled: group (at the highest-level), template
 * (inside a template definition) and expression (inside an expression). Since
 * an expression may include a template, we maintain a stack of lexing states.
 * </p>
 * 
 * @author Matthieu Wipliez
 * 
 */
public class CustomSTLexer extends Lexer {

	private static enum LexingState {
		EXPRESSION, GROUP, TEMPLATE
	}

	private static final int AND;

	private static final int AT;

	private static final int BANG;

	private static final int COLON;

	private static final int COMMA;

	private static final int DOT;

	private static final int ELLIPSIS;

	private static final int EQUALS;

	private static final int ID;

	private static final Map<String, Integer> keywords;

	private static final int LBRACK;

	private static final int LCURLY;

	private static final int LDELIM;

	private static final int LPAREN;

	private static final int ML_COMMENT;

	private static final int OR;

	private static final int PIPE;

	private static final int QUOTE;

	private static final int RBRACK;

	private static final int RCURLY;

	private static final int RDELIM;

	private static final int REGION_END;

	private static final int RPAREN;

	private static final int SEMI;

	private static final int SL_COMMENT;

	private static final int STRING;

	private static final int TEXT;

	private static final int TMPL_BEGIN;

	private static final int TMPL_DEF;

	private static final int TMPL_END;

	private static final int WS;

	static {
		Map<String, Integer> tokenMap = initTokenValues();
		AND = getTokenId(tokenMap, "&&");
		AT = getTokenId(tokenMap, "@");
		BANG = getTokenId(tokenMap, "!");
		COLON = getTokenId(tokenMap, ":");
		COMMA = getTokenId(tokenMap, ",");
		DOT = getTokenId(tokenMap, ".");
		ELLIPSIS = getTokenId(tokenMap, "...");
		EQUALS = getTokenId(tokenMap, "=");
		LBRACK = getTokenId(tokenMap, "[");
		LCURLY = getTokenId(tokenMap, "{");
		LDELIM = getTokenId(tokenMap, "<");
		LPAREN = getTokenId(tokenMap, "(");
		OR = getTokenId(tokenMap, "||");
		PIPE = getTokenId(tokenMap, "|");
		QUOTE = getTokenId(tokenMap, "\"");
		RBRACK = getTokenId(tokenMap, "]");
		RCURLY = getTokenId(tokenMap, "}");
		RDELIM = getTokenId(tokenMap, ">");
		REGION_END = getTokenId(tokenMap, "@end");
		RPAREN = getTokenId(tokenMap, ")");
		SEMI = getTokenId(tokenMap, ";");
		TMPL_BEGIN = getTokenId(tokenMap, "<<");
		TMPL_DEF = getTokenId(tokenMap, "::=");
		TMPL_END = getTokenId(tokenMap, ">>");

		ID = getTokenId(tokenMap, "RULE_ID");
		ML_COMMENT = getTokenId(tokenMap, "RULE_ML_COMMENT");
		SL_COMMENT = getTokenId(tokenMap, "RULE_SL_COMMENT");
		STRING = getTokenId(tokenMap, "RULE_STRING");
		TEXT = getTokenId(tokenMap, "RULE_TEXT");
		WS = getTokenId(tokenMap, "RULE_WS");

		keywords = new HashMap<String, Integer>();
		keywords.put("default", getTokenId(tokenMap, "default"));
		keywords.put("else", getTokenId(tokenMap, "else"));
		keywords.put("elseif", getTokenId(tokenMap, "elseif"));
		keywords.put("endif", getTokenId(tokenMap, "endif"));
		keywords.put("false", getTokenId(tokenMap, "false"));
		keywords.put("if", getTokenId(tokenMap, "if"));
		keywords.put("import", getTokenId(tokenMap, "import"));
		keywords.put("super", getTokenId(tokenMap, "super"));
		keywords.put("true", getTokenId(tokenMap, "true"));
	}

	private static int getTokenId(Map<String, Integer> tokenMap, String name) {
		Integer id = tokenMap.get(name);
		if (id == null) {
			return Token.INVALID_TOKEN_TYPE;
		}
		return id;
	}

	/**
	 * Initializes the values of tokens.
	 */
	private static Map<String, Integer> initTokenValues() {
		Map<String, Integer> tokenMap = new HashMap<String, Integer>();
		ClassLoader loader = CustomSTLexer.class.getClassLoader();
		InputStream in = loader
				.getResourceAsStream("net/sf/hastee/parser/antlr/internal/InternalST.tokens");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
			String line = br.readLine();
			Pattern pattern = Pattern.compile("(('(.*)')|(.*))=(\\d+)");
			while (line != null) {
				Matcher m = pattern.matcher(line);
				if (!m.matches()) {
					throw new IllegalStateException("Couldn't match line : '"
							+ line + "'");
				}

				int antlrTokenType = Integer.parseInt(m.group(5));
				String antlrTokenDef = m.group(3) != null ? m.group(3) : m
						.group(4);
				tokenMap.put(antlrTokenDef, antlrTokenType);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tokenMap;
	}

	public static boolean isIDLetter(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0'
				&& c <= '9' || c == '_' || c == '/';
	}

	public static boolean isIDStartLetter(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
	}

	public static boolean isWS(int c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	private int delimiterStartChar = '<';

	private int delimiterStopChar = '>';

	private boolean bigString;

	private Stack<LexingState> lexingState;

	private int subtemplateDepth;

	public CustomSTLexer() {
		lexingState = new Stack<CustomSTLexer.LexingState>();
		lexingState.push(LexingState.GROUP);
	}

	/** ':' | '::=' */
	private int mColonOrTmplDef() {
		input.consume();
		int c = input.LA(1);
		if (c == ':' && input.LA(2) == '=') {
			input.consume();
			input.consume();
			return TMPL_DEF;
		}
		return COLON;
	}

	/** SL_COMMENT | ML_COMMENT */
	private int mComment() throws RecognitionException {
		input.consume();
		int c = input.LA(1);
		if (c == '/') {
			return mCommentSL();
		} else if (c == '*') {
			return mCommentML();
		}

		throw new NoViableAltException("mTopLevel", 0, state.type, input);
	}

	/** ML_COMMENT: '/' *' ( options {greedy=false;} : . )* '*' '/' */
	private int mCommentML() {
		input.consume();
		while (!(input.LA(1) == '*' && input.LA(2) == '/')) {
			input.consume();
		}
		return ML_COMMENT;
	}

	/** SL_COMMENT: '//' ~('\n'|'\r')* '\r'? '\n' */
	private int mCommentSL() {
		input.consume();
		int c = input.LA(1);
		while (c != '\r' && c != '\n') {
			input.consume();
			c = input.LA(1);
		}

		input.consume();
		if (c == '\r') {
			c = input.LA(1);
			if (c == '\n') {
				input.consume();
			}
		}

		return SL_COMMENT;
	}

	private int mExpression() throws RecognitionException {
		while (true) {
			int c = input.LA(1);
			switch (c) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				input.consume();
				return WS;
			case '.':
				input.consume();
				if (input.LA(1) == '.' && input.LA(2) == '.') {
					input.consume();
					input.consume();
					return ELLIPSIS;
				}
				return DOT;
			case ',':
				input.consume();
				return COMMA;
			case ':':
				input.consume();
				return COLON;
			case ';':
				input.consume();
				return SEMI;
			case '(':
				input.consume();
				return LPAREN;
			case ')':
				input.consume();
				return RPAREN;
			case '[':
				input.consume();
				return LBRACK;
			case ']':
				input.consume();
				return RBRACK;
			case '=':
				input.consume();
				return EQUALS;
			case '!':
				input.consume();
				return BANG;
			case '@':
				input.consume();
				if (c == 'e' && input.LA(2) == 'n' && input.LA(3) == 'd') {
					input.consume();
					input.consume();
					input.consume();
					return REGION_END;
				}
				return AT;
			case '"':
				return mSTRING();
			case '&':
				input.consume();
				match('&');
				return AND; // &&
			case '|':
				input.consume();
				if (input.LA(1) == '|') {
					input.consume();
					return OR; // ||
				}
				return PIPE;
			case '{':
				input.consume();
				subtemplateDepth++;
				lexingState.push(LexingState.TEMPLATE);
				return LCURLY;
			default:
				if (c == delimiterStopChar) {
					input.consume();
					lexingState.pop(); // get out of expression
					return RDELIM;
				}
				if (isIDStartLetter(c)) {
					return mIDOrKeyword();
				}
				RecognitionException re = new NoViableAltException("", 0, 0,
						input);
				re.line = state.tokenStartLine;
				re.charPositionInLine = state.tokenStartCharPositionInLine;
				throw re;
			}
		}
	}

	private int mGroup() throws RecognitionException {
		int c = input.LA(1);
		if (isWS(c)) {
			return mWS(c);
		} else if (isIDStartLetter(c)) {
			return mIDOrKeyword();
		} else {
			switch (c) {
			case '/':
				return mComment();
			case '@':
				input.consume();
				return AT;
			case ':':
				return mColonOrTmplDef();
			case ',':
				input.consume();
				return COMMA;
			case '.':
				input.consume();
				return DOT;
			case '=':
				input.consume();
				return EQUALS;
			case '[':
				input.consume();
				return LBRACK;
			case '(':
				input.consume();
				return LPAREN;
			case '"':
				input.consume();
				bigString = false;
				lexingState.push(LexingState.TEMPLATE);
				return QUOTE;
			case ']':
				input.consume();
				return RBRACK;
			case ')':
				input.consume();
				return RPAREN;
			case '<':
				return mTmplBegin();
			}
		}

		throw new NoViableAltException("mTopLevel", 0, state.type, input);
	}

	/** ID : ('a'..'z'|'A'..'Z'|'_'|'/') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')* ; */
	private int mIDOrKeyword() {
		input.consume();
		int c = input.LA(1);
		while (isIDLetter(c)) {
			input.consume();
			c = input.LA(1);
		}

		// emit an ID token tentatively
		state.type = ID;
		Token id = emit();
		String name = id.getText();
		Integer value = keywords.get(name);
		if (value != null) {
			// if ID is a keyword, forget the token emitted and set the type
			state.token = null;
			state.type = value;
		}
		return state.type;
	}

	/** STRING : '"' ( '\\' '"' | '\\' ~'"' | ~('\\'|'"') )* '"' ; */
	private int mSTRING() {
		input.consume();
		int c = input.LA(1);
		while (c != '"') {
			input.consume();
			if (c == '\\') {
				// in STRING \\ can escape anything
				input.consume();
			}
			c = input.LA(1);
		}
		input.consume();

		return STRING;
	}

	private int mTemplate() throws RecognitionException {
		int c = input.LA(1);
		if (c == delimiterStartChar) {
			input.consume();
			c = input.LA(1);
			switch (c) {
			case '!':
				return mTmplCOMMENT();
			case '\\':
				return mTmplESCAPE(); // <\\> <\uFFFF> <\n> etc...
			default:
				lexingState.push(LexingState.EXPRESSION); // start expression
				return LDELIM;
			}
		}

		if (c == '}' && subtemplateDepth > 0) {
			input.consume();
			lexingState.pop(); // get out of template
			subtemplateDepth--;
			return RCURLY;
		} else if (bigString) {
			if (c == '>' && input.LA(2) == '>') {
				input.consume();
				input.consume();
				lexingState.pop();
				return TMPL_END;
			}
		} else if (c == '"') {
			// implied (bigString == false)
			input.consume();
			lexingState.pop();
			return QUOTE;
		}

		return mTEXT();
	}

	/** TEXT : anything until delimiterStartChar or '}' */
	private int mTEXT() {
		int c = input.LA(1);
		while (true) {
			if (c == Token.EOF) {
				return Token.EOF;
			} else if (c == delimiterStartChar) {
				break; // end of TEXT token
			} else if (c == '}' && subtemplateDepth > 0) {
				break; // end of TEXT token
			} else if (c == '\\') {
				input.consume(); // toss out \ char
			} else if (bigString) {
				if (c == '>' && input.LA(2) == '>') {
					break; // end of TEXT token
				}
			} else if (c == '"') {
				// implied (bigString == false)
				break; // end of TEXT token
			}

			input.consume();
			c = input.LA(1);
		}

		return TEXT;
	}

	/** TMPL_BEGIN : '&lt;' '&lt;' */
	private int mTmplBegin() throws RecognitionException {
		bigString = true;
		input.consume();
		match('<');
		lexingState.push(LexingState.TEMPLATE);
		return TMPL_BEGIN;
	}

	/** COMMENT : '&lt;' '!' .* '!' '&gt;' */
	private int mTmplCOMMENT() throws RecognitionException {
		input.consume(); // kill !
		int c = input.LA(1);
		while (!(c == '!' && input.LA(2) == delimiterStopChar)) {
			if (c == Token.EOF) {
				RecognitionException re = new MismatchedTokenException(
						(int) '!', input);
				re.line = input.getLine();
				re.charPositionInLine = input.getCharPositionInLine();
				throw re;
			}
			input.consume();
			c = input.LA(1);
		}

		// consume '!' '>'
		input.consume();
		match(delimiterStopChar);

		// skip token
		state.token = Token.SKIP_TOKEN;
		return 0;
	}

	/** */
	private int mTmplESCAPE() throws RecognitionException {
		input.consume(); // kill \\
		int c = input.LA(1);
		switch (c) {
		// TODO ESCAPE
		}
		input.consume();
		match(delimiterStopChar);
		return TEXT;
	}

	@Override
	public void mTokens() throws RecognitionException {
		LexingState topState = lexingState.peek();
		switch (topState) {
		case GROUP:
			state.type = mGroup();
			break;

		case TEMPLATE:
			state.type = mTemplate();
			break;

		case EXPRESSION:
			state.type = mExpression();
			break;
		}
	}

	private int mWS(int c) {
		while (Character.isWhitespace((char) c)) {
			input.consume();
			c = input.LA(1);
		}

		return WS;
	}

}
