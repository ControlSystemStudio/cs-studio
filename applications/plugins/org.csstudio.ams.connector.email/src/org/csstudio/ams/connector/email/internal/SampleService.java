/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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

import org.csstudio.ams.connector.email.EMailConnectorPlugin;
import org.csstudio.ams.connector.email.internal.SampleService;

/**
 * This example service demonstrates that the preference IDs that are used on
 * preference pages should be defined by the services that use them.<br>
 * </p>
 * Beside the definition of the preference IDs, this services provides the
 * method <code>showPreferences</code> that reads out the preference values
 * from the plugin's preference store and displays them. This demonstrates how
 * the preferences are accessed.
 * 
 * @author Alexander Will
 * 
 */
public class SampleService {
	
	public static final String P_MAILSENDERADRESS = "org.csstudio.ams.connector.email.preferences.MailSenderAdress";
	public static final String P_MAILAUTHUSER = "org.csstudio.ams.connector.email.preferences.MailAuthUser";
	public static final String P_MAILAUTHPASSWORD = "org.csstudio.ams.connector.email.preferences.MailAuthPassword";

	public static final String P_MAILSUBJECT = "org.csstudio.ams.connector.email.preferences.MailSubject";
	public static final String P_MAILCONTENT = "org.csstudio.ams.connector.email.preferences.MailContent";
	public static final String P_MAILSERVERCONFIG = "org.csstudio.ams.connector.email.preferences.MailServerConfig";

	/**
	 * The only one instance of this service.
	 */
	private static SampleService _instance;

	/**
	 * Private constructor due to the singleton pattern.
	 */
	private SampleService() {
		// do nothing particular.
	}

	/**
	 * Return the only one instance of this service.
	 * 
	 * @return The only one instance of this service.
	 */
	public static SampleService getInstance() {
		if (_instance == null) {
			_instance = new SampleService();
		}

		return _instance;
	}

	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 * 
	 */
	public final void showPreferences() {
		IPreferenceStore store = EMailConnectorPlugin.getDefault().getPreferenceStore();

		System.out.println(P_MAILSENDERADRESS + ": " + store.getString(P_MAILSENDERADRESS));
		System.out.println(P_MAILAUTHUSER + ": " + store.getString(P_MAILAUTHUSER));
		System.out.println(P_MAILAUTHPASSWORD + ": " + store.getString(P_MAILAUTHPASSWORD));
		System.out.println(P_MAILSUBJECT + ": " + store.getString(P_MAILSUBJECT));
		System.out.println(P_MAILCONTENT + ": " + store.getString(P_MAILCONTENT));
		System.out.println(P_MAILSERVERCONFIG + ": " + store.getString(P_MAILSERVERCONFIG));
	}
}