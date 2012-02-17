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
package org.csstudio.scan.server.internal;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.logger.DataLogger;
import org.csstudio.scan.logger.MemoryDataLogger;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanContext;

/** Implementation of a {@link ScanContext}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanContextImpl implements ScanContext
{
    final private DeviceContext devices;

	final private MemoryDataLogger data_logger = new MemoryDataLogger();

	final private AtomicLong work_performed = new AtomicLong();

	private volatile boolean run = true;

	private boolean paused = false;

	private volatile ScanCommandImpl<?> current_command = null;

	public ScanContextImpl(final DeviceContext devices)
    {
	    this.devices = devices;
    }

    /** Start all devices
     *  @throws Exception on error with a device
     */
	void startDevices() throws Exception
	{
	    devices.startDevices();
	}

    /** Stop all devices */
	void stopDevices()
    {
        devices.stopDevices();
    }

	/** Get a device by name
	 *  @param name
	 *  @return {@link Device} with that (alias) name
	 *  @throws Exception when device name not known
	 */
	@Override
	public Device getDevice(final String name) throws Exception
	{
	    return devices.getDeviceByAlias(name);
	}

    /** @return All Devices */
    public Device[] getDevices()
    {
        return devices.getDevices();
    }

    /** @return {@link DataLogger} of this scan */
    public DataLogger getDataLogger()
    {
        return data_logger;
    }

    /** @param commands {@link ScanCommandImpl}s to execute
     *  @throws Exception on error in executing a command
     */
    @Override
    public void execute(final List<ScanCommandImpl<?>> commands) throws Exception
    {
        for (ScanCommandImpl<?> command : commands)
        {
            if (! run)
                return;
            execute(command);
        }
    }

    /** @param command {@link ScanCommandImpl} to execute
     *  @throws Exception on error in executing the command
     */
    @Override
    public void execute(final ScanCommandImpl<?> command) throws Exception
    {
        synchronized (this)
        {
            while (paused)
            {
                wait();
            }
        }
        try
        {
            current_command = command;
            command.execute(this);
        }
        catch (InterruptedException ex)
        {
            final String message = "Command aborted: " + command.toString();
            Logger.getLogger(getClass().getName()).log(Level.INFO, message, ex);
            throw ex;
        }
        catch (Exception ex)
        {
            final String message = "Command failed: " + command.toString();
            Logger.getLogger(getClass().getName()).log(Level.WARNING, message, ex);
            throw ex;
        }
    }

    /** Ask next command execution to pause
     *  @see ScanContextImpl#resume()
     */
    synchronized void pause()
    {
        paused = true;
    }

    /** Resume from pause
     *  @see ScanContextImpl#pause()
     */
    synchronized void resume()
    {
        paused = false;
        notifyAll();
    }

    /** Request abort of command execution
     *  Will not interrupt a command that is right
     *  now executed.
     *  For that, the corresponding engine thread
     *  needs to be interrupted.
     */
    void abort()
    {
        run = false;
    }

    /** Log a sample, i.e. add it to the data set produced by the
	 *  scan
	 *  @param sample {@link ScanSample}
	 */
    @Override
	public void logSample(final ScanSample sample)
	{
		data_logger.log(sample);
	}

	/** Inform scan context that work has been performed.
	 *  Meant to be called by {@link ScanCommandImpl}s
	 *  @param work_units Number of performed work units
	 */
    @Override
	public void workPerformed(final int work_units)
	{
	    work_performed.addAndGet(work_units);
	}

	/** @return Number of work units that have so far been executed */
    public long getWorkPerformed()
    {
        return work_performed.get();
    }

    /** @return String representation of current command */
    public String getCurrentCommand()
    {
        final ScanCommandImpl<?> command = current_command;
        if (command == null)
            return "";
        return command.toString();
    }
}
