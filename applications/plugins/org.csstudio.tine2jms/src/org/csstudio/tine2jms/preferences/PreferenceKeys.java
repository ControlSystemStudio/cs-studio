
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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
 *
 */

package org.csstudio.tine2jms.preferences;

import org.csstudio.tine2jms.TineToJmsActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceKeys
{
    private static final Logger LOG = LoggerFactory.getLogger(PreferenceKeys.class);
    
    public final static String XMPP_SERVER = "xmppServer";
    public final static String XMPP_USER = "xmppUser";
    public final static String XMPP_PASSWORD = "xmppPassword";
    public final static String XMPP_SHUTDOWN = "xmppShutdown";
    public final static String JMS_PROVIDER_URL = "jmsProviderUrl";
    public final static String JMS_CLIENT_ID = "jmsClientId";
    public final static String JMS_TOPICS_ALARM = "jmsTopicsAlarm";
    public final static String TINE_FACILITY_NAMES = "tineFacilityNames";
    
    public static void showPreferences()
    {
        IPreferencesService p = Platform.getPreferencesService();
        
        LOG.info(XMPP_SERVER + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, XMPP_SERVER, "NONE", null));
        LOG.info(XMPP_USER + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, XMPP_USER, "NONE", null));
        LOG.info(XMPP_PASSWORD + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, XMPP_PASSWORD, "NONE", null));
        LOG.info(XMPP_SHUTDOWN + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, XMPP_SHUTDOWN, "NONE", null));
        LOG.info(JMS_PROVIDER_URL + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, JMS_PROVIDER_URL, "NONE", null));
        LOG.info(JMS_CLIENT_ID + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, JMS_CLIENT_ID, "NONE", null));
        LOG.info(JMS_TOPICS_ALARM + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, JMS_TOPICS_ALARM, "NONE", null));
        LOG.info(TINE_FACILITY_NAMES + ": " + p.getString(TineToJmsActivator.PLUGIN_ID, TINE_FACILITY_NAMES, "NONE", null));
    }
}
