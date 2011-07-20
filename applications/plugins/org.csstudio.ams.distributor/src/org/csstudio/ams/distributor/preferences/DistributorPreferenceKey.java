
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

package org.csstudio.ams.distributor.preferences;

import org.csstudio.ams.Log;
import org.csstudio.ams.distributor.DistributorPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class DistributorPreferenceKey {
	
    public static final String P_XMPP_SERVER = "xmppServer";
	public static final String P_XMPP_USER = "xmppUser";
	public static final String P_XMPP_PASSWORD = "xmppPassword";
	
	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 * 
	 */
	public static final void showPreferences() {
		
	    IPreferencesService pref = Platform.getPreferencesService();

		Log.log(Log.INFO, P_XMPP_SERVER + ": " + pref.getString(DistributorPlugin.PLUGIN_ID, P_XMPP_SERVER, "NONE", null));
		Log.log(Log.INFO, P_XMPP_USER + ": " + pref.getString(DistributorPlugin.PLUGIN_ID, P_XMPP_USER, "NONE", null));
		Log.log(Log.INFO, P_XMPP_PASSWORD + ": " + pref.getString(DistributorPlugin.PLUGIN_ID, P_XMPP_PASSWORD, "NONE", null));
	}
}
