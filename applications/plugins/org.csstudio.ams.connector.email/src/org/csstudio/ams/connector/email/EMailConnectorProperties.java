
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

package org.csstudio.ams.connector.email;

import org.csstudio.ams.connector.email.internal.EMailConnectorPreferenceKey;
import org.eclipse.jface.preference.IPreferenceStore;

public class EMailConnectorProperties {
	private String mailSenderAdress = null;
	private String mailAuthUser = null;
	private String mailAuthPassword = null;
	private String mailSubject = null;
	private String mailContent = null;
	private String mailServerConfig = null;

	public EMailConnectorProperties() {
		IPreferenceStore store = EMailConnectorPlugin.getDefault().getPreferenceStore();
		mailSenderAdress = store.getString(EMailConnectorPreferenceKey.P_MAILSENDERADRESS);
		mailAuthUser = store.getString(EMailConnectorPreferenceKey.P_MAILAUTHUSER);
		mailAuthPassword = store.getString(EMailConnectorPreferenceKey.P_MAILAUTHPASSWORD);
		mailSubject = store.getString(EMailConnectorPreferenceKey.P_MAILSUBJECT);
		mailContent = store.getString(EMailConnectorPreferenceKey.P_MAILCONTENT);
		mailServerConfig = store.getString(EMailConnectorPreferenceKey.P_MAILSERVERCONFIG);
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