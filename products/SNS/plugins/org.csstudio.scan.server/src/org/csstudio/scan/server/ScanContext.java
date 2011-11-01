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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.CommandImpl;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.logger.DataLogger;
import org.csstudio.scan.logger.PrintDataLogger;

/** Context in which the {@link CommandImpl}s of a {@link Scan} are executed.
 *
 *  <ul>
 *  <li>{@link Device}s with which commands can interact.
 *  <li>Methods to execute commands, supporting Pause/Continue/Abort
 *  <li>Data logger for {@link ScanSample}s
 *  <li>Progress information
 *  </ul>
 *
 *  @author Kay Kasemir
 */
public class ScanContext
{
    final private DeviceContext devices;

	final private DataLogger data_logger;

	final private AtomicLong work_performed = new AtomicLong();

	private volatile boolean run = true;

	private boolean paused = false;

	private volatile ScanCommand current_command = null;

	public ScanContext(final DeviceContext devices)
	{
		this(devices, new PrintDataLogger());
	}

	public ScanContext(final DeviceContext devices, final DataLogger data_logger)
    {
	    this.devices = devices;
		this.data_logger = data_logger;
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
	 *  @return {@link Device} with that name
	 *  @throws Exception when device name not known
	 */
	public Device getDevice(final String name) throws Exception
	{
	    return devices.getDevice(name);
	}

    /** @return All Devices */
    public Device[] getDevices()
    {
        return devices.getDevices();
    }

    /** @param commands {@link CommandImpl}s to execute
     *  @throws Exception on error in executing a command
     */
    public void execute(final List<CommandImpl> commands) throws Exception
    {
        for (CommandImpl command : commands)
        {
            if (! run)
                return;
            execute(command);
        }
    }

    /** @param command {@link CommandImpl} to execute
     *  @throws Exception on error in executing the command
     */
    public void execute(final CommandImpl command) throws Exception
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
     *  @see ScanContext#resume()
     */
    synchronized void pause()
    {
        paused = true;
    }

    /** Resume from pause
     *  @see ScanContext#pause()
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
	public void logSample(final ScanSample sample)
	{
		data_logger.log(sample);
	}

	/** Inform scan context that work has been performed.
	 *  Meant to be called by {@link CommandImpl}s
	 *  @param work_units Number of performed work units
	 */
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
        final ScanCommand command = current_command;
        if (command == null)
            return "";
        return command.toString();
    }
}
