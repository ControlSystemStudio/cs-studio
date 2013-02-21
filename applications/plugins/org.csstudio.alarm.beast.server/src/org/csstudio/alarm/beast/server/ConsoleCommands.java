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
package org.csstudio.alarm.beast.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/** Commands for the OSGi console
 *  Application registers this {@link CommandProvider}
 *
 *  @see CommandProvider
 *  @see Application#start(org.eclipse.equinox.app.IApplicationContext)
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ConsoleCommands implements CommandProvider
{
    final private AlarmServer server;
    
    /** Initialize
     *  @param server {@link AlarmServer}
     */
    public ConsoleCommands(final AlarmServer server)
    {
        this.server = server;
    }

    /** Provide help to the console
     *  @see CommandProvider
     * {@inheritDoc}
     */
    @Override
    public String getHelp()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("---ScanServer commands---\n");
        buf.append("\tdump           - Dump complete alarm tree\n");
        return buf.toString();
    }

    // Note:
    // Every method that starts with underscore
    // and takes CommandInterpreter arg will be accessible
    // as command in console

    /** 'dump' command */
    public Object _dump(final CommandInterpreter intp)
    {
        try
        {
            final ByteArrayOutputStream buf = new ByteArrayOutputStream();
            final PrintStream out = new PrintStream(buf);
            server.dump(out);
            out.close();
            intp.println(buf.toString());
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }
}
