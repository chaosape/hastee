package net.sf.hastee;

import net.sf.hastee.st.Element;
import net.sf.hastee.st.Template;
import net.sf.hastee.st.TextContents;

public class Util {

	public static String toString(Template template) {
		StringBuilder builder = new StringBuilder();
		for (Element element : template.getElements()) {
			if (element instanceof TextContents) {
				TextContents contents = (TextContents) element;
				builder.append(contents.getContents());
			}
		}
		return builder.toString();
	}

}
