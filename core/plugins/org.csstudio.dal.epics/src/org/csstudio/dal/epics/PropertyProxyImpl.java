/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_STS_String;
import gov.aps.jca.dbr.LABELS;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.csstudio.dal.AccessType;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.EnumPropertyCharacteristics;
import org.csstudio.dal.NumericPropertyCharacteristics;
import org.csstudio.dal.PatternPropertyCharacteristics;
import org.csstudio.dal.PropertyCharacteristics;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.AbstractPropertyProxyImpl;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.MonitorProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.SyncPropertyProxy;
import org.csstudio.dal.simple.impl.DataUtil;
import org.csstudio.dal.spi.Plugs;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.util.BitCondition;

/**
 * Simulations implementations of proxy.
 *
 * @author ikriznar
 *
 */
public class PropertyProxyImpl<T> extends AbstractPropertyProxyImpl<T,EPICSPlug,MonitorProxyImpl<T>> implements
		PropertyProxy<T,EPICSPlug>, SyncPropertyProxy<T,EPICSPlug>, DirectoryProxy<EPICSPlug>,
		ConnectionListener, GetListener {

	/** C_CONDITION_WHEN_CLEARED characteristic for pattern channel */
	public static BitCondition[] patternWhenCleared = new BitCondition[] {
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED
		};

	/** C_CONDITION_WHEN_SET characteristic for pattern channel */
	public static BitCondition[] patternWhenSet = new BitCondition[] {
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK
		};

	/** C_BIT_DESCRIPTION characteristic for pattern channel */
	public static String[] patternBitDescription = new String[] {
			"bit 0", "bit 1", "bit 2", "bit 3", "bit 4", "bit 5", "bit 6",
			"bit 7", "bit 8", "bit 9", "bit 10", "bit 11", "bit 12", "bit 13",
			"bit 14", "bit 15"
		};

	/** C_BIT_MASK characteristic for pattern channel */
	public static BitSet patternBitMask = new BitSet(16);
	{
		patternBitMask.set(0, 16);
	}

	protected Channel channel;

	protected String condDesc;

	protected DBRType type;

	protected Class<T> dataType;

	protected int elementCount;

	private ThreadPoolExecutor executor;

	private boolean initializeCharacteristicsRunning = false;

	// This task changes channel with INITIAL state to CONNECTION_FAILED.
	private class AbortConnectionRunnable implements Runnable {
		@Override
        public void run() {
			final ConnectionState cs = getConnectionState();
			if (cs == ConnectionState.CONNECTING) {
				synchronized (abortConnectionTask) {
					if (connectionStateMachine.isConnecting()) {
						setConnectionState(
								ConnectionState.CONNECTION_FAILED,
								new RemoteException(
										PropertyProxyImpl.this,
										"Timeout '"+Plugs.getInitialConnectionTimeout(plug.getConfiguration())+"ms' while connecting!"));
					}
				}
			}
		}
	}
	private TimerTask abortConnectionTask = null;
	//private boolean abortConnection = false;

	/**
	 * Create a new proprty instance (channel).
	 * @param plug plug hosting this property.
	 * @param name name of the property.
	 * @param dataType java data type to work with.
	 * @param type channel type to work with.
	 * @throws RemoteException thrown on failure.
	 */
	public PropertyProxyImpl(final EPICSPlug plug, final String name, final Class<T> dataType, final DBRType type) throws RemoteException {
		super(name,plug);

		if (type.getValue() >= DBR_STS_String.TYPE.getValue()) {
            throw new IllegalArgumentException("type must be value-only type");
        }

		synchronized (this) {
			this.type = type;
			this.dataType = dataType;
			setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE, DynamicValueState.NO_VALUE)));
			setConnectionState(ConnectionState.READY,null);
			setConnectionState(ConnectionState.CONNECTING,null);
			// create channel
			try {
				this.channel = plug.getContext().createChannel(name, this);
			} catch (final Throwable th) {
				throw new RemoteException(this, "Failed create CA channel: "+PlugUtilities.toShortErrorReport(th), th);
			}
			abortConnectionTask = plug.schedule(new AbortConnectionRunnable(), Plugs.getInitialConnectionTimeout(plug.getConfiguration()), 0);
		}


	}

	/*
	 * @see org.csstudio.dal.proxy.AbstractProxyImpl#destroy()
	 */
	@Override
	public synchronized void destroy() {

		if (connectionStateMachine.isConnected()) {
			setConnectionState(ConnectionState.DISCONNECTING,null);
		}

		super.destroy();

		if (channel.getConnectionState() != Channel.CLOSED) { // FIXME workaround because CAJChannel.removeConnectionListener throws IllegalStateException: "Channel closed."
			try {
				channel.removeConnectionListener(this);
			} catch (final IllegalStateException e) {
				// we ignore
			} catch (final CAException e) {
				Logger.getLogger(this.getClass()).warn("Removing CA listener: "+PlugUtilities.toShortErrorReport(e), e);
			}
		}
		// destory channel
		channel.dispose();

		if (connectionStateMachine.getConnectionState()==ConnectionState.DISCONNECTING) {
			setConnectionState(ConnectionState.DISCONNECTED,null);
		}
		setConnectionState(ConnectionState.DESTROYED,null);
	}

	/*
	 * @see org.csstudio.dal.proxy.PropertyProxy#getValueAsync(org.csstudio.dal.ResponseListener)
	 */
	@Override
    public Request<T> getValueAsync(final ResponseListener<T> callback)
			throws DataExchangeException {
		final GetRequest<T> r = new GetRequest<T>(this, callback);
		try {
			channel.get(type, channel.getElementCount(), r);
			plug.flushIO();
		} catch (final Exception e) {
			r.addResponse(new ResponseImpl<T>(this, r, null, "value", false, e,
					getCondition(), null, true));
		}
		return r;
	}

	/*
	 * @see org.csstudio.dal.proxy.PropertyProxy#setValueAsync(T, org.csstudio.dal.ResponseListener)
	 */
	@Override
    public Request<T> setValueAsync(final T value, final ResponseListener<T> callback)
			throws DataExchangeException {
		final PutRequest<T> r = new PutRequest<T>(this, callback, value);
		try {
			final Object o = PlugUtilities.toDBRValue(value, channel.getFieldType());
			if (channel instanceof CAJChannel) {
                ((CAJChannel) channel).put(PlugUtilities.toDBRType(value.getClass()), Array.getLength(o), o, r);
            } else {
				// TODO workaround until Channel supports put(DBRType, int, Object, PutListener)
				PlugUtilities.put(channel, o, r);
			}
			plug.flushIO();
		} catch (final Exception e) {
			r.addResponse(new ResponseImpl<T>(this, r, value, "value", false, e,
					getCondition(), null, true));
		}
		return r;
	}

	/**
	 * Get listener implementation to implement sync. get.
	 */
	private class GetListenerImpl implements GetListener {
		volatile GetEvent event = null;

		@Override
        public synchronized void getCompleted(final GetEvent ev) {
			event = ev;
			this.notifyAll();
		}
	}
	/**
	 * Connection listener implementation to implement sync. get.
	 */
	private class ConnectionListenerImpl implements ConnectionListener {
		//volatile ConnectionEvent event= null;

		@Override
        public synchronized void connectionChanged(final ConnectionEvent arg0) {
			//event=arg0;
			this.notifyAll();
		}

	}

	/*
	 * @see org.csstudio.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	@Override
    public T getValueSync() throws DataExchangeException {
		try
		{

			final GetListenerImpl listener = new GetListenerImpl();
	         synchronized (listener) {
				channel.get(type, channel.getElementCount(), listener);
				plug.flushIO();

				try {
					listener.wait((long) (plug.getTimeout() * 1000));
				} catch (final InterruptedException e) {
					// noop
				}
			}

			final GetEvent event = listener.event;
			if (event == null) {
                throw new TimeoutException("Get timeout.");
            }

			// status check
			if (event.getStatus() != CAStatus.NORMAL) {
                throw new CAStatusException(event.getStatus(), "Get failed.");
            }

			// sanity check
			if (event.getDBR() == null) {
                throw new DataExchangeException(this, "Get failed.");
            }

			return toJavaValue(event.getDBR());
		} catch (final CAException e) {
			throw new DataExchangeException(this, "Get failed: "+PlugUtilities.toShortErrorReport(e), e);
		} catch (final TimeoutException e) {
			throw new DataExchangeException(this, "Get failed with timeout.", e);
		}
	}

	/*
	 * @see org.csstudio.dal.proxy.SyncPropertyProxy#setValueSync(java.lang.Object)
	 */
	@Override
    public void setValueSync(final Object value) throws DataExchangeException {
		try {
			final Object o = PlugUtilities.toDBRValue(value, channel.getFieldType());
			if (channel instanceof CAJChannel) {
                ((CAJChannel) channel).put(PlugUtilities.toDBRType(value.getClass()), Array.getLength(o), o);
            } else {
				// TODO workaround until Channel supports put(DBRType, int, Object)
				PlugUtilities.put(channel, o);
			}
			// put does not affect on pendIO
			plug.flushIO();
		} catch (final CAException e) {
			throw new DataExchangeException(this, "Set failed: "+PlugUtilities.toShortErrorReport(e), e);
		}
	}


	/*
	 * @see org.csstudio.dal.proxy.PropertyProxy#isSettable()
	 */
	@Override
    public boolean isSettable() {
		return channel.getWriteAccess();
	}

	/*
	 * @see org.csstudio.dal.proxy.PropertyProxy#createMonitor(org.csstudio.dal.ResponseListener)
	 */
	@Override
    public synchronized MonitorProxy createMonitor(final ResponseListener<T> callback, final Map<String,Object> param)
			throws RemoteException {

		if (getConnectionState() == ConnectionState.DESTROYED) {
            throw new RemoteException(this, "Proxy destroyed.");
        }
		try {
			final MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(plug, this, callback, param);
			return m;
		} catch (final Throwable th) {
			throw new RemoteException(this, "Failed to create new monitor for " + this.getUniqueName() + ": " + PlugUtilities.toShortErrorReport(th), th);
		}
	}

	/**
	 * Characteristics async get listener.
	 * @see gov.aps.jca.event.GetListener#getCompleted(gov.aps.jca.event.GetEvent)
	 */
	@Override
    public void getCompleted(final GetEvent ev) {
		if (!connectionStateMachine.isConnected()
				|| channel.getConnectionState()!= Channel.CONNECTED)
		{
			/*
			 * It could happen that SimpleDAL broker does simple get and then destroys connection before CTRL_DBR request finishes.
			 * In this case CTRL_DBR has nothing to do any more.
			 */
			return;
		}
		if (ev.getStatus() == CAStatus.NORMAL && ev.getDBR()!=null) {
            createCharacteristics(ev.getDBR());
        } else if (ev.getDBR() == null) {
			recoverFromNullDbr();
		}

	}

	/**
	 * Creates default characteristics.
	 */
	protected void createDefaultCharacteristics() {
		synchronized (getCharacteristics()) {

			updateCharacteristic(PropertyCharacteristics.C_DESCRIPTION, "EPICS Channel '" + name + "'");
			updateCharacteristic(PropertyCharacteristics.C_DISPLAY_NAME, name);
			updateCharacteristic(PropertyCharacteristics.C_POSITION, new Double(0));
			updateCharacteristic(PropertyCharacteristics.C_PROPERTY_TYPE, "property");
			updateCharacteristic(NumericPropertyCharacteristics.C_SCALE_TYPE, "linear");

			updateCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH, new Integer(elementCount));

			if (channel != null
					&& channel.getConnectionState() == Channel.CONNECTED
					&& getConnectionState() == ConnectionState.CONNECTED) {

				try {

					final DBRType ft= channel.getFieldType();
					updateCharacteristic("fieldType",ft);

					if (ft.isENUM()) {
						updateCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION, 0xF);
					} else if (ft.isBYTE()) {
						updateCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION, 0x8);
					} else if (ft.isSHORT()) {
						updateCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION, 0xFF);
					} else {
						updateCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION, 0xFFFF);
					}

					updateCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE,AccessType.getAccess(channel.getReadAccess(),channel.getWriteAccess()));
					updateCharacteristic(PropertyCharacteristics.C_HOSTNAME, channel.getHostName());
					updateCharacteristic(EpicsPropertyCharacteristics.EPICS_NUMBER_OF_ELEMENTS, channel.getElementCount());

				} catch (final IllegalStateException ex) {
					/*
					 * JCA channel was probably closed in the mean time,
					 * nothing to do.
					 */

					updateCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION, 0xFFFF);

					updateCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE,AccessType.NONE);
					updateCharacteristic(PropertyCharacteristics.C_HOSTNAME,"unknown");
					updateCharacteristic(EpicsPropertyCharacteristics.EPICS_NUMBER_OF_ELEMENTS,1);
				}

			} else {

				updateCharacteristic(NumericPropertyCharacteristics.C_RESOLUTION, 0xFFFF);

				updateCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE,AccessType.NONE);
				updateCharacteristic(PropertyCharacteristics.C_HOSTNAME,"unknown");
				updateCharacteristic(EpicsPropertyCharacteristics.EPICS_NUMBER_OF_ELEMENTS,1);
			}

			updateCharacteristic(PropertyCharacteristics.C_DATATYPE,PlugUtilities.getDataType(null));

			//characteristics.put(NumericPropertyCharacteristics.C_SCALE_TYPE, );

			updateCharacteristic(PatternPropertyCharacteristics.C_CONDITION_WHEN_SET, patternWhenSet);
			updateCharacteristic(PatternPropertyCharacteristics.C_CONDITION_WHEN_CLEARED, patternWhenCleared);

			updateCharacteristic(PatternPropertyCharacteristics.C_BIT_MASK, patternBitMask);
			updateCharacteristic(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS, patternBitDescription);

		}
	}

	/*private void abortInitalDBR() {
		synchronized (characteristics) {
			characteristics.notifyAll();
			initializeCharacteristicsRunning = false;
		}
	}*/

	/**
	 * Creates characteristics from given DBR.
	 * @param dbr DBR containign characteristics.
	 */
	protected void createCharacteristics(final DBR dbr)
	{
		synchronized (getCharacteristics()) {

			if (channel ==null || channel.getConnectionState()!= Channel.CONNECTED) {
				/*
				 * It could happen that SimpleDAL broker does simple get and then destroys connection before CTRL_DBR request finishes.
				 * In this case CTRL_DBR has nothing to do any more.
				 */
				return;
			}

//			System.out.println(">>> "+name+" Creating characteristics from DBR");


			updateCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE,channel != null ? AccessType.getAccess(channel.getReadAccess(),channel.getWriteAccess()) : AccessType.NONE);
			updateCharacteristic(PropertyCharacteristics.C_HOSTNAME,channel != null ? channel.getHostName() : "unknown");
			updateCharacteristic(EpicsPropertyCharacteristics.EPICS_NUMBER_OF_ELEMENTS, channel != null ? channel.getElementCount() : 1);
			updateCharacteristic(PropertyCharacteristics.C_DATATYPE,PlugUtilities.getDataType(dbr.getType()));

			updateCharacteristicsWithDBR(dbr,false);

			DynamicValueCondition condition=null;
			if(dbr.isSTS()) {
				condition = deriveNewConditionWithDBR((STS)dbr);
			}

			createSpecificCharacteristics(dbr);

			if (condition==null) {
				updateConditionWith(DynamicValueCondition.METADATA_AVAILABLE_MESSAGE, DynamicValueState.HAS_METADATA);
			} else {
				condition.getStates().add(DynamicValueState.HAS_METADATA);
				setCondition(condition);
			}

//			System.out.println(">>> "+name+" characteristics from DBR "+getCharacteristics());

			getCharacteristics().notifyAll();
			initializeCharacteristicsRunning = false;
		}
	}

	protected void updateCharacteristicsWithDBR(final DBR dbr, final boolean changeOnly)
	{
		synchronized (getCharacteristics()) {

			boolean change = false;

			if (dbr.isCTRL())
			{
				final CTRL gr = (CTRL)dbr;
				change |= updateCharacteristic(NumericPropertyCharacteristics.C_UNITS, gr.getUnits());
				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_UNITS, gr.getUnits());

				// Integer -> Long needed here
				if (dbr.isINT())
				{
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_MINIMUM, new Long(gr.getLowerCtrlLimit().longValue()));
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_MAXIMUM, new Long(gr.getUpperCtrlLimit().longValue()));

					change |= updateCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MIN, new Long(gr.getLowerDispLimit().longValue()));
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MAX, new Long(gr.getUpperDispLimit().longValue()));

					change |= updateCharacteristic(NumericPropertyCharacteristics.C_WARNING_MIN, new Long(gr.getLowerWarningLimit().longValue()));
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_WARNING_MAX, new Long(gr.getUpperWarningLimit().longValue()));

					change |= updateCharacteristic(NumericPropertyCharacteristics.C_ALARM_MIN, new Long(gr.getLowerAlarmLimit().longValue()));
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_ALARM_MAX, new Long(gr.getUpperAlarmLimit().longValue()));


				}
				else
				{
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_MINIMUM, gr.getLowerCtrlLimit());
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_MAXIMUM, gr.getUpperCtrlLimit());

					change |= updateCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MIN, gr.getLowerDispLimit());
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_GRAPH_MAX, gr.getUpperDispLimit());

					change |= updateCharacteristic(NumericPropertyCharacteristics.C_WARNING_MIN, gr.getLowerWarningLimit());
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_WARNING_MAX, gr.getUpperWarningLimit());

					change |= updateCharacteristic(NumericPropertyCharacteristics.C_ALARM_MIN, gr.getLowerAlarmLimit());
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_ALARM_MAX, gr.getUpperAlarmLimit());
				}

				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_MIN, getCharacteristics().get(NumericPropertyCharacteristics.C_MINIMUM));
				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_MAX, getCharacteristics().get(NumericPropertyCharacteristics.C_MAXIMUM));

				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_OPR_MIN, getCharacteristics().get(NumericPropertyCharacteristics.C_GRAPH_MIN));
				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_OPR_MAX, getCharacteristics().get(NumericPropertyCharacteristics.C_GRAPH_MAX));

				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_WARNING_MAX, getCharacteristics().get(NumericPropertyCharacteristics.C_WARNING_MAX));
				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_WARNING_MIN, getCharacteristics().get(NumericPropertyCharacteristics.C_WARNING_MIN));

				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_ALARM_MAX, getCharacteristics().get(NumericPropertyCharacteristics.C_ALARM_MAX));
				change |= updateCharacteristic(EpicsPropertyCharacteristics.EPICS_ALARM_MIN, getCharacteristics().get(NumericPropertyCharacteristics.C_ALARM_MIN));

			} else {
				if (!changeOnly) {
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_UNITS, "N/A");
				}
			}

			if (!changeOnly) {
				if (dbr.isPRECSION())
				{
					final int precision = ((PRECISION)dbr).getPrecision();
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_FORMAT, "%."  + precision + "f");
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_PRECISION, precision);
				} else if (dbr.isSTRING()) {
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_FORMAT, "%s");
				} else {
					change |= updateCharacteristic(NumericPropertyCharacteristics.C_FORMAT, "%d");
				}

				if (dbr.isLABELS())
				{
					final String[] labels = ((LABELS)dbr).getLabels();
					change |= updateCharacteristic(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS, labels);
					change |= updateCharacteristic(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS, labels);

					// create array of values (Long values)
					final Object[] values = new Object[labels.length];
					for (int i = 0; i < values.length; i++) {
						values[i] = new Long(i);
					}

					change |= updateCharacteristic(EnumPropertyCharacteristics.C_ENUM_VALUES, values);

//					updateCharacteristic(CharacteristicInfo.C_META_DATA.getName(), DataUtil.createEnumeratedMetaData(labels,values));

				}
			}

			if (change) {
				updateCharacteristic(CharacteristicInfo.C_META_DATA.getName(), DataUtil.createMetaData(getCharacteristics()));
			}

		}
	}

	protected void createSpecificCharacteristics(final DBR dbr) {
		// specific proxy implementation may override this and provide own characteristic initialization
	}

	/**
	 * Initiate characteristics search.
	 */
	protected void initializeCharacteristics()
	{
		synchronized (getCharacteristics()) {
			if (!connectionStateMachine.isConnected()
					|| channel.getConnectionState() != Channel.CONNECTED)
			{
				return;
			}

//			System.out.println(">>> "+name+" initialize started");

			if (initializeCharacteristicsRunning) {
                return;
            }
			initializeCharacteristicsRunning = true;

			// convert to CTRL value
			characteristicsRequestTimestamp = System.currentTimeMillis();
			try {
				elementCount = channel.getElementCount();

				createDefaultCharacteristics();

				final int CTRL_OFFSET = 28;
				final DBRType ctrlType = DBRType.forValue(type.getValue() + CTRL_OFFSET);
				channel.get(ctrlType, 1, this);
				plug.flushIO();
			} catch (final Throwable th) {
				if (!connectionStateMachine.isConnected()
						|| channel.getConnectionState() != Channel.CONNECTED)
				{
					return;
				}
				createDefaultCharacteristics();
				updateConditionWith("Meta data request failed: "+PlugUtilities.toShortErrorReport(th), DynamicValueState.ERROR);
				synchronized (getCharacteristics()) {
					getCharacteristics().notifyAll();
				}
			}
		}
	}

	protected static final long CHARACTERISTICS_TIMEOUT = 5000;
	protected long characteristicsRequestTimestamp = System.currentTimeMillis();

	/*
	 * @see DirectoryProxy#getCharacteristicNames()
	 */
	@Override
    public String[] getCharacteristicNames() throws DataExchangeException {
		synchronized (getCharacteristics())
		{
			// characteristics not initialized yet... wait
			if (getCharacteristics().size() == 0)
			{
				initializeCharacteristics();
				final long timeToWait = CHARACTERISTICS_TIMEOUT - (System.currentTimeMillis() - characteristicsRequestTimestamp);
				if (timeToWait > 0)
				{
					try {
						getCharacteristics().wait(timeToWait);
					} catch (final InterruptedException e) {
						// noop
					}
				}

			}

			// get names
			final String[] names = new String[getCharacteristics().size()];
			getCharacteristics().keySet().toArray(names);
			return names;
		}
	}

	@Override
	protected Object processCharacteristicBeforeCache(Object value,
			final String characteristicName)
	{
		if (value!=null) {
			return value;
		}
		synchronized (getCharacteristics())
		{
//			System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process before size "+getCharacteristics().size());
			// characteristics not iniialized yet... wait
			if (getCharacteristics().size() == 0)
			{
				initializeCharacteristics();
				final long timeToWait = CHARACTERISTICS_TIMEOUT +100 - (System.currentTimeMillis() - characteristicsRequestTimestamp);
//				System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process before wait "+timeToWait);
				if (timeToWait > 0)
				{
					try {
						getCharacteristics().wait(timeToWait);
					} catch (final InterruptedException e) {
						// noop
					}
				}
				value= getCharacteristics().get(characteristicName);
			}
		}
//		System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process before wait done");
		return value;
	}

	@Override
	protected Object processCharacteristicAfterCache(Object value,
			final String characteristicName)
	{
		if (value==null && initializeCharacteristicsRunning) {
//			System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process after");
			synchronized (getCharacteristics()) {
				if (initializeCharacteristicsRunning) {
					final long timeToWait = CHARACTERISTICS_TIMEOUT + 100 - (System.currentTimeMillis() - characteristicsRequestTimestamp);
//					System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process after wait "+timeToWait);
					if (timeToWait > 0)
					{
						try {
							getCharacteristics().wait(timeToWait);
						} catch (final InterruptedException e) {
							// noop
						}
					}
					value= getCharacteristics().get(characteristicName);
				}
			}
		}
//		System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process after wait done");
		if (value == null && characteristicName.length() <= 4) {
			value = getCharacteristicFromField(characteristicName);
			if (value!=null) {
				synchronized (getCharacteristics()) {
					updateCharacteristic(characteristicName, value);
				}
			}
//			System.out.println(">>> "+name+" char "+characteristicName+" "+value+" process after from field");
		}
		return value;
	}

	private Object getCharacteristicFromField(final String characteristicName) {
		if (channel.getConnectionState() != Channel.CONNECTED) {
            return null;
        }

		final GetListenerImpl listener = new GetListenerImpl();
        synchronized (listener) {
        	try {
        		CAJChannel ch=null;
        		final ConnectionListenerImpl conn= new ConnectionListenerImpl();
        		synchronized (conn) {
    				ch = (CAJChannel)plug.getContext().createChannel(name+"."+characteristicName,conn);
    				if (ch.getConnectionState() != Channel.CONNECTED) {
	    				try {
	    					conn.wait((long) (plug.getTimeout() * 1000));
	    				} catch (final InterruptedException e) {
	    					// noop
	    				}
    				}
				}
				ch.get(1, listener);
				plug.flushIO();
				try {
					listener.wait((long) (plug.getTimeout() * 1000));
				} catch (final InterruptedException e) {
					// noop
				}
				ch.dispose();
			} catch (final IllegalStateException e1) {
				Logger.getLogger(this.getClass()).warn("Characteristic failed.", e1);
			} catch (final CAException e1) {
				Logger.getLogger(this.getClass()).warn("Characteristic failed: "+PlugUtilities.toShortErrorReport(e1), e1);
			}
		}

		final GetEvent event = listener.event;
		if (event == null || event.getStatus() != CAStatus.NORMAL || event.getDBR() == null) {
			return null;
		}

		return event.getDBR().getValue();
	}


	@Override
	protected void handleCharacteristicsReponses(final String[] characteristics,
			final ResponseListener<Object> callback,
			final RequestImpl<Object> request)
	{

		final Runnable getCharsAsync = new Runnable () {

			@Override
            public void run() {
				handleCharacteristicsReponsesSync(characteristics, callback, request);
			}

		};
		execute(getCharsAsync);
	}

	/**
	 * Convert DBR to Java value.
	 * @param dbr DBR to convert.
	 * @return converted Java value.
	 */
	public final T toJavaValue(final DBR dbr) {
		return PlugUtilities.toJavaValue(dbr, dataType, channel.getFieldType());
	}

	/**
	 * Get CA channel.
	 * @return channel.
	 */
	protected Channel getChannel() {
		return channel;
	}

	/**
	 * Get DBR type (used to query data).
	 * @return DBR type.
	 */
	protected DBRType getType() {
		return type;
	}

	/**
	 * Update conditions.
	 * @param dbr status DBR.
	 */
	public void updateConditionWithDBR(final DBR dbr) {
		if (dbr== null  || !dbr.isSTS()) {
			return;
		}
		final STS sts= (STS)dbr;
		final DynamicValueCondition cond= deriveNewConditionWithDBR(sts);
		setCondition(cond);
		if (plug.isDbrUpdatesCharacteristics()) {
			synchronized (getCharacteristics()) {
				updateCharacteristic(
						CharacteristicInfo.C_SEVERITY.getName()
						,getLocalProxyCharacteristic(CharacteristicInfo.C_SEVERITY.getName()));
				updateCharacteristic(
						CharacteristicInfo.C_STATUS.getName()
						,getLocalProxyCharacteristic(CharacteristicInfo.C_STATUS.getName()));
				updateCharacteristic(
						CharacteristicInfo.C_TIMESTAMP.getName()
						,getLocalProxyCharacteristic(CharacteristicInfo.C_TIMESTAMP.getName()));
				updateCharacteristicsWithDBR(dbr,true);
			}
		}
	}

	/**
	 * Creates copy of current condition condition .
	 *
	 * @param dbr status DBR.
	 */
	private DynamicValueCondition deriveNewConditionWithDBR(final STS dbr) {

		final Status st = dbr.getStatus();
		final Severity se = dbr.getSeverity();
		EnumSet<DynamicValueState> states = getCondition().getStates();

		final boolean change=
			se == Severity.NO_ALARM && !states.contains(DynamicValueState.NORMAL) ||
			se == Severity.MINOR_ALARM && !states.contains(DynamicValueState.WARNING) ||
			se == Severity.MAJOR_ALARM && !states.contains(DynamicValueState.ALARM) ||
			se == Severity.INVALID_ALARM && !states.contains(DynamicValueState.ERROR);

		if (!change) {
			return null;
		}

		condDesc = st.getName();
		states = EnumSet.copyOf(getCondition().getStates());

		if (se == Severity.NO_ALARM) {
			states.add(DynamicValueState.NORMAL);
			states.remove(DynamicValueState.WARNING);
			states.remove(DynamicValueState.ALARM);
			states.remove(DynamicValueState.ERROR);
		} else if (se == Severity.MINOR_ALARM) {
			states.remove(DynamicValueState.NORMAL);
			states.add(DynamicValueState.WARNING);
			states.remove(DynamicValueState.ALARM);
			states.remove(DynamicValueState.ERROR);
		} else if (se == Severity.MAJOR_ALARM) {
			states.remove(DynamicValueState.NORMAL);
			states.remove(DynamicValueState.WARNING);
			states.add(DynamicValueState.ALARM);
			states.remove(DynamicValueState.ERROR);
		} else if (se == Severity.INVALID_ALARM) {
			states.remove(DynamicValueState.NORMAL);
			states.remove(DynamicValueState.WARNING);
			states.remove(DynamicValueState.ALARM);
			states.add(DynamicValueState.ERROR);
		}

		Timestamp timestamp = null;
		//((TIME)dbr).getTimeStamp() != null - could happen
		if (dbr instanceof TIME && ((TIME)dbr).getTimeStamp() != null) {
			timestamp = PlugUtilities.convertTimestamp(((TIME) dbr).getTimeStamp());
		}

		return new DynamicValueCondition(states, timestamp, condDesc);

	}

	/*
	 * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
	 */
	@Override
    public synchronized void connectionChanged(final ConnectionEvent event) {
		if (abortConnectionTask != null) {
			abortConnectionTask.cancel();
		}

		// this prevented the proxy from ever connecting
//		if (abortConnection) return;

		final Runnable connChangedRunnable = new Runnable () {

			@Override
            public void run() {
//				 Maps JCA states to DAL states
				final gov.aps.jca.Channel.ConnectionState c= channel.getConnectionState();
				if (c==null) {
					Logger.getLogger(PropertyProxyImpl.class).debug(PropertyProxyImpl.class.getName()+": JCA connection state for "+channel.getName()+" is NULL, connection event ignored!");
					return;
				}
				if (c == gov.aps.jca.Channel.ConnectionState.CLOSED) {
					setConnectionState(ConnectionState.DESTROYED,null);
				} else if (c == gov.aps.jca.Channel.ConnectionState.CONNECTED) {
					if (abortConnectionTask!=null) {
						synchronized (abortConnectionTask) {
							setConnectionState(ConnectionState.CONNECTED,null);
						}
					} else {
						setConnectionState(ConnectionState.CONNECTED,null);
					}
					if (plug.isInitializeCharacteristicsOnConnect()) {
						synchronized (getCharacteristics()) {
							if (getCharacteristics().size() == 0) {
								initializeCharacteristics();
							}
						}
					}
				} else if (c == gov.aps.jca.Channel.ConnectionState.DISCONNECTED) {
					setConnectionState(ConnectionState.CONNECTION_LOST,null);
				} else if (c == gov.aps.jca.Channel.ConnectionState.NEVER_CONNECTED) {
					setConnectionState(ConnectionState.CONNECTING,null);
				}
			}

		};

		if (getPlug().getMaxThreads() == 0) {
			execute(connChangedRunnable);
		} else if (!getExecutor().isShutdown()) {
			execute(connChangedRunnable);
		}
	}

	/*
	 * @see org.csstudio.dal.proxy.AbstractProxyImpl#setConnectionState(org.csstudio.dal.context.ConnectionState)
	 */
	@Override
	public void setConnectionState(final ConnectionState s, final Throwable error) {
		super.setConnectionState(s, error);
		if (s == ConnectionState.DESTROYED) {
			if (getPlug().getMaxThreads() != 0 && !getPlug().isUseCommonExecutor()) {
				getExecutor().shutdown();
		        try {
		            if (!getExecutor().awaitTermination(1, TimeUnit.SECONDS)) {
                        getExecutor().shutdownNow();
                    }
		        } catch (final InterruptedException ie) {  }
			}
		}
	}

	/*
	 * @see org.csstudio.dal.proxy.DirectoryProxy#refresh()
	 */
	@Override
    public void refresh() {
		initializeCharacteristics();
	}

	/**
	 * Executes a <code>Runnable</code>. The <code>Runnable</code> is run in the same thread if
	 * {@link EPICSPlug#PROPERTY_MAX_THREADS} is equal to 0. Otherwise it is delegated to the
	 * <code>Executor</code> ({@link #getExecutor()}).
	 *
	 * @param r the <code>Runnable</code> to run
	 */
	protected void execute(final Runnable r) {
		if (getPlug().getMaxThreads() > 0) {
			getExecutor().execute(r);
		}
		else {
			r.run();
		}
	}

	/**
	 * This method should be called only if {@link EPICSPlug#PROPERTY_MAX_THREADS} is
	 * a number greater than 0.
	 * <p>
	 * If {@link EPICSPlug#PROPERTY_USE_COMMON_EXECUTOR} is set to <code>true</code> the
	 * <code>Executor</code> from {@link EPICSPlug#getExecutor()} is returned. Otherwise
	 * a new </code>ThreadPoolExecutor</code> is created.
	 * </p>
	 *
	 * @return the executor
	 * @throws IllegalStateException if maximum number of threads defined by {@link EPICSPlug}
	 * is equal to 0.
	 */
	public ThreadPoolExecutor getExecutor() {
		if (executor==null) {
			synchronized (this) {
				if (getPlug().getMaxThreads() == 0) {
                    throw new IllegalStateException("Maximum number of threads must be greater than 0.");
                }
				if (getPlug().isUseCommonExecutor()) {
                    executor = getPlug().getExecutor();
                } else {
					executor= new ThreadPoolExecutor(getPlug().getCoreThreads(),getPlug().getMaxThreads(),Long.MAX_VALUE, TimeUnit.NANOSECONDS,
			                new LinkedBlockingQueue<Runnable>());
					executor.prestartAllCoreThreads();
				}
				executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

					@Override
                    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
//						plug.getLogger().warn("ThreadPoolExecutor has rejected the execution of a runnable.");
					}
				});
			}
		}
		return executor;
	}

	private static boolean equal(final String s1, final String s2) {
		if (s1 == null || s2 == null) {
			if (s1 == s2) {
				return true;
			}
			return false;
		}
		return s1.equals(s2);
	}

	private boolean fallbackInProgress = false;
	private final GetListener fallbackListener = new GetListener() {

		@Override
        public void getCompleted(final GetEvent ev) {
			if (!connectionStateMachine.isConnected()
					|| channel.getConnectionState()!= Channel.CONNECTED)
			{
				/*
				 * It could happen that SimpleDAL broker does simple get and then destroys connection before CTRL_DBR request finishes.
				 * In this case CTRL_DBR has nothing to do any more.
				 */
				return;
			}

			try {
				final DBR dbr = ev.getDBR();
				if (dbr == null) {
                    return;
                }

				createCharacteristics(dbr);

				/*
				T defaultValue = PlugUtilities.defaultValue(dataType);
				if (isMonitorListCreated()) {
					synchronized (getMonitors()) {
						for (MonitorProxyImpl<T> monitor : getMonitors()) {
							monitor.addFallbackResponse(defaultValue);
						}
					}
				}*/
			} catch (final Throwable t) {
				plug.getLogger().warn("Recovery from null DBR failed.", t);
			} finally {
				fallbackInProgress = false;
			}
		}
	};

	protected void recoverFromNullDbr() {
		synchronized (fallbackListener) {
			if (fallbackInProgress) {
                return;
            }
			fallbackInProgress = true;
		}
		getExecutor().execute(new Runnable() {
			@Override
            public void run() {

				try {
					plug.getLogger().warn("Received NULL DBR, trying again with reovery procedure.");
					getChannel().get(DBRType.CTRL_STRING, 1, fallbackListener);
					plug.flushIO();
				} catch (final Throwable e) {
					plug.getLogger().warn("Recovery from null DBR failed.", e);
					fallbackInProgress = false;
				}

			}
		});
	}

	@Override
	protected String getRemoteHostInfo() {
		if (channel!=null) {
			return channel.getHostName();
		}
		return super.getRemoteHostInfo();
	}

}
