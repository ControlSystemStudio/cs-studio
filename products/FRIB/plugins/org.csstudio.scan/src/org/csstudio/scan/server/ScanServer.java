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

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.device.DeviceInfo;

/** Interface to the scan server
 *
 *  <p>Used by (remote) clients to communicate with the scan server.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface ScanServer extends Remote
{
    /** Serialization version used for all RMI interfaces */
    final public static long SERIAL_VERSION = 1;

    /** Default host for scan server */
    final public static String DEFAULT_HOST = "localhost";

    /** Default port used by scan server
     *
     *  <p>Default RMI port is 1099,
     *  but use a different port for the scan server
     *  to avoid conflicts with other RMI tools.
     *
     *  <p>Note that the scan server then uses the 'next'
     *  port for itself.
     *  So setting this to 4810 means: 4810 will be the RMI registry
     *  and 4811 will be the scan server published on that registry.
     */
    final public static int DEFAULT_PORT = 4810;

    /** System property for overriding the scan server host */
    final public static String HOST_PROPERTY = "ScanServerHost";

    /** System property for overriding the scan server port */
    final public static String PORT_PROPERTY = "ScanServerPort";

    /** Name under which this interface is registered with RMI */
    final public static String RMI_SCAN_SERVER_NAME = "ScanServer";

    /** @return Info about the scan server
     *  @throws RemoteException on error in remote access
     */
    public ScanServerInfo getInfo() throws RemoteException;

    /** Query server for devices used by a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *            or -1 for default devices
     *  @return Info about devices
     *  @throws RemoteException on error in remote access
     */
    public DeviceInfo[] getDeviceInfos(long id) throws RemoteException;

    /** Submit a scan for simulation
     *  @param commands_as_xml Commands to simulate in XML format
     *  @return {@link SimulationResult}
     *  @throws RemoteException
     */
    public SimulationResult simulateScan(String commands_as_xml) throws RemoteException;

    /** Submit a sequence of commands as a 'scan' to be executed
     *  @param scan_name Name of the scan
     *  @param commands_as_xml Commands to execute within the scan in XML format
     *  @return ID that uniquely identifies the scan (within JVM of the scan engine)
     *  @throws RemoteException on error in remote access
     */
    public long submitScan(String scan_name, String commands_as_xml) throws RemoteException;

    /** Query server for scans
     *  @return Info for each scan on the server, most recently submitted scan first
     *  @throws RemoteException on error in remote access
     */
    public List<ScanInfo> getScanInfos() throws RemoteException;

    /** Query server for scan info
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return Info for that scan on the server or <code>null</code>
     *  @throws RemoteException on error in remote access
     */
    public ScanInfo getScanInfo(long id) throws RemoteException;

    /** Query server for the commands in a scan
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return Scan commands in XML format for that scan on the server or <code>null</code>
     *  @throws RemoteException on error in remote access
     */
    public String getScanCommands(long id) throws RemoteException;

    /** Get serial of last logged sample.
     *
     *  <p>Can be used to determine if there are new samples
     *  that should be fetched via <code>getScanData()</code>
     *
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return Serial of last sample in scan data
     *  @see #getScanData(long)
     */
    public long getLastScanDataSerial(long id) throws RemoteException;

    /** Query server for scan data
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @return Data for that scan on the server or <code>null</code>
     *  @throws RemoteException on error in remote access
     *  @see #getLastScanDataSerial(long)
     */
    public ScanData getScanData(long id) throws RemoteException;

    /** Ask server to update a command parameter to a new value
     *  @param id ID that uniquely identifies a scan (within JVM of the scan engine)
     *  @param address Address of the command
     *  @param property_id Property to update
     *  @param value New value for the property
     *  @throws RemoteException on error in remote access
     */
    public void updateScanProperty(long id, long address, String property_id, Object value) throws RemoteException;

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
     *            -1 to abort all scans
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
