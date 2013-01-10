/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import java.util.concurrent.TimeUnit;

/** Helper for checking a delay or timeout.
 * 
 *  <p>Does not actively run a timer or issue any action.
 *  Meant to be used in loops that need to check if a certain
 *  time has passed.
 *  
 *  TODO Move to apputil
 *  @author Kay Kasemir
 */
public class DelayCheck
{
	final private long milli_duration;
	private long next_expiration_milli;
	
	public DelayCheck(long duration, TimeUnit units)
    {
		milli_duration = units.toMillis(duration);
		next_expiration_milli = System.currentTimeMillis() + milli_duration;
    }

	public boolean expired()
    {
		final long now = System.currentTimeMillis();
		if (now >= next_expiration_milli)
		{
			next_expiration_milli = now + milli_duration;
			return true;
		}
	    return false;
    }
}
