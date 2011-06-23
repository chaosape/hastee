package net.sf.hastee.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

/**
 * This class defines the highlighting configuration for the ST editor.
 * 
 * @author Herve Yviquel
 * 
 */
public class STHighlightingConfiguration extends
		DefaultHighlightingConfiguration {

	public static final String SEPARATORS = "Separators";

	public static final String TEMPLATE_NAME = "TemplateName";

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(TEMPLATE_NAME, "Template name",
				templateNameTextStyle());
		acceptor.acceptDefaultHighlighting(SEPARATORS, "Template separators",
				separatorsTextStyle());
	}

	public TextStyle separatorsTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 255));
		return textStyle;
	}

	public TextStyle templateNameTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setStyle(SWT.BOLD);
		textStyle.setColor(new RGB(255, 128, 0));
		return textStyle;
	}

}
