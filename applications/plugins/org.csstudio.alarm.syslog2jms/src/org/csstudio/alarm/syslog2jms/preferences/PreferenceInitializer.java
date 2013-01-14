
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.alarm.syslog2jms.preferences;

import org.csstudio.alarm.syslog2jms.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 25.07.2011
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences prefs = new DefaultScope().getNode(Activator.getDefault().getPluginId());

        prefs.put(PreferenceConstants.XMPP_USER_NAME, "anonymous");
        prefs.put(PreferenceConstants.XMPP_PASSWORD, "anonymous");
        prefs.put(PreferenceConstants.XMPP_SERVER, "xmppserver.where.ever");
        prefs.put(PreferenceConstants.JMS_PRODUCER_URL, "");
        prefs.put(PreferenceConstants.JMS_PRODUCER_FACTORY, "");
        prefs.put(PreferenceConstants.JMS_PRODUCER_TOPIC_NAME, "");
        prefs.put(PreferenceConstants.JMS_PRODUCER_TOPIC_NAME_BEACON, "BEACON");
        prefs.put(PreferenceConstants.BEACON_REP_RATE, "10000");
        prefs.put(PreferenceConstants.DESCRIPTION , "Headless application");
        prefs.put(PreferenceConstants.DATA_PORT_NUMBER , "514");
        prefs.put(PreferenceConstants.BUFFER_SIZE , "1024");
        prefs.put(PreferenceConstants.NUMBER_OF_THREADS , "100");
    }
}
