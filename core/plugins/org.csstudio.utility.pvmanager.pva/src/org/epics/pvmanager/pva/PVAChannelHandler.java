/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvaccess.client.Channel;
import org.epics.pvaccess.client.Channel.ConnectionState;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.client.ChannelPut;
import org.epics.pvaccess.client.ChannelPutRequester;
import org.epics.pvaccess.client.ChannelRequester;
import org.epics.pvaccess.client.CreateRequestFactory;
import org.epics.pvaccess.client.GetFieldRequester;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.misc.BitSet;
import org.epics.pvdata.monitor.Monitor;
import org.epics.pvdata.monitor.MonitorElement;
import org.epics.pvdata.monitor.MonitorRequester;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.Field;
import org.epics.pvdata.pv.MessageType;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.Status;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.Structure;
import org.epics.pvmanager.ChannelHandlerReadSubscription;
import org.epics.pvmanager.ChannelWriteCallback;
import org.epics.pvmanager.MultiplexedChannelHandler;
import org.epics.pvmanager.ValueCache;
import org.epics.util.array.CollectionNumbers;
import org.epics.util.array.ListNumber;

/**
 * 
 * @author msekoranja
 */
public class PVAChannelHandler extends
		MultiplexedChannelHandler<PVAChannelHandler, PVStructure> implements
		ChannelRequester, GetFieldRequester, ChannelPutRequester, MonitorRequester {

	private final ChannelProvider pvaChannelProvider;
	private final short priority;
	private final PVATypeSupport pvaTypeSupport;

	private volatile Channel channel = null;

	private final AtomicBoolean monitorCreated = new AtomicBoolean(false);
	//private volatile Monitor monitor = null;
	
	private volatile Field channelType = null;
	private volatile boolean isChannelEnumType = false;
	
	private final AtomicBoolean channelPutCreated = new AtomicBoolean(false);
	private volatile ChannelPut channelPut = null;
	private volatile PVField channelPutValueField = null;


	private static final Logger logger = Logger.getLogger(PVAChannelHandler.class.getName());

	private static PVStructure standardPutPVRequest = CreateRequestFactory.createRequest("field(value)", null);
	private static PVStructure enumPutPVRequest = CreateRequestFactory.createRequest("field(value.index)", null);
	
	public PVAChannelHandler(String channelName,
			ChannelProvider channelProvider, short priority,
			PVATypeSupport typeSupport) {
		super(channelName);
		this.pvaChannelProvider = channelProvider;
		this.priority = priority;
		this.pvaTypeSupport = typeSupport;
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * @return the channelType
	 */
	public Field getChannelType() {
		return channelType;
	}

	@Override
	public String getRequesterName() {
		return this.getClass().getName();
	}

	@Override
	public void message(String message, MessageType messageType) {
		logger.log(toLoggerLevel(messageType), message);
	}

	/**
	 * Converts MessageType to Java Logging API Level.
	 * @param messageType pvData message type.
	 * @return Corresponded Java Logging API Level.
	 */
	public static Level toLoggerLevel(MessageType messageType) {
		switch (messageType) {
		case info:
			return Level.INFO;
		case warning:
			return Level.WARNING;
		case error:
		case fatalError:
			return Level.SEVERE;
		default:
			return Level.INFO;
		}
	}
	
	private void reportStatus(String message, Status status)
	{
		if (!status.isSuccess()) {
			logger.log(Level.WARNING, message + ": " + status.getMessage());

			// for developers
			String dump = status.getStackDump();
			if (dump != null && !dump.isEmpty())
				logger.log(Level.FINER, message + ": " + status.getMessage() + ", cause:\n" + dump);
		}
	}
	
	@Override
	public void connect() {
		pvaChannelProvider.createChannel(getChannelName(), this, priority);
	}

	@Override
	public void channelCreated(Status status, Channel channel) {
		reportStatus("Failed to create channel instance '" + channel.getChannelName(), status);
		this.channel = channel;
	}

	@Override
	public void channelStateChange(Channel channel, ConnectionState connectionState) {
		try {

			// introspect
			if (connectionState == ConnectionState.CONNECTED) {
				channel.getField(this, null);
			}
			else
			{
				processConnection(this);
			}

		} catch (Exception ex) {
			reportExceptionToAllReadersAndWriters(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvaccess.client.GetFieldRequester#getDone(org.epics.pvdata.pv.Status, org.epics.pvdata.pv.Field)
	 */
	@Override
	public void getDone(Status status, Field field) {
		reportStatus("Failed to instrospect channel '" + channel.getChannelName() + "'", status);
		
		if (status.isSuccess())
		{
			channelType = field;
		
			Field valueField = ((Structure)channelType).getField("value");
			if (valueField != null && valueField.getID().equals("enum_t"))
			{
				isChannelEnumType = true;
				// TODO could create a monitor just to get value.choices
			}
			else
				isChannelEnumType = false;
		}
		
		processConnection(this);
	}

	@Override
	public boolean isConnected(PVAChannelHandler channel) {
		final Channel c = channel.getChannel();
		return c != null && c.isConnected();
	}

    @Override
    protected boolean isWriteConnected(PVAChannelHandler channel) {
    	// NOTE: access-rights not yet supported
		final Channel c = channel.getChannel();
		return c != null && c.isConnected();
    }

    @Override
    public synchronized Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        if (channel != null) {
            properties.put("Channel name", channel.getChannelName());
            properties.put("Connection state", channel.getConnectionState().name());
            properties.put("Provider name", channel.getProvider().getProviderName());
            if (channel.getConnectionState() == Channel.ConnectionState.CONNECTED) {
                properties.put("Remote address", channel.getRemoteAddress());
                properties.put("Channel type", channelType.getID());
                //properties.put("Read access", channel.getReadAccess());
                //properties.put("Write access", channel.getWriteAccess());
            }
        }
        return properties;
    }

    @Override
	public void disconnect() {
		// Close the channel
		try {
			channel.destroy();
		} finally {
			channel = null;
			
			//monitor = null;
			monitorCreated.set(false);
			
			channelType = null;
			
			channelPut = null;
			channelPutValueField = null;
			channelPutCreated.set(false);
		}
	}
	
	static class WriteRequest
	{
		private final Object newValue;
		private final ChannelWriteCallback callback;

		public WriteRequest(Object newValue, ChannelWriteCallback callback) {
			this.newValue = newValue;
			this.callback = callback;
		}

		public Object getNewValue() {
			return newValue;
		}

		public ChannelWriteCallback getCallback() {
			return callback;
		}
	}
	
	private final LinkedList<WriteRequest> writeRequests = new LinkedList<WriteRequest>(); 

	@Override
	public void write(Object newValue, ChannelWriteCallback callback) {
		
		boolean wasEmpty;
		synchronized (writeRequests)
		{
			wasEmpty = writeRequests.isEmpty();
			writeRequests.add(new WriteRequest(newValue, callback));
		}
		
		if (!channelPutCreated.getAndSet(true))
		{
			channel.createChannelPut(this, isChannelEnumType ? enumPutPVRequest : standardPutPVRequest);
		}
		else if (wasEmpty)
		{
			doNextWrite();
		}
	}

	private void doNextWrite()
	{
		WriteRequest writeRequest;
		synchronized (writeRequests)
		{
			writeRequest = writeRequests.peek();
		}
		
		if (writeRequest != null)
		{
			try {
				if (channelPutValueField == null)
					throw new RuntimeException("No 'value' field");
					
				fromObject(channelPutValueField, writeRequest.getNewValue());
				channelPut.put(false);
			} catch (Exception ex) {
				writeRequests.poll();
				writeRequest.getCallback().channelWritten(ex);
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.epics.pvaccess.client.ChannelPutRequester#channelPutConnect(org.epics.pvdata.pv.Status, org.epics.pvaccess.client.ChannelPut, org.epics.pvdata.pv.PVStructure, org.epics.pvdata.misc.BitSet)
	 */
	@Override
	public void channelPutConnect(Status status, ChannelPut channelPut, PVStructure pvStructure, BitSet bitSet) {
		reportStatus("Failed to create ChannelPut instance", status);

		if (status.isSuccess())
		{
			this.channelPut = channelPut;
			
			if (isChannelEnumType)
			{
				// handle inconsistent behavior
				this.channelPutValueField = pvStructure.getSubField("value");
				if (this.channelPutValueField instanceof PVStructure)
					this.channelPutValueField = ((PVStructure)pvStructure).getSubField("index");
			}
			else
			{
				this.channelPutValueField = pvStructure.getSubField("value");
			}

			
			// set BitSet
			bitSet.clear();	// re-connect case
			if (this.channelPutValueField != null)
				bitSet.set(channelPutValueField.getFieldOffset());
		}
		
		doNextWrite();
	}

	/* (non-Javadoc)
	 * @see org.epics.pvaccess.client.ChannelPutRequester#putDone(org.epics.pvdata.pv.Status)
	 */
	@Override
	public void putDone(Status status) {
		reportStatus("Failed to put value", status);
		
		WriteRequest writeRequest;
		synchronized (writeRequests)
		{
			writeRequest = writeRequests.poll();
		}

		if (writeRequest != null)
		{
			if (status.isSuccess())
			{
				writeRequest.getCallback().channelWritten(null);
			}
			else
			{
				writeRequest.getCallback().channelWritten(new Exception(status.getMessage()));
			}
			
			doNextWrite();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.epics.pvaccess.client.ChannelPutRequester#getDone(org.epics.pvdata.pv.Status)
	 */
	@Override
	public void getDone(Status status) {
		// never used, i.e. ChannelPut.get() never called
	}

	private final static Convert convert = ConvertFactory.getConvert();
	
	// TODO check if non-V types can ever be given as newValue
	private final void fromObject(PVField field, Object newValue)
	{
		// enum support
		if (isChannelEnumType)
		{
			// value.index int field expected
			PVInt indexPutField = (PVInt)channelPutValueField;
			
			int index = -1;
			if (newValue instanceof Number)
			{
				index = ((Number)newValue).intValue();
			}
			else if (newValue instanceof String)
			{
				String nv = (String)newValue; 
				
				PVStructure lastValue = getLastMessagePayload();
				if (lastValue == null)
					throw new IllegalArgumentException("no monitor on '" + getChannelName() +"' created to get list of valid enum choices");
				
				PVStringArray pvChoices = (PVStringArray)lastValue.getSubField("value.choices");
				StringArrayData data = new StringArrayData();
				pvChoices.get(0, pvChoices.getLength(), data);
				final String[] choices = data.data;
				
				for (int i = 0; i < choices.length; i++)
				{
					if (nv.equals(choices[i]))
					{
						index = i;
						break;
					}
				}
				
				if (index == -1)
					throw new IllegalArgumentException("enumeration '" + nv +"' is not a valid choice");
			}
			
			indexPutField.put(index);
			
			return;
		}
		
        if (channelPutValueField instanceof PVScalar)
        {
	        if (newValue instanceof Double)
				convert.fromDouble((PVScalar)field, ((Double)newValue).doubleValue());
			else if (newValue instanceof Integer)
				convert.fromInt((PVScalar)field, ((Integer)newValue).intValue());
			else if (newValue instanceof String)
				convert.fromString((PVScalar)field, (String)newValue);
	        
			else if (newValue instanceof Byte)
				convert.fromByte((PVScalar)field, ((Byte)newValue).byteValue());
			else if (newValue instanceof Short)
				convert.fromShort((PVScalar)field, ((Short)newValue).shortValue());
			else if (newValue instanceof Long)
				convert.fromLong((PVScalar)field, ((Long)newValue).longValue());
			else if (newValue instanceof Float)
				convert.fromFloat((PVScalar)field, ((Float)newValue).floatValue());
    		else
    			throw new RuntimeException("Unsupported write, cannot put '" + newValue.getClass() + "' into scalar '" + channelPutValueField.getField() + "'");
        }
        else if (channelPutValueField instanceof PVScalarArray)
        {
            // if it's a ListNumber, extract the array
            if (newValue instanceof ListNumber) {
                ListNumber data = (ListNumber) newValue;
                Object wrappedArray = CollectionNumbers.wrappedArray(data);
                if (wrappedArray == null) {
                    newValue = CollectionNumbers.doubleArrayCopyOf(data);
                } else {
                    newValue = wrappedArray;
                }
            }
            else if (!newValue.getClass().isArray())
            {
            	// create an array
            	Object newValueArray = Array.newInstance(newValue.getClass(), 1);
            	Array.set(newValueArray, 0, newValue);
            	newValue = newValueArray;
            }
            
            if (newValue instanceof double[])
    			convert.fromDoubleArray((PVScalarArray)field, 0, ((double[])newValue).length, (double[])newValue, 0);
    		else if (newValue instanceof int[])
    			convert.fromIntArray((PVScalarArray)field, 0, ((int[])newValue).length, (int[])newValue, 0);
    		else if (newValue instanceof String[])
    			convert.fromStringArray((PVScalarArray)field, 0, ((String[])newValue).length, (String[])newValue, 0);
            // special case from string to array
    		else if (newValue instanceof String)
    		{
    			String str = ((String)newValue).trim();
    			
    			// remove []
    			if (str.charAt(0) == '[' && str.charAt(str.length()-1) == ']')
    				str = str.substring(1, str.length()-1);
    			
    			// split on commas and whitespaces
    			String[] splitValues = str.split("[,\\s]+");
    			convert.fromStringArray((PVScalarArray)field, 0, splitValues.length, splitValues, 0);
    		}
    		
    		else if (newValue instanceof byte[])
    			convert.fromByteArray((PVScalarArray)field, 0, ((byte[])newValue).length, (byte[])newValue, 0);
    		else if (newValue instanceof short[])
    			convert.fromShortArray((PVScalarArray)field, 0, ((short[])newValue).length, (short[])newValue, 0);
    		else if (newValue instanceof long[])
    			convert.fromLongArray((PVScalarArray)field, 0, ((long[])newValue).length, (long[])newValue, 0);
    		else if (newValue instanceof float[])
    			convert.fromFloatArray((PVScalarArray)field, 0, ((float[])newValue).length, (float[])newValue, 0);
    		else
    			throw new RuntimeException("Unsupported write, cannot put '" + newValue.getClass() + "' into array'" + channelPutValueField.getField() + "'");
        }
		else
			throw new RuntimeException("Unsupported write, cannot put '" + newValue.getClass() + "' into '" + channelPutValueField.getField() + "'");

        
	}
	


	
	@Override
	protected PVATypeAdapter findTypeAdapter(
			ValueCache<?> cache, PVAChannelHandler connection) {
		PVATypeAdapter pta = null;
		try	{
			pta = pvaTypeSupport.find(cache, connection);
		} catch (Throwable th) { th.printStackTrace(); }
		return pta;
	}

	@Override
	public void addReader(ChannelHandlerReadSubscription subscription) {
		super.addReader(subscription);
		
		if (!monitorCreated.getAndSet(true))
		{
			// TODO remove this....
			for (int i = 0; i < 100 && channel.getConnectionState() == ConnectionState.NEVER_CONNECTED; i++)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) { }
			}
			// TODO optimize fields
			channel.createMonitor(this, CreateRequestFactory.createRequest("field()", this));
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvdata.monitor.MonitorRequester#monitorConnect(org.epics.pvdata.pv.Status, org.epics.pvdata.monitor.Monitor, org.epics.pvdata.pv.Structure)
	 */
	@Override
	public void monitorConnect(Status status, Monitor monitor, Structure structure) {
		reportStatus("Failed to create monitor", status);
		
		if (status.isSuccess())
		{
			//this.monitor = monitor;
			monitor.start();
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvdata.monitor.MonitorRequester#monitorEvent(org.epics.pvdata.monitor.Monitor)
	 */
	@Override
	public void monitorEvent(Monitor monitor) {
		MonitorElement monitorElement;
		while ((monitorElement = monitor.poll()) != null)
		{
			// TODO combine bitSet, etc.... do we need to copy structure?
			processMessage(monitorElement.getPVStructure());
			monitor.release(monitorElement);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvdata.monitor.MonitorRequester#unlisten(org.epics.pvdata.monitor.Monitor)
	 */
	@Override
	public void unlisten(Monitor monitor) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String toString() {
		return "PVAChannelHandler [getChannelName()=" + getChannelName() + "]";
	}
}
