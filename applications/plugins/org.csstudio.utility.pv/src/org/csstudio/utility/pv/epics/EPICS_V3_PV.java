package org.csstudio.utility.pv.epics;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_CTRL_Short;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DBR_TIME_Enum;
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

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.value.DoubleValue;
import org.csstudio.value.EnumValue;
import org.csstudio.value.EnumeratedMetaData;
import org.csstudio.value.IntegerValue;
import org.csstudio.value.MetaData;
import org.csstudio.value.NumericMetaData;
import org.csstudio.value.Severity;
import org.csstudio.value.StringValue;
import org.csstudio.value.Value;

/**
 * EPICS ChannelAccess implementation of the PV interface.
 * <p>
 * Also creates a shared pool of PVs:<br>
 * The underlying pure java CA client implementation actually returns the
 * same 'channel' when trying to access the same PV name multiple times.
 * That's good, but I don't know how to determine if the channel for this
 * EPICS_V3_PV is actually shared.
 * Calling destroy() on such a shared channel creates problems.<br>
 * This class therefore adds its own hash map of channels and keeps a reference
 * count.
 * @see PV
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EPICS_V3_PV
          implements PV, ConnectionListener, GetListener, MonitorListener
{
    /** Compile-time option for debug messages. */
    private static final boolean debug = false;

    /** Set to <code>true</code> if the pure Java CA context should be used.
     *  <p>
     *  Changes only have an effect before the very first channel is created.
     */
    public static boolean use_pure_java = true;

    /** The Java CA Library instance. */
    static private JCALibrary jca = null;

    /** The JCA context. */
    static private Context jca_context = null;

    /** The JCA context reference count. */
    static private long jca_refs = 0;

    /** Initialize the JA library. */
    static private void initJCA() throws Exception
    {
        synchronized (Context.class)
        {
            if (jca_refs == 0)
            {
                if (debug)
                    System.out.println("Initializing JCA "
                                    + (use_pure_java ? "(pure Java)" : "(JNI)"));
                jca = JCALibrary.getInstance();
                final String type = use_pure_java ?
                    JCALibrary.CHANNEL_ACCESS_JAVA : JCALibrary.JNI_THREAD_SAFE;
                jca_context = jca.createContext(type);
            }
            ++jca_refs;
        }
    }

    /** Disconnect from the JA library.
     *  <p>
     *  Without this step, JCA threads can stay around and prevent the
     *  application from quitting.
     */
    static private void exitJCA() throws Throwable
    {
        synchronized (Context.class)
        {
            --jca_refs;
            if (jca_refs == 0)
            {
                try
                {
                    jca_context.destroy();
                    jca = null;
                    if (debug)
                        System.out.println("Finalized JCA");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /** A Channel with thread-safe reference count. */
    class RefCountedChannel
    {
        private Channel channel;

        private int refs;

        public RefCountedChannel(Channel channel)
        {
            this.channel = channel;
            refs = 1;
        }

        synchronized public void incRefs()
        {   ++refs;  }

        synchronized public int decRefs()
        {
            --refs;
            return refs;
        }

        public Channel getChannel()
        {   return channel;   }

        public void dispose()
        {
            try
            {
                channel.destroy();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            channel = null;
        }
    }

    /** map of channels. */
    static private HashMap<String, RefCountedChannel> channels =
        new HashMap<String, RefCountedChannel>();

    /** PVListeners of this PV */
    private CopyOnWriteArrayList<PVListener> listeners
        = new CopyOnWriteArrayList<PVListener>();

    /** Use plain mode?
     *  @see #EPICS_V3_PV(String, boolean)
     */
    private final boolean plain;

    /** Channel name. */
    private final String name;

    /** isRunning? */
    private volatile boolean running = false;

    /** isConnected? */
    private volatile boolean connected = false;

    /** JCA channel. */
    private RefCountedChannel channel_ref;

    /** Assert that we only subscribe once. */
    private volatile boolean was_connected;

    /** Meta data obtained during connection cycle. */
    private MetaData meta = null;

    /** Most recent 'live' value. */
    private volatile Value value = null;

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

    /** @return Returns the value. */
    public Value getValue()
    {   return value;  }

    public void addListener(PVListener listener)
    {   listeners.add(listener);  }

    public void removeListener(PVListener listener)
    {   listeners.remove(listener);   }

    public void start() throws Exception
    {
        if (running)
            return;
        running = true;
        was_connected = false;
        initJCA();

        synchronized (channels)
        {
            channel_ref = channels.get(name);
            if (channel_ref == null)
            {
                if (debug)
                    System.out.println("Creating CA channel " + name);
                final Channel channel = jca_context.createChannel(name);
                if (channel == null)
                    throw new Exception("Cannot create channel '" + name + "'");
                channel_ref = new RefCountedChannel(channel);
                channels.put(name, channel_ref);
                jca_context.flushIO();
            }
            else
            {
                channel_ref.incRefs();
                if (debug)
                    System.out.println("Re-using CA channel " + name);
            }
        }
        channel_ref.getChannel().addConnectionListener(this);
        if (channel_ref.getChannel().getConnectionState() == ConnectionState.CONNECTED)
        {
            if (debug)
                System.out.println("Channel is immediately connected: " + name);

            handleConnected();
        }
    }

    public boolean isRunning()
    {   return running;  }

    public boolean isConnected()
    {   return connected;  }

    public void stop()
    {
        if (!running)
            return;
        running = false;
        try
        {
            channel_ref.getChannel().removeConnectionListener(this);
            synchronized (channels)
            {
                if (channel_ref.decRefs() <= 0)
                {
                    if (debug)
                        System.out.println("Deleting CA channel " + name);
                    channels.remove(name);
                    channel_ref.dispose();
                }
                else if (debug)
                    System.out.println("CA channel " + name + " still ref'ed");
            }
            channel_ref = null;
            exitJCA();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        connected = false;
        fireDisconnected();
    }

    /** Set PV to given value. */
    public void setValue(Object new_value)
    {
        if (!connected)
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
            // TODO: Delay the flush for multiple 'setValue' calls?
            // this applies to all the flushIO() calls in here...
            jca_context.flushIO();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** ConnectionListener interface. */
    public void connectionChanged(ConnectionEvent ev)
    {
        if (ev.isConnected())
            handleConnected();
        else
            handleDisconnected();
    }

    /** PV is connected.
     *  Get meta info, or subscribe right away.
     */
    private void handleConnected()
    {
        if (debug)
            System.out.println("Channel '" + name + "' connected");
        try
        {
            DBRType type = channel_ref.getChannel().getFieldType();
            if (! (plain || type.isSTRING()))
            {
                if (debug)
                    System.out.println("Getting meta info for type "
                                    + type.getName());
                if (type.isDOUBLE()  ||  type.isFLOAT())
                    type = DBRType.CTRL_DOUBLE;
                else if (type.isENUM())
                    type = DBRType.LABELS_ENUM;
                else
                    type = DBRType.CTRL_SHORT;
                channel_ref.getChannel().get(type, 1, this);
                jca_context.flushIO();
                return;
            }
        }
        catch (Exception e)
        {
            System.out.println("Channel '" + name + "' handleConnected:\n"
                            + e.getMessage());
        }

        // Meta info is not requested, not available for this type,
        // or there was an error in the get call.
        // So reset it, then just move on to the subscription.
        meta = null;
        subscribe();
    }

    /** GetListener interface, handles result of getting DBR_CTRL... */
    public void getCompleted(GetEvent event)
    {
        if (event.getStatus().isSuccessful())
        {
            DBR dbr = event.getDBR();
            if (dbr.isLABELS())
            {
                DBR_LABELS_Enum labels = (DBR_LABELS_Enum)dbr;
                meta = new EnumeratedMetaData(labels.getLabels());
                if (debug)
                    System.out.println("Channel '" + name + "' got meta:"
                                    + meta);
            }
            else if (dbr instanceof DBR_CTRL_Double)
            {
                DBR_CTRL_Double ctrl = (DBR_CTRL_Double)dbr;
                meta = new NumericMetaData(
                                ctrl.getUpperDispLimit().doubleValue(),
                                ctrl.getLowerDispLimit().doubleValue(),
                                ctrl.getUpperAlarmLimit().doubleValue(),
                                ctrl.getLowerAlarmLimit().doubleValue(),
                                ctrl.getUpperWarningLimit().doubleValue(),
                                ctrl.getLowerWarningLimit().doubleValue(),
                                ctrl.getPrecision(),
                                ctrl.getUnits());
                if (debug)
                    System.out.println("Channel '" + name + "' got meta:"
                                    + meta);
            }
            else if (dbr instanceof DBR_CTRL_Short)
            {
                DBR_CTRL_Short ctrl = (DBR_CTRL_Short)dbr;
                meta = new NumericMetaData(
                                ctrl.getUpperDispLimit().doubleValue(),
                                ctrl.getLowerDispLimit().doubleValue(),
                                ctrl.getUpperAlarmLimit().doubleValue(),
                                ctrl.getLowerAlarmLimit().doubleValue(),
                                ctrl.getUpperWarningLimit().doubleValue(),
                                ctrl.getLowerWarningLimit().doubleValue(),
                                0, // no precision
                                ctrl.getUnits());
                if (debug)
                    System.out.println("Channel '" + name + "' got meta:"
                                    + meta);
            }
            else
            {
                System.out.println("Channel '" + name + "' getCompleted: "
                                + "got " + dbr.getClass().getName());
            }
        }
        else
        {
            System.out.println("Channel '" + name + "' getCompleted error: "
                            + event.getStatus().getMessage());
        }
        subscribe();
    }

    private void subscribe()
    {
        if (was_connected == false)
        {
            was_connected = true;
            try
            {
                DBRType type = channel_ref.getChannel().getFieldType();
                if (type.isDOUBLE())
                    type = plain ? DBRType.DOUBLE : DBRType.TIME_DOUBLE;
                else if (type.isSHORT())
                    type = plain ? DBRType.SHORT : DBRType.TIME_SHORT;
                else if (type.isENUM())
                    type = plain ? DBRType.SHORT : DBRType.TIME_ENUM;
                else
                    // default: get as string
                    type = plain ? DBRType.STRING : DBRType.TIME_STRING;
                if (debug)
                    System.out.println("Channel '" + name
                                    + "': subscribing as " + type.getName());
                channel_ref.getChannel().addMonitor(type,
                        channel_ref.getChannel().getElementCount(), 1, this);
                jca_context.flushIO();
            }
            catch (Exception e)
            {
                System.out.println("Channel '" + name + "' subscribe error:\n"
                        + e.getMessage());
            }
        }
    }

    /** PV is disconnected */
    private void handleDisconnected()
    {
        if (debug)
            System.out.println("Channel '" + name + "' disconnected");
        connected = false;
        fireDisconnected();
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
        // Ignore values that arrive after stop()
        if (!running)
            return;
        if (ev.getStatus().isSuccessful())
        {
            DBR dbr = ev.getDBR();
            try
            {
                ITimestamp time = null;
                Severity severity = null;
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
                    value = new DoubleValue(time, severity, status,
                                    (NumericMetaData)meta, v);
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': double value " + value);
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
                    value = new IntegerValue(time, severity, status,
                                    (NumericMetaData)meta, short2int(v));
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': short value " + value);
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
                    value = new StringValue(time, severity, status, v[0]);
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': string value " + value);
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
                    value = new EnumValue(time, severity, status,
                                    (EnumeratedMetaData)meta, short2int(v));
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': enum value " + value);
                }
                else
                    // handle many more types!!
                    throw new Exception("Cannot decode " + dbr);
                if (!connected)
                    connected = true;
                fireValueUpdate();
            }
            catch (Exception e)
            {
                System.out.println("Channel '" + name + "' value error:"
                        + e.getMessage());
            }
        }
        else
        {
            System.out.println("Channel '" + name + "':"
                    + ev.getStatus().getMessage());
        }
    }

    /** Convert short array to int array. */
    private int[] short2int(final short[] v)
    {
        int ival[] = new int[v.length];
        for (int i = 0; i < ival.length; i++)
            ival[i] = v[i];
        return ival;
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

    /** TODO Helge, how about comments?
     *  TODO Remove, because this is wrong.
     *       If a PV should have a precision (units, limits, ...),
     *       that we would add that to the PV base class.
     *       But instead, we add it to the Value's MetaData.
     *       Because some value types have no precision,
     *       and even if they do, different samples can have
     *       different meta data, so asking the PV for the meta
     *       data seems wrong.
     */
    public int getPrecision(){
    	if (meta instanceof NumericMetaData) {
			NumericMetaData numMeta = (NumericMetaData) meta;
			return numMeta.getPrecision();

		}else return 0;
    }
}
