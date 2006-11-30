package org.csstudio.utility.pv;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_CTRL_Double;
import gov.aps.jca.dbr.DBR_CTRL_Short;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Enum;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;
import gov.aps.jca.dbr.DBR_TIME_Double;
import gov.aps.jca.dbr.DBR_TIME_Enum;
import gov.aps.jca.dbr.DBR_TIME_Short;
import gov.aps.jca.dbr.DBR_TIME_String;
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
public class EPICS_V3_PV 
          implements PV, ConnectionListener, GetListener, MonitorListener
{
    /** Compile-time option for debug messages. */
    private static final boolean debug = false;

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
                    System.out.println("Initializing JCA");
                jca = JCALibrary.getInstance();
                jca_context = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
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
    private CopyOnWriteArrayList<PVListener> listeners;

    /** Use plain mode?
     *  @see #EPICS_V3_PV(String, boolean)
     */
    private boolean plain;
    
    /** Channel name. */
    private String name;

    /** isRunning? */
    private volatile boolean running;

    /** isConnected? */
    private volatile boolean connected;

    /** JCA channel. */
    private RefCountedChannel channel_ref;
    
    /** Assert that we only subscribe once. */
    private volatile boolean was_connected;

    // Meta data
    
    /** The unit description. */
    private String units = "";
    
    /** The display precision. */
    private int precision = 0;

    // The most recent 'live' data
    
    /** Most recent time. */
    private volatile ITimestamp time;

    /** Most recent value. */
    private volatile Object value;

    /** Most recent severity code. */
    private volatile int severity;

    /** Most recent status info. */
    private volatile String status;

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
        listeners = new CopyOnWriteArrayList<PVListener>();
        this.name = name;
        this.plain = plain;
        running = false;
        connected = false;
        value = null;
        time = null;
    }

    /** @return Returns the name. */
    public String getName()
    {   return name;  }

    /** @return Returns the value. */
    public Object getValue()
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
                channel_ref = new RefCountedChannel(
                        jca_context.createChannel(name));
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
    
    public String getUnits()
    {   return units; }

    public int getPrecision()
    {   return precision; }
    
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
                double val = PVValue.toDouble(new_value);
                channel_ref.getChannel().put(val);
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

    /** @return Returns the last time stamp. */
    public ITimestamp getTime()
    {   return time; }

    /** A severity code, where 0 means 'OK',
     *  higher numbers reflect a higher severity.
     *  @return Returns the severity code.
     *  @see #getSeverity(int)
     */
    public int getSeverityCode()
    {   return severity;  }

    /** @return Returns the severity text or <code>null</code>. */
    public String getSeverity()
    {
        switch (severity)
        {
        case 0: return "";
        case 1: return "MINOR";
        case 2: return "MAYOR";
        case 3: return "INVALID";
        }
        return "Unkown severity " + severity;
    }
    
    /** @return Returns the status string or <code>null</code>. */
    public String getStatus()
    {   return status; }

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
            if (! (plain || type.isSTRING() || type.isENUM()))
            {
                if (debug)
                    System.out.println("Getting meta info for type "
                                    + type.getName());
                if (type.isDOUBLE()  ||  type.isFLOAT())
                    type = DBRType.CTRL_DOUBLE;
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
        units = "";
        precision = 0;
        subscribe();
    }
 
    /** GetListener interface, handles result of getting DBR_CTRL... */
    public void getCompleted(GetEvent event)
    {
        if (event.getStatus().isSuccessful())
        {
            DBR dbr = event.getDBR();
            if (! dbr.isCTRL())
            {
                System.out.println("Channel '" + name + "' getCompleted: "
                                + "got " + dbr.getClass().getName());
            }
            else
            {
                if (dbr.isDOUBLE())
                {
                    DBR_CTRL_Double ctrl = (DBR_CTRL_Double)dbr;
                    units = ctrl.getUnits();
                    precision = ctrl.getPrecision();
                }
                else
                {
                    DBR_CTRL_Short ctrl = (DBR_CTRL_Short)dbr;
                    units = ctrl.getUnits();
                }
                if (debug)
                    System.out.println("Channel '" + name + "' got meta info\n"
                                    + "Units    : '" + units + "'\n"
                                    + "Precision: " + precision);
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
                else
                    // default: get as string
                    type = plain ? DBRType.STRING : DBRType.TIME_STRING;
                channel_ref.getChannel().addMonitor(type,
                        channel_ref.getChannel().getElementCount(), 1, this);
                jca_context.flushIO();
            }
            catch (Exception e)
            {
                System.out.println("Channel '" + name + "' subscribe:\n"
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
                if (dbr.isDOUBLE())
                {
                    double v[];
                    if (plain)
                        v = ((DBR_Double)dbr).getDoubleValue();
                    else
                    {
                        DBR_TIME_Double dt = (DBR_TIME_Double) dbr;
                        severity = dt.getSeverity().getValue();
                        status = dt.getStatus().getName();
                        time = createTimeFromEPICS(dt.getTimeStamp());
                        v = dt.getDoubleValue();
                    }
                    value = new Double(v[0]);
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': double value.");
                }
                else if (dbr.isSHORT())
                {
                    short v[];
                    if (plain)
                        v = ((DBR_Short)dbr).getShortValue();
                    else
                    {
                        DBR_TIME_Short dt = (DBR_TIME_Short) dbr;
                        severity = dt.getSeverity().getValue();
                        status = dt.getStatus().getName();
                        time = createTimeFromEPICS(dt.getTimeStamp());
                        v = dt.getShortValue();
                    }
                    value = new Double(v[0]);
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': short value.");
                }
                else if (dbr.isSTRING())
                {
                    String v[];
                    if (plain)
                        v = ((DBR_String)dbr).getStringValue();
                    else
                    {
                        DBR_TIME_String dt = (DBR_TIME_String) dbr;
                        severity = dt.getSeverity().getValue();
                        status = dt.getStatus().getName();
                        time = createTimeFromEPICS(dt.getTimeStamp());
                        v = dt.getStringValue();
                    }
                    value = v[0];
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': string value.");
                }
                else if (dbr.isENUM())
                {
                    short v[];
                    if (plain)
                        v = ((DBR_Enum)dbr).getEnumValue();
                    else
                    {
                        DBR_TIME_Enum dt = (DBR_TIME_Enum) dbr;
                        severity = dt.getSeverity().getValue();
                        status = dt.getStatus().getName();
                        time = createTimeFromEPICS(dt.getTimeStamp());
                        v = dt.getEnumValue();
                    }
                    value = new Integer((int) v[0]);
                    if (debug)
                        System.out.println("Channel '" + name
                                + "': enum value.");
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
