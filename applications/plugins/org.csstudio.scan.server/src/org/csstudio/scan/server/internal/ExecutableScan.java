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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.commandimpl.WaitForDevicesCommand;
import org.csstudio.scan.commandimpl.WaitForDevicesCommandImpl;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceContextHelper;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.log.DataLog;
import org.csstudio.scan.log.DataLogFactory;
import org.csstudio.scan.server.MacroContext;
import org.csstudio.scan.server.MemoryInfo;
import org.csstudio.scan.server.Scan;
import org.csstudio.scan.server.ScanCommandImpl;
import org.csstudio.scan.server.ScanCommandUtil;
import org.csstudio.scan.server.ScanContext;
import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.server.ScanState;
import org.epics.util.time.TimeDuration;

/** Scan that can be executed: Commands, device context, state
 *
 *  <p>Combines a {@link DeviceContext} with {@link ScanContextImpl}ementations
 *  and can execute them.
 *  When a command is executed, it receives a {@link ScanContext} view
 *  of the scan for limited access to the devices, data logger etc.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ExecutableScan extends LoggedScan implements ScanContext, Callable<Object>
{
    /** Commands to execute */
    final private transient List<ScanCommandImpl<?>> pre_scan, implementations, post_scan;

    /** Macros for resolving device names */
    final private MacroContext macros;
    
    /** Devices used by the scan */
    final protected DeviceContext devices;

    /** Log each device access, or require specific log command? */
    private volatile boolean automatic_log_mode = false;

    /** Data logger, non-null while when executing the scan
     *  SYNC on this for access
     */
    private DataLog data_logger = null;

    /** Total number of commands to execute */
    final private long total_work_units;

    /** Commands executed so far */
    final protected AtomicLong work_performed = new AtomicLong();

    /** State of this scan
     *  SYNC on this for access
     */
    private ScanState state = ScanState.Idle;

    private volatile String error = null;

    /** Start time, set when execution starts */
    private volatile long start_ms = 0;

    /** Actual or estimated end time */
    private volatile long end_ms = 0;

    /** Last valid address
     *  <p>The current_command may be within an IncludeCommand,
     *  where addresses are no longer set.
     *  This address tracks the most recent valid address.
     */
    private volatile long current_address = -1;
    
    /** Currently executed command or <code>null</code> */
    private volatile ScanCommandImpl<?> current_command = null;
    
    /** {@link Future} after scan has been submitted to {@link ExecutorService} */
    private volatile Future<Object> future = null;

    /** Device Names for status PVs */
	private String device_active = null, device_status = null, device_progress = null, device_finish = null;

	/** Timeout for updating the status PVs */
	final private static TimeDuration timeout = TimeDuration.ofSeconds(10);

	/** Initialize
     *  @param name User-provided name for this scan
     *  @param devices {@link DeviceContext} to use for scan
     *  @param implementations Commands to execute in this scan
     *  @throws Exception on error (cannot access log, ...)
     */
    public ExecutableScan(final String name, final DeviceContext devices, ScanCommandImpl<?>... implementations) throws Exception
    {
        this(name, devices,
            Collections.<ScanCommandImpl<?>>emptyList(),
            Arrays.asList(implementations),
            Collections.<ScanCommandImpl<?>>emptyList());
    }

    /** Initialize
     *  @param name User-provided name for this scan
     *  @param devices {@link DeviceContext} to use for scan
     *  @param pre_scan Commands to execute before the 'main' section of the scan
     *  @param implementations Commands to execute in this scan
     *  @param post_scan Commands to execute before the 'main' section of the scan
     *  @throws Exception on error (cannot access log, ...)
     */
    public ExecutableScan(final String name, final DeviceContext devices,
            final List<ScanCommandImpl<?>> pre_scan,
            final List<ScanCommandImpl<?>> implementations,
            final List<ScanCommandImpl<?>> post_scan) throws Exception
    {
        this(DataLogFactory.createDataLog(name), devices, pre_scan, implementations, post_scan);
    }

    /** Initialize
     *  @param scan {@link Scan}
     *  @param devices {@link DeviceContext} to use for scan
     *  @param pre_scan Commands to execute before the 'main' section of the scan
     *  @param implementations Commands to execute in this scan
     *  @param post_scan Commands to execute before the 'main' section of the scan
     *  @throws Exception on error (cannot access log, ...)
     */
    public ExecutableScan(final Scan scan, final DeviceContext devices,
            final List<ScanCommandImpl<?>> pre_scan,
            final List<ScanCommandImpl<?>> implementations,
            final List<ScanCommandImpl<?>> post_scan) throws Exception
    {
        super(scan);
        this.macros = new MacroContext(ScanSystemPreferences.getMacros());
        this.devices = devices;
        this.pre_scan = pre_scan;
        this.implementations = implementations;
        this.post_scan = post_scan;

        // Assign addresses to all commands,
        // determine work units
        long address = 0;
        long work_units = 0;
        for (ScanCommandImpl<?> impl : implementations)
        {
            address = impl.setAddress(address);
        	work_units += impl.getWorkUnits();
        }
        total_work_units = work_units;
    }

    /** Submit scan for execution
     *  @param executor {@link ExecutorService} to use
     *  @throws IllegalStateException if scan had been submitted before
     */
    public void submit(final ExecutorService executor)
    {
        if (future != null)
            throw new IllegalStateException("Already submitted for execution");
        future = executor.submit(this);
    }

    /** @return {@link ScanState} */
    @Override
    public synchronized ScanState getScanState()
    {
        return state;
    }

    /** @return Info about current state of this scan */
    @Override
    public ScanInfo getScanInfo()
    {
        final long address = current_address;
        final ScanCommandImpl<?> command = current_command;
        final String command_name;
        final ScanState state = getScanState();
        final long runtime;
        final long performed_work_units;

        if (start_ms <= 0)
        {   // Not started
            command_name = "";
            runtime = 0;
            performed_work_units = 0;
        }
        else if (state.isDone())
        {   // Finished, aborted
            command_name = "- end -";
            runtime = end_ms - start_ms;
            performed_work_units = total_work_units;
        }
        else
        {   // Running
            command_name = command == null ? "" : command.toString();
            final long now = System.currentTimeMillis();
            runtime = now - start_ms;
            performed_work_units = work_performed.get();
            
            // Estimate end time
            final long finish_estimate = performed_work_units <= 0
                ? now
                : start_ms + runtime*total_work_units/performed_work_units;
            
            // Somewhat smoothly update end time w/ estimate
            if (end_ms <= 0)
                end_ms = finish_estimate;
            else
                end_ms = 4*(end_ms/5) + finish_estimate/5;
        }

        return new ScanInfo(this, state, error, runtime, end_ms, performed_work_units, total_work_units, address, command_name);
    }

    /** @return Commands executed by this scan */
    public List<ScanCommand> getScanCommands()
    {
        // Fetch underlying commands for implementations
        final List<ScanCommand> commands = new ArrayList<ScanCommand>(implementations.size());
        for (ScanCommandImpl<?> impl : implementations)
            commands.add(impl.getCommand());
        return commands;
    }

    /** @param address Command address
     *  @return ScanCommand with that address
     *  @throws Exception when not found
     */
    public ScanCommand getCommandByAddress(final long address) throws Exception
    {
        final ScanCommand found = findCommandByAddress(getScanCommands(), address);
        if (found == null)
            throw new Exception("Invalid command address " + address);
        return found;
    }

    /** Recursively search for command by address
     *  @param commands Command list
     *  @param address Desired command address
     *  @return Command with that address or <code>null</code>
     */
    private ScanCommand findCommandByAddress(final List<ScanCommand> commands,
            final long address)
    {
        for (ScanCommand command : commands)
        {
            if (command.getAddress() == address)
                return command;
            else if (command instanceof LoopCommand)
            {
                final LoopCommand loop = (LoopCommand) command;
                final ScanCommand found = findCommandByAddress(loop.getBody(), address);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    /** Attempt to update a command parameter to a new value
     *  @param address Address of the command
     *  @param property_id Property to update
     *  @param value New value for the property
     *  @throws Exception on error
     */
    public void updateScanProperty(final long address, final String property_id,
        final Object value) throws Exception
    {
        final ScanCommand command = getCommandByAddress(address);
        try
        {
            command.setProperty(property_id, value);
        }
        catch (Exception ex)
        {
            throw new Exception("Cannot update " + property_id + " of " +
                    command.getCommandName(), ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public MacroContext getMacros()
    {
        return macros;
    }

    /** Obtain devices used by this scan.
     *
     *  <p>Note that the result can differ before and
     *  after the scan gets executed because devices
     *  are added to the device context as needed.
     *
     *  @return Devices used by this scan
     */
    public Device[] getDevices()
    {
        return devices.getDevices();
    }

    /** {@inheritDoc} */
    @Override
    public Device getDevice(final String name) throws Exception
    {
        return devices.getDevice(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setLogMode(final boolean automatic)
    {
        automatic_log_mode = automatic;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutomaticLogMode()
    {
        return automatic_log_mode;
    }

	/** {@inheritDoc} */
    @Override
    public synchronized DataLog getDataLog()
    {
        return data_logger;
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized long getLastScanDataSerial() throws Exception
    {
        if (data_logger == null)
            return super.getLastScanDataSerial();
        return data_logger.getLastScanDataSerial();
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized ScanData getScanData() throws Exception
    {
        if (data_logger == null)
            return super.getScanData();
        return data_logger.getScanData();
    }
    
    /** Callable for executing all commands on the scan,
     *  turning exceptions into a 'Failed' scan state.
     */
    @Override
    public Object call() throws Exception
    {
        final Logger log = Logger.getLogger(getClass().getName());
        log.log(Level.INFO, "Executing {0} [{1}]", new Object[] { getName(), new MemoryInfo()});

        try
        (
            final DataLog logger = DataLogFactory.getDataLog(this);
        )
        {
            // Set logger for execution of scan
            synchronized (this)
            {
                data_logger = logger;
            }
            execute_or_die_trying();
        }
        catch (InterruptedException ex)
        {
        	synchronized (this)
        	{
        		state = ScanState.Aborted;
        	}
            error = "Interrupted";
        }
        catch (Exception ex)
        {
        	synchronized (this)
        	{
        		state = ScanState.Failed;
        	}
            error = ex.getMessage();
            log.log(Level.WARNING, "Scan " + getName() + " failed", ex);
        }
        // Set actual end time, not estimated
        end_ms = System.currentTimeMillis();
        // Un-set data logger
        synchronized (this)
        {
            data_logger = null;
        }
        return null;
    }

    /** Execute all commands on the scan,
     *  passing exceptions back up.
     *  @throws Exception on error
     */
    private void execute_or_die_trying() throws Exception
    {
    	synchronized (this)
    	{
	        // Was scan aborted before it ever got to run?
	        if (state == ScanState.Aborted)
	            return;
	        // Otherwise expect 'Idle'
	        if (state != ScanState.Idle)
	            throw new IllegalStateException("Cannot run Scan that is " + state);
	        state = ScanState.Running;
    	}
    	start_ms = System.currentTimeMillis();
    	
        // Locate devices for status PVs
        final String prefix = ScanSystemPreferences.getStatusPvPrefix();
        if (prefix != null   &&   !prefix.isEmpty())
        {
        	device_active = prefix + "Active";
            devices.addPVDevice(new DeviceInfo(device_active));
            
            device_status = prefix + "Status";
            devices.addPVDevice(new DeviceInfo(device_status));

            device_progress = prefix + "Progress";
            devices.addPVDevice(new DeviceInfo(device_progress));

            device_finish = prefix + "Finish";
            devices.addPVDevice(new DeviceInfo(device_finish));
        }
        
        // Add devices used by commands
        DeviceContextHelper.addScanDevices(devices, macros, pre_scan);
        DeviceContextHelper.addScanDevices(devices, macros, implementations);
        DeviceContextHelper.addScanDevices(devices, macros, post_scan);

        // Start Devices
        devices.startDevices();
        
        // Execute commands
        try
        {
            execute(new WaitForDevicesCommandImpl(new WaitForDevicesCommand(devices.getDevices()), null));
            
            // Initialize scan status PVs. Error will prevent scan from starting.
            if (device_active != null)
            {
            	getDevice(device_status).write(getName());
            	ScanCommandUtil.write(this, device_active, Double.valueOf(1.0), 0.1, timeout);
            	ScanCommandUtil.write(this, device_progress, Double.valueOf(0.0), 0.1, timeout);
            	getDevice(device_finish).write("Starting ...");
            }
            
            try
            {
                // Execute pre-scan commands
                execute(pre_scan);

                // Reset work step counter to only count the 'main' commands
                work_performed.set(0);

                // Execute the submitted commands
                execute(implementations);

                // Successful finish
                synchronized (this)
                {
                	state = ScanState.Finished;
                }
            }
            finally
            {
                // Try post-scan commands even if submitted commands ran into problems or were aborted.
            	// Save the state before going back to Running for post commands.
            	final long saved_steps = work_performed.get();
                final ScanState saved_state;
                synchronized (this)
                {
                	saved_state = state;
                	state = ScanState.Running;
                }

                execute(post_scan);

                // Restore saved state
                work_performed.set(saved_steps);
                synchronized (this)
                {
                	state = saved_state;
                }
            }
        }
        finally
        {
            current_address = -1;
            current_command = null;
            end_ms = System.currentTimeMillis();

            try
            {
                // Final status PV update.
                if (device_active != null)
                {
                    getDevice(device_status).write("");
                    getDevice(device_finish).write(ScanSampleFormatter.format(new Date()));
                    ScanCommandUtil.write(this, device_progress, Double.valueOf(100.0), 0.1, timeout);
                    ScanCommandUtil.write(this, device_active, Double.valueOf(0.0), 0.1, timeout);
                }            
            }
            catch (Exception ex)
            {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Final Scan status PV update failed", ex);
            }
            
            // Stop devices
            devices.stopDevices();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final List<ScanCommandImpl<?>> commands) throws Exception
    {
        for (ScanCommandImpl<?> command : commands)
        {
        	synchronized (this)
        	{
	            if (state != ScanState.Running  &&
	                state != ScanState.Paused)
	                return;
        	}
            execute(command);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final ScanCommandImpl<?> command) throws Exception
    {
        synchronized (this)
        {
            while (state == ScanState.Paused)
            {
                wait();
            }
        }
        
        boolean retry;
        do
        {
            retry = false;
            try
            {
                // Update current command, but only track address if valid.
                // Will NOT update the address when going into IncludeCommand.
                current_command = command;
                if (current_command.getCommand().getAddress() >= 0)
                    current_address = current_command.getCommand().getAddress();
                Logger.getLogger(getClass().getName()).log(Level.FINE, "@{0}: {1}", new Object[] { current_address, command });
                command.execute(this);
            }
            catch (InterruptedException abort)
            {   // Command was interrupted on purpose
                final String message = "Command aborted: " + command.toString();
                Logger.getLogger(getClass().getName()).log(Level.INFO, message, abort);
                throw abort;
            }
            catch (Exception error)
            {   // Command generated an error
                final String message = "Command failed: " + command.toString();
                Logger.getLogger(getClass().getName()).log(Level.WARNING, message, error);
                // Error handler determines how to proceed
                switch (command.handleError(this, error))
                {
                case Abort:
                    // Abort on the original error
                    throw error;
                case Continue:
                    // Ignore the error, move on
                    return;
                case Retry:
                    retry = true;
                }
            }
            
            // Try to update Scan PVs on progress. Log errors, but continue scan
			if (device_status != null)
	        {
			    final ScanInfo info = getScanInfo();
			    try
			    {
                	ScanCommandUtil.write(this, device_progress, Double.valueOf(info.getPercentage()), 0.1, timeout);
                	getDevice(device_finish).write(ScanSampleFormatter.formatCompactDateTime(info.getFinishTime()));
			    }
			    catch (Exception ex)
			    {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error updating status PVs", ex);
			    }
	        }
        }
        while (retry);
    }

    /** Pause execution of a currently executing scan */
    public synchronized void pause()
    {
        if (state == ScanState.Running)
            state = ScanState.Paused;
    }

    /** Resume execution of a paused scan */
    public synchronized void resume()
    {
        if (state == ScanState.Paused)
        {
            state = ScanState.Running;
            notifyAll();
        }
    }

    /** Ask for execution to stop */
    public synchronized void abort()
    {
        if (! state.isDone())
            state = ScanState.Aborted;

        if (future != null)
            future.cancel(true);
        notifyAll();
    }

    /** {@inheritDoc} */
    @Override
    public void workPerformed(final int work_units)
    {
        work_performed.addAndGet(work_units);
    }
}
