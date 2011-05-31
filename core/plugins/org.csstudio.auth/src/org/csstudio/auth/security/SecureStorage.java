package org.csstudio.auth.security;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.internal.preferences.PreferencesHelper;
import org.csstudio.auth.securestore.SecureStore;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

/**Secure Storage is used to save sensitive data such as user name and password in 
 * encrypted style. The secure storage file with a name of <code>secure_storage</code>
 * is under the program's "configuration" area.
 * At runtime, this location can be checked via the "osgi.configuration.area" property,
 * which defaults to the "configuration" subdirectory of the "installation" area
 * (see Help/About ..., Installation Details, Configuration).
 * <p> 
 * To encrypt the sensitive data, a master password provider must be provided which could be
 * configured under <code>General->Security->Secure Storage</code>. For more information, 
 * please see {@link ISecurePreferences}
 * </p>
 * <p>
 * Note: SecureStorage is different from {@link SecureStore} whose data is encrypted by 
 * the login user's password. SecureStorage is used to store sensitive data per installation
 * whereas {@link SecureStore} store sensitive data for per user.
 * </p>
 * This class includes the helpful functions for secure storage. 
 * 
 * @author Xihui Chen
 * @author Helge Rickens
 * @author Kay Kasemir
 * @author Lana Abadie (config. instead of install area)
 */
@SuppressWarnings("nls")
public class SecureStorage
{
    /** @return URL of the file that stores the secure settings */
	private static URL getStorageURL() throws MalformedURLException
    {
		URL secureStorageLocation;
		switch (PreferencesHelper.getSecureStorageLocation()) {		
		case INSTALL_LOCATION:
			secureStorageLocation = Platform.getInstallLocation().getURL();
			break;
		case CONFIGURATION_LOCATION:
		default:
			secureStorageLocation = Platform.getConfigurationLocation().getURL();
			break;
		}
        return new URL(secureStorageLocation + "secure_storage");
    }

    /**Retrieve value of the key specified from the secure storage file  
	 * @param qualifier path to the root node
	 * @param key key with this the value is associated
	 * @return value associated the key. 
	 * If value was stored in an encrypted form, it will be decrypted. 
	 * null if the secure storage file or the key does not exist.
	 */
    public static String retrieveSecureStorage(final String qualifier, final String key)
	{
    	try
    	{
            final URL url = getStorageURL();
            // Check if the file exists.
            // This code went through several iterations because of issues
            // with URL, URI and File, see also
            // http://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
            File file;
            try
            {
                // In principle, the API is clear: URL -> URI, then use the
                // matching File(URI) constructor.
                // The URL may contain spaces as '%20', and this converts
                // them to plain spaces.
                // But it fails with URLs like
                // "file:///c:/Some Path/Some File"
                file = new File(url.toURI());
            }
            catch (URISyntaxException e)
            {   
                // ... in which case using the 'Path' (stuff after "file:")
                // works out:
                file = new File(url.getPath());
            }
            if (file.exists())
    		{
	        	final ISecurePreferences root = SecurePreferencesFactory.open(url, null);
	    		if (root != null)
	    		{	    			
	    		    final ISecurePreferences node = root.node(qualifier);
		      		final String value = node.get(key, null);
                    //if the key exist in this node, return the value, otherwise return the default preference
		      		if (value != null)
		      			return value;
	    		}
    		}
    	}
    	catch (Exception e)
    	{
    	    // Cannot call CentralLogger for the case that CentralLogger
    	    // is just starting up, loading preferences, calling SecureStorage,
    	    // running into an error:
    	    // In that case, calling CentralLogger from here would create
    	    // infinite loop.
    		Logger.getLogger(AuthActivator.ID).log(Level.SEVERE,
    				"Failed to read value of " + key + " from secure storage", e);
		}		
    	//in case of no value in secure storage, return the preference value 
    	return Platform.getPreferencesService().getString(qualifier, key, null, null);
    }
	
	 /**Get node for security storage. The secure storage file with a name of 
	  * <code>secure_storage</code> is under the directory where the program is installed.
	 * @param nodePath path to the root node
	 * @return node associated with the nodePath
	 * @throws Exception
	 */
	public static ISecurePreferences getNode(String nodePath) throws Exception
	{
	    final URL secureFileLoc = getStorageURL(); 	
        final ISecurePreferences root = SecurePreferencesFactory.open(secureFileLoc, null);
    	if (root == null)	
    		throw new Exception("Unable to get root node of secure storage");
	    return root.node(nodePath);    		
	 }	
}