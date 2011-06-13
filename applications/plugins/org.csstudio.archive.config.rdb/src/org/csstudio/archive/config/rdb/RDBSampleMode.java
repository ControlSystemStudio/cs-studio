/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.config.rdb;

/** Sample mode as stored in RDB
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RDBSampleMode
{
	final private String name;
	final private String description;
	final private boolean monitor;

    public RDBSampleMode(final String name, final String description)
    {
		this.name = name;
		this.description = description;
		monitor = "Monitor".equalsIgnoreCase(name);
    }

    public String getName()
    {
    	return name;
    }

	public String getDescription()
    {
    	return description;
    }

	/** @return <code>true</code> for monitored mode, otherwise scan */
	public boolean isMonitor()
    {
    	return monitor;
    }
}
