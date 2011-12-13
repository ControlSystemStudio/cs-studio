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
     *  TODO Other scan configuration GUIs.
     *       'Spreadsheet' of loop variable, start/end?
     *       Process block GUI?
     *  
     *  TODO Extend WaitForValueCommand to also allow waiting for value to be above or below some threshold,
     *       not just "at" the desired value
     *  
     *  TODO Unclear if devices will ever be anything but PVs.
     *       If they're just PVs, one could use PVs.
     *       If they're something else, then an API for
     *       getting the device names _and_ underlying PVs from
     *       server would be useful for GUI that can show them,
     *       access other PV tools from device PVs etc.
     *
     *  TODO 'log' sends values to data collector, once it's
     *       clear what that data collector is
     *  
     *  TODO Client settings (system properties for scan server host, port) via Eclipse preferences
     *  
     *  TODO Move jython.jar and /Lib into own plugin. Share with BOY.
     *
     *  TODO put callback? Maybe support Channel Access put-callback,
     *       or add a wait-for-value to the set command?
     *
     *  TODO RMI timeout?
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
