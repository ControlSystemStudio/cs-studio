package org.csstudio.archive.engine.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.archive.engine.ThrottledLogger;
import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 */
abstract public class ArchiveChannel
{
    /** Throttled log for NaN samples */
    private static ThrottledLogger NaN_log = 
                    new ThrottledLogger(Level.INFO, "log_nan"); //$NON-NLS-1$
    
    /** Group to which this channel belongs.
     *  <p>
     *  Using thread safe array so that HTTPD can access
     *  as well as main thread and PV
     */
    final private CopyOnWriteArrayList<ArchiveGroup> groups =
                                new CopyOnWriteArrayList<ArchiveGroup>();
    
    /** Control system PV */
    final private PV pv;
    
    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    private boolean is_running = false;

    /** Do we need to log a 'write error' sample?
     *  <p>
     *  The sample buffer will indicate write errors.
     *  While in error, we keep adding samples, which
     *  will probably cause overrides.
     *  When we can write again, we add one info sample.
     */
    private boolean need_write_error_sample = false;
    
    /** Do we need to log a 'first' sample?
     *  <p>
     *  After startup, or after a network disconnect,
     *  the first sample we receive
     *  might be time-stamped days ago,
     *  while the archive has an 'off' or 'disconnected' info sample
     *  that's already newer.
     *  This flag is used to force one initial
     *  sample into the archive with current time stamp.
     */
    private boolean need_first_sample = true;
    
    /** How channel affects its groups */
    final private Enablement enablement;
    
    /** Is this channel currently enabled? */
    private boolean enabled = true;
    
    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive. 
     *  <p>
     *  SYNC:Lock on <code>this</code> for access.
     */
    protected IValue most_recent_value = null;
    
    /** Last value in the archive, i.e. the one most recently written.
     *  <p>
     *  SYNC: Lock on <code>this</code> for access.
     */
    protected IValue last_archived_value = null;
    
    /** Buffer of received samples, periodically written */
    private final SampleBuffer buffer;
    
    /** Construct an archive channel
     *  @param name Name of the channel (PV)
     *  @param enablement How channel affects its groups
     *  @param buffer_capacity Size of sample buffer
     *  @param last_archived_value Last value from storage, or <code>null</code>.
     *  @throws Exception On error in PV setup
     */
    public ArchiveChannel(final String name,
                          final Enablement enablement,
                          final int buffer_capacity,
                          final IValue last_archived_value) throws Exception
    {
        this.enablement = enablement;
        this.last_archived_value = last_archived_value;
        this.buffer = new SampleBuffer(name, buffer_capacity);
        if (last_archived_value == null)
            CentralLogger.getInstance().getLogger(this).info(
                    name + ": No known last value"); //$NON-NLS-1$
        
        pv = PVFactory.createPV(name);
        pv.addListener(new PVListener()
        {
            public void pvValueUpdate(final PV pv)
            {
                // PV already suppresses updates after 'stop', but check anyway
                if (is_running)
                {
                    final IValue value = pv.getValue();
                    if (enablement != Enablement.Passive)
                        handleEnablement(value);
                    handleNewValue(value);
                }
            }

            public void pvDisconnected(final PV pv)
            {
                if (is_running)
                    handleDisconnected();
            }
        });
    }

    /** @return Name of channel */
    final public String getName()
    {
        return pv.getName();
    }
    
    /** @return How channel affects its groups */
    final public Enablement getEnablement()
    {
        return enablement;
    }
    
    /** @return <code>true</code> if channel is currently enabled */
    final public boolean isEnabled()
    {
        return enabled ;
    }
    
    /** @return Short description of sample mechanism */
    abstract public String getMechanism();
    
    /** @return Number of Groups to which this channel belongs */
    final public int getGroupCount()
    {
        return groups.size();
    }
    
    /** @return One Group to which this channel belongs */
    final public ArchiveGroup getGroup(final int index)
    {
        return groups.get(index);
    }

    /** Tell channel that it belogs to group */
    final void addGroup(final ArchiveGroup group)
    {
        groups.add(group);
    }

    /** Tell channel that it no longer belogs to group */
    @SuppressWarnings("nls")
    final void removeGroup(final ArchiveGroup group)
    {
        if (!groups.remove(group))
            throw new Error("Channel " + getName() + " doesn't belong to group"
                            + group.getName());
    }
    
    /** @return <code>true</code> if connected */
    final public boolean isConnected()
    {
        return pv.isConnected();
    }

    /** @return Human-readable info on internal state of PV */
    public String getInternalState()
    {
        return pv.getStateInfo();
    }

    /** Start archiving this channel. */
    final void start() throws Exception
    {
        is_running = true;
        need_first_sample = true;
        pv.start();
    }

    /** Stop archiving this channel */
    final void stop()
    {
    	if (!is_running)
    		return;
        is_running = false;
        pv.stop();
        addInfoToBuffer(ValueButcher.createOff());
    }

    /** @return Most recent value of the channel's PV */
    final public String getCurrentValue()
    {
        synchronized (this)
        {
            if (last_archived_value == null)
                return "null"; //$NON-NLS-1$
            return last_archived_value.toString();
        }
    }

    /** @return Last value written to archive */
    final public String getLastArchivedValue()
    {
        synchronized (this)
        {
            if (last_archived_value == null)
                return "null"; //$NON-NLS-1$
            return last_archived_value.toString();
        }
    }

    /** @return Sample buffer */
    final public SampleBuffer getSampleBuffer()
    {
        return buffer;
    }
    
    /** Enable or disable groups based on received value */
    final private void handleEnablement(final IValue value)
    {
        if (enablement == Enablement.Passive)
            throw new Error("Not to be called when passive"); //$NON-NLS-1$
        // Get boolean value (true <==> >0.0)
        final double number = ValueUtil.getDouble(value);
        final boolean yes = number > 0.0;
        // Do we enable or disable based on that value?
        final boolean enable = enablement == Enablement.Enabling ? yes : !yes;
        // Check which group needs to _change_
        for (ArchiveGroup group : groups)
        {
            if (group.isEnabled() != enable)
                group.enable(enable);
        }
    }

    /** Called for each value received from PV.
     *  <p>
     *  Base class remembers the <code>most_recent_value</code>,
     *  and asserts that one 'first' sample is archived.
     *  Derived class <b>must</b> call <code>super()</code>.
     */
    @SuppressWarnings("nls")
    protected void handleNewValue(final IValue value)
    {
        synchronized (this)
        {
            most_recent_value = value;
        }
        // NaN test
        if (value instanceof IDoubleValue)
        {
            final IDoubleValue dbl = (IDoubleValue) value;
            if (Double.isNaN(dbl.getValue()))
                NaN_log.log("NaN for '" + getName() + "': "
                        + value.format());
            
        }
        if (!enabled)
            return;
        
        // Did we recover from write errors?
        if (need_write_error_sample &&
            SampleBuffer.isInErrorState() == false)
        {
            need_write_error_sample = false;
            addInfoToBuffer(ValueButcher.createWriteError());
            need_first_sample = true;
        }
        // Is this the first sample after startup or an error?
        if (need_first_sample)
        {
            need_first_sample = false;
            addInfoToBuffer(ValueButcher.transformTimestampToNow(value));
        }
    }
    
    /** Handle a disconnect event.
     *  <p>
     *  Base class clears the <code>most_recent_value</code> and
     *  adds a 'disconnected' info sample.
     *  Subclasses may override, but must call <code>super()</code>.
     */
    protected void handleDisconnected()
    {
        synchronized (this)
        {
            most_recent_value = null;
        }
        addInfoToBuffer(ValueButcher.createDisconnected());
        need_first_sample = true;
    }

    /** Add given info value to buffer, tweaking its time stamp if necessary
     *  @param value Value to archive 
     */
    final protected void addInfoToBuffer(IValue value)
    {
        synchronized (this)
        {
            if (last_archived_value != null)
            {
                final ITimestamp last = last_archived_value.getTime();
                if (last.isGreaterOrEqual(value.getTime()))
                {   // Patch the time stamp
                    final ITimestamp next =
                        TimestampFactory.createTimestamp(last.seconds()+1, 0);
                    value = ValueButcher.transformTimestamp(value, next);
                }
                // else: value is OK as is
            }
        }
        addValueToBuffer(value);
    }

    /** Add given sample to buffer, performing a back-in-time check,
     *  updating the sample buffer error state.
     *  @param value Value to archive
     *  @return <code>false</code> if value failed back-in-time check,
     *          <code>true</code> if value was added.
     */
    @SuppressWarnings("nls")
    final protected boolean addValueToBuffer(final IValue value)
    {
        synchronized (this)
        {
            if (last_archived_value != null &&
                last_archived_value.getTime().isGreaterOrEqual(value.getTime()))
            {   // Cannot use this sample because of back-in-time problem.
                // Usually this is NOT an error:
                // We logged an initial sample, disconnected, disabled, ...,
                // and now we got an update from the IOC which still
                // carries the old, original time stamp of the PV,
                // and that's back in time...
                final Logger log = CentralLogger.getInstance().getLogger(this);
                if (log.isDebugEnabled())
                    log.debug(getName() + " skips back-in-time:\n" +
                        "last: " + last_archived_value.toString() + "\n" +
                        "new : " + value.toString());
                return false;
            }
            // else ...
            last_archived_value = value;
        }
        buffer.add(value);
        if (SampleBuffer.isInErrorState())
            need_write_error_sample = true;
        return true;
    }

    /** Determine if the channel is enabled.
     *  <p>
     *  Checks all groups to which the channel belongs.
     *  If they're all disabled, so is the channel.
     */
    final void computeEnablement()
    {
        // 'Active' channels always stay enabled
        if (enablement != Enablement.Passive)
            return;
        for (ArchiveGroup group : groups)
        {
            if (group.isEnabled())
            {   // Found at least one enabled group
                updateEnabledState(true);
                return;
            }
        }
        // else: All groups are disabled
        updateEnabledState(false);
    }

    /** Update the enablement state in case of change */
    final private void updateEnabledState(final boolean new_enabled_state)
    {
        // Any change?
        if (new_enabled_state == enabled)
            return;
        enabled = new_enabled_state;
        // In case this arrived after shutdown, don't log it.
        if (!is_running)
            return;
        if (enabled)
        {   // If we have the 'current' value of the PV...
            IValue value;
            synchronized (this)
            {
                value = most_recent_value;
            }
            if (value != null)
            {   // Add to the buffer with timestamp 'now' to show
                // the re-enablement
                value = ValueButcher.transformTimestampToNow(value);
                addValueToBuffer(value);
            }
        }
        else
            addInfoToBuffer(ValueButcher.createDisabled());
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Channel " + getName() + ", " + getMechanism();
    }
}
