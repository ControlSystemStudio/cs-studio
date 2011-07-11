
package org.csstudio.nams.service.preferenceservice.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "org.csstudio.nams.service.preferenceservice.ui.messages"; //$NON-NLS-1$
	
    public static String AbstractNewAMSFieldEditorPreferencePage_separator_between_label_and_field;
	public static String AMSInternalMessagingServerSetUp_title;
	public static String AMSInternalMessagingTopicsSetUp_title;
	public static String ApplicationDatabaseSetUp_title;
	public static String ConfigurationDatabaseSetUp_title;
	public static String NewAMSRootPage_title;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	    // Avoid instantiation
	}
}
