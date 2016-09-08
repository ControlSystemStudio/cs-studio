/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.internal;

import org.csstudio.vtype.pv.PVPlugin;
import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.csstudio.vtype.pv.mqtt.MQTT_PVFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    private static String getString(final String plugin, final String setting, final String default_value)
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return default_value;
        return service.getString(plugin, setting, default_value, null);
    }

    public static String defaultType()
    {
        return getString(PVPlugin.ID, "default_type", JCA_PVFactory.TYPE);

    }

    public static String getMQTTBroker()
    {
        return getString(PVPlugin.ID, "mqtt_broker", MQTT_PVFactory.BROKER_URL);
    }

}
