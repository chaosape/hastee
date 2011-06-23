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
