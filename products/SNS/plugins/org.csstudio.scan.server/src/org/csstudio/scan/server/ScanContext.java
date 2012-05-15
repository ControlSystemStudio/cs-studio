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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.server.internal.Scan;

/** Context in which the {@link ScanCommandImpl}s of a {@link Scan} are executed.
 *
 *  <ul>
 *  <li>{@link Device}s with which commands can interact.
 *  <li>Methods to execute commands, supporting Pause/Continue/Abort
 *  <li>Data logger for {@link ScanSample}s
 *  <li>Progress information
 *  </ul>
 *
 *  <p>Scan commands can only interact with this restricted API
 *  of the actual {@link Scan}.
 *
 *  @author Kay Kasemir
 */
abstract public class ScanContext
{
	private volatile boolean automatic_log_mode = false;

    final protected DeviceContext devices;

    final protected AtomicLong work_performed = new AtomicLong();

    public ScanContext(final DeviceContext devices)
    {
    	this.devices = devices;
    }

	/** Get a device by (alias) name
	 *  @param name
	 *  @return {@link Device} with that name
	 *  @throws Exception when device name not known
	 */
	public Device getDevice(final String name) throws Exception
    {
        return devices.getDeviceByAlias(name);
    }

    /** Execute a list of commands
     *  @param commands {@link ScanCommandImpl}s to execute
     *  @throws Exception on error in executing a command
     */
    abstract public void execute(final List<ScanCommandImpl<?>> commands) throws Exception;

    /** Execute a single command
     *  @param command {@link ScanCommandImpl} to execute
     *  @throws Exception on error in executing the command
     */
    abstract public void execute(final ScanCommandImpl<?> command) throws Exception;

    /** Set log mode
     *  @param automatic Should commands automatically log every change that they perform/observe?
     */
    public void setLogMode(final boolean automatic)
    {
    	automatic_log_mode = automatic;
    }

    /** @return Should commands automatically log every change that they perform/observe?
     */
    public boolean isAutomaticLogMode()
    {
    	return automatic_log_mode;
    }

    /** @return Next unique {@link ScanSample} serial */
    abstract public long getNextScanDataSerial();

    /** @return {@link ScanData} of currently logged data or <code>null</code>
	 *  @throws Exception on error
     */
    abstract public ScanData getScanData() throws Exception;

    /** Log a sample, i.e. add it to the data set produced by the
	 *  scan
	 *  @param sample {@link ScanSample}
	 *  @throws Exception on error
	 */
    abstract public void logSample(final ScanSample sample) throws Exception;

	/** Inform scan context that work has been performed.
	 *  Meant to be called by {@link ScanCommandImpl}s
	 *  @param work_units Number of performed work units
	 */
    public void workPerformed(final int work_units)
    {
        work_performed.addAndGet(work_units);
    }
}
