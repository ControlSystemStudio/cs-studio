
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

package org.csstudio.ams.delivery.email.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.csstudio.ams.delivery.email.Activator;

/**
 * Preference initializer implemenation. This class initializes the preferences
 * with default values. New preference settings should be initialized in this
 * class, too.
 * 
 * @author Alexander Will
 */
public final class PreferencesInitializer extends AbstractPreferenceInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(Activator.PLUGIN_ID);

		node.put(EMailConnectorPreferenceKey.P_XMPP_SERVER, "server.where.ever");
		node.put(EMailConnectorPreferenceKey.P_XMPP_USER, "anonymous");
		node.put(EMailConnectorPreferenceKey.P_XMPP_PASSWORD, "anonymous");
		node.put(EMailConnectorPreferenceKey.P_MAILSENDERADRESS, "@desy.de");
		node.put(EMailConnectorPreferenceKey.P_MAILAUTHUSER, "");
		node.put(EMailConnectorPreferenceKey.P_MAILAUTHPASSWORD, "");
		node.put(EMailConnectorPreferenceKey.P_MAILSUBJECT, "Desy Ams Alarm Message");
		node.put(EMailConnectorPreferenceKey.P_MAILCONTENT, "Dear %N,\nDesy Ams sends you the following Alarm:\n\n%AMSG\n\n");
		node.put(EMailConnectorPreferenceKey.P_MAILSERVERCONFIG, 
				"# Configuration file for javax.mail\n"
				+"# If a value for an item is not provided, then system defaults will be used.\n"
				+"\n"
				+"# --> Host whose mail services will be used \n"
				+"#mail.host=smtp.desy.de\n"
				+"# --> Return address to appear on emails (Default value : username@host)\n"
				+"#mail.from=\n"
				+"# --> Other possible items include:\n"
				+"#mail.user= \n"
				+"#mail.store.protocol=\n"
				+"#mail.transport.protocol=\n"
				+"\n"
				+"mail.smtp.host=smtp.desy.de\n"
				+"\n"
				+"#mail.smtp.user=\n"
				+"#mail.smtp.auth=false\n"
				+"#mail.debug= \n");
	}
}
