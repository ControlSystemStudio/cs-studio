/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv.internal;

import org.csstudio.vtype.pv.jca.JCA_PVFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Preference settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences
{
    public static String defaultType()
    {
        final IPreferencesService service = Platform.getPreferencesService();
        if (service == null)
            return JCA_PVFactory.TYPE;
        return service.getString(Activator.ID, "default_type", JCA_PVFactory.TYPE, null);

    }
}
