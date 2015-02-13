/*
 * 	Copyright (c) 2011, IETR/INSA of Rennes
 * Copyright (c) 2012, Synflow
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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
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
		DELIMITERS, EXPRESSION, GROUP, IMPORTS, TEMPLATE, TEMPLATE_ANON
	}

	private static final int AND;

	private static final int AT;

	private static final int BANG;

	private static final int COLON;

	private static final int COMMA;

	private static final int DELIMITERS;

	private static final int DOT;

	private static final int ELLIPSIS;

	private static final int EQUALS;

	private static final int ID;

	private static final int IMPORT;

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
	
	private static final int ESCAPE;

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
		ESCAPE = getTokenId(tokenMap, "RULE_ESCAPE");
		WS = getTokenId(tokenMap, "RULE_WS");

		keywords = new HashMap<String, Integer>();
		addKeyword(tokenMap, "default");
		addKeyword(tokenMap, "else");
		addKeyword(tokenMap, "elseif");
		addKeyword(tokenMap, "endif");
		addKeyword(tokenMap, "false");
		addKeyword(tokenMap, "if");
		addKeyword(tokenMap, "super");
		addKeyword(tokenMap, "true");

		DELIMITERS = addKeyword(tokenMap, "delimiters");
		IMPORT = addKeyword(tokenMap, "import");
	}

	private static int addKeyword(Map<String, Integer> tokenMap, String kwd) {
		int id = getTokenId(tokenMap, kwd);
		keywords.put(kwd, id);
		return id;
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

	public static boolean isHexDigit(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
	}
	
	public static boolean isWS(int c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}

	private boolean bigString;

	private int delimIndex;

	private int delimiterStartChar = '<';

	private int delimiterStopChar = '>';

	private boolean endOfGroupHeader;

	private Deque<LexingState> lexingState;

	private int subtemplateDepth;

	public CustomSTLexer() {
		lexingState = new ArrayDeque<CustomSTLexer.LexingState>();
		lexingState.push(LexingState.GROUP);
	}

	public String getErrorMessage(Token t) {
		String retval = super.getErrorMessage(t);
		if ( retval == null) {
			// TODO this will screw up the whole editor 
			System.err.println("Dazed and confused: No error messge for token: " + t.toString());
			return "Lexer error at: " + t.toString();
		}
		return retval;
	}
	

	/** ',' | ID | WS | '|' */
	private int mAnonTemplate() throws RecognitionException {
		int c = input.LA(1);
		if (isWS(c)) {
			return mWS();
		} else if (isIDStartLetter(c)) {
			return mIDOrKeyword(false); // do not look for keywords
		} else {
			switch (c) {
			case ',':
				input.consume();
				return COMMA;
			case '|':
				input.consume();
				lexingState.pop(); // get out of template anonymous state
				lexingState.push(LexingState.TEMPLATE);
				return PIPE;
			}
		}

		throw new NoViableAltException("mAnonTemplate", 0, state.type, input);
	}

	/** ((',' | ID | WS)* '|')? */
	private int mAnonTmplStart() {
		input.consume();
		subtemplateDepth++;

		int c = input.LA(1);
		int i = 1;
		boolean pipeSeen = false;
		while (!pipeSeen && c != '}') {
			if (c == '|') {
				pipeSeen = true;
			} else if (!isWS(c) && c != ',' && !isIDLetter(c)) {
				// no whitespace, comma or ID char => we should not see a pipe
				break;
			} else {
				i++;
				c = input.LA(i);
			}
		}

		lexingState.push(pipeSeen ? LexingState.TEMPLATE_ANON
				: LexingState.TEMPLATE);
		return LCURLY;
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
		int c = input.LA(1);
		while (c != Token.EOF) {
			if (c == '*') {
				input.consume();
				c = input.LA(1);
				if (c == '/') {
					input.consume();
					return ML_COMMENT;
				}
				continue;
			}

			input.consume();
			c = input.LA(1);
		}
		return Token.EOF;
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

	/** matches comma, and two STRING before going back to group */
	private int mDelimiters() throws RecognitionException {
		int c = input.LA(1);
		if (isWS(c)) {
			return mWS();
		} else {
			switch (c) {
			case ',':
				input.consume();
				return COMMA;

			case '"': {
				state.type = mSTRING();
				Token delim = emit();
				char delimChar = delim.getText().charAt(1);
				if (delimIndex == 0) {
					delimiterStartChar = delimChar;
					delimIndex++;
				} else {
					delimiterStopChar = delimChar;
					delimIndex = 0;
					lexingState.pop(); // back to group
				}
				return state.type;
			}
			}
		}

		throw new NoViableAltException("mDelimiters", 0, state.type, input);
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
				match('|');
				return OR; // ||
			case '{':
				return mAnonTmplStart();
			case '\\':
				if ( c == '\\' && input.LA(2) == '\"' && !bigString) {
					return mSUBSTRING();
				}
			default:
				if (c == delimiterStopChar) {
					input.consume();
					lexingState.pop(); // get out of expression
					return RDELIM;
				}
				if (isIDStartLetter(c)) {
					return mIDOrKeyword(true);
				}

				MismatchedTokenException re = new MismatchedTokenException(
						delimiterStopChar, input);
				recover(re);
				lexingState.pop(); // get out of expression
				throw re;
			}
		}
	}

	private int mGroup() throws RecognitionException {
		int c = input.LA(1);
		if (isWS(c)) {
			return mWS();
		} else if (isIDStartLetter(c)) {
			int token = mIDOrKeyword(true);
			if (!endOfGroupHeader) {
				if (token == DELIMITERS) {
					lexingState.push(LexingState.DELIMITERS);
				} else if (token == IMPORT) {
					lexingState.push(LexingState.IMPORTS);
				} else {
					// identifier other than delimiters/import means the header
					// is over
					endOfGroupHeader = true;
				}
			}
			return token;
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
	private int mIDOrKeyword(boolean replaceKeywords) {
		input.consume();
		int c = input.LA(1);
		while (isIDLetter(c)) {
			input.consume();
			c = input.LA(1);
		}

		// emit an ID token tentatively
		state.type = ID;
		if (replaceKeywords) {
			Token id = emit();
			String name = id.getText();
			Integer value = keywords.get(name);
			if (value != null) {
				// if ID is a keyword, forget the token emitted and set the type
				state.token = null;
				state.type = value;
			}
		}
		return state.type;
	}

	/** matches a single STRING and goes back to group mode */
	private int mImports() throws RecognitionException {
		int c = input.LA(1);
		if (isWS(c)) {
			return mWS();
		} else if (c == '"') {
			lexingState.pop(); // back to group
			return mSTRING();
		}

		throw new NoViableAltException("mImports", 0, state.type, input);
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
	
	/** STRING inside string(bigstring=false): setTest(ranges) ::= "<ranges; separator=\" || \">" */ 
	private int mSUBSTRING() {
		// assume that both \ and " were checked by caller
		input.consume();
		input.consume();
		int c = input.LA(1);
		while (c != '\\' && input.LA(2)!='"' ) {
			input.consume();
			if (c == '\\') {
				// in STRING \\ can escape anything
				input.consume();
			}
			c = input.LA(1);
		}
		input.consume();
		input.consume();

		return STRING;
	}
	

	private int mTemplate() throws RecognitionException {
		int c = input.LA(1);
		if (bigString) {
			// allows ST 4.0.2 "%>" template end
			if ((c == '%' || c == '>') && input.LA(2) == '>') {
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
		}

		return mTEXT();
	}

	/** TEXT : anything until delimiterStartChar or '}' */
	private int mTEXT() {
		int c = input.LA(1);
		while (true) {
			if (c == Token.EOF) {
				return Token.EOF;
			} else if (bigString) {
				// allows ST 4.0.2 "%>" end of template
				if ((c == '%' || c == '>') && input.LA(2) == '>') {
					break; // end of TEXT token
				}
			} else if (c == '"') {
				// implied (bigString == false)
				break; // end of TEXT token
			}

			if (c == delimiterStartChar) {
				break; // end of TEXT token
			} else if (c == '}' && subtemplateDepth > 0) {
				break; // end of TEXT token
			} else if (c == '\\') {
				input.consume(); // toss out \ char
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
		// allows ST 4.0.2 "<%" for beginning a template
		if (input.LA(1) == '%') {
			input.consume();
		} else {
			// otherwise, match a '<'
			match('<');
		}
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

	// XXX: This method should handle all escaped forms within delimiters.
	private int mTmplESCAPE() throws RecognitionException {
		boolean newLineCheck = false;
		input.consume(); // kill \\
		int c = input.LA(1);
		switch (c) {
		//XXX: The cheat sheet for ST 4.0 indicates that we should be able to multiple unicode characters in escapes
		// of these kinds. However, when looking at the ST code it seems to no accept multiple unicode characters.
		//uni
		case 'u':
			input.consume();
			for(int i = 0;i<4;i++) {
				if(isHexDigit(input.LA(1))) {
					input.consume();
				} else {
					new MismatchedTokenException(input.LA(1), input);
				}
			}
			break;
		case '\\':
			input.consume();
			newLineCheck = true;
			break;
		// XXX: The following case does not truly belong in this function since it is not detecting an escape but 
		// a comment.
		case '!':
			input.consume();
			while(!(input.LA(1)=='!'&&input.LA(2)==delimiterStopChar)) {input.consume();}
			input.consume();
			input.consume();
			return ML_COMMENT;
		default: 
			throw new NoViableAltException("mTmplESCAPE", 0, state.type, input);
		}
		match(delimiterStopChar);
		if(newLineCheck) {
			for(c=input.LA(1);c==' '||c=='\t';c=input.LA(1)) {input.consume();}
			if(c=='\r') {input.consume();}
			match('\n');
		}
		return ESCAPE;
	}
	@Override
	public void mTokens() throws RecognitionException {
		LexingState topState = lexingState.peek();
		switch (topState) {
		case GROUP:
			state.type = mGroup();
			break;

		case DELIMITERS:
			state.type = mDelimiters();
			break;

		case IMPORTS:
			state.type = mImports();
			break;

		case TEMPLATE:
			state.type = mTemplate();
			break;

		case EXPRESSION:
			state.type = mExpression();
			break;

		case TEMPLATE_ANON:
			state.type = mAnonTemplate();
			break;

		default:
			break;
		}
	}

	private int mWS() {
		int c = input.LA(1);
		while (Character.isWhitespace((char) c)) {
			input.consume();
			c = input.LA(1);
		}

		return WS;
	}

}
