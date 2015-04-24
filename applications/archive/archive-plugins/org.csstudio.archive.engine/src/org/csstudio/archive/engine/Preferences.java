/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

@SuppressWarnings("nls")
public class Preferences
{
	public static long getIgnoredFutureSecs()
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final long ignored_future = 24*60*60; // 1 day
        if (prefs == null)
        	return ignored_future;
        return prefs.getLong(Activator.ID, "ignored_future", ignored_future, null);
	}
	
	public static int getWritePeriodSecs()
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final int write_period = 30;
        if (prefs == null)
        	return write_period;
        return prefs.getInt(Activator.ID, "write_period", write_period, null);
	}

	public static int getMaxRepeats()
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final int max_repeats = 60;
        if (prefs == null)
        	return max_repeats;
        return prefs.getInt(Activator.ID, "max_repeats", max_repeats, null);
	}

	public static int getBatchSize()
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final int batch_size = 500;
        if (prefs == null)
        	return batch_size;
        return prefs.getInt(Activator.ID, "batch_size", batch_size, null);
	}
	
	public static double getBufferReserve()
	{
        final IPreferencesService prefs = Platform.getPreferencesService();
        final double buffer_reserve = 2.0;
        if (prefs == null)
        	return buffer_reserve;
        return prefs.getDouble(Activator.ID, "buffer_reserve", buffer_reserve, null);
	}

	
}
