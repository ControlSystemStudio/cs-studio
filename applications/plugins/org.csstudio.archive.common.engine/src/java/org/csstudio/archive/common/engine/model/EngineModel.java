/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.archive.common.engine.Activator;
import org.csstudio.archive.common.engine.scanner.ScanThread;
import org.csstudio.archive.common.engine.scanner.Scanner;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.IArchiveEngineConfigService;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.samplemode.ArchiveSampleMode;
import org.csstudio.archive.common.service.samplemode.IArchiveSampleMode;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Data model of the archive engine.
 *  @author Kay Kasemir
 */
public class EngineModel
{
    /** Version code. See also webroot/version.html */
    final public static String VERSION = "1.2.3"; //$NON-NLS-1$

    /** Name of this model */
    private String name = "Archive Engine";  //$NON-NLS-1$

    /** RDB Archive to which samples are written.
     *  <p>
     *  <b>NOTE Thread Usage:</b>
     *  During startup, <code>addChannel</code> might
     *  access the archive, but later on only the <code>WriteThread</code>
     *  touches the archive to avoid thread issues.
     */
    //final private RDBArchive archive;

    /** Thread that writes to the <code>archive</code> */
    final private WriteThread writer;

    /** All the channels.
     *  <p>
     *  Accessed by HTTPD and main thread, so lock on <code>this</code>
     */
    final List<ArchiveChannel> channels = new ArrayList<ArchiveChannel>();

    /** Channels mapped by name.
     *  <p>
     *  @see channels about thread safety
     */
    final Map<String, ArchiveChannel> channel_by_name = new HashMap<String, ArchiveChannel>();

    /** Groups of archived channels
     *  <p>
     *  @see channels about thread safety
     */
    final List<ArchiveGroup> groups = new ArrayList<ArchiveGroup>(); // Why not a hashmap?

    /** Scanner for scanned channels */
    final Scanner scanner = new Scanner();

    /** Thread that runs the scanner */
    final ScanThread scan_thread = new ScanThread(scanner);

    /** Engine states */
    public enum State
    {
        /** Initial model state before <code>start()</code> */
        IDLE,
        /** Running model, state after <code>start()</code> */
        RUNNING,
        /** State after <code>requestStop()</code>; still running. */
        SHUTDOWN_REQUESTED,
        /** State after <code>requestRestart()</code>; still running. */
        RESTART_REQUESTED,
        /** State while in <code>stop()</code>; will then be IDLE again. */
        STOPPING
    }

    /** Engine state */
    private State state = State.IDLE;

    /** Start time of the model */
    private ITimestamp start_time = null;

    /** Write period in seconds */
    private int write_period = 30;

    /** Maximum number of repeat counts for scanned channels */
    private int max_repeats = 60;

    /** Write batch size */
    private int batch_size = 500;

    /** Buffer reserve (N times what's ideally needed) */
    private double buffer_reserve = 2.0;

    /**
     * Construct model that writes to archive
     */
    public EngineModel()
        throws OsgiServiceUnavailableException, ArchiveConnectionException
    {
        applyPreferences();

        writer = new WriteThread();
    }

    /** Read preference settings */
    @SuppressWarnings("nls")
    private void applyPreferences()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null) {
            return;
        }
        write_period = prefs.getInt(Activator.PLUGIN_ID, "write_period", write_period, null);
        max_repeats = prefs.getInt(Activator.PLUGIN_ID, "max_repeats", max_repeats, null);
        batch_size = prefs.getInt(Activator.PLUGIN_ID, "batch_size", batch_size, null);
        buffer_reserve = prefs.getDouble(Activator.PLUGIN_ID, "buffer_reserve", buffer_reserve, null);
    }

    /** @return Name (description) */
    final public String getName()
    {
        return name;
    }

    /** @return Seconds into the future that should be ignored */
    public static long getIgnoredFutureSeconds()
    {
        // TODO make configurable
        // 1 day
        return 24*60*60;
    }

    /** @return Write period in seconds */
    final public int getWritePeriod()
    {
        return write_period;
    }

    /** @return Write batch size */
    final public int getBatchSize()
    {
        return batch_size;
    }

    /** @return Current model state */
    final public State getState()
    {
        return state;
    }

    /** @return Start time of the engine or <code>null</code> if not running */
    final public ITimestamp getStartTime()
    {
        return start_time;
    }

    /** Get existing or add new group.
     *  @param name Name of the group to find or add.
     *  @return ArchiveGroup
     *  @throws Exception on error (wrong state)
     */
    final public ArchiveGroup addGroup(final String name) throws Exception
    {
        if (state != State.IDLE)
         {
            throw new Exception("Cannot add group while " + state); //$NON-NLS-1$
        }
        // Avoid duplicates
        synchronized (this)
        {
        ArchiveGroup group = getGroup(name);
        if (group != null) {
            return group;
        }
        // Add new group
        group = new ArchiveGroup(name);
        groups.add(group);
        return group;
        }
    }

    /** @return Number of groups */
    final synchronized public int getGroupCount()
    {
        return groups.size();
    }

    /** Get one archive group.
     *  @param group_index 0...<code>getGroupCount()-1</code>
     *  @return group
     *  @see #getGroupCount()
     */
    final synchronized public ArchiveGroup getGroup(final int group_index)
    {
        return groups.get(group_index);
    }

    /** @return Group by that name or <code>null</code> if not found */
    final synchronized public ArchiveGroup getGroup(final String name)
    {
        for (final ArchiveGroup group : groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    /** @return Number of channels */
    final synchronized public int getChannelCount()
    {
        return channels.size();
    }

    /** @param i Channel index, 0 ... <code>getChannelCount()-1</code> */
    final synchronized public ArchiveChannel getChannel(final int i)
    {
        return channels.get(i);
    }

    /** @return Channel by that name or <code>null</code> if not found */
    final synchronized public ArchiveChannel getChannel(final String name)
    {
        return channel_by_name.get(name);
    }

    /** Add a channel to the engine under given group.
     *  @param channelName Channel name
     *  @param group Name of the group to which to add
     *  @param enablement How channel acts on the group
     *  @param monitor Monitor or scan?
     *  @param sample_val Sample mode configuration value: 'delta' for monitor
     *  @param period Estimated update period [seconds]
     *  @return {@link ArchiveChannel}
     *  @throws Exception on error from channel creation
     */
    @SuppressWarnings("nls")
    final public ArchiveChannel addChannel(final String channelName,
                                           final ArchiveGroup group,
                                           final Enablement enablement,
                                           final boolean monitor,
                                           final double sample_val,
                                           final double period) throws Exception
    {
        if (state != State.IDLE)
        {
            throw new Exception("Cannot add channel while " + state); //$NON-NLS-1$
        }

        // Is this an existing channel?
        ArchiveChannel channel = getChannel(channelName);

        // For the engine, channels can be in more than one group
        // if configuration matches.
        if (channel != null)
        {
            final String gripe = String.format(
                    "Group '%s': Channel '%s' already in group '%s'",
                     group.getName(), channelName, channel.getGroup(0).getName());
            if (channel.getEnablement() != enablement) {
                throw new Exception(gripe + " with different enablement");
            }
            if (// Now monitor, but not before?
                monitor && channel instanceof ScannedArchiveChannel
                ||
                // Or now scanned, but before monitor, or other scan rate?
                !monitor
                 && (channel instanceof MonitoredArchiveChannel
                     || ((ScannedArchiveChannel)channel).getPeriod() != period)) {
                throw new Exception(gripe + " with different sample mechanism");
            }
        }
        else
        {   // Channel is new to this engine.
            // See if there's already a sample in the archive,
            // because we won't be able to go back-in-time before that sample.
        	IValue last_sample = null;

        	final IArchiveWriterService service = Activator.getDefault().getArchiveWriterService();

        	final ITimestamp last_stamp  =
        	    service.getLatestTimestampForChannel(channelName);

        	if (last_stamp != null) {
        	    // Create fake string sample with that time
        	    last_sample = ValueFactory.createStringValue(last_stamp,
        	                                                 ValueFactory.createOKSeverity(),
        	                                                 "", IValue.Quality.Original,
        	                                                 new String [] { "Last timestamp in archive" });
        	}

            // Determine buffer capacity
            int buffer_capacity = (int) (write_period / period * buffer_reserve);
            // When scan or update period exceeds write period,
            // simply use the reserve for the capacity
            if (buffer_capacity < buffer_reserve) {
                buffer_capacity = (int)buffer_reserve;
            }

            // Create new channel
            if (monitor)
            {
                if (sample_val > 0) {
                    channel = new DeltaArchiveChannel(channelName, enablement,
                            buffer_capacity, last_sample, period, sample_val);
                } else {
                    channel = new MonitoredArchiveChannel(channelName, enablement,
                                                 buffer_capacity, last_sample,
                                                 period);
                }
            }
            else
            {
                channel = new ScannedArchiveChannel(channelName, enablement,
                                        buffer_capacity, last_sample, period,
                                        max_repeats);
                scanner.add((ScannedArchiveChannel) channel, period);
            }
            synchronized (this)
            {
                channels.add(channel);
                channel_by_name.put(channel.getName(), channel);
            }
            writer.addChannel(channel);
        }
        // Connect new or old channel to group
        channel.addGroup(group);
        group.add(channel);

        return channel;
    }

    /** Start processing all channels and writing to archive. */
    final public void start() throws Exception
    {
        start_time = TimestampFactory.now();
        state = State.RUNNING;
        writer.start(write_period, batch_size);
        for (final ArchiveGroup group : groups)
        {
            group.start();
            // Check for stop request.
            // Unfortunately, we don't check inside group.start(),
            // which could have run for some time....
            if (state == State.SHUTDOWN_REQUESTED) {
                break;
            }
        }
        scan_thread.start();
    }

    /** @return Timestamp of end of last write run */
    public ITimestamp getLastWriteTime()
    {
        return writer.getLastWriteTime();
    }

    /** @return Average number of values per write run */
    public double getWriteCount()
    {
        return writer.getWriteCount();
    }

    /** @return  Average duration of write run in seconds */
    public double getWriteDuration()
    {
        return writer.getWriteDuration();
    }

    /** @see Scanner#getIdlePercentage() */
    final public double getIdlePercentage()
    {
        return scanner.getIdlePercentage();
    }

    /** Ask the model to stop.
     *  Merely updates the model state.
     *  @see #getState()
     */
    final public void requestStop()
    {
        state = State.SHUTDOWN_REQUESTED;
    }

    /** Ask the model to restart.
     *  Merely updates the model state.
     *  @see #getState()
     */
    final public void requestRestart()
    {
        state = State.RESTART_REQUESTED;
    }

    /** Reset engine statistics */
    public void reset()
    {
        writer.reset();
        scanner.reset();
        synchronized (this)
        {
            for (final ArchiveChannel channel : channels) {
                channel.reset();
            }
        }
    }

    /** Stop monitoring the channels, flush the write buffers. */
    @SuppressWarnings("nls")
    final public void stop() throws Exception
    {
        state = State.STOPPING;
        CentralLogger.getInstance().getLogger(this).info("Stopping scanner");
        // Stop scanning
        scan_thread.stop();
        // Assert that scanning has stopped before we add 'off' events
        scan_thread.join();
        // Disconnect from network
        CentralLogger.getInstance().getLogger(this).info("Stopping archive groups");
        for (final ArchiveGroup group : groups) {
            group.stop();
        }
        // Flush all values out
        CentralLogger.getInstance().getLogger(this).info("Stopping writer");
        writer.shutdown();

        // Close the engine config connection
        // Activator.getDefault().getArchiveEngineConfigService().disconnect();

        // Update state
        state = State.IDLE;
        start_time = null;
    }


    /** Read configuration of model from RDB.
     *  @param p_name Name of engine in RDB
     *  @param port Current HTTPD port
     */
    @SuppressWarnings("nls")
    final public void readConfig(final String p_name, final int port) throws Exception
    {
        this.name = p_name;

        final IArchiveEngineConfigService service = Activator.getDefault().getArchiveEngineConfigService();

        final IArchiveEngine engine = service.findEngine(p_name);
        if (engine == null) {
            throw new Exception("Unknown engine '" + p_name + "'");
        }

        // Is the configuration consistent?
        if (engine.getUrl().getPort() != port) {
            throw new Exception("Engine running on port " + port +
                " while configuration requires " + engine.getUrl().toString());
        }

        // Get groups
        final Collection<IArchiveChannelGroup> engine_groups =
            service.getGroupsForEngine(engine.getId());

        for (final IArchiveChannelGroup group_config : engine_groups)
        {
            final ArchiveGroup group = addGroup(group_config.getName());

            // Add channels to group
            final Collection<IArchiveChannel> channel_configs =
                service.getChannelsByGroupId(group_config.getId());

            for (final IArchiveChannel channel_config : channel_configs)
            {
                Enablement enablement = Enablement.Passive;
                if (group_config.getEnablingChannelId().equals(channel_config.getId())) {
                    enablement = Enablement.Enabling;
                }
                // channel name
                //
                final IArchiveSampleMode mode =
                    service.getSampleModeById(channel_config.getSampleModeId());

                addChannel(channel_config.getName(),
                           group,
                           enablement,
                           ArchiveSampleMode.MONITOR.equals(mode),
                           channel_config.getSampleValue(),
                           channel_config.getSamplePeriod());
            }
        }
    }

    /** Remove all channels and groups. */
    @SuppressWarnings("nls")
    final public void clearConfig()
    {
        if (state != State.IDLE) {
            throw new IllegalStateException("Only allowed in IDLE state");
        }
        synchronized (this)
        {
            groups.clear();
            channel_by_name.clear();
            channels.clear();
        }
        scanner.clear();
    }

    /** Write debug info to stdout */
    @SuppressWarnings("nls")
    public void dumpDebugInfo()
    {
        System.out.println(TimestampFactory.now().toString() + ": Debug info");
        for (int c=0; c<getChannelCount(); ++c)
        {
            final ArchiveChannel channel = getChannel(c);
            final StringBuilder buf = new StringBuilder();
            buf.append("'" + channel.getName() + "' (");
            for (int i=0; i<channel.getGroupCount(); ++i)
            {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(channel.getGroup(i).getName());
            }
            buf.append("): ");
            buf.append(channel.getMechanism());

            buf.append(channel.isEnabled() ? ", enabled" : ", DISABLED");
            buf.append(channel.isConnected() ? ", connected (" : ", DISCONNECTED (");
            buf.append(channel.getInternalState() + ")");
            buf.append(", value " + channel.getCurrentValue());
            buf.append(", last stored " + channel.getLastArchivedValue());
            System.out.println(buf.toString());
        }
    }
}
