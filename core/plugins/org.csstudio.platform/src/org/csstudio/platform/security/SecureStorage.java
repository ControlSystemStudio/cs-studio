package org.csstudio.platform.security;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.securestore.SecureStore;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

/**Secure Storage is used to save sensitive data such as user name and password in 
 * encrypted style. The secure storage file with a name of <code>secure_storage</code>
 * is under the directory where the program is installed.
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
 */
@SuppressWarnings("nls")
public class SecureStorage
{
    /** @return URL of the file that stores the secure settings */
	private static URL getStorageURL() throws MalformedURLException
    {
        return new URL(Platform.getInstallLocation().getURL()+"secure_storage");
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
            // This code went through several iterations:
            // Check if the file exists.
            // URL should start with "file:...", but then unclear:
            // File.separator could be '/' or '\',
            // there could be spaces in the path.
            // From the basic API, URL.toURI() gives an URI and File(URI ...)
            // is a matching constructor, but unclear if it really works
            // out in all cases on all operating systems.
            final File file = new File(url.toURI());
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
    	    CentralLogger.getInstance().getLogger(new SecureStorage()).error(
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