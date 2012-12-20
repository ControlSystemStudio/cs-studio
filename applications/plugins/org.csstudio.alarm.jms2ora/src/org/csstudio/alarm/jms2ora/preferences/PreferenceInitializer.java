
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.alarm.jms2ora.preferences;

import org.csstudio.alarm.jms2ora.Jms2OraActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * @author Markus Moeller
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
        
        IEclipsePreferences prefs = new DefaultScope().getNode(Jms2OraActivator.getDefault().getPluginId());

        prefs.put(PreferenceConstants.XMPP_USER_NAME, "anonymous");
        prefs.put(PreferenceConstants.XMPP_PASSWORD, "anonymous");
        prefs.put(PreferenceConstants.XMPP_SERVER, "xmppserver.where.ever");
        prefs.put(PreferenceConstants.XMPP_REMOTE_USER_NAME, "anonymous");
        prefs.put(PreferenceConstants.XMPP_REMOTE_PASSWORD, "anonymous");
        prefs.put(PreferenceConstants.XMPP_SHUTDOWN_PASSWORD, "");
        prefs.put(PreferenceConstants.JMS_PROVIDER_URLS, "");
        prefs.put(PreferenceConstants.JMS_PRODUCER_URL, "");
        prefs.put(PreferenceConstants.JMS_TOPIC_NAMES, "");
        prefs.put(PreferenceConstants.JMS_CONTEXT_FACTORY_CLASS, "");
        prefs.put(PreferenceConstants.DISCARD_TYPES, "");
        prefs.put(PreferenceConstants.DISCARD_NAMES, "");
        prefs.put(PreferenceConstants.DEFAULT_VALUE_PRECISION, "");
        prefs.put(PreferenceConstants.MESSAGE_PROCESSOR_SLEEPING_TIME, "30000");
        prefs.put(PreferenceConstants.TIME_BETWEEN_STORAGE, "60");
        prefs.put(PreferenceConstants.WATCHDOG_WAIT, "");
        prefs.put(PreferenceConstants.WATCHDOG_PERIOD, "");
        prefs.put(PreferenceConstants.FILTER_SEND_BOUND, "");
        prefs.put(PreferenceConstants.FILTER_MAX_SENT_MESSAGES, "");
        prefs.put(PreferenceConstants.STORE_EMPTY_VALUES, "false");
        prefs.put(PreferenceConstants.DESCRIPTION, "I am a simple but happy application.");
        prefs.putBoolean(PreferenceConstants.LOG_STATISTIC, true);
    }
}
