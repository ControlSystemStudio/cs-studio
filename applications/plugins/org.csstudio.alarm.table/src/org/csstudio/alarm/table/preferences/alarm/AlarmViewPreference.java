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
package org.csstudio.alarm.table.preferences.alarm;

import javax.annotation.Nonnull;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.domain.desy.preferences.AbstractPreference;

/**
 * Constant definitions for alarm view preferences (mimicked enum with inheritance).
 *
 * @param <T> the type of the preference. It must match the type of the default value.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010
 */
public final class AlarmViewPreference<T> extends AbstractPreference<T> {
    
    public static final String STRING_LIST_SEPARATOR = ";";
    
    public static final AlarmViewPreference<String> ALARMVIEW_P_STRING_ALARM = new AlarmViewPreference<String>("Alarm.column_names",
        "TYPE,100;EVENTTIME,100;NAME,100;SEVERITY;STATUS;VALUE;TEXT;USER;HOST;APPLICATION-ID;PROCESS-ID;CLASS;" +
        "DOMAIN;FACILITY;LOCATION;VALUE;DESTINATION");
    
    //Sets of JMS topics to be monitored. List separated with ';'. The optional name for the menu is separated with '?'.
    public static final AlarmViewPreference<String> ALARMVIEW_TOPIC_SET = new AlarmViewPreference<String>("Alarm.topic_set",
        "default?ALARM,ACK?Default?false?false?Tahoma,0,8?false;");
    
    public static final AlarmViewPreference<Boolean> ALARMVIEW_SHOW_OUTDATED_MESSAGES =
        new AlarmViewPreference<Boolean>("showOutdatedMessages", Boolean.FALSE);
    
    /**
     * Constructor.
     * @param keyAsString
     * @param defaultValue
     */
    private AlarmViewPreference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) AlarmViewPreference.class;
    }
    
    @Override
    public String getPluginID() {
        return JmsLogsPlugin.PLUGIN_ID;
    }
    
}
