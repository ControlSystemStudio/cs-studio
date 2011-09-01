
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
 */

package org.csstudio.ams.messageminder.preference;

import org.csstudio.ams.messageminder.MessageMinderActivator;
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
    @Override
    public void initializeDefaultPreferences() {
    	
        IEclipsePreferences prefs = new DefaultScope().getNode(MessageMinderActivator.PLUGIN_ID);

    	prefs.putInt(MessageMinderPreferenceKey.P_INT_MAX_YOUNG_MASSAGES, 4); //$NON-NLS-1$
        prefs.putLong(MessageMinderPreferenceKey.P_LONG_PERIOD, 20); //$NON-NLS-1$
        prefs.putLong(MessageMinderPreferenceKey.P_LONG_TIME2CLEAN, 60); //$NON-NLS-1$
        prefs.putLong(MessageMinderPreferenceKey.P_LONG_TO_OLD_TIME, 26); //$NON-NLS-1$
        prefs.put(MessageMinderPreferenceKey.P_STRING_KEY_WORDS, "HOST,FACILITY,AMS-FILTERID"); //$NON-NLS-1$
        prefs.put(MessageMinderPreferenceKey.P_STRING_XMPP_SERVER, "krynfs.desy.de");
        prefs.put(MessageMinderPreferenceKey.P_STRING_XMPP_USER_NAME, "ams-message-minder");
        prefs.put(MessageMinderPreferenceKey.P_STRING_XMPP_PASSWORD, "ams");

    }
}