/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config;

/** Archive engine sample mode description
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SampleMode
{
	final private boolean monitor;
	
	final private double delta;
	
	final private double period;

	/** Initialize
	 *  @param monitor Monitor/subscription or active scan?
	 *  @param delta Value change threshold
	 *  @param period (Expected) update period in seconds
	 */
	public SampleMode(final boolean monitor, final double delta, final double period)
    {
	    this.monitor = monitor;
	    this.delta = delta;
	    this.period = period;
    }

	/** @return <code>true</code> for monitored mode, otherwise scan */
	public boolean isMonitor()
    {
    	return monitor;
    }

	/** @return Sample delta for monitored mode */
	public double getDelta()
    {
    	return delta;
    }

	/** @return Scan period resp. expected monitor period in seconds */
	public double getPeriod()
    {
    	return period;
    }

	/** @return Debug representation */
    @Override
    public String toString()
    {
    	if (monitor)
    	{
    		if (delta > 0.0)
    			return "Monitor [threshold " + delta + "] @ min. period " + period + " sec";
    		else
    			return "Monitor @ min. period " + period + " sec";
    	}
    	else
    		return "Scan @ " + period + " sec";
    }
}
