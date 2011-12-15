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

import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanContext;

/** {@link CommandImpl} that delays the scan until a device reaches a certain value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitCommandImpl extends CommandImpl<WaitCommand>
{
	/** Initialize
	 *  @param command Command description
	 */
    public WaitCommandImpl(final WaitCommand command)
    {
        super(command);
    }

    /** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context) throws Exception
    {
		Logger.getLogger(getClass().getName()).log(Level.FINE, "Wait for {0} to reach {1}",
				new Object[] { command.getDeviceName(), command.getDesiredValue() });
        final Device device = context.getDevice(command.getDeviceName());

        final DeviceValueCondition condition =
            new DeviceValueCondition(device, command.getDesiredValue(), command.getTolerance());
        condition.await();
        context.workPerformed(1);
    }
}
