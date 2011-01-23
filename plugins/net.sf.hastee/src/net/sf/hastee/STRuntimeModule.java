/*
 * generated by Xtext
 */
package net.sf.hastee;

import net.sf.hastee.linking.StLinkingService;
import net.sf.hastee.naming.STNameProvider;

import org.eclipse.xtext.linking.ILinkingService;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class STRuntimeModule extends net.sf.hastee.AbstractSTRuntimeModule {

	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return STNameProvider.class;
	}
	
	@Override
	public Class<? extends ILinkingService> bindILinkingService() {
		return StLinkingService.class;
	}

}
