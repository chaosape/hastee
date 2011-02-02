package net.sf.hastee.ui.editor;

import org.eclipse.xtext.ui.editor.syntaxcoloring.antlr.DefaultAntlrTokenToAttributeIdMapper;

public class STAntlrTokenToAttributeIdMapper extends
		DefaultAntlrTokenToAttributeIdMapper {

	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if ("'<<'".equals(tokenName)) {
			return STHighlightingConfiguration.RULEOPENING_ID;
		}
		if ("'>>'".equals(tokenName)) {
			return STHighlightingConfiguration.RULECLOSING_ID;
		}
		if ("'::='".equals(tokenName)) {
			return STHighlightingConfiguration.RULEEQUAL_ID;
		}
		return super.calculateId(tokenName, tokenType);
	}
}
