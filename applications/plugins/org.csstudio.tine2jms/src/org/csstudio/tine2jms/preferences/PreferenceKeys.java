
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

import org.csstudio.platform.logging.CentralLogger;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceKeys
{
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
        CentralLogger logger = CentralLogger.getInstance();
        
        logger.info(PreferenceKeys.class, XMPP_SERVER + ": ");
        logger.info(PreferenceKeys.class, XMPP_USER + ": ");
        logger.info(PreferenceKeys.class, XMPP_PASSWORD + ": ");
        logger.info(PreferenceKeys.class, XMPP_SHUTDOWN + ": ");
        logger.info(PreferenceKeys.class, JMS_PROVIDER_URL + ": ");
        logger.info(PreferenceKeys.class, JMS_CLIENT_ID + ": ");
        logger.info(PreferenceKeys.class, JMS_TOPICS_ALARM + ": ");
        logger.info(PreferenceKeys.class, TINE_FACILITY_NAMES + ": ");
    }
}
