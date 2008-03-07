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
 package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class AlarmViewerPreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JmsLogsPlugin.getDefault().getPreferenceStore();
		store.setDefault(AlarmViewerPreferenceConstants.P_STRINGAlarm,
				"TYPE,100" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"EVENTTIME,100" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"NAME,100" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"SEVERITY" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"STATUS" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"VALUE" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"TEXT" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"USER" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"HOST" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"APPLICATION-ID" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"PROCESS-ID" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"CLASS" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"DOMAIN" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"FACILITY" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"LOCATION" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"VALUE" + ";" + //$NON-NLS-1$ //$NON-NLS-2$
				"DESTINATION" //$NON-NLS-1$
		);
		store.setDefault(AlarmViewerPreferenceConstants.MAX, 200);
		store.setDefault(AlarmViewerPreferenceConstants.REMOVE, 10);
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.PRIMARY_URL, "failover:(tcp://elogbook.desy.de:64616)?maxReconnectAttempts=2,maxReconnectDelay=5000"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.SECONDARY_URL, "failover:(tcp://krynfs.desy.de:62616)?maxReconnectAttempts=2,maxReconnectDelay=5000"); //$NON-NLS-1$
		store.setDefault(AlarmViewerPreferenceConstants.QUEUE, "ALARM,ACK"); //$NON-NLS-1$
        store.setDefault(AlarmViewerPreferenceConstants.SENDER_URL, "failover:(tcp://elogbook.desy.de:64616,tcp://krynfs.desy.de:62616)?maxReconnectDelay=5000"); //$NON-NLS-1$
	}

}
