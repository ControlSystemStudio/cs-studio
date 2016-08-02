/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import org.csstudio.scan.command.ScanCommand;

/** Hook for simulation customization
 *  @author Kay Kasemir
 */
public class SimulationHook
{
    /**
     *
     * @param command
     * @param context
     * @return <code>true</code> if hook handled the simulation of the command,
     *         <code>false</code> if default simulation should be used
     */
    public boolean handle(ScanCommand command, SimulationContext context)
    {
        return true;
    }
}
