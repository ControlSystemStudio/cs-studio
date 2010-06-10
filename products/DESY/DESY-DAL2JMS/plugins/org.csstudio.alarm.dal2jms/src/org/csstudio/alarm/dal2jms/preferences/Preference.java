package org.csstudio.alarm.dal2jms.preferences;

import javax.annotation.Nonnull;

import org.csstudio.alarm.dal2jms.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchroton,
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

/**
 * Constant definitions for plug-in preferences
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
// TODO (jpenning) extract abstract class
public final class Preference<T> {

    public static final Preference<Integer> JMS_TIME_TO_LIVE_ALARMS = new Preference<Integer>("TimeToLiveAlarms",
                                                                                              3600000);
    public static final Preference<String> JMS_ALARM_TOPIC_NAME = new Preference<String>("AlarmTopicName",
                                                                                         "ALARM");
    public static final Preference<String> XMPP_DAL2JMS_USER_NAME = new Preference<String>("XmppUserName",
                                                                                           "anonymous");
    public static final Preference<String> XMPP_DAL2JMS_PASSWORD = new Preference<String>("XmppPassword",
                                                                                          "anonymous");

    private static final Preference<?>[] ALL_PREFERENCES = new Preference<?>[] {
            JMS_TIME_TO_LIVE_ALARMS, JMS_ALARM_TOPIC_NAME, XMPP_DAL2JMS_USER_NAME,
            XMPP_DAL2JMS_PASSWORD };

    private final String _keyAsString;
    private final T _defaultValue;
    private final Class<?> _type;

    private Preference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        _keyAsString = keyAsString;
        _defaultValue = defaultValue;
        _type = defaultValue.getClass();
    }

    @Nonnull
    public String getKeyAsString() {
        return _keyAsString;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public T getValue() {
        IPreferencesService prefs = Platform.getPreferencesService();

        Object result = null;

        if (_type.equals(String.class)) {
            result = prefs.getString(Preference.getPluginID(),
                                     getKeyAsString(),
                                     (String) _defaultValue,
                                     null);
        } else if (_type.equals(Integer.class)) {
            result = prefs.getInt(Preference.getPluginID(),
                                  getKeyAsString(),
                                  (Integer) _defaultValue,
                                  null);
        }
        // TODO (jpenning) add further types

        assert result != null : "result must not be null";
        return (T) result;
    }

    @Nonnull
    private static String getPluginID() {
        return Activator.PLUGIN_ID;
    }

    @Nonnull
    private String getDefaultAsString() {
        return _defaultValue.toString();
    }

    /**
     * Intended to be called from the preference initializer.
     */
    public static void initializeDefaultPreferences() {
        final IEclipsePreferences prefs = new DefaultScope().getNode(Preference.getPluginID());

        for (Preference<?> preference : ALL_PREFERENCES) {
            prefs.put(preference.getKeyAsString(), preference.getDefaultAsString());
        }

    }

}
