/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.server;

/** State of a scan
 *  @author Kay Kasemir
 */
public enum ScanState
{
    /** Scan is waiting to be executed */
    Idle("Idle"),

    /** Scan is currently being executed */
    Running("Running..."),

    /** Scan was Running, currently paused */
    Paused("Paused..."),

    /** Scan was aborted by user */
    Aborted("Aborted"),

    /** Scan failed because of an error */
    Failed("Failed"),

    /** Scan ended normally, i.e. not aborted or failed */
    Finished("Finished - OK");

    final private String name;

    private ScanState(final String name)
    {
        this.name = name;
    }

    /** @return Human-readable representation */
    @Override
    public String toString()
    {
        return name;
    }
}
