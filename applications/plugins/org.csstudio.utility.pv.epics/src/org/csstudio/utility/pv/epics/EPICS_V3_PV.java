package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_CTRL_Short;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import gov.aps.jca.dbr.DBR_TIME_Float;
import gov.aps.jca.dbr.DBR_TIME_Int;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.dbr.DBR_TIME_String;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TimeStamp;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
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

    
    final IValue.Quality quality = IValue.Quality.Original;


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
                    pv_data.meta = decodeMetaData(dbr);
                    if (PVContext.debug)
                        System.out.println("Channel '" + name
                                            + "' got Meta data: "
                                            + pv_data.meta);
                }
            }
            else
                System.out.println("Channel '" + name + "' getCompleted error: "
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
    
    /** Listener to the get-callback for data.
     *  Decodes the meta & data, and notifies
     *  on <code>get_callback</code>
     */
    private final GetListener get_callback = new GetListener()
    {
        public void getCompleted(GetEvent event)
        {   // This runs in a CA thread
            synchronized (pv_data)
            {
                if (event.getStatus().isSuccessful())
                {
                    final DBR dbr = event.getDBR();
                    pv_data.meta = decodeMetaData(dbr);
                    try
                    {
                        pv_data.value = decodeValue(dbr);
                    }
                    catch (Exception ex)
                    {
                        Activator.logException("PV " + getName(), ex);
                        pv_data.value = null;
                    }
                    if (PVContext.debug)
                        System.out.println("Channel '" + name
                                            + "' got Meta data: "
                                            + pv_data.value);
                }
                else
                {
                        pv_data.meta = null;
                        pv_data.value = null;
                }
            }
            synchronized (this)
            {
                this.notifyAll();
            }
        }
    };
    
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
    }

    /** @return Returns the name. */
    public String getName()
    {   return name;  }

    public IValue getValue(double timeout_seconds) throws Exception
    {
        final long end_time = System.currentTimeMillis() +
                                (long)(timeout_seconds * 1000);
        // Wait for connection
        connect();
        synchronized (pv_data)
        {
            while (! pv_data.connected)
            {   // Wait...
                final long remain = end_time - System.currentTimeMillis();
                if (remain < 0)
                    throw new Exception("Connection timeout: PV " + name);
                pv_data.wait(remain);
            }
        }
        final DBRType type = getCtrlType();
        if (PVContext.debug)
            System.out.println("Channel '" + name + "': get as " + type.getName());
        channel_ref.getChannel().get(
                        type, channel_ref.getChannel().getElementCount(),
                        get_callback);
        PVContext.flush();
        // Wait for value callback
        synchronized (get_callback)
        {
            final long remain = end_time - System.currentTimeMillis();
            if (remain < 0)
                throw new Exception("Get timeout: PV " + name);
            get_callback.wait(remain);
        }
        synchronized (pv_data)
        {
            return pv_data.value;
        }
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
        }
        if (channel_ref.getChannel().getConnectionState() == ConnectionState.CONNECTED)
        {
            if (PVContext.debug)
                System.out.println("Channel is immediately connected: " + name);
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
            final DBRType type = getTimeType();
            if (PVContext.debug)
                System.out.println("Channel '" + name
                                + "': subscribing as " + type.getName());
            synchronized (pv_data)
            {
                pv_data.subscription = channel_ref.getChannel().addMonitor(type,
                           channel_ref.getChannel().getElementCount(), 1, this);
            }
            PVContext.flush();
        }
        catch (Exception e)
        {
            System.out.println("Channel '" + name + "' subscribe error:\n"
                    + e.getMessage());
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
                    Activator.logException("Unsubscribe from " + getName(), ex);
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
    public void setValue(Object new_value)
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
                    double val = ((Double)new_value).doubleValue();
                    channel_ref.getChannel().put(val);
                }
                else if (new_value instanceof Integer)
                {
                    double val = ((Integer)new_value).intValue();
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
            Activator.logException("Set PV " + getName() + " = " + new_value, ex);
        }
    }

    /** In case SWT (display) is available, run something in UI Thread.
     *  If no SWT, just run it.
     *  @param runnable What to run
     */
    private void runInUI(final Runnable runnable)
    {
        Display display;
        try
        {
            display = Display.getDefault();
        }
        // If there is no display lib because for example
        // we run in a non-SWT unit test, just call directly.
        catch (UnsatisfiedLinkError ex)
        {
            runnable.run();
            return;
        }
        catch (NoClassDefFoundError ex)
        {
            runnable.run();
            return;
        }
        // The 'normal' case under SWT: Transfer to UI thread.
        display.asyncExec(runnable);
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
                System.out.println("Channel '" + name + "' disconnected");
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
            System.out.println("Channel '" + name + "' connected");
        
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
            Activator.logException("Channel '" + name + "' handleConnected", ex);
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

    /** Convert the EPICS time stamp (based on 1990) into the usual 1970 epoch. */
    private ITimestamp createTimeFromEPICS(TimeStamp t)
    {
        return TimestampFactory.createTimestamp(
                        t.secPastEpoch() + 631152000L, t.nsec());
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
            System.out.println("Channel '" + name + "':"
                    + ev.getStatus().getMessage());
            return;
        }
    
        try
        {
            synchronized (pv_data)
            {
                pv_data.value = decodeValue(ev.getDBR());
                if (PVContext.debug)
                    System.out.println("Monitor: " + name + " = " + pv_data.value);
                if (!pv_data.connected)
                    pv_data.connected = true;
            }
            fireValueUpdate();
        }
        catch (Exception ex)
        {
            Activator.logException("Channel '" + name + "' value error", ex);
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
    
    /** @return Meta data extracted from dbr */
    private IMetaData decodeMetaData(final DBR dbr)
    {
        if (dbr.isLABELS())
        {
            final DBR_LABELS_Enum labels = (DBR_LABELS_Enum)dbr;
            return ValueFactory.createEnumeratedMetaData(labels.getLabels());
        }
        else if (dbr instanceof DBR_CTRL_Double)
        {
            final DBR_CTRL_Double ctrl = (DBR_CTRL_Double)dbr;
            return ValueFactory.createNumericMetaData(
                            ctrl.getLowerDispLimit().doubleValue(),
                            ctrl.getUpperDispLimit().doubleValue(),
                            ctrl.getLowerWarningLimit().doubleValue(),
                            ctrl.getUpperWarningLimit().doubleValue(),
                            ctrl.getLowerAlarmLimit().doubleValue(),
                            ctrl.getUpperAlarmLimit().doubleValue(),
                            ctrl.getPrecision(),
                            ctrl.getUnits());
        }
        else if (dbr instanceof DBR_CTRL_Short)
        {
            final DBR_CTRL_Short ctrl = (DBR_CTRL_Short)dbr;
            return ValueFactory.createNumericMetaData(
                            ctrl.getLowerDispLimit().doubleValue(),
                            ctrl.getUpperDispLimit().doubleValue(),
                            ctrl.getLowerWarningLimit().doubleValue(),
                            ctrl.getUpperWarningLimit().doubleValue(),
                            ctrl.getLowerAlarmLimit().doubleValue(),
                            ctrl.getUpperAlarmLimit().doubleValue(),
                            0, // no precision
                            ctrl.getUnits());
        }
        return null;
    }

    /** @return CTRL_... type for this channel. */
    private DBRType getCtrlType()
    {
        final DBRType type = channel_ref.getChannel().getFieldType();
        if (type.isDOUBLE())
            return plain ? DBRType.DOUBLE : DBRType.CTRL_DOUBLE;
        else if (type.isFLOAT())
            return plain ? DBRType.FLOAT : DBRType.CTRL_FLOAT;
        else if (type.isINT())
            return plain ? DBRType.INT : DBRType.CTRL_INT;
        else if (type.isSHORT())
            return plain ? DBRType.SHORT : DBRType.CTRL_SHORT;
        else if (type.isENUM())
            return plain ? DBRType.SHORT : DBRType.CTRL_ENUM;
        // default: get as string
        return plain ? DBRType.STRING : DBRType.CTRL_STRING;
    }
    
    /** @return TIME_... type for this channel. */
    private DBRType getTimeType()
    {
        final DBRType type = channel_ref.getChannel().getFieldType();
        if (type.isDOUBLE())
            return plain ? DBRType.DOUBLE : DBRType.TIME_DOUBLE;
        else if (type.isFLOAT())
            return plain ? DBRType.FLOAT : DBRType.TIME_FLOAT;
        else if (type.isINT())
            return plain ? DBRType.INT : DBRType.TIME_INT;
        else if (type.isSHORT())
            return plain ? DBRType.SHORT : DBRType.TIME_SHORT;
        else if (type.isENUM())
            return plain ? DBRType.SHORT : DBRType.TIME_ENUM;
        // default: get as string
        return plain ? DBRType.STRING : DBRType.TIME_STRING;
    }
    
    /** Convert short array to int array. */
    private int[] short2int(final short[] v)
    {
        int result[] = new int[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert short array to long array. */
    private long[] short2long(final short[] v)
    {
        long result[] = new long[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert int array to long array. */
    private long[] int2long(final int[] v)
    {
        long result[] = new long[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }

    /** Convert float array to a double array. */
    private double[] float2double(final float[] v)
    {
        double result[] = new double[v.length];
        for (int i = 0; i < result.length; i++)
            result[i] = v[i];
        return result;
    }    

    /** @return Value extracted from dbr */
    private IValue decodeValue(final DBR dbr) throws Exception
    {
        ITimestamp time = null;
        ISeverity severity = null;
        String status = "";
        if (plain)
        {
            time = TimestampFactory.now();
            severity = SeverityUtil.forCode(0);
        }
        if (dbr.isDOUBLE())
        {
            double v[];
            if (plain)
                v = ((DBR_Double)dbr).getDoubleValue();
            else
            {
                DBR_TIME_Double dt = (DBR_TIME_Double) dbr;
                severity = SeverityUtil.forCode(dt.getSeverity().getValue());
                Status stat = dt.getStatus();
                status = stat.getValue() == 0 ? "" : stat.getName();
                time = createTimeFromEPICS(dt.getTimeStamp());
                v = dt.getDoubleValue();
            }
            return ValueFactory.createDoubleValue(time, severity,
                        status, (INumericMetaData)pv_data.meta, quality, v);
        }
        else if (dbr.isFLOAT())
        {
            float v[];
            if (plain)
                v = ((DBR_Float)dbr).getFloatValue();
            else
            {
                DBR_TIME_Float dt = (DBR_TIME_Float) dbr;
                severity = SeverityUtil.forCode(dt.getSeverity().getValue());
                Status stat = dt.getStatus();
                status = stat.getValue() == 0 ? "" : stat.getName();
                time = createTimeFromEPICS(dt.getTimeStamp());
                v = dt.getFloatValue();
            }
            return ValueFactory.createDoubleValue(time, severity,
                            status, (INumericMetaData)pv_data.meta, quality,
                            float2double(v));
        }
        else if (dbr.isINT())
        {
            int v[];
            if (plain)
                v = ((DBR_Int)dbr).getIntValue();
            else
            {
                DBR_TIME_Int dt = (DBR_TIME_Int) dbr;
                severity = SeverityUtil.forCode(dt.getSeverity().getValue());
                Status stat = dt.getStatus();
                status = stat.getValue() == 0 ? "" : stat.getName();
                time = createTimeFromEPICS(dt.getTimeStamp());
                v = dt.getIntValue();
            }
            return ValueFactory.createLongValue(time, severity,
                            status, (INumericMetaData)pv_data.meta, quality,
                            int2long(v));
        }
        else if (dbr.isSHORT())
        {
            short v[];
            if (plain)
                v = ((DBR_Short)dbr).getShortValue();
            else
            {
                DBR_TIME_Short dt = (DBR_TIME_Short) dbr;
                severity = SeverityUtil.forCode(dt.getSeverity().getValue());
                Status stat = dt.getStatus();
                status = stat.getValue() == 0 ? "" : stat.getName();
                time = createTimeFromEPICS(dt.getTimeStamp());
                v = dt.getShortValue();
            }
            return ValueFactory.createLongValue(time, severity,
                                status, (INumericMetaData)pv_data.meta, quality,
                                short2long(v));
        }
        else if (dbr.isSTRING())
        {
            String v[];
            if (plain)
                v = ((DBR_String)dbr).getStringValue();
            else
            {
                DBR_TIME_String dt = (DBR_TIME_String) dbr;
                severity = SeverityUtil.forCode(dt.getSeverity().getValue());
                Status stat = dt.getStatus();
                status = stat.getValue() == 0 ? "" : stat.getName();
                time = createTimeFromEPICS(dt.getTimeStamp());
                v = dt.getStringValue();
            }
            return ValueFactory.createStringValue(time, severity,
                                status, quality, v[0]);
        }
        else if (dbr.isENUM())
        {
            short v[];
            // 'plain' mode would subscribe to SHORT,
            // so this must be a TIME_Enum:
            DBR_TIME_Enum dt = (DBR_TIME_Enum) dbr;
            severity = SeverityUtil.forCode(dt.getSeverity().getValue());
            Status stat = dt.getStatus();
            status = stat.getValue() == 0 ? "" : stat.getName();
            time = createTimeFromEPICS(dt.getTimeStamp());
            v = dt.getEnumValue();
            return ValueFactory.createEnumeratedValue(time, severity,
                                status, (IEnumeratedMetaData)pv_data.meta, quality,
                                short2int(v));
        }
        else
            // handle many more types!!
            throw new Exception("Cannot decode " + dbr);
    }

}
