
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

package org.csstudio.ams.connector.email.internal;

import org.eclipse.jface.preference.IPreferenceStore;

import org.csstudio.ams.Log;
import org.csstudio.ams.connector.email.EMailConnectorPlugin;
import org.csstudio.ams.connector.email.internal.EMailConnectorPreferenceKey;

/**
 * 
 * @author Markus Moeller
 * 
 */
public class EMailConnectorPreferenceKey
{
	public static final String P_MAILSENDERADRESS = "mailSenderAdress";
	public static final String P_MAILAUTHUSER = "mailAuthUser";
	public static final String P_MAILAUTHPASSWORD = "mailAuthPassword";
	public static final String P_MAILSUBJECT = "mailSubject";
	public static final String P_MAILCONTENT = "mailContent";
	public static final String P_MAILSERVERCONFIG = "mailServerConfig";
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
		IPreferenceStore store = EMailConnectorPlugin.getDefault().getPreferenceStore();

		Log.log(Log.INFO, P_MAILSENDERADRESS + ": " + store.getString(P_MAILSENDERADRESS));
		Log.log(Log.INFO, P_MAILAUTHUSER + ": " + store.getString(P_MAILAUTHUSER));
		Log.log(Log.INFO, P_MAILAUTHPASSWORD + ": " + store.getString(P_MAILAUTHPASSWORD));
		Log.log(Log.INFO, P_MAILSUBJECT + ": " + store.getString(P_MAILSUBJECT));
		Log.log(Log.INFO, P_MAILCONTENT + ": " + store.getString(P_MAILCONTENT));
		Log.log(Log.INFO, P_MAILSERVERCONFIG + ": " + store.getString(P_MAILSERVERCONFIG));
		Log.log(Log.INFO, P_XMPP_SERVER + ": " + store.getString(P_XMPP_SERVER));
		Log.log(Log.INFO, P_XMPP_USER + ": " + store.getString(P_XMPP_USER));
		Log.log(Log.INFO, P_XMPP_PASSWORD + ": " + store.getString(P_XMPP_PASSWORD));
	}
}
