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
package org.csstudio.scan.server.app;

import java.util.List;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.MacroTable;
import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanDataIterator;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanServer;
import org.csstudio.scan.server.internal.ScanServerImpl;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.service.prefs.Preferences;

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
    final private ScanServerImpl server;

    /** Initialize
     *  @param server {@link ScanServer}
     */
    public ConsoleCommands(final ScanServerImpl server)
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
        buf.append("\tscans           - List all scans\n");
        buf.append("\tinfo            - Scan server info\n");
        buf.append("\tinfo ID         - Info about scan with given ID\n");
        buf.append("\tdevices         - List default devices\n");
        buf.append("\tdevices ID      - List devices used by scan with given ID\n");
        buf.append("\tmacros          - List macros\n");
        buf.append("\tdata  ID        - Dump log data for scan with given ID\n");
        buf.append("\tpause           - Pause current scan\n");
        buf.append("\tresume          - Resume paused scan\n");
        buf.append("\tabort  ID       - Abort scan with given ID\n");
        buf.append("\tcommands ID     - Show commands of scan with given ID\n");
        buf.append("\tremove ID       - Remove (finished) scan with given ID\n");
        buf.append("\tremoveCompleted - Remove completed scans\n");
        buf.append("\tprefs           - List all preferences\n");
        return buf.toString();
    }

    // Note:
    // Every method that starts with underscore
    // and takes CommandInterpreter arg will be accessible
    // as command in console

    /** 'scans' command */
    public Object _scans(final CommandInterpreter intp)
    {
        try
        {
            // The user is probably most interested in the most recent
            // scan.
            // List is provided most-recent-first because that
            // is best for GUI tools: Table, drop-down, ...
            // On console, it's best to list the most recent _last_
            // so that it's still visible while all the older scans
            // have already scrolled 'up' in the terminal.
            final List<ScanInfo> infos = server.getScanInfos();
            if (infos.size() <= 0)
                intp.println("- No scans -");
            else
                for (int i=infos.size()-1; i>=0; --i)
                    intp.println(infos.get(i).toString());
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }

    /** @param intp CommandInterpreter
     *  @param command Command for whic to get scan ID
     *  @return Scan ID or <code>null</code> if cannot be obtained
     */
    private Long getScanId(final CommandInterpreter intp, final String command)
    {
        final String arg = intp.nextArgument();
        if (arg == null)
        {
            intp.println("Syntax:");
            intp.println("   " + command + " ID-of-scan");
            return null;
        }
        try
        {
            return Long.parseLong(arg.trim());
        }
        catch (NumberFormatException ex)
        {
            intp.println("Expected: " + command + " ID-of-scan");
            return null;
        }
    }

    /** 'info' command */
    public Object _info(final CommandInterpreter intp)
    {
        final String arg = intp.nextArgument();
        try
        {
            if (arg == null)
                intp.println(server.getInfo());
            else
            {
                final long id;
                try
                {
                    id = Long.parseLong(arg.trim());
                }
                catch (NumberFormatException ex)
                {
                    intp.println("Expected: info ID-of-scan");
                    return null;
                }
                final ScanInfo info = server.getScanInfo(id);
                intp.println(info);
                intp.println("Created    : " + ScanSampleFormatter.format(info.getCreated()));
                intp.println("Runtime    : " + info.getRuntimeText());
                if (info.getFinishTime() != null)
                    intp.println("Finish Time: " + ScanSampleFormatter.format(info.getFinishTime()));
                intp.println("Address    : " + info.getCurrentAddress());
                intp.println("Command    : " + info.getCurrentCommand());
            }
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }

    /** 'devices' command */
    public Object _devices(final CommandInterpreter intp)
    {
        final long id;

        final String arg = intp.nextArgument();
        try
        {
            if (arg == null)
                id = -1;
            else
                id = Long.parseLong(arg.trim());

            final Device[] devices = server.getDevices(id);
            for (Device device : devices)
                intp.println(device);
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
            return null;
        }

        return null;
    }

    /** 'macros' command */
    public Object _macros(final CommandInterpreter intp)
    {
        try
        {
            final IMacroTableProvider macros = new MacroTable(ScanSystemPreferences.getMacros());
            intp.println(macros);
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
            return null;
        }

        return null;
    }
    
    /** 'commands' command */
    public Object _commands(final CommandInterpreter intp)
    {
        final Long id = getScanId(intp, "commands");
        if (id == null)
            return null;
        try
        {
            intp.print(server.getScanCommands(id));
            intp.println();
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }
    
    /** 'data' command */
    public Object _data(final CommandInterpreter intp)
    {
        final Long id = getScanId(intp, "data");
        if (id == null)
            return null;
        try
        {
            // Dump data
            final ScanData data = server.getScanData(id);
            final long last_serial = server.getLastScanDataSerial(id);
            final ScanDataIterator sheet = new ScanDataIterator(data);

            // Header: Device names
            for (String device : sheet.getDevices())
                intp.print(device + "  ");
            intp.println();
            // Rows
            while (sheet.hasNext())
            {
                final ScanSample[] line = sheet.getSamples();
                for (ScanSample sample : line)
                	intp.print(sample + "  ");
                intp.println();
            }
            intp.println("Last sample serial: " + last_serial);

            // Dump scan info
            intp.println(server.getScanInfo(id));
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
        }
        return null;
    }

    /** 'pause' command */
    public Object _pause(final CommandInterpreter intp)
    {
        try
        {
            server.pause(-1);
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return _scans(intp);
    }

    /** 'resume' command */
    public Object _resume(final CommandInterpreter intp)
    {
        try
        {
            server.resume(-1);
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return _scans(intp);
    }

    /** 'abort' command */
    public Object _abort(final CommandInterpreter intp)
    {
        final Long id = getScanId(intp, "abort");
        if (id == null)
            return null;
        try
        {
            server.abort(id);
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
        }
        return _scans(intp);
    }

    /** 'remove' command */
    public Object _remove(final CommandInterpreter intp)
    {
        final Long id = getScanId(intp, "remove");
        if (id == null)
            return null;
        try
        {
            server.remove(id);
        }
        catch (Throwable ex)
        {
            intp.printStackTrace(ex);
        }
        return _scans(intp);
    }
    
    /** 'removeCompleted' command */
    public Object _removeCompleted(final CommandInterpreter intp)
    {
        try
        {
            server.removeCompletedScans();
        }
        catch (Exception ex)
        {
            intp.printStackTrace(ex);
        }
        return _scans(intp);
    }

    /** 'prefs' command */
    public Object _prefs(final CommandInterpreter intp)
    {
        final StringBuilder buf = new StringBuilder();
        final IPreferencesService service = Platform.getPreferencesService();
        try
        {
            dumpPreferences(buf, service.getRootNode());
        }
        catch (Exception ex)
        {
            buf.append("Exception: ").append(ex.getMessage());
        }
        intp.println(buf.toString());
        return null;
    }

    /** @param buf Buffer to which to add preferences
     *  @param node Node from which preferences are read, recursively
     *  @throws Exception on error
     */
    private void dumpPreferences(final StringBuilder buf, final Preferences node) throws Exception
    {
        for (String key : node.keys())
        {
            final String path = node.absolutePath();
            buf.append(path).append('/');
            buf.append(key).append(" = ").append(node.get(key, "<null>")).append("\n");
        }
        for (String child : node.childrenNames())
            dumpPreferences(buf, node.node(child));
    }
}
