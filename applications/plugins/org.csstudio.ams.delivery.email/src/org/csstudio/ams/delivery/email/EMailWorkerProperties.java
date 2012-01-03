
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

package org.csstudio.ams.delivery.email;

import org.csstudio.ams.delivery.email.internal.EMailConnectorPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class EMailWorkerProperties {
	private String mailSenderAdress = null;
	private String mailAuthUser = null;
	private String mailAuthPassword = null;
	private String mailSubject = null;
	private String mailContent = null;
	private String mailServerConfig = null;

	public EMailWorkerProperties() {
		IPreferencesService prefs = Platform.getPreferencesService();
		mailSenderAdress = prefs.getString(Activator.PLUGIN_ID,
		                                   EMailConnectorPreferenceKey.P_MAILSENDERADRESS,
		                                   "NONE",
		                                   null);
		mailAuthUser = prefs.getString(Activator.PLUGIN_ID,
                                       EMailConnectorPreferenceKey.P_MAILAUTHUSER,
                                       "NONE",
                                       null);
		mailAuthPassword = prefs.getString(Activator.PLUGIN_ID,
                                           EMailConnectorPreferenceKey.P_MAILAUTHPASSWORD,
                                           "NONE",
                                           null);
		mailSubject = prefs.getString(Activator.PLUGIN_ID,
                                      EMailConnectorPreferenceKey.P_MAILSUBJECT,
                                      "NONE",
                                      null);
		mailContent = prefs.getString(Activator.PLUGIN_ID,
                                      EMailConnectorPreferenceKey.P_MAILCONTENT,
                                      "NONE",
                                      null);
		mailServerConfig = prefs.getString(Activator.PLUGIN_ID,
                                           EMailConnectorPreferenceKey.P_MAILSERVERCONFIG,
                                           "NONE",
                                           null);
	}

	public String getMailAuthPassword() {
		return mailAuthPassword;
	}

	public String getMailAuthUser() {
		return mailAuthUser;
	}

	public String getMailContent() {
		return mailContent;
	}

	public String getMailSenderAdress() {
		return mailSenderAdress;
	}

	public String getMailServerConfig() {
		return mailServerConfig;
	}

	public String getMailSubject() {
		return mailSubject;
	}
}