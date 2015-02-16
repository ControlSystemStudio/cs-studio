package org.csstudio.platform.internal.jassauthentication.preference;

import java.util.ArrayList;
import java.util.List;



/**
 * model used for JAAS authentication preference page. 
 * @author Xihui Chen
 *
 */
@SuppressWarnings("nls")
public class JAASPreferenceModel {
	
	public static final String FLAG_OPTIONAL = "optional"; //$NON-NLS-1$

	public static final String FLAG_SUFFICIENT = "sufficient"; //$NON-NLS-1$

	public static final String FLAG_REQUISITE = "requisite"; //$NON-NLS-1$

	public static final String FLAG_REQUIRED = "required"; //$NON-NLS-1$

	public static final String SOURCE_PREFERENCE_PAGE = "PreferencePage"; //$NON-NLS-1$

	public static final String SOURCE_FILE = "File"; //$NON-NLS-1$

	/**
	 * login module flags represented in string
	 */
	public static final String[] FLAGS = new String[]{
			FLAG_REQUIRED, FLAG_REQUISITE, FLAG_SUFFICIENT, FLAG_OPTIONAL}; 
	
	/**
	 * the login configuration source
	 */
	public static final String[] CONFIG_SOURCES = new String[] {
			SOURCE_FILE, SOURCE_PREFERENCE_PAGE};
	
	/**
	 * This is the configuration entries object to be edited in preference page 
	 */
	public static final List<JAASConfigurationEntry> configurationEntryList = 
			new ArrayList<JAASConfigurationEntry>();
	
	
	
	
	
}
