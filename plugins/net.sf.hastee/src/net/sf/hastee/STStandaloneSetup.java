
package net.sf.stui;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class STStandaloneSetup extends STStandaloneSetupGenerated{

	public static void doSetup() {
		new STStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

