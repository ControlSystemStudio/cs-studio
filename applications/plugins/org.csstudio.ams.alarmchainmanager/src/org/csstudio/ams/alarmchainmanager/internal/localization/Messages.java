
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 
package org.csstudio.ams.alarmchainmanager.internal.localization;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
    private static final String BUNDLE_NAME = "org.csstudio.ams.alarmchainmanager.internal.localization.messages";
	
	public static String ManagerTable_tbl_Colum1;
	public static String ManagerTable_tbl_Colum2;
	public static String ManagerTable_tbl_Colum3;
	public static String ManagerTable_tbl_Colum4;
	public static String ManagerTable_tbl_Colum5;
	
	public static String AMSMessageManagerView_filterComposite;	
    public static String AMSMessageManagerView_Name;
    public static String AMSMessageManagerView_Confirm;
    public static String AMSMessageManagerView_MsgChainStopped;
    public static String AMSMessageManagerView_MsgChainNotStopped;
    public static String AMSMessageManagerView_ChainIdNotPrepared;
    public static String AMSMessageManagerView_JmsError;
    public static String AMSMessageManagerView_TextWorking;
    public static String AMSMessageManagerView_TextFailed;

    /**
     * Initializes the given class with the values
     * from the specified message bundle.
     */
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * The localization messages ressource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    /**
     * This method does nothing. 
     */
    private Messages() {
        // Avoid instantiation
    }
    
    /**
     * Gets a string for the given key from this resource bundle
     * or one of its parents. 
     * 
     * @param key   String
     * @return String
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
