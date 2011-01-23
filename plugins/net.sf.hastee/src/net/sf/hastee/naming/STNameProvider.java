package net.sf.hastee.naming;

import net.sf.hastee.st.Attribute;
import net.sf.hastee.st.Option;
import net.sf.hastee.st.Property;
import net.sf.hastee.st.SubTemplate;

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
