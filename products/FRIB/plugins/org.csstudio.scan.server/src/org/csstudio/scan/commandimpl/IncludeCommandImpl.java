/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
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
package org.csstudio.scan.commandimpl;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.command.IncludeCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.server.JythonSupport;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandImplTool;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.SimulationContext;
import org.csstudio.scan.server.internal.PathStreamTool;

/** {@link ScanCommandImpl} that includes another scan
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class IncludeCommandImpl extends ScanCommandImpl<IncludeCommand>
{
    private List<ScanCommandImpl<?>> scan_impl;

    /** {@inheritDoc} */
	public IncludeCommandImpl(final IncludeCommand command, final JythonSupport jython) throws Exception
    {
	    super(command, jython);

	    // Parse scan
	    final String[] paths = ScanSystemPreferences.getScriptPaths();
	    final InputStream scan_stream = PathStreamTool.openStream(paths, command.getScanFile());
        final XMLCommandReader reader = new XMLCommandReader(new ScanCommandFactory());
        final List<ScanCommand> commands = reader.readXMLStream(scan_stream);
        
        // Implement
        final ScanCommandImplTool implementor = ScanCommandImplTool.getInstance();
        scan_impl = implementor.implement(commands, jython);
    }
	
    /** {@inheritDoc} */
    @Override
    public long getWorkUnits()
    {
        long included_units = 0;
        for (ScanCommandImpl<?> command : scan_impl)
            included_units += command.getWorkUnits();
        return included_units;
    }
	
    /** {@inheritDoc} */
	@Override
    public String[] getDeviceNames(final ScanContext context) throws Exception
	{
	    context.pushMacros(command.getMacros());
	    try
	    {
            final Set<String> devices = new HashSet<String>();
            for (ScanCommandImpl<?> command : scan_impl)
            {
                for (String device_name : command.getDeviceNames(context))
                    devices.add(device_name);
            }
    	    return devices.toArray(new String[devices.size()]);
	    }
	    finally
	    {
	        context.popMacros();
	    }
	}
	
	/** {@inheritDoc} */
    @Override
    public void simulate(final SimulationContext context) throws Exception
    {
        context.pushMacros(command.getMacros());
        try
        {
            context.logExecutionStep(context.resolveMacros(command.toString()), 0.0);
            context.simulate(scan_impl);
        }
        finally
        {
            context.popMacros();
        }
    }

	/** {@inheritDoc} */
	@Override
	public void execute(final ScanContext context) throws Exception
	{
	    context.pushMacros(command.getMacros());
	    try
	    {
	        context.execute(scan_impl);
	    }
	    finally
	    {
	        context.popMacros();
	    }
	}
}
