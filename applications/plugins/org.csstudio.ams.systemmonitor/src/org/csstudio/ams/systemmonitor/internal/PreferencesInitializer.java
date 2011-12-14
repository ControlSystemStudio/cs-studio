
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

package org.csstudio.ams.systemmonitor.internal;

import org.csstudio.ams.systemmonitor.AmsSystemMonitorActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Preference initializer implemenation. This class initializes the preferences
 * with default values. New preference settings should be initialized in this
 * class, too.
 * 
 * @author Alexander Will
 * @author Markus Moeller
 * 
 */
public final class PreferencesInitializer extends AbstractPreferenceInitializer
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void initializeDefaultPreferences()
	{
		IEclipsePreferences node = new DefaultScope().getNode(AmsSystemMonitorActivator.PLUGIN_ID);

		node.put(PreferenceKeys.P_MAIL_SERVER, "smtp.desy.de");
        node.put(PreferenceKeys.P_MAIL_SUBJECT, "Alarm from the AMS System Monitor");
        node.put(PreferenceKeys.P_MAIL_DOMAIN_PART, "sms.desy.de");
        node.put(PreferenceKeys.P_MAIL_LOCAL_PART, "sms/${NUMBER}");
        node.put(PreferenceKeys.P_MAIL_SENDER, "MKS2-System@desy.de");
        node.put(PreferenceKeys.P_SMS_EMERGENCY_NUMBER, "");
        node.put(PreferenceKeys.P_SMS_EMERGENCY_MAIL, "");
        node.put(PreferenceKeys.P_SMS_CHECK_INTERVAL, "15");
        node.put(PreferenceKeys.P_SMS_WAIT_TIME, "120000");
        node.put(PreferenceKeys.P_AMS_WAIT_TIME, "30000");
        node.put(PreferenceKeys.P_ALLOWED_TIMEOUT_COUNT, "2");
        node.put(PreferenceKeys.P_SMS_REPEAT_INTERVAL, "15");
        node.put(PreferenceKeys.P_SMS_USE_OAS, "false");
        node.put(PreferenceKeys.P_AMS_GROUP, "AMSAdmin");
        node.put(PreferenceKeys.P_JMX_HOST_1, "krykjmsa.desy.de");
        node.put(PreferenceKeys.P_JMX_PORT_1, "1199");
        node.put(PreferenceKeys.P_JMX_HOST_2, "krykjmsb.desy.de");
        node.put(PreferenceKeys.P_JMX_PORT_2, "1199");
	}
}
