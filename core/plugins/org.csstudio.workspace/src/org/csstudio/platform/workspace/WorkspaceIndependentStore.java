package org.csstudio.platform.workspace;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/** To store preferences which are independent from workspace. 
 * 	These preferences are stored at <code>Install_Location/configuration/.settings</code>
 *  You can add your own preference by inheriting this class
 *  @author Xihui Chen
 */
public class WorkspaceIndependentStore
{
    /** Qualifier used as preference store ID */
    private static final String PREF_QUALIFIER = "org.csstudio.platform";//$NON-NLS-1$

    /** Preference ID */
    private static final String LAST_LOGIN_USER = "last_login_user"; //$NON-NLS-1$

    
    /**
     * @return last login user
     */
    public static String readLastLoginUser()
    {
        return getString(LAST_LOGIN_USER);      
    }
    
    /** write the last logged in user's name into ConfigurationScope preference store
     * @param lastLoginUserName
     */
    public static void writeLastLoginUser(String lastLoginUserName) {
    	writeString(LAST_LOGIN_USER, lastLoginUserName);
    }
    
    /**Get the value of the input preference ID.
     * @param preferenceID the key trying to get its value
     * @return value associated with preferenceID. return "" if no value got.
     */
    protected static String getString(final String preferenceID) {
    	Preferences configurationNode = new ConfigurationScope().getNode(PREF_QUALIFIER);
    	String value = configurationNode.get(preferenceID, "");
    	return value;    
    }
    
    /**Write the value of the input preference ID into ConfigurationScope preference store
     * @param preferenceID the key trying to save its value
     * @param value value associated with preferenceID
     */
    protected static void writeString(final String preferenceID, final String value) {
    	 final Preferences node =
            new ConfigurationScope().getNode(PREF_QUALIFIER);
    	 node.put(preferenceID, value);    
    	 try {
			node.flush();
		} catch (BackingStoreException e) {
		    Activator.getInstance().getLog().log(
                    new Status(IStatus.ERROR, Activator.ID,
                            "Cannot persist " + preferenceID + ": "
                            + e.getMessage()));
		}
    }

}