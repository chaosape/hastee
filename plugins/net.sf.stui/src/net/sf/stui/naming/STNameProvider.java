package net.sf.stui.naming;

import net.sf.stui.st.Attribute;
import net.sf.stui.st.Option;
import net.sf.stui.st.Property;
import net.sf.stui.st.SubTemplate;

import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;

public class STNameProvider extends DefaultDeclarativeQualifiedNameProvider {
	
	public String qualifiedName(Option option) {
		// name is private
		return null;
	}

	public String qualifiedName(Property property) {
		// name is private
		return null;
	}
	
	public String qualifiedName(Attribute attribute) {
		// name is private
		return null;
	}
	
	public String qualifiedName(SubTemplate template) {
		// name is private
		return null;
	}

}
