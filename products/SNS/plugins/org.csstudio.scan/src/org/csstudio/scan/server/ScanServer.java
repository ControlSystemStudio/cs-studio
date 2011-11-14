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
package org.csstudio.scan.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.data.ScanData;

/** RMI interface for the scan server engine
 *
 *  <p>Used by the (remote) client to communicate
 *  with the scan server.
 *  @author Kay Kasemir
 */
public interface ScanServer extends Remote
{
    /** Serialization version used for all RMI interfaces */
    final public static long SERIAL_VERSION = 1;

    /** Default host for scan server */
    final public static String RMI_HOST = "localhost";

    /** Port used by RMI
     *  Default RMI port is 1099, but use a different port for
     *  the scan server.
     */
    final public static int RMI_PORT = 4810;

    /** Name under which this interface is registered with RMI */
    final public static String RMI_SCAN_SERVER_NAME = "ScanServer";

    /** Port on which this interface's implementation is exported with RMI */
    final public static int RMI_SCAN_SERVER_PORT = 4811;

    /** @return Human-readable info about the scan server
     *  @throws RemoteException on error in remote access
     */
    public String getInfo() throws RemoteException;

    /** Query server for devices used by a scan 
     *  @return Info about devices
     *  @throws RemoteException on error in remote access
     */
    public DeviceInfo[] getDeviceInfos() throws RemoteException;
    
    /** Submit a sequence of commands as a 'scan' to be executed
     *  @param scan_name Name of the scan
     *  @param commands Commands to execute within the scan.
     *                  The command sequence can not be changed
     *                  once it has been submitted to the server!
     *  @return ID that uniquely identifies the scan (within JVM of the scan engine)
     *  @throws RemoteException on error in remote access
     */
    public long submitScan(String scan_name, List<ScanCommand> commands) throws RemoteException;

    /** Query server for scans
     *  @return Info for each scan on the server
     *  @throws RemoteException on error in remote access
     */
    public List<ScanInfo> getScanInfos() throws RemoteException;

    /** Query server for scan info
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return Info for that scan on the server or <code>null</code>
     *  @throws RemoteException on error in remote access
     */
    public ScanInfo getScanInfo(long id) throws RemoteException;
    
    /** Query server for scan data
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return Data for that scan on the server or <code>null</code>
     *  @throws RemoteException on error in remote access
     */
    public ScanData getScanData(long id) throws RemoteException;

    /** Ask server to pause a scan
     *
     *  <p>Note that pausing has no effect if the scan is not running.
     *  It is specifically not possible to pause an idle scan, i.e.
     *  one that hasn't started.
     *  We consider that a feature:
     *  If you don't really want your scan to start, simply don't submit it
     *  in the first place.
     *  You cannot submit a scan and pause it right away, i.e. block
     *  all subsequent scans.
     *
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *            -1 to pause all running scans
     *  @throws RemoteException on error in remote access
     */
    public void pause(long id) throws RemoteException;

    /** Ask server to resume a paused scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *            -1 to resume all paused scans
     *  @throws RemoteException on error in remote access
     */
    public void resume(long id) throws RemoteException;

    /** Ask server to abort a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @throws RemoteException on error in remote access
     */
    public void abort(long id) throws RemoteException;

    /** Ask server to remove a (finished) scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @throws RemoteException on error in remote access
     */
    public void remove(long id) throws RemoteException;

    /** Remove completed scans (NOP if there aren't any)
     *  @throws RemoteException on error in remote access
     */
    public void removeCompletedScans() throws RemoteException;
}
