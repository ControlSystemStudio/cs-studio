package org.csstudio.apptuil.securestorage;

import java.io.File;
import java.net.URL;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;

/**This class includes the useful functions related with secure storage.
 * @author Xihui Chen
 */
public class SecureStorage {
	
	/**Retrieve value of the key specified from the secure storage file  
	 * @param nodePath path to the root node
	 * @param key key with this the value is associated
	 * @return value associated the key. 
	 * If value was stored in an encrypted form, it will be decrypted. 
	 * null if the secure storage file or the key does not exist.
	 */
	public static String retrieveSecureStorage(String nodePath, String key) {
    	try {
    		File file = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"secure_storage");
    		if(file.exists()) {
	        	URL url = file.toURI().toURL();    	
	        	ISecurePreferences root = SecurePreferencesFactory.open(url, null);
	    		if(root != null) {	    			
		    		ISecurePreferences node = root.node(nodePath);
		      		String value = node.get(key, null);
		      		if(value != null){ //if the key exist in this node, return the value, otherwise return the default preference
		      			return value;
		      		}
	    		}
    		}else {
    			CentralLogger.getInstance().getLogger(SecureStorage.class).debug(
    					"The secure storage file does not exist.");
    		}
    	} catch (Exception e) {
			CentralLogger.getInstance().getLogger(SecureStorage.class).error(e);
		}		
		return null;
    }
	
	 /**Get node for security storage. The secure storage file with a name of 
	  * <code>secure_storage</code> is under the user's 
	  * working directory. Generally, it is same as where the program is installed.
	 * @param nodePath path to the root node
	 * @return node associated with the nodePath
	 * @throws Exception
	 */
	public static ISecurePreferences getNode(String nodePath) throws Exception {
	    	File file = new File(System.getProperty("user.dir") + 
	    			System.getProperty("file.separator")+"secure_storage");
	        URL url = file.toURI().toURL();    	
	        ISecurePreferences root = SecurePreferencesFactory.open(url, null);
	    	if(root == null)	
	    		throw new Exception("Unable to get root node of secure storage");
		    return root.node(nodePath);    		
	 }	
}