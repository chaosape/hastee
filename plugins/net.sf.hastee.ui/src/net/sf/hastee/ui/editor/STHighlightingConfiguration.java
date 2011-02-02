package net.sf.hastee.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class STHighlightingConfiguration extends
		DefaultHighlightingConfiguration {

	public static final String RULENAME_ID = "rulename";
	public static final String RULEOPENING_ID = "ruleopening";
	public static final String RULECLOSING_ID = "ruleclosing";
	public static final String RULEEQUAL_ID = "ruleequal";

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(RULENAME_ID, "RuleName",
				ruleNameTextStyle());
		acceptor.acceptDefaultHighlighting(RULEOPENING_ID, "RuleOpening",
				ruleOpeningTextStyle());
		acceptor.acceptDefaultHighlighting(RULECLOSING_ID, "RuleClosing",
				ruleClosingTextStyle());
		acceptor.acceptDefaultHighlighting(RULEEQUAL_ID, "RuleEqual",
				ruleEqualTextStyle());
	}

	public TextStyle ruleNameTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	public TextStyle ruleOpeningTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 255));
		return textStyle;
	}

	public TextStyle ruleClosingTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 255));
		return textStyle;
	}

	public TextStyle ruleEqualTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 255));
		return textStyle;
	}
}
