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
package org.csstudio.alarm.service.preferences;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.platform.AbstractPreference;

/**
 * Constant definitions for alarm service preferences
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public final class AlarmPreference<T> extends AbstractPreference<T> {

    public static final AlarmPreference<Boolean> ALARMSERVICE_DAL = new AlarmPreference<Boolean>("alarm.service.dal",
                                                                                                 true);
    public static final AlarmPreference<Boolean> ALARMSERVICE_LDAP = new AlarmPreference<Boolean>("alarm.service.ldap",
                                                                                                  false);
    public static final AlarmPreference<String> ALARMSERVICE_CONFIG_FILENAME = new AlarmPreference<String>("alarm.service.configFileName",
                                                                                                           "resource/alarmServiceConfig.xml");
    public static final AlarmPreference<String> ALARMSERVICE_TOPICS_DEFAULT = new AlarmPreference<String>("alarm.service.topicsDefault",
                                                                                                          "ALARM,ACK");
    public static final AlarmPreference<String> ALARMSERVICE_FACILITIES_DEFAULT = new AlarmPreference<String>("alarm.service.facilitiesDefault",
                                                                                                              "Test");

    private AlarmPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @Override
    protected String getPluginID() {
        return AlarmServiceActivator.PLUGIN_ID;
    }

    @Nonnull
    static List<AlarmPreference<?>> getAllPreferences() {
        return Arrays.asList(new AlarmPreference<?>[] {ALARMSERVICE_DAL, ALARMSERVICE_LDAP,
                ALARMSERVICE_CONFIG_FILENAME, ALARMSERVICE_TOPICS_DEFAULT,
                ALARMSERVICE_FACILITIES_DEFAULT});
    }

}
