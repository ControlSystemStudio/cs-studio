/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import static org.junit.Assert.assertEquals;

import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess;
import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource;
import org.csstudio.diirt.util.core.preferences.pojo.CompositeDataSource.DataSourceProtocol;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.csstudio.diirt.util.core.preferences.pojo.DataSources;
import org.csstudio.diirt.util.core.preferences.pojo.JCAContext;
import org.csstudio.diirt.util.preferences.DIIRTPreferencesPlugin.WrawwingPreferenceStore;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 7 Mar 2017
 */
public class DIIRTPreferencesPluginTest {

    @Test
    public void testCopyChannelAccess ( ) {

        ChannelAccess ca1 = new ChannelAccess(
            new DataSourceOptions(false, true, MonitorMask.ARCHIVE, 468, false, VariableArraySupport.AUTO),
            new JCAContext("foffi", true, 67, 234.6, 567, 44653, 59485)
        );

        DIIRTPreferences sourcePpreferences = new DIIRTPreferences(new TestScope());

        ca1.updateDefaultsAndValues(sourcePpreferences);

        WrawwingPreferenceStore store = new WrawwingPreferenceStore(new DIIRTPreferences(new TestScope()));
        DIIRTPreferences detinationPpreferences = new DIIRTPreferences(new TestScope());

        DIIRTPreferencesPlugin.copyChannelAccess(sourcePpreferences, store);
        DIIRTPreferencesPlugin.copyChannelAccess(store, detinationPpreferences);

        ChannelAccess ca3 = new ChannelAccess(detinationPpreferences);

        assertEquals(ca1, ca3);

        ChannelAccess ca2 = new ChannelAccess(
            new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE),
            new JCAContext("fuffa", false, 23, 43.2, 12345, 23414, 23453)
        );

        ca2.updateValues(sourcePpreferences);

        detinationPpreferences = new DIIRTPreferences(new TestScope());

        DIIRTPreferencesPlugin.copyChannelAccess(sourcePpreferences, store);
        DIIRTPreferencesPlugin.copyChannelAccess(store, detinationPpreferences);

        ChannelAccess ca4 = new ChannelAccess(detinationPpreferences);

        assertEquals(ca2, ca4);

    }

    @Test
    public void testCopyDataSources ( ) {

        DataSources ds1 = new DataSources(new CompositeDataSource(DataSourceProtocol.pva, "zxc"));

        DIIRTPreferences sourcePpreferences = new DIIRTPreferences(new TestScope());

        ds1.updateDefaultsAndValues(sourcePpreferences);

        WrawwingPreferenceStore store = new WrawwingPreferenceStore(new DIIRTPreferences(new TestScope()));
        DIIRTPreferences detinationPpreferences = new DIIRTPreferences(new TestScope());

        DIIRTPreferencesPlugin.copyDataSources(sourcePpreferences, store);
        DIIRTPreferencesPlugin.copyDataSources(store, detinationPpreferences);

        DataSources ds3 = new DataSources(detinationPpreferences);

        assertEquals(ds1, ds3);

        DataSources ds2 = new DataSources(new CompositeDataSource(DataSourceProtocol.ca, "wery"));

        ds2.updateValues(sourcePpreferences);

        detinationPpreferences = new DIIRTPreferences(new TestScope());

        DIIRTPreferencesPlugin.copyDataSources(sourcePpreferences, store);
        DIIRTPreferencesPlugin.copyDataSources(store, detinationPpreferences);

        DataSources ds4 = new DataSources(detinationPpreferences);

        assertEquals(ds2, ds4);

    }

}
