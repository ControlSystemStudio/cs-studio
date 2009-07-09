package org.csstudio.archive.engine.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.archive.engine.Activator;
import org.csstudio.archive.engine.scanner.ScanThread;
import org.csstudio.archive.engine.scanner.Scanner;
import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.rdb.engineconfig.ChannelGroupConfig;
import org.csstudio.archive.rdb.engineconfig.SampleEngineConfig;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/** Data model of the archive engine.
 *  @author Kay Kasemir
 */
public class EngineModel
{
    /** Version code */
    final public static String VERSION = "1.0.2d"; //$NON-NLS-1$

    /** Name of this model */
    private String name = "Archive Engine";  //$NON-NLS-1$
    
    /** RDB Archive to which samples are written.
     *  <p>
     *  <b>NOTE Thread Usage:</b>
     *  During startup, <code>addChannel</code> might
     *  access the archive, but later on only the <code>WriteThread</code>
     *  touches the archive to avoid thread issues.
     */
    final private RDBArchive archive;
    
    /** Thread that writes to the <code>archive</code> */
    final private WriteThread writer;
    
    /** All the channels.
     *  <p>
     *  Thread save to allow HTTPD and main thread to access it.
     *  Expensive during startup & shutdown because of copies,
     *  but save and allows concurrent access by HTTPD.
     */
    final CopyOnWriteArrayList<ArchiveChannel> channels =
        new CopyOnWriteArrayList<ArchiveChannel>();
    
    /** Groups of archived channels
     *  <p>
     *  @see channels about thread safety
     */
    final CopyOnWriteArrayList<ArchiveGroup> groups =
        new CopyOnWriteArrayList<ArchiveGroup>();
    
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

    /** Construct model that writes to archive */
    public EngineModel(final RDBArchive archive)
    {
        this.archive = archive;
        applyPreferences();
        writer = new WriteThread(archive);
    }

    /** Read preference settings */
    @SuppressWarnings("nls")
    private void applyPreferences()
    {
        final IPreferencesService prefs = Platform.getPreferencesService();
        if (prefs == null)
            return;
        write_period = prefs.getInt(Activator.ID, "write_period", write_period, null);
        max_repeats = prefs.getInt(Activator.ID, "max_repeats", max_repeats, null);
        batch_size = prefs.getInt(Activator.ID, "batch_size", batch_size, null);
        buffer_reserve = prefs.getDouble(Activator.ID, "buffer_reserve", buffer_reserve, null);
    }

    /** @return Name (description) */
    final public String getName()
    {
        return name;
    }
    
    /** @return Seconds into the future that should be ignored */
    public static long getIgnoredFutureSeconds()
    {
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
            throw new Exception("Cannot add group while " + state); //$NON-NLS-1$
        // Avoid duplicates
        ArchiveGroup group = getGroup(name);
        if (group != null)
            return group;
        // Add new group
        group = new ArchiveGroup(name);
        groups.add(group);
        return group;
    }
    
    /** @return Number of groups */
    final public int getGroupCount()
    {
        return groups.size();
    }
    
    /** Get one archive group.
     *  @param group_index 0...<code>getGroupCount()-1</code>
     *  @return group
     *  @see #getGroupCount()
     */
    final public ArchiveGroup getGroup(final int group_index)
    {
        return groups.get(group_index);
    }

    /** @return Group by that name or <code>null</code> if not found */
    final public ArchiveGroup getGroup(final String name)
    {
        for (ArchiveGroup group : groups)
            if (group.getName().equals(name))
                return group;
        return null;
    }
    
    /** @return Number of channels */
    public int getChannelCount()
    {
        return channels.size();
    }

    /** @param i Channel index, 0 ... <code>getChannelCount()-1</code> */
    public ArchiveChannel getChannel(int i)
    {
        return channels.get(i);
    }

    /** @return Channel by that name or <code>null</code> if not found */
    final public ArchiveChannel getChannel(final String name)
    {
        for (ArchiveChannel channel : channels)
            if (channel.getName().equals(name))
                return channel;
        return null;
    }

    /** Add a channel to the engine under given group.
     *  @param name Channel name
     *  @param group Name of the group to which to add
     *  @param enablement How channel acts on the group
     *  @param period Estimated update period [seconds]
     *  @return {@link ArchiveChannel}
     *  @throws Exception on error from channel creation
     */
    @SuppressWarnings("nls")
    final public ArchiveChannel addChannel(final String name,
                         final ArchiveGroup group,
                         final Enablement enablement, final double period,
                         final boolean monitor) throws Exception
    {
        if (state != State.IDLE)
            throw new Exception("Cannot add channel while " + state); //$NON-NLS-1$
        
        // Is this an existing channel?
        ArchiveChannel channel = getChannel(name);

        // For the engine, channels can be in more than one group
        // if configuration matches.
        if (channel != null)
        {
            final String gripe = String.format(
                    "Group '%s': Channel '%s' already in group '%s'",
                     group.getName(), name, channel.getGroup(0).getName());
            if (channel.getEnablement() != enablement)
                throw new Exception(gripe + " with different enablement");
            if (/** Now monitor, but not before? */
                (monitor && (channel instanceof ScannedArchiveChannel))
                ||
                /** Or now scanned, but before monitor, or other scan rate? */
                (!monitor
                 && ((channel instanceof MonitoredArchiveChannel)
                     || ((ScannedArchiveChannel)channel).getPeriod() != period)
                ))
                throw new Exception(gripe + " with different sample mechanism");
        }
        else
        {   // Channel is new to this engine.
            // See if there's already a sample in the archive,
            // because we won't be able to go back-in-time before that sample.
        	IValue last_sample = null;
        	final ChannelConfig channel_id = archive.getChannel(name);
        	if (channel_id != null)
        	{
	            final ITimestamp last_stamp = channel_id.getLastTimestamp();
	            if (last_stamp != null)
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
            if (buffer_capacity < buffer_reserve)
                buffer_capacity = (int)buffer_reserve;
            
            // Create new channel
            if (monitor)
                channel = new MonitoredArchiveChannel(name, enablement,
                                                 buffer_capacity, last_sample,
                                                 period);
            else
            {
                channel = new ScannedArchiveChannel(name, enablement,
                                        buffer_capacity, last_sample, period,
                                        max_repeats);
                scanner.add((ScannedArchiveChannel)channel, period);
            }
            channels.add(channel);
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
        for (ArchiveGroup group : groups)
        {
            group.start();
            // Check for stop request.
            // Unfortunately, we don't check inside group.start(),
            // which could have run for some time....
            if (state == State.SHUTDOWN_REQUESTED)
                break;
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
        for (ArchiveChannel channel : channels)
            channel.getSampleBuffer().reset();
    }

    /** Stop monitoring the channels, flush the write buffers. */
    @SuppressWarnings("nls")
    final public void stop() throws Exception
    {
        state = State.STOPPING;
        // Stop scanning
        scan_thread.stop();
        // Disconnect from network
        CentralLogger.getInstance().getLogger(this).info("Stopping archive groups");
        for (ArchiveGroup group : groups)
            group.stop();
        // Assert that scanning has stopped
        scan_thread.join();
        // Flush all values out
        CentralLogger.getInstance().getLogger(this).info("Stopping writer");
        writer.shutdown();
        // Update state
        state = State.IDLE;
        start_time = null;
    }

    /** Read configuration of model from RDB.
     *  @param name Name of engine in RDB
     *  @param port Current HTTPD port
     */
    @SuppressWarnings("nls")
    final public void readConfig(final String name, final int port) throws Exception
    {
        this.name = name;
        final SampleEngineConfig engine = archive.findEngine(name);
        if (engine == null)
            throw new Exception("Unknown engine '" + name + "'");
        
        // Is the configuration consistent?
        if (engine.getUrl().getPort() != port)
            throw new Exception("Engine running on port " + port +
                " while configuration requires " + engine.getUrl().toString());
        
        // Get groups
        final ChannelGroupConfig[] engine_groups = engine.getGroups();
        for (ChannelGroupConfig group_config : engine_groups)
        {
            final ArchiveGroup group = addGroup(group_config.getName());
            // Add channels to group
            final ChannelConfig[] channel_configs = group_config.getChannels();
            for (ChannelConfig channel_config : channel_configs)
            {
                Enablement enablement = Enablement.Passive;
                if (group_config.getEnablingChannelId() == channel_config.getId())
                    enablement = Enablement.Enabling;
                addChannel(channel_config.getName(), group, enablement,
                        channel_config.getSamplePeriod(),
                        channel_config.getSampleMode().isMonitor());
            }
        }
    }

    /** Remove all channels and groups. */
    @SuppressWarnings("nls")
    final public void clearConfig()
    {
        if (state != State.IDLE)
            throw new IllegalStateException("Only allowed in IDLE state");
        groups.clear();
        channels.clear();
        scanner.clear();
    }

    /** Write debug info to stdout */
    @SuppressWarnings("nls")
    public void dumpDebugInfo()
    {
        System.out.println(TimestampFactory.now().toString() + ": Debug info");
        for (ArchiveChannel channel : channels)
        {
            StringBuilder buf = new StringBuilder();
            buf.append("'" + channel.getName() + "' (");
            for (int i=0; i<channel.getGroupCount(); ++i)
            {
                if (i > 0)
                    buf.append(", ");
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
