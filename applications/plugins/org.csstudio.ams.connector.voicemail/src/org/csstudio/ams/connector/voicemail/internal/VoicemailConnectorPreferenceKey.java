
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.ams.connector.voicemail.internal;

import org.csstudio.ams.Log;
import org.csstudio.ams.connector.voicemail.VoicemailConnectorPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * 
 * @author Markus Moeller
 * 
 */
public class VoicemailConnectorPreferenceKey
{
	public static final String P_VM_SERVICE = "vmService";
	public static final String P_VM_PORT = "vmPort";
	public static final String P_MARY_HOST = "maryHost";
    public static final String P_MARY_PORT = "maryPort";
    public static final String P_MARY_DEFAULT_LANGUAGE = "maryLanguage";
	public static final String P_XMPP_SERVER = "xmppServer";
	public static final String P_XMPP_USER = "xmppUser";
	public static final String P_XMPP_PASSWORD = "xmppPassword";

	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 * 
	 */
	public static final void showPreferences()
	{
		IPreferenceStore store = VoicemailConnectorPlugin.getDefault().getPreferenceStore();

		Log.log(Log.INFO, P_VM_SERVICE + ": " + store.getString(P_VM_SERVICE));
		Log.log(Log.INFO, P_VM_PORT + ": " + store.getString(P_VM_PORT));
		Log.log(Log.INFO, P_MARY_HOST + ": " + store.getString(P_MARY_HOST));
		Log.log(Log.INFO, P_MARY_PORT + ": " + store.getString(P_MARY_PORT));
		Log.log(Log.INFO, P_MARY_DEFAULT_LANGUAGE + ": " + store.getString(P_MARY_DEFAULT_LANGUAGE));
		Log.log(Log.INFO, P_XMPP_SERVER + ": " + store.getString(P_XMPP_SERVER));
		Log.log(Log.INFO, P_XMPP_USER + ": " + store.getString(P_XMPP_USER));
		Log.log(Log.INFO, P_XMPP_PASSWORD + ": " + store.getString(P_XMPP_PASSWORD));
	}
}
