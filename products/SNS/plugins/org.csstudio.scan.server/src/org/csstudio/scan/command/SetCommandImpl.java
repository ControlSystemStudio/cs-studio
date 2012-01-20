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

import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanContext;

/** {@link CommandImpl} that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommandImpl extends CommandImpl<SetCommand>
{
    /** Initialize
     *  @param command Command description
     */
    public SetCommandImpl(final SetCommand command)
    {
        super(command);
    }

	/** {@inheritDoc} */
	@Override
    public void execute(final ScanContext context)  throws Exception
    {
		Logger.getLogger(getClass().getName()).log(Level.FINE, "Set {0} to {1}",
				new Object[] { command.getDeviceName(), command.getValue() });
		final Device device = context.getDevice(command.getDeviceName());
		device.write(command.getValue());

		// TODO Wait for the device to reach the value?
		// Use put-callback?
		// Use separate readback PV as in WaitForValueCommand?
		// .. or with 'above', 'below'?
		
        // For now, a separate WaitForValueCommand is needed
		
		// TODO Log the value

		context.workPerformed(1);
    }
}
