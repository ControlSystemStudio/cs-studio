package org.csstudio.utility.casnooper.preferences;
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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

import org.csstudio.utility.casnooper.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs = new DefaultScope().getNode(
				Activator.getDefault().getPluginId());

		prefs.put(PreferenceConstants.XMPP_USER_NAME, "casnooper");
		prefs.put(PreferenceConstants.XMPP_PASSWORD, "casnooper");
		prefs.put(PreferenceConstants.UPDATE_TIME, "30000");	//30sec
		prefs.put(PreferenceConstants.SHOW_MESSAGE_INDICATOR, "false");
		prefs.put(PreferenceConstants.SENT_START_ID, "5000000");
		prefs.put(PreferenceConstants.JMS_CONTEXT_FACTORY, "ACTIVEMQ");
		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_ALARMS, "3600000");
		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_LOGS, "600000");
		prefs.put(PreferenceConstants.JMS_TIME_TO_LIVE_PUT_LOGS, "3600000");
		prefs.put(PreferenceConstants.PRIMARY_JMS_URL, "failover:(tcp://krynfs.desy.de:62616,tcp://krykjmsb.desy.de:64616)?maxReconnectDelay=5000");
		prefs.put(PreferenceConstants.CONNECTION_CLIENT_ID, "caSnooper");
	}

}
