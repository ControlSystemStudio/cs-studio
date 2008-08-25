package org.csstudio.config.authorizeid;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.csstudio.config.authorizeid.messages";
	
	public static String AuthorizeIdView_SELECT_GROUP; 
	public static String AuthorizeIdView_GROUP;
	public static String AuthorizeIdView_NEW;
	public static String AuthorizeIdView_EDIT; 
	public static String AuthorizeIdView_DELETE; 
	public static String AuthorizeIdView_EAIN; 
	public static String AuthorizeIdView_EAIG; 
	public static String AuthorizeIdView_EAIR; 
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages() {
	}
}
