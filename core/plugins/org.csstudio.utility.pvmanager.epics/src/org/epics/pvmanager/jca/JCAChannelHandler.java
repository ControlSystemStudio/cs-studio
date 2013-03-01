/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.ValueCache;
import com.cosylab.epics.caj.CAJMonitor;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.*;
import gov.aps.jca.event.AccessRightsEvent;
import gov.aps.jca.event.AccessRightsListener;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import gov.aps.jca.event.PutEvent;
import gov.aps.jca.event.PutListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.epics.pvmanager.*;
import org.epics.util.array.CollectionNumbers;
import org.epics.util.array.ListNumber;

/**
 * A ChannelHandler for the JCADataSource.
 * <p>
 * NOTE: this class is extensible as per Bastian request so that DESY can hook
 * a different type factory. This is a temporary measure until the problem
 * is solved in better, more general way, so that data sources
 * can work only with data source specific types, while allowing
 * conversions to normalized type through operators. The contract of this
 * class is, therefore, expected to change.
 * <p>
 * Related changes are marked so that they are not accidentally removed in the
 * meantime, and can be intentionally removed when a better solution is implemented.
 *
 * @author carcassi
 */
class JCAChannelHandler extends MultiplexedChannelHandler<JCAConnectionPayload, JCAMessagePayload> {

    private static final int LARGE_ARRAY = 100000;
    private final JCADataSource jcaDataSource;
    private final String jcaChannelName;
    private volatile Channel channel;
    private volatile boolean needsMonitor;
    private volatile boolean largeArray = false;
    private final boolean putCallback;
    private final boolean longString;
    
    // For the AccessChaneListener we need to guard it differently
    private final AtomicBoolean needsAccessChangeListener = new AtomicBoolean(false);
    
    public static Pattern longStringPattern = Pattern.compile(".+\\..*\\$.*");
    private final static Pattern hasOptions = Pattern.compile("(.*) (\\{.*\\})");

    public JCAChannelHandler(String channelName, JCADataSource jcaDataSource) {
        super(channelName);
        this.jcaDataSource = jcaDataSource;
        
        boolean longStringName = longStringPattern.matcher(channelName).matches();
        
        // Parse parameters
        // Done here so that they can be immutable
        Matcher matcher = hasOptions.matcher(getChannelName());
        if (matcher.matches()) {
            jcaChannelName = matcher.group(1);
            String clientOptions = matcher.group(2);
            // TODO: Hack, this should have a real JSON parser
            switch (clientOptions) {
                case "{\"putCallback\":true}":
                    putCallback = true;
                    longString = longStringName;
                    break;
                case "{\"putCallback\":false}":
                    putCallback = false;
                    longString = longStringName;
                    break;
                case "{\"longString\":true}":
                    putCallback = false;
                    longString = true;
                    break;
                case "{\"longString\":false}":
                    putCallback = false;
                    longString = false;
                    break;
                default:
                    throw new IllegalArgumentException("Option not recognized for " + getChannelName());
            }
        } else {
            longString = longStringName;
            putCallback = false;
            jcaChannelName = channelName;
        }
    }

    /**
     * Whether this channel should be written using a put callback.
     * 
     * @return true if a put callback should be used
     */
    public boolean isPutCallback() {
        return putCallback;
    }

    /**
     * Return whether this channel should be treated as a long string,
     * meaning a BYTE[] that really represents an encoded string.
     * 
     * @return true if the channel should be handled as a long string
     */
    public boolean isLongString() {
        return longString;
    }

    /**
     * The datasource this channel refers to.
     * 
     * @return a jca data source
     */
    public JCADataSource getJcaDataSource() {
        return jcaDataSource;
    }
 
    /**
     * The name used for the actual connection.
     * 
     * @return the name of the ca channel
     */
    public String getJcaChannelName() {
        return jcaChannelName;
    }
    
    @Override
    protected JCATypeAdapter findTypeAdapter(ValueCache<?> cache, JCAConnectionPayload connPayload) {
        return jcaDataSource.getTypeSupport().find(cache, connPayload);
    }

    @Override
    public void connect() {
        try {
            // Give the listener right away so that no event gets lost
	    // If it's a large array, connect using lower priority
	    if (largeArray) {
                channel = jcaDataSource.getContext().createChannel(getJcaChannelName(), connectionListener, Channel.PRIORITY_MIN);
	    } else {
                channel = jcaDataSource.getContext().createChannel(getJcaChannelName(), connectionListener, (short) (Channel.PRIORITY_MIN + 1));
	    }
            needsMonitor = true;
            needsAccessChangeListener.set(true);
        } catch (CAException ex) {
            throw new RuntimeException("JCA Connection failed", ex);
        }
    }

    private void putWithCallback(Object newValue, final ChannelWriteCallback callback) throws CAException {
        PutListener listener = new PutListener() {

            @Override
            public void putCompleted(PutEvent ev) {
                if (ev.getStatus().isSuccessful()) {
                    callback.channelWritten(null);
                } else {
                    callback.channelWritten(new Exception(ev.toString()));
                }
            }
        };
        if (newValue instanceof String) {
            if (isLongString()) {
                channel.put(toBytes(newValue.toString()), listener);
            } else {
                channel.put(newValue.toString(), listener);
            }
        } else if (newValue instanceof byte[]) {
            channel.put((byte[]) newValue, listener);
        } else if (newValue instanceof short[]) {
            channel.put((short[]) newValue, listener);
        } else if (newValue instanceof int[]) {
            channel.put((int[]) newValue, listener);
        } else if (newValue instanceof float[]) {
            channel.put((float[]) newValue, listener);
        } else if (newValue instanceof double[]) {
            channel.put((double[]) newValue, listener);
        } else if (newValue instanceof Byte || newValue instanceof Short
                || newValue instanceof Integer || newValue instanceof Long) {
            channel.put(((Number) newValue).longValue(), listener);
        } else if (newValue instanceof Float || newValue instanceof Double) {
            channel.put(((Number) newValue).doubleValue(), listener);
        } else {
            throw new RuntimeException("Unsupported type for CA: " + newValue.getClass());
        }
        jcaDataSource.getContext().flushIO();
    }

    private void put(Object newValue, final ChannelWriteCallback callback) throws CAException {
        // If it's a ListNumber, extract the array
        if (newValue instanceof ListNumber) {
            ListNumber data = (ListNumber) newValue;
            Object wrappedArray = CollectionNumbers.wrappedArray(data);
            if (wrappedArray == null) {
                newValue = CollectionNumbers.doubleArrayCopyOf(data);
            } else {
                newValue = wrappedArray;
            }
        }
        
        if (newValue instanceof String) {
            if (isLongString()) {
                channel.put(toBytes(newValue.toString()));
            } else {
                channel.put(newValue.toString());
            }
        } else if (newValue instanceof byte[]) {
            channel.put((byte[]) newValue);
        } else if (newValue instanceof short[]) {
            channel.put((short[]) newValue);
        } else if (newValue instanceof int[]) {
            channel.put((int[]) newValue);
        } else if (newValue instanceof float[]) {
            channel.put((float[]) newValue);
        } else if (newValue instanceof double[]) {
            channel.put((double[]) newValue);
        } else if (newValue instanceof Byte || newValue instanceof Short
                || newValue instanceof Integer || newValue instanceof Long) {
            channel.put(((Number) newValue).longValue());
        } else if (newValue instanceof Float || newValue instanceof Double) {
            channel.put(((Number) newValue).doubleValue());
        } else {
            callback.channelWritten(new Exception(new RuntimeException("Unsupported type for CA: " + newValue.getClass())));
            return;
        }
        jcaDataSource.getContext().flushIO();
        callback.channelWritten(null);
    }

    private void setup(Channel channel) throws CAException {
        DBRType metaType = metadataFor(channel);

        // If metadata is needed, get it
        if (metaType != null) {
            // Need to use callback for the listener instead of doing a synchronous get
            // (which seemed to perform better) because JCA (JNI implementation)
            // would return an empty list of labels for the Enum metadata
            channel.get(metaType, 1, new GetListener() {

                @Override
                public void getCompleted(GetEvent ev) {
                    synchronized(JCAChannelHandler.this) {
                        // In case the metadata arrives after the monitor
                        MonitorEvent event = null;
                        if (getLastMessagePayload() != null) {
                            event = getLastMessagePayload().getEvent();
                        }
                        processMessage(new JCAMessagePayload(ev.getDBR(), event));
                    }
                }
            });
        }

        // Start the monitor only if the channel was (re)created, and
        // not because a disconnection/reconnection
        if (needsMonitor) {
            channel.addMonitor(valueTypeFor(channel), countFor(channel), jcaDataSource.getMonitorMask(), monitorListener);
            needsMonitor = false;
        }
        
        // Setup metadata monitor if required
        if (jcaDataSource.isDbePropertySupported() && metaType != null) {
            channel.addMonitor(metaType, 1, Monitor.PROPERTY, new MonitorListener() {

                @Override
                public void monitorChanged(MonitorEvent ev) {
                    synchronized(JCAChannelHandler.this) {
                        // In case the metadata arrives after the monitor
                        MonitorEvent event = null;
                        if (getLastMessagePayload() != null) {
                            event = getLastMessagePayload().getEvent();
                        }
                        processMessage(new JCAMessagePayload(ev.getDBR(), event));
                    }
                }
            });
        }

        // Flush the entire context (it's the best we can do)
        channel.getContext().flushIO();
    }
    
    private final ConnectionListener connectionListener = new ConnectionListener() {

            @Override
            public void connectionChanged(ConnectionEvent ev) {
                synchronized(JCAChannelHandler.this) {
                    try {
                        // Take the channel from the event so that there is no
                        // synchronization problem
                        Channel channel = (Channel) ev.getSource();

                        // Check whether the channel is large and was opened
                        // as large. Reconnect if does not match
                        if (ev.isConnected() && channel.getElementCount() >= LARGE_ARRAY && !largeArray) {
                            disconnect();
                            largeArray = true;
                            connect();
                            return;
                        }

                        // Setup monitors on connection
                        processConnection(new JCAConnectionPayload(JCAChannelHandler.this, channel, getConnectionPayload()));
                        if (ev.isConnected()) {
                            setup(channel);
                        }
                        
                    } catch (Exception ex) {
                        reportExceptionToAllReadersAndWriters(ex);
                    }
                }
                
                // XXX: because of the JNI implementation this section cannot
                // be part of the previous atomic section. The problem is that
                // adding the listener causes the listener to be called
                // right away on a different thread, and the addAccessRightsListener
                // seems to return only after the event is processed. This
                // means that you cannot serialize the addListener call
                // and the listener callback.
                // Since we have to make a choice between having either the add
                // or the callback properly synchronized, we choose the callback
                boolean addListener = needsAccessChangeListener.getAndSet(false);
                if (addListener) {
                    try {
                        Channel channel = (Channel) ev.getSource();
                        channel.addAccessRightsListener(new AccessRightsListener() {

                            @Override
                            public void accessRightsChanged(AccessRightsEvent ev) {
                                // Some JNI implementation lock if calling getState
                                // from within this callback. We context switch in that case
                                final Channel channel = (Channel) ev.getSource();
                                Runnable task = new Runnable() {

                                    @Override
                                    public void run() {
                                        synchronized(JCAChannelHandler.this) {
                                            processConnection(new JCAConnectionPayload(JCAChannelHandler.this, channel, getConnectionPayload()));
                                        }
                                    }
                                };
                                if (jcaDataSource.useContextSwitchForAccessRightCallback()) {
                                    jcaDataSource.getContextSwitch().submit(task);
                                } else {
                                    task.run();
                                }
                            }
                        });
                    } catch (Exception ex) {
                        reportExceptionToAllReadersAndWriters(ex);
                    }
                }
            }
        };;
    
    private final MonitorListener monitorListener = new MonitorListener() {

        @Override
        public void monitorChanged(MonitorEvent event) {
            synchronized(JCAChannelHandler.this) {
                DBR metadata = null;
                if (getLastMessagePayload() != null) {
                    metadata = getLastMessagePayload().getMetadata();
                }
                processMessage(new JCAMessagePayload(metadata, event));
            }
        }
    };

    @Override
    public void disconnect() {
        try {
            // Close the channel
            if (channel.getConnectionState() != Channel.ConnectionState.CLOSED) {
                channel.destroy();
            }
        } catch (CAException ex) {
            throw new RuntimeException("JCA Disconnect fail", ex);
        } finally {
            channel = null;
            processConnection(null);
        }
    }

    @Override
    public void write(Object newValue, final ChannelWriteCallback callback) {
        try {
            if (isPutCallback())
                putWithCallback(newValue, callback);
            else
                put(newValue, callback);
        } catch (CAException ex) {
            callback.channelWritten(ex);
        }
    }
    
    @Override
    protected boolean isConnected(JCAConnectionPayload connPayload) {
        return connPayload != null && connPayload.isChannelConnected();
    }

    @Override
    protected boolean isWriteConnected(JCAConnectionPayload connPayload) {
        return connPayload != null && connPayload.isWriteConnected();
    }

    @Override
    public synchronized Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        if (channel != null) {
            properties.put("Channel name", channel.getName());
            properties.put("Connection state", channel.getConnectionState().getName());
            if (channel.getConnectionState() == Channel.ConnectionState.CONNECTED) {
                properties.put("Hostname", channel.getHostName());
                properties.put("Channel type", channel.getFieldType().getName());
                properties.put("Element count", channel.getElementCount());
                properties.put("Read access", channel.getReadAccess());
                properties.put("Write access", channel.getWriteAccess());
            }
            properties.put("isLongString", isLongString());
            properties.put("isPutCallback", isPutCallback());
        }
        return properties;
    }

    protected DBRType metadataFor(Channel channel) {
        DBRType type = channel.getFieldType();
        
        if (type.isBYTE() || type.isSHORT() || type.isINT() || type.isFLOAT() || type.isDOUBLE())
            return DBR_CTRL_Double.TYPE;
        
        if (type.isENUM())
            return DBR_LABELS_Enum.TYPE;
        
        return null;
    }

    protected int countFor(Channel channel) {
        if (channel.getElementCount() == 1)
            return 1;
        
        if (jcaDataSource.isVarArraySupported())
            return 0;
        else
            return channel.getElementCount();
    }
    
    static Pattern rtypeStringPattern = Pattern.compile(".+\\.RTYP.*");

    protected DBRType valueTypeFor(Channel channel) {
        DBRType type = channel.getFieldType();
        
        if (type.isBYTE()) {
            return DBR_TIME_Byte.TYPE;
        } else if (type.isSHORT()) {
            return DBR_TIME_Short.TYPE;
        } else if (type.isINT()) {
            return DBR_TIME_Int.TYPE;
        } else if (type.isFLOAT()) {
            return DBR_TIME_Float.TYPE;
        } else if (type.isDOUBLE()) {
            return DBR_TIME_Double.TYPE;
        } else if (type.isENUM()) {
            return DBR_TIME_Enum.TYPE;
        } else if (type.isSTRING()) {
            if (jcaDataSource.isRtypValueOnly() &&
                    rtypeStringPattern.matcher(channel.getName()).matches()) {
                return DBR_String.TYPE;
            }
            return DBR_TIME_String.TYPE;
        }
        
        throw new IllegalArgumentException("Unsupported type " + type);
    }
    
    /**
     * Converts a String into byte array.
     * 
     * @param text the string to be converted
     * @return byte array, always including '\0' termination
     */
    static byte[] toBytes(final String text) {
        // TODO: it's unclear what encoding is used and how
        
        // Write string as byte array WITH '\0' TERMINATION!
        final byte[] bytes = new byte[text.length() + 1];
        System.arraycopy(text.getBytes(), 0, bytes, 0, text.length());
        bytes[text.length()] = '\0';
        return bytes;
    }
    
    /**
     * Converts a byte array into a String. It
     * 
     * @param data the array to be converted
     * @return the string
     */
    static String toString(byte[] data) {
        int index = 0;
        while (index < data.length && data[index] != '\0') {
            index++;
        }
        
        return new String(data, 0, index);
    }
}
