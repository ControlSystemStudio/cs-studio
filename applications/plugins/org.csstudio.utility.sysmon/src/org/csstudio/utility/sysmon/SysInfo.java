/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.sysmon;

/** System info, one snapshot of system data.
 *  @author Kay Kasemir
 */
public class SysInfo
{
    final static double MB = 1024.0*1024.0;

    final double freeMB;
    final double totalMB;
    final double maxMB;
    
    public SysInfo()
    {
        final Runtime runtime = Runtime.getRuntime();
        freeMB = runtime.freeMemory()/MB;
        totalMB = runtime.totalMemory()/MB;
        maxMB = runtime.maxMemory()/MB;
    }

    /** @return free memory in megabytes */
    public final double getFreeMB()
    {
        return freeMB;
    }

    /** @return total application memory in megabytes */
    public final double getTotalMB()
    {
        return totalMB;
    }

    /** @return total Java memory in megabytes */
    public final double getMaxMB()
    {
        return maxMB;
    }
}
