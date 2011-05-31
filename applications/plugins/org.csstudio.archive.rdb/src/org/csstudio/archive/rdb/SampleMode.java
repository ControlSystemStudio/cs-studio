/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import org.csstudio.platform.utility.rdb.StringID;

/** Description of a sample mode: "Monitor", ...
 *  <p>
 *  In reality we only support MONITOR and SCAN, not
 *  arbitrary modes.
 *  @author Kay Kasemir
 */
public class SampleMode extends StringID
{
    /** Name of the monitored sample mode */
    final public static String MONITOR = "Monitor"; //$NON-NLS-1$

    /** Name of the scanned sample mode */
    final public static String SCAN = "Scan"; //$NON-NLS-1$

    final private String description;

    /** Constructor.
     *  <p> sample mode; called within package
     *  Note: API end users can't simply make a sample mode up.
     *  They can only use what <code>RDBArchive.getSampleModes()</code>
     *  offers.
     */
    public SampleMode(final int id, final String name, final String description)
    {
        super(id, name);
        this.description = description;
    }

    /** @return <code>true</code> for monitor, <code>false</code> for scan */
    public boolean isMonitor()
    {
        return MONITOR.equalsIgnoreCase(getName());
    }
    
    /** @return Description */
    public String getDescription()
    {
        return description;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Sample Mode " + getName() + ": " + description;
    }
}
