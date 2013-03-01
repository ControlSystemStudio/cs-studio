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

import org.csstudio.alarm.beast.AlarmTreePath;
import org.csstudio.alarm.beast.TreeItem;
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
    private String pwd = AlarmTreePath.PATH_SEP;
    
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
        buf.append("\tdump               - Dump complete alarm tree\n");
        buf.append("\tpvs                - List PVs\n");
        buf.append("\tpvs -d             - List disconnected PVs\n");
        buf.append("\tls '/path/to/item' - List alarm tree based on path\n");
        buf.append("\tpwd                - Print working 'directory'\n");
        buf.append("\tcd '/path'         - Change working 'directory'\n");
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

    /** 'pvs' command */
    public Object _pvs(final CommandInterpreter intp)
    {
        final boolean only_disconnected = "-d".equals(intp.nextArgument());
        try
        {
            final AlarmPV[] pvs = server.getPVs();
            for (AlarmPV pv : pvs)
            {
                if (only_disconnected  &&  pv.isConnected())
                    continue;
                intp.println(pv.toString());
            }
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }

    /** 'pwd' command */
    public Object _pwd(final CommandInterpreter intp)
    {
        intp.println("Path: '" + pwd + "'");
        return null;
    }
    
    /** 'cd' command */
    public Object _cd(final CommandInterpreter intp)
    {
        pwd = AlarmTreePath.update(pwd, intp.nextArgument());
        return _pwd(intp);
    }
    
    /** 'ls' command */
    public Object _ls(final CommandInterpreter intp)
    {
        String path = intp.nextArgument();
        // No arg provided? Use pwd
        if (path == null  ||  path.isEmpty())
            path = pwd;
        else // else use given path (based on pwd)
            path = AlarmTreePath.update(pwd, path);
        try
        {
            final TreeItem item = server.getItemByPath(path);
            if (item == null)
                intp.println("No item '" + path + "'");
            else
            {
                intp.println(item);
                for (int i=0; i<item.getChildCount(); ++i)
                    intp.println(item.getChild(i));
            }
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }
}
