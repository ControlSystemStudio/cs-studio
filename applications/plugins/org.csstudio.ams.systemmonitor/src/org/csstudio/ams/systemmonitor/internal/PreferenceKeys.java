
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceKeys
{
    public static final String P_MAIL_SERVER = "mailServer";
    public static final String P_MAIL_SUBJECT = "mailSubject";
    public static final String P_MAIL_DOMAIN_PART = "mailDomainPart";
    public static final String P_MAIL_LOCAL_PART = "mailLocalPart";
    public static final String P_MAIL_SENDER = "mailSender";
    public static final String P_SMS_CHECK_INTERVAL = "smsCheckInterval";
    public static final String P_SMS_WAIT_TIME = "smsWaitTime";
    public static final String P_AMS_WAIT_TIME = "amsWaitTime";
    public static final String P_ALLOWED_TIMEOUT_COUNT = "allowedTimeoutCount";
    public static final String P_SMS_EMERGENCY_NUMBER = "emergencyNumber";
    public static final String P_SMS_EMERGENCY_MAIL = "emergencyMail";
    public static final String P_SMS_REPEAT_INTERVAL = "smsRepeatInterval";
    public static final String P_SMS_USE_OAS = "useOAS";
    public static final String P_AMS_GROUP = "amsGroup";
    public static final String P_JMX_PORT_1 = "jmxPort1";
    public static final String P_JMX_HOST_1 = "jmxHostname1";
    public static final String P_JMX_PORT_2 = "jmxPort2";
    public static final String P_JMX_HOST_2 = "jmxHostname2";

	/**
	 * Read out the preference from the plugin's preference store and display
	 * them on the console.
	 * 
	 */
	public final void showPreferences()
	{
        IPreferencesService pref = Platform.getPreferencesService();

		System.out.println(P_MAIL_SERVER + ": " + pref.getString(AmsSystemMonitorActivator.PLUGIN_ID, P_MAIL_SERVER, "NONE", null));
	}
}
