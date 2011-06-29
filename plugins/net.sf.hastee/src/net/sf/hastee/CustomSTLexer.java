package net.sf.hastee;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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

	public static final int AND;
	public static final int AT;
	public static final int BANG;
	public static final int COLON;
	public static final int COMMA;
	public static final int DOT;
	public static final int ELLIPSIS;
	public static final int ELSE;
	public static final int ELSEIF;
	public static final int ENDIF;
	public static final int EQUALS;
	public static final int ID;
	public static final int IF;
	public static final int LBRACK;
	public static final int LCURLY;
	public static final int LDELIM;
	public static final int LPAREN;
	public static final int NEWLINE;
	public static final int OR;
	public static final int PIPE;
	public static final int RBRACK;
	public static final int RCURLY;
	public static final int RDELIM;
	public static final int RPAREN;
	public static final int SEMI;
	public static final int STRING;
	public static final int SUPER;

	static {
		Map<String, Integer> tokenMap = initTokenValues();
		AND = getTokenId(tokenMap, "&&");
		AT = getTokenId(tokenMap, "@");
		BANG = getTokenId(tokenMap, "!");
		COLON = getTokenId(tokenMap, ":");
		COMMA = getTokenId(tokenMap, ",");
		DOT = getTokenId(tokenMap, ".");
		ELLIPSIS = getTokenId(tokenMap, "...");
		ELSE = getTokenId(tokenMap, "else");
		ELSEIF = getTokenId(tokenMap, "elseif");
		ENDIF = getTokenId(tokenMap, "endif");
		EQUALS = getTokenId(tokenMap, "=");
		ID = getTokenId(tokenMap, "RULE_ID");
		IF = getTokenId(tokenMap, "if");
		LBRACK = getTokenId(tokenMap, "[");
		LCURLY = getTokenId(tokenMap, "{");
		LDELIM = getTokenId(tokenMap, "<");
		LPAREN = getTokenId(tokenMap, "(");
		NEWLINE = getTokenId(tokenMap, "\n");
		OR = getTokenId(tokenMap, "||");
		PIPE = getTokenId(tokenMap, "|");
		RBRACK = getTokenId(tokenMap, "]");
		RCURLY = getTokenId(tokenMap, "}");
		RDELIM = getTokenId(tokenMap, ">");
		RPAREN = getTokenId(tokenMap, ")");
		SEMI = getTokenId(tokenMap, ";");
		STRING = getTokenId(tokenMap, "STRING");
		SUPER = getTokenId(tokenMap, "super");
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

	@Override
	public void mTokens() throws RecognitionException {
		// TODO Auto-generated method stub
		state.type = ELSE;

		// to skip tokens
		state.token = Token.SKIP_TOKEN;

		// look ahead
		// int c = input.LA(1);
	}

}
