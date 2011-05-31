package org.csstudio.config.authorizeid;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.csstudio.config.authorizeid.messages"; //$NON-NLS-1$
	
	
	public static String AuthorizeIdView_AUTH_IDS_FROM_LDAP;
	public static String AuthorizeIdView_AUTH_IDS_REGISTERED;

	public static String AuthorizeIdView_DelEntry;

    public static String AuthorizeIdView_DelWarn;

    public static String AuthorizeIdView_DelWarn2;

    public static String AuthorizeIdView_Error;

    public static String AuthorizeIdView_GroupEdit;

    public static String AuthorizeIdView_GroupError;

    public static String AuthorizeIdView_GroupErrorDesc;

    public static String AuthorizeIdView_InvalidGroup;

    public static String AuthorizeIdView_MessageWrong1;

    public static String AuthorizeIdView_Name;

    public static String AuthorizeIdView_NameEdit;

    public static String AuthorizeIdView_RoleEdit;

    public static String AuthorizeIdView_SELECT_GROUP; 
	public static String AuthorizeIdView_GROUP;
	public static String AuthorizeIdView_NEW;
	public static String AuthorizeIdView_EDIT; 
	public static String AuthorizeIdView_DELETE; 
	public static String AuthorizeIdView_EAIN; 
	public static String AuthorizeIdView_IS_REGISTERED;
	public static String AuthorizeIdView_DESCRIPTION;
	public static String AuthorizeIdView_ORIGINATING_PLUGIN;
	public static String AuthorizeIdView_EAIG; 
	public static String AuthorizeIdView_EAIR;
	public static String AuthorizeIdView_USERS;

    public static String AuthorizeIdView_SelGroup;

    public static String AuthorizeIdView_SelRole;

    public static String NewDataValidator_ValidatorDesc;

    public static String NewDataValidator_ValidatorWarn; 
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages() {
	}
}
