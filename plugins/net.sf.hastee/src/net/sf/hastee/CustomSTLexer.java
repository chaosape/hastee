package net.sf.hastee;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Stack;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.xtext.parser.antlr.Lexer;

/**
 * This class defines a hand-written lexer for ST that allows custom separators.
 * 
 * @author Matthieu Wipliez
 * 
 */
public class CustomSTLexer extends Lexer {

	private static enum LexingState {
		TOP_LEVEL
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

	private static final int NEWLINE;

	private static final int OR;

	private static final int PIPE;

	private static final int QUOTE;

	private static final int RBRACK;

	private static final int RCURLY;

	private static final int RDELIM;

	private static final int RPAREN;

	private static final int SEMI;

	private static final int STRING;

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
		NEWLINE = getTokenId(tokenMap, "\n");
		OR = getTokenId(tokenMap, "||");
		PIPE = getTokenId(tokenMap, "|");
		QUOTE = getTokenId(tokenMap, "\"");
		RBRACK = getTokenId(tokenMap, "]");
		RCURLY = getTokenId(tokenMap, "}");
		RDELIM = getTokenId(tokenMap, ">");
		RPAREN = getTokenId(tokenMap, ")");
		SEMI = getTokenId(tokenMap, ";");
		TMPL_BEGIN = getTokenId(tokenMap, "<<");
		TMPL_DEF = getTokenId(tokenMap, "::=");
		TMPL_END = getTokenId(tokenMap, ">>");

		ID = getTokenId(tokenMap, "RULE_ID");
		STRING = getTokenId(tokenMap, "RULE_STRING");
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
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String) entry.getKey();
			key = key.replaceFirst("'([^']*)'", "$1");
			Integer value = Integer.valueOf((String) entry.getValue());
			tokenMap.put(key, value);
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

	private Stack<LexingState> lexingState;

	public CustomSTLexer() {
		lexingState = new Stack<CustomSTLexer.LexingState>();
		lexingState.push(LexingState.TOP_LEVEL);
	}

	/** ':' | '::=' */
	private int mColonOrTmplDef() {
		input.consume();
		int c = input.LA(1);
		if (c == ':') {
			c = input.LA(2);
			if (c == '=') {
				return TMPL_DEF;
			}
		}
		return COLON;
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

	@Override
	public void mTokens() throws RecognitionException {
		LexingState topState = lexingState.peek();
		switch (topState) {
		case TOP_LEVEL:
			state.type = mTopLevel();
			break;
		}
	}

	private int mTopLevel() {
		int c = input.LA(1);
		if (isWS(c)) {
			return mWS(c);
		} else if (isIDStartLetter(c)) {
			return mIDOrKeyword();
		} else {
			switch (c) {
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
				return QUOTE;
			case ']':
				input.consume();
				return RBRACK;
			case ')':
				input.consume();
				return RPAREN;
			case '<':
				return mTmplBegin();
			case '>':
				return mTmplEnd();
			}
		}
		return 0;
	}

	private int mTmplBegin() {
		input.consume();
		int c = input.LA(1);
		if (c == '<') {
			input.consume();
			return TMPL_BEGIN;
		}
		return 0;
	}

	private int mTmplEnd() {
		input.consume();
		int c = input.LA(1);
		if (c == '>') {
			input.consume();
			return TMPL_END;
		}
		return 0;
	}

	private int mWS(int c) {
		while (Character.isWhitespace((char) c)) {
			input.consume();
			c = input.LA(1);
		}

		return WS;
	}

}
