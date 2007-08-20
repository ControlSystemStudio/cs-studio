package org.csstudio.utility.pv.epics;

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
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.swt.widgets.Display;

/** EPICS ChannelAccess implementation of the PV interface.
 *  <p>
 *  Most callbacks (value, disconnect) are just passed through from 
 *  the underlying CA library callback, so the user needs to be prepared
 *  to receive events from a non-UI thread.
 *  <p>
 *  With event-intensive apps like the PV Tree that connects, reads,
 *  then disconnects many PVs, there were deadlocks with the JNI CA
 *  client lib:
 *  <ul>
 *  <li>The main thread might try to destroy a PV...
 *  <li>.. while a CA connect callback tries to get
 *      meta info or subscribe.
 *  </ul>
 *  With CAJ, that worked OK, but JNI deadlocked.
 *  For that reason, this class might pass CA events up to the user code
 *  within the CA thread, but if it needs to call CA back from within
 *  a CA callback, it transfers to the UI thread.
 *  That seems to work OK, but does add an SWT dependency to this plugin.
 *  
 *  @see PV
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV
          implements PV, ConnectionListener, MonitorListener
{
    /** Use plain mode?
     *  @see #EPICS_V3_PV(String, boolean)
     */
    final private boolean plain;
    
    /** Channel name. */
    final private String name;
    
    /** PVListeners of this PV */
    final private CopyOnWriteArrayList<PVListener> listeners
                                    = new CopyOnWriteArrayList<PVListener>();

    /** JCA channel. */
    RefCountedChannel channel_ref = null;

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
            // Prevent deadlock with other UI code that calls into CA
            // by also subscribing in UI Thread
            runInUI(new Runnable()
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
        PVContext.flush();
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
        // Already attempted a connection?
        if (channel_ref == null)
        {
            channel_ref = PVContext.getChannel(name);
            channel_ref.getChannel().addConnectionListener(this);
            PVContext.flush();
        }
        if (channel_ref.getChannel().getConnectionState() == ConnectionState.CONNECTED)
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
            channel_ref.getChannel().removeConnectionListener(this);
            PVContext.releaseChannel(channel_ref);
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
            final DBRType type = DBR_Helper.getTimeType(plain,
                                    channel_ref.getChannel().getFieldType());
            if (PVContext.debug)
                System.out.println(name + " subscribed as " + type.getName());
            synchronized (pv_data)
            {
                pv_data.subscription = channel_ref.getChannel().addMonitor(type,
                           channel_ref.getChannel().getElementCount(), 1, this);
            }
            PVContext.flush();
        }
        catch (Exception ex)
        {
            Activator.logException(name + " subscribe error", ex);
        }
    }
    
    /** Unsubscribe from value updates. */
    private void unsubscribe()
    {
        synchronized (pv_data)
        {
            if (pv_data.subscription != null)
            {
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
        synchronized (pv_data)
        {
            return pv_data.connected;
        }
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
            // Delay the flush for multiple 'setValue' calls?
            // Would improve performance, but is really hard to do,
            // since this general-purpose PV doesn't "know" when
            // the application is "done".
            //
            // This applies to all the flushIO() calls in here...
            PVContext.flush();
        }
        catch (Exception ex)
        {
            Activator.logException(name + " set error for new value " + new_value, ex);
        }
    }

    /** In case SWT (display) is available, run something in UI Thread.
     *  If no SWT, just run it.
     *  @param runnable What to run
     */
    private void runInUI(final Runnable runnable)
    {
        // Tried Display.getDefault() == null to determine
        // if we're running with an SWT main loop,
        // but in unit tests that suddenly returned a valid
        // display, yet asyncExec never functioned for lack
        // of a main loop.
        // So now we check if the plugin was loaded.
        // If not, we assume unit test
        if (Activator.getDefault() == null)
        {
            if (PVContext.debug)
                System.out.println("EPICS_V3_PV runInUI runs directly");
            runnable.run();
            return;
        }
            
        try
        {
            final Display display = Display.getDefault();
            display.asyncExec(runnable);
        }
        catch (Throwable ex)
        {
            Activator.logException("Cannot run in UI thread", ex);
            runnable.run();
            return;
        }
    }
    
    /** ConnectionListener interface. */
    public void connectionChanged(ConnectionEvent ev)
    {
    	// This runs in a CA thread
        if (ev.isConnected())
        {
            // handleConnected() performs CA calls,
            // so prevent deadlocks under JNI by
            // transferring to UI thread.
            runInUI(new Runnable()
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
            synchronized (pv_data)
            {
                pv_data.connected = false;
                pv_data.notifyAll();
            }
            fireDisconnected();
        }
    }

    /** PV is connected.
     *  Get meta info, or subscribe right away.
     */
    private void handleConnected()
    {
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
                PVContext.flush();
                return;
            }
        }
        catch (Exception ex)
        {
            Activator.logException(name + " connection handling error", ex);
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
    
        try
        {
            synchronized (pv_data)
            {
                pv_data.value =
                    DBR_Helper.decodeValue(plain, pv_data.meta, ev.getDBR());
                if (PVContext.debug)
                    System.out.println(name + " monitor: " + pv_data.value);
                if (!pv_data.connected)
                    pv_data.connected = true;
            }
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
}
