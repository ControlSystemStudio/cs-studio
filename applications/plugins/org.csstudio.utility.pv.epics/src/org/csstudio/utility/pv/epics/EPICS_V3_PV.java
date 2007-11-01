package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;
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

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.IValue;
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

    /** JCA channel. */
    private RefCountedChannel channel_ref = null;

    /** The data of this PV in a thread-safe container */
    final private PVData pv_data = new PVData();
    
    /** isRunning?
     *  <code>true</code> if we want to receive value updates.
     */
    boolean running = false;

    /** Listener to the get... for meta data */
    private final GetListener meta_get_listener = new GetListener()
    {
        public void getCompleted(GetEvent event)
        {   // This runs in a CA thread
            if (event.getStatus().isSuccessful())
            {
                state = State.GotMetaData;
                final DBR dbr = event.getDBR();
                synchronized (pv_data)
                {
                    pv_data.meta = DBR_Helper.decodeMetaData(dbr);
                    if (PVContext.debug)
                        System.out.println(name + " meta: " + pv_data.meta);
                }
            }
            else
                System.out.println(name + " meta data get error: "
                                        + event.getStatus().getMessage());
            subscribe();
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
                if (PVContext.debug)
                    System.out.println(name + " meta: " + meta);
                try
                {
                    value = DBR_Helper.decodeValue(plain, meta, dbr);
                }
                catch (Exception ex)
                {
                    Activator.logException("PV " + name, ex);
                    value = null;
                }
                if (PVContext.debug)
                    System.out.println(name + " value: " + value);
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
    public EPICS_V3_PV(String name)
    {
        this(name, false);
    }

    /** Generate an EPICS PV.
     *  @param name The PV name.
     *  @param plain When <code>true</code>, only the plain value is requested.
     *               No time etc.
     *               Some PVs only work in plain mode, example: "record.RTYP".
     */
    public EPICS_V3_PV(String name, boolean plain)
    {
        this.name = name;
        this.plain = plain;
        if (PVContext.debug)
            System.out.println(name + " created as EPICS_V3_PV");
    }
    
    /** Use finalize as last resort for cleanup, but give warnings. */
    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        if (channel_ref != null)
        {
            Activator.logError("EPICS_V3_PV " + name + " not properly stopped");
            try
            {
                stop();
            }
            catch (Throwable ex)
            {
                Activator.logException(name + " finalize error", ex);
            }
        }
        if (PVContext.debug)
            System.out.println(name + " finalized.");
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
    public IValue getValue(double timeout_seconds) throws Exception
    {
        final long end_time = System.currentTimeMillis() +
                                (long)(timeout_seconds * 1000);
        // Try to connect (NOP if already connected)
        connect();
        // Wait for connection
        synchronized (pv_data)
        {
            while (! pv_data.connected)
            {   // Wait...
                final long remain = end_time - System.currentTimeMillis();
                if (remain <= 0)
                    throw new Exception("PV " + name + " connection timeout");
                pv_data.wait(remain);
            }
        }
        // Reset the callback data
        get_callback.reset();
        // Issue the 'get'
        final DBRType type = DBR_Helper.getCtrlType(plain,
                                      channel_ref.getChannel().getFieldType());
        if (PVContext.debug)
            System.out.println(name + " get-callback as " + type.getName());
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
        synchronized (pv_data)
        {
            pv_data.value = get_callback.value;
        }
        return get_callback.value;
    }
    
    /** {@inheritDoc} */
    public IValue getValue()
    {
        synchronized (pv_data)
        {
            return pv_data.value;
        }
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
        if (channel_ref == null)
            channel_ref = PVContext.getChannel(name, EPICS_V3_PV.this);
        if (channel_ref.getChannel().getConnectionState()
            == ConnectionState.CONNECTED)
        {
            if (PVContext.debug)
                System.out.println(name + " is immediately connected");
            handleConnected();
        }
    }
    
    /** Disconnect from the PV.
     *  OK to call more than once.
     */
    private void disconnect()
    {
        // Never attempted a connection?
        if (channel_ref == null)
            return;
        try
        {
            PVContext.releaseChannel(channel_ref, this);
            channel_ref = null;
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        synchronized (pv_data)
        {
            pv_data.connected = false;
            pv_data.notifyAll();
        }
        fireDisconnected();
    }
    
    /** Subscribe for value updates. */
    private void subscribe()
    {
        synchronized (pv_data)
        {
            // Prevent multiple subscriptions.
            if (pv_data.subscription != null)
                return;
        }
        try
        {
            final Channel channel = channel_ref.getChannel();
            final DBRType type = DBR_Helper.getTimeType(plain,
                                    channel.getFieldType());
            if (PVContext.debug)
                System.out.println(name + " subscribed as " + type.getName());
            state = State.Subscribing;
            synchronized (pv_data)
            {
                pv_data.subscription = channel.addMonitor(type,
                           channel.getElementCount(), 1, this);
            }
        }
        catch (Exception ex)
        {
            Activator.logException(name + " subscribe error", ex);
        }
    }
    
    /** Unsubscribe from value updates. */
    private void unsubscribe()
    {
        if (pv_data.subscription == null)
            return;
        try
        {
            pv_data.subscription.clear();
        }
        catch (Exception ex)
        {
            Activator.logException(name + " unsubscribe error", ex);
        }
        pv_data.subscription = null;
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
        synchronized (pv_data)
        {
            return pv_data.connected;
        }
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

    /** Set PV to given value. */
    public void setValue(final Object new_value)
    {
        if (!isConnected())
            return;
        try
        {
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
        catch (Exception ex)
        {
            Activator.logException(name + " set error for new value " + new_value, ex);
        }
    }

    /** ConnectionListener interface. */
    public void connectionChanged(ConnectionEvent ev)
    {
    	// This runs in a CA thread
        if (ev.isConnected())
        {   // Transfer to JCACommandThread to avoid deadlocks
            PVContext.scheduleCommand(new Runnable()
            {
                public void run()
                {
                    handleConnected();
                }
            });
        }
        else
        {
            if (PVContext.debug)
                System.out.println(name + " disconnected");
            state = State.Disconnected;
            synchronized (pv_data)
            {
                pv_data.connected = false;
                pv_data.notifyAll();
            }
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
    private void handleConnected()
    {
    	// Connection after already disconnected?
    	if (channel_ref == null)
    		return;
    	
        state = State.Connected;
        if (PVContext.debug)
            System.out.println(name + " connected");
        
        // If we're "running", we need to get the meta data and
        // then subscribe.
        // Otherwise, we're done.
        if (!running)
        {
            synchronized (pv_data)
            {
                pv_data.connected = true;
                pv_data.meta = null;
                pv_data.notifyAll();
            }
            return;
        }
        // else: running, get meta data, then subscribe
        try
        {
            DBRType type = channel_ref.getChannel().getFieldType();
            if (! (plain || type.isSTRING()))
            {
                state = State.GettingMetadata;
                if (PVContext.debug)
                    System.out.println("Getting meta info for type "
                                    + type.getName());
                if (type.isDOUBLE()  ||  type.isFLOAT())
                    type = DBRType.CTRL_DOUBLE;
                else if (type.isENUM())
                    type = DBRType.LABELS_ENUM;
                else
                    type = DBRType.CTRL_SHORT;
                channel_ref.getChannel().get(type, 1, meta_get_listener);
                return;
            }
        }
        catch (Exception ex)
        {
            Activator.logException(name + " connection handling error", ex);
            return;
        }

        // Meta info is not requested, not available for this type,
        // or there was an error in the get call.
        // So reset it, then just move on to the subscription.
        synchronized (pv_data)
        {
            pv_data.meta = null;
        }
        subscribe();
    }

   /** MonitorListener interface. */
    public void monitorChanged(MonitorEvent ev)
    {
        // This runs in a CA thread.
        // Ignore values that arrive after stop()
        if (!running)
            return;
        if (! ev.getStatus().isSuccessful())
        {
            Activator.logError(name + " monitor error :"
                               + ev.getStatus().getMessage());
            return;
        }
    
        state = State.GotMonitor;
        try
        {
            synchronized (pv_data)
            {
                pv_data.value =
                    DBR_Helper.decodeValue(plain, pv_data.meta, ev.getDBR());
                if (!pv_data.connected)
                    pv_data.connected = true;
            }
            if (PVContext.debug)
                System.out.println(name + " monitor: " + pv_data.value);
            fireValueUpdate();
        }
        catch (Exception ex)
        {
            Activator.logException(name + " monitor value error", ex);
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
