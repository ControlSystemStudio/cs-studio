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
package org.csstudio.scan;

public interface TODO
{
    /**
     *  Done Talk to real PVs
     *
     *  Done wait for PVs
     *
     *  Done Track progress of a scan
     *
     *  Done Engine that queues scans
     *
     *  Done Pause/resume scans
     *
     *  Done Abort individual scan
     *
     *  Done Connect to server, Submit scan, Monitor scan(s), pause, abort scan
     *
     *  Done GUI to monitor/stop/kill scans
     *
     *  Done RMI error handling: No server, server vanishes, reconnect
     *
     *  Done Initial idea for jython for client
     *
     *  Done Run Scan Server as [headless] RCP Application
     *
     *  Done Feature to include PyDev with the scan client and monitor
     *
     *  Done Readme on how to configure pydev
     *       to be aware of scan classes for completion in editor
     *
     *  Done Basic Jython shell/command view via PyDev Jython console:
     *       Command-completion, tooltips show doc strings.
     *
     *  Done Device context can initialize from a config file that
     *       lists the devices for a beamline
     *
     *  Done Allow configuration of config file location
     *
     *  Done Query scan server for device info
     *
     *  Done fetch data data from ongoing or finished scans?
     *
     *  Done plot data of (ongoing) scan
     *
     *  Done Start plot from scan monitor
     *
     *  Done Improve scan data updates, use 'last update' time to suppress no-change updates
     *
     *  Done Start plot from script
     *
     *  Done Allow multiple scan plots, use memento
     *
     *  Done Plot: Show/Hide (default) toolbar
     *
     *  Done Load/Save scan as XML
     *
     *  Done Extension point for (basic sequential) commands.
     *       All commands are based on extension points
     *       for description and implementation.
     *       Loop has special handling in Scan Tree Editor.
     *       Addition of an If-Then-Else command would also
     *       require special handling.
     *
     *  Done Scan Monitor appears "re-connected" when server is re-started.
     *
     *  Done Extend WaitCommand to also allow waiting for value to be above or below some threshold,
     *       not just "at" the desired value
     *
     *  Done WaitCommand has a timeout
     *
     *  Done 'Automatically' log values for LoopCommand, SetCommand, ...
     *
     *  Done Copy/paste support in Scan Tree
     *
     *  Done Undo for Scan Tree
     *
     *  Done Server can create PV devices for names
     *       that are not found in predefined (aliasing) device context.
     *
     *  Done Scan tree editor shows predefined (alias) device names
     *
     *  Done Scan tree 'live' view of running scan
     *
     *  Done Benchmarks
     *
     *  Done Settings:
     *       Server uses preferences for RMI port.
     *       Clients use system properties to allow use outside of Eclipse,
     *       but scan plugins set sys props from Eclipse preferences
     *       and offer GUI.
     *
     *  Done Scan tree use 'virtual' viewer
     *
     *  Done ant build.xml to create standalone client lib
     *
     *  Done Support pre- and post-scan commands, configured when scan server starts,
     *       to for example enable and disable the data acquisition.
     *
     *  Done Move jython.jar and /Lib into own plugin. Share with BOY.
     *
     *  Done Allow update of command parameters while scan is running
     *
     *  Done Other scan configuration GUIs: 'Spreadsheet' table of loop variable, start/end
     *
     *  Done 'WaitingForDevices' shows missing devices
     *
     *  Done Details of pre- and post scan command progress info
     *
     *  Idea Unclear if devices will ever be anything but PVs.
     *
     *  Idea 'log' sends values to data collector, once it's
     *       clear what that data collector is
     *
     *  Idea Add REST API to scan server?
     *
     *  Idea RMI timeout?
     *  System.setProperty("sun.rmi.transport.tcp.responseTimeout", "10000");
     *  sun.rmi.transport.connectionTimeout
     *  RMISocketFactory.setSocketFactory( new RMISocketFactory()
            {
                public Socket createSocket( String host, int port )
                    throws IOException
                {
                    Socket socket = new Socket();
                    socket.setSoTimeout( timeoutMillis );
                    socket.setSoLinger( false, 0 );
                    socket.connect( new InetSocketAddress( host, port ), timeoutMillis );
                    return socket;
                }

                public ServerSocket createServerSocket( int port )
                    throws IOException
                {
                    return new ServerSocket( port );
                }
            } );
     */
}
