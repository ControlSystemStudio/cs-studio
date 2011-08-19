
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

import org.csstudio.ams.Log;
import org.csstudio.ams.messageminder.MessageMinderActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * Constant definitions for plug-in preferences.
 */
public class MessageMinderPreferenceKey {
	
    public static final String P_LONG_TIME2CLEAN = "time2Clean";
	public static final String P_LONG_TO_OLD_TIME = "toOldTime";
	public static final String P_LONG_PERIOD = "period";
	public static final String P_INT_MAX_YOUNG_MASSAGES = "maxYoungMessages";
	public static final String P_STRING_KEY_WORDS = "keyWords";
	public static final String P_STRING_XMPP_SERVER = "xmppServer";
	public static final String P_STRING_XMPP_USER_NAME = "xmppUser";
    public static final String P_STRING_XMPP_PASSWORD = "xmppPassword";
    
    public static final void showPreferences() {
    	
        IPreferencesService store = Platform.getPreferencesService();

    	Log.log(Log.INFO, P_LONG_TIME2CLEAN + ":" + store.getLong(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_LONG_TIME2CLEAN, -1, null));
    	Log.log(Log.INFO, P_LONG_TO_OLD_TIME + ":" + store.getLong(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_LONG_TO_OLD_TIME, -1 , null));
    	Log.log(Log.INFO, P_LONG_PERIOD + ":" + store.getLong(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_LONG_PERIOD, -1, null));
    	Log.log(Log.INFO, P_INT_MAX_YOUNG_MASSAGES + ":" + store.getInt(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_INT_MAX_YOUNG_MASSAGES, -1, null));
    	Log.log(Log.INFO, P_STRING_KEY_WORDS + ":" + store.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_KEY_WORDS, "NONE", null));
    	Log.log(Log.INFO, P_STRING_XMPP_SERVER + ":" + store.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_SERVER, "NONE", null));
    	Log.log(Log.INFO, P_STRING_XMPP_USER_NAME + ":" + store.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_USER_NAME, "NONE", null));
    	Log.log(Log.INFO, P_STRING_XMPP_PASSWORD + ":" + store.getString(MessageMinderActivator.PLUGIN_ID, MessageMinderPreferenceKey.P_STRING_XMPP_PASSWORD, "NONE", null));
    }
}
