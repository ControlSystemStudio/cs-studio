package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.libs.epics.EpicsPlugin.MonitorMask;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.PlatformObject;

/** EPICS ChannelAccess implementation of the PV interface.
 *  
 *  @see PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV
            extends PlatformObject
            implements PV, ConnectionListener, MonitorListener
{
    /** Use plain mode?
     *  @see #EPICS_V3_PV(String, boolean)
     */
    final private boolean plain;
    
    /** Channel name. */
    final private String name;
    
    private enum State
    {
        /** Nothing happened, yet */
        Idle,
        /** Trying to connect */
        Connecting,
        /** Got basic connection */
        Connected,
        /** Requested MetaData */
        GettingMetadata,
        /** Received MetaData */
        GotMetaData,
        /** Subscribing to receive value updates */
        Subscribing,
        /** Received Value Updates
         *  <p>
         *  This is the ultimate state!
         */
        GotMonitor,
        /** Got disconnected */
        Disconnected
    }
    
    private State state = State.Idle;
    
    /** PVListeners of this PV */
    final private CopyOnWriteArrayList<PVListener> listeners
                                    = new CopyOnWriteArrayList<PVListener>();

    /** JCA channel. LOCK <code>this</code> on change. */
    private RefCountedChannel channel_ref = null;

    /** Either <code>null</code>, or the subscription identifier.
     *  LOCK <code>this</code> on change */
    private Monitor subscription = null;
    
    /** isConnected?
     *  <code>true</code> if we are currently connected
     *  (based on the most recent connection callback).
     *  <p>
     *  EPICS_V3_PV also runs notifyAll() on <code>this</code>
     *  whenever the connected flag changes to <code>true</code>.
     */
    private boolean connected = false;

    /** Meta data obtained during connection cycle. */
    private IMetaData meta = null;

    /** Most recent 'live' value. */
    private IValue value = null;
    
    /** isRunning?
     *  <code>true</code> if we want to receive value updates.
     */
    private boolean running = false;

    /** Listener to the get... for meta data */
    private final GetListener meta_get_listener = new GetListener()
    {
        public void getCompleted(GetEvent event)
        {   // This runs in a CA thread
            if (event.getStatus().isSuccessful())
            {
                state = State.GotMetaData;
                final DBR dbr = event.getDBR();
                meta = DBR_Helper.decodeMetaData(dbr);
                Activator.getLogger().debug(name + " meta: " + meta);
            }
            else
                System.out.println(name + " meta data get error: "
                                        + event.getStatus().getMessage());
            // Subscribe, but outside of callback (JCA deadlocks)
            PVContext.scheduleCommand(new Runnable()
            {
            	public void run()
            	{
                    subscribe();
            	}
            });
        }
    };
    
    /** Listener to a get-callback for data. */
    private class GetCallbackListener implements GetListener
    {
        /** The received meta data/ */
        IMetaData meta = null;
        
        /** The received value. */
        IValue value = null;
        
        /** After updating <code>meta</code> and <code>value</code>,
         *  this flag is set, and then <code>notify</code> is invoked
         *  on <code>this</code>.
         */ 
        boolean got_response = false;
        
        public void reset()
        {
            got_response = false;
        }
        
        public void getCompleted(GetEvent event)
        {   // This runs in a CA thread
            if (event.getStatus().isSuccessful())
            {
                final DBR dbr = event.getDBR();
                meta = DBR_Helper.decodeMetaData(dbr);
                Activator.getLogger().debug(name + " meta: " + meta);
                try
                {
                    value = DBR_Helper.decodeValue(plain, meta, dbr);
                }
                catch (Exception ex)
                {
                    Activator.getLogger().error("PV " + name, ex);
                    value = null;
                }
                Activator.getLogger().debug(name + " value: " + value);
            }
            else
            {
                meta = null;
                value = null;
            }
            synchronized (this)
            {
                got_response = true;
                this.notifyAll();
            }
        }
    }
    private final GetCallbackListener get_callback = new GetCallbackListener();
    
    /** Generate an EPICS PV.
     *  @param name The PV name.
     */
    public EPICS_V3_PV(final String name)
    {
        this(name, false);
    }

    /** Generate an EPICS PV.
     *  @param name The PV name.
     *  @param plain When <code>true</code>, only the plain value is requested.
     *               No time etc.
     *               Some PVs only work in plain mode, example: "record.RTYP".
     */
    public EPICS_V3_PV(final String name, final boolean plain)
    {
        this.name = name;
        this.plain = plain;
        Activator.getLogger().debug(name + " created as EPICS_V3_PV");
    }
    
    /** Use finalize as last resort for cleanup, but give warnings. */
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (channel_ref != null)
        {
            Activator.getLogger().error("EPICS_V3_PV " + name + " not properly stopped");
            try
            {
                stop();
            }
            catch (Throwable ex)
            {
                Activator.getLogger().error(name + " finalize error", ex);
            }
        }
        Activator.getLogger().debug(name + " finalized.");
    }

    /** @return Returns the name. */
    public String getName()
    {   return name;  }
    
    /** @return CSS type ID for IProcessVariable */
    public String getTypeId()
    {
        return IProcessVariable.TYPE_ID;
    }

    /** {@inheritDoc} */
    public IValue getValue(final double timeout_seconds) throws Exception
    {
        final long end_time = System.currentTimeMillis() +
                                (long)(timeout_seconds * 1000);
        // Try to connect (NOP if already connected)
        connect();
        // Wait for connection
        while (! connected)
        {   // Wait...
            final long remain = end_time - System.currentTimeMillis();
            if (remain <= 0)
                throw new Exception("PV " + name + " connection timeout");
            synchronized (this)
            {
            	this.wait(remain);
			}
        }
        // Reset the callback data
        get_callback.reset();
        // Issue the 'get'
        final DBRType type = DBR_Helper.getCtrlType(plain,
                                      channel_ref.getChannel().getFieldType());
        Activator.getLogger().debug(name + " get-callback as " + type.getName());
        channel_ref.getChannel().get(
                        type, channel_ref.getChannel().getElementCount(),
                        get_callback);
        // Wait for value callback
        synchronized (get_callback)
        {
            while (! get_callback.got_response)
            {   // Wait...
                final long remain = end_time - System.currentTimeMillis();
                if (remain <= 0)
                    throw new Exception("PV " + name + " value timeout");
                get_callback.wait(remain);
            }
        }
        value = get_callback.value;
        return get_callback.value;
    }
    
    /** {@inheritDoc} */
    public IValue getValue()
    {
        return value;
    }

    /** {@inheritDoc} */
    public void addListener(PVListener listener)
    {   listeners.add(listener);  }

    /** {@inheritDoc} */
    public void removeListener(PVListener listener)
    {   listeners.remove(listener);   }

    /** Try to connect to the PV.
     *  OK to call more than once.
     */
    private void connect() throws Exception
    {
        state = State.Connecting;
        // Already attempted a connection?
        synchronized (this)
        {
            if (channel_ref == null)
                channel_ref = PVContext.getChannel(name, EPICS_V3_PV.this);
		}
        if (channel_ref.getChannel().getConnectionState()
            == ConnectionState.CONNECTED)
        {
            Activator.getLogger().debug(name + " is immediately connected");
            handleConnected(channel_ref.getChannel());
        }
    }
    
    /** Disconnect from the PV.
     *  OK to call more than once.
     */
    private void disconnect()
    {
    	synchronized (this)
    	{
	        // Never attempted a connection?
	 		if (channel_ref == null)
	            return;
	        try
	        {
	            PVContext.releaseChannel(channel_ref, this);
	        }
	        catch (Throwable e)
	        {
	            e.printStackTrace();
	        }
	        channel_ref = null;
		}
        fireDisconnected();
    }
    
    /** Subscribe for value updates. */
    private void subscribe()
    {
    	synchronized (this)
    	{
            // Prevent multiple subscriptions.
            if (subscription != null)
                return;
            // Late callback, channel already closed?
            final RefCountedChannel ch_ref = channel_ref;
            if (ch_ref == null)
            	return;
    		final Channel channel = ch_ref.getChannel();
            final Logger logger = Activator.getLogger();
            try
            {
                final DBRType type = DBR_Helper.getTimeType(plain,
                                        channel.getFieldType());
                final MonitorMask mask = PVContext.monitor_mask;
                if (logger.isDebugEnabled())
                    logger.debug(name + " subscribed as " + type.getName()
                            + " (" + mask + ")");
                state = State.Subscribing;
                subscription = channel.addMonitor(type,
                       channel.getElementCount(),
                       mask.getMask(), this);
            }
            catch (Exception ex)
            {
                logger.error(name + " subscribe error", ex);
            }
		}
    }
    
    /** Unsubscribe from value updates. */
    private void unsubscribe()
    {
    	Monitor sub_copy;
    	// Atomic access
    	synchronized (this)
    	{
    		sub_copy = subscription;
    		subscription = null;
		}
		if (sub_copy == null)
            return;
        try
        {
        	sub_copy.clear();
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(name + " unsubscribe error", ex);
        }
    }

    /** {@inheritDoc} */
    public void start() throws Exception
    {
        if (running)
            return;
        running = true;
        connect();
    }

    /** {@inheritDoc} */
    public boolean isRunning()
    {   return running;  }

    /** {@inheritDoc} */
    public boolean isConnected()
    {
        return connected;
    }

    /** {@inheritDoc} */ 
    public String getStateInfo()
    {
        return state.toString();
    }

    /** {@inheritDoc} */
    public void stop()
    {
        running = false;
        unsubscribe();
        disconnect();
    }

    /** {@inheritDoc} */
    public void setValue(final Object new_value) throws Exception
    {
        if (!isConnected())
            throw new Exception(name + " is not connected");
        // Send strings as strings..
        if (new_value instanceof String)
            channel_ref.getChannel().put((String)new_value);
        else
        {   // other types as double.
            if (new_value instanceof Double)
            {
                final double val = ((Double)new_value).doubleValue();
                channel_ref.getChannel().put(val);
            }
            else if (new_value instanceof Double [])
            {
                final Double dbl[] = (Double [])new_value;
                final double val[] = new double[dbl.length];
                for (int i=0; i<val.length; ++i)
                    val[i] = dbl[i].doubleValue();
                channel_ref.getChannel().put(val);
            }
            else if (new_value instanceof Integer)
            {
                final double val = ((Integer)new_value).intValue();
                channel_ref.getChannel().put(val);
            }
            else throw new Exception("Cannot handle type "
                            + new_value.getClass().getName());
        }
    }

    /** ConnectionListener interface. */
    public void connectionChanged(final ConnectionEvent ev)
    {
    	// This runs in a CA thread
        if (ev.isConnected())
        {   // Transfer to JCACommandThread to avoid deadlocks
        	// The connect event can actually happen 'right away'
        	// when the channel is created, before we even get to assign
        	// the channel_ref. So use the channel from the event, not
        	// the channel_ref which might still be null.
            PVContext.scheduleCommand(new Runnable()
            {
                public void run()
                {
                    handleConnected((Channel) ev.getSource());
                }
            });
        }
        else
        {
            Activator.getLogger().debug(name + " disconnected");
            state = State.Disconnected;
            connected = false;
            PVContext.scheduleCommand(new Runnable()
            {
                public void run()
                {
                    fireDisconnected();
                }
            });
        }
    }

    /** PV is connected.
     *  Get meta info, or subscribe right away.
     */
    private void handleConnected(final Channel channel)
    {
    	if (state == State.Connected)
    		return;
        state = State.Connected;
        Activator.getLogger().debug(name + " connected");
        
        // If we're "running", we need to get the meta data and
        // then subscribe.
        // Otherwise, we're done.
        if (!running)
        {
            connected = true;
            meta = null;
            synchronized (this)
            {
                this.notifyAll();
            }
            return;
        }
        // else: running, get meta data, then subscribe
        try
        {
            DBRType type = channel.getFieldType();
            if (! (plain || type.isSTRING()))
            {
                state = State.GettingMetadata;
                Activator.getLogger().debug("Getting meta info for type "
                                    + type.getName());
                if (type.isDOUBLE()  ||  type.isFLOAT())
                    type = DBRType.CTRL_DOUBLE;
                else if (type.isENUM())
                    type = DBRType.LABELS_ENUM;
                else
                    type = DBRType.CTRL_SHORT;
                channel.get(type, 1, meta_get_listener);
                return;
            }
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(name + " connection handling error", ex);
            return;
        }

        // Meta info is not requested, not available for this type,
        // or there was an error in the get call.
        // So reset it, then just move on to the subscription.
        meta = null;
        subscribe();
    }

   /** MonitorListener interface. */
    public void monitorChanged(final MonitorEvent ev)
    {
        // This runs in a CA thread.
        // Ignore values that arrive after stop()
        if (!running)
            return;
        if (! ev.getStatus().isSuccessful())
        {
            Activator.getLogger().error(name + " monitor error :"
                               + ev.getStatus().getMessage());
            return;
        }
    
        state = State.GotMonitor;
        try
        {
            value =
                DBR_Helper.decodeValue(plain, meta, ev.getDBR());
            if (!connected)
                connected = true;
            // Logging every received value is expensive and chatty.
            // Use TRACE Level? But CSS GUI doesn't support this...
            final Logger log = Activator.getLogger();
            if (log.isDebugEnabled())
                log.debug(name + " monitor: " + value);
            fireValueUpdate();
        }
        catch (Exception ex)
        {
            Activator.getLogger().error(name + " monitor value error", ex);
        }
    }

    /** Notify all listeners. */
    private void fireValueUpdate()
    {
        for (PVListener listener : listeners)
            listener.pvValueUpdate(this);
    }

    /** Notify all listeners. */
    private void fireDisconnected()
    {
        for (PVListener listener : listeners)
            listener.pvDisconnected(this);
    }

    @Override
    public String toString()
    {
        return "EPICS_V3_PV '" + name + "'";
    }
}
