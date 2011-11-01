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
package org.csstudio.scan.command;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanServer;

/** {@link CommandImpl} that delays the scan for some time
 *  @author Kay Kasemir
 */
public class DelayCommandImpl extends DelayCommand implements CommandImpl
{
    /** Serialization ID */
    private static final long serialVersionUID = ScanServer.SERIAL_VERSION;

    /** Initialize
     *  @param seconds Delay in seconds
     */
    public DelayCommandImpl(final double seconds)
    {
        super(seconds);
    }

    /** Initialize
     *  @param command Command description
     */
    public DelayCommandImpl(final DelayCommand command)
    {
        this(command.getSeconds());
    }

    /** {@inheritDoc} */
    @Override
    public int getWorkUnits()
    {
        return 1;
    }

	/** {@inheritDoc} */
	@Override
    public void execute(final ScanContext command_context) throws Exception
    {
		Logger.getLogger(getClass().getName()).log(Level.FINE, "Delay {0} secs",
				getSeconds());
		Thread.sleep(Math.round(getSeconds() * 1000));
        command_context.workPerformed(1);
    }
}
