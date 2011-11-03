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
     *  TODO Client settings (scan server host, port system properties) via Eclipse preferences
     *  
     *  TODO Move jython.jar and /Lib into own plugin. Share with BOY.
     *
     *  TODO Device context should initialize from a config file that
     *       lists the devices for a beamline
     *
     *  TODO fetch data data from ongoing or finished scans
     *
     *  TODO plot data from ongoing scan?
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
