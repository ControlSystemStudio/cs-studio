package org.csstudio.platform.internal.simpledal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.internal.simpledal.converters.ConverterUtil;
import org.csstudio.platform.internal.simpledal.local.LocalChannelPool;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkAdapter;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * Standard implementation of {@link IProcessVariableConnectionService}.
 * 
 * This service is stateful as it needs to track open connections to the control
 * system.
 * 
 * @author Sven Wende
 * 
 * TODO: Schreiben von Werten ermöglichen!
 * 
 * TODO: Sync/Async Lesen von Werten (x)
 * 
 * TODO: Ping-Funktion
 * 
 * 
 * TODO: SDS auf diesen Service umstellen
 * 
 */
public class ProcessVariableConnectionService implements
		IProcessVariableConnectionService {

	private Map<MapKey, AbstractConnector> _connectors;

	/**
	 * All DAL connectors that have been created.
	 */
	// private Map<IProcessVariableAddress, DalConnector> _dalConnectors;
	/**
	 * A cleanup thread which disposes unnecessary connections.
	 */
	private Thread _cleanupThread;

	/**
	 * The singleton instance.
	 */
	private static IProcessVariableConnectionService _instance;

	/**
	 * Constructor.
	 */
	ProcessVariableConnectionService() {
		_connectors = new HashMap<MapKey, AbstractConnector>();
		_cleanupThread = new CleanupThread();
	}

	public static IProcessVariableConnectionService getInstance() {
		if (_instance == null) {
			_instance = new ProcessVariableConnectionService();
		}

		return _instance;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			long value) {
		return doSetValue(processVariableAddress, ValueType.LONG, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			long[] value) {
		return doSetValue(processVariableAddress, ValueType.LONG_SEQUENCE,
				value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			double value) {
		return doSetValue(processVariableAddress, ValueType.DOUBLE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			double[] value) {
		return doSetValue(processVariableAddress, ValueType.DOUBLE_SEQUENCE,
				value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			String value) {
		return doSetValue(processVariableAddress, ValueType.STRING, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			String[] value) {
		return doSetValue(processVariableAddress, ValueType.STRING_SEQUENCE,
				value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Object value, ValueType expectedValueType) {
		return doSetValue(processVariableAddress, expectedValueType, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Object[] value) {
		return doSetValue(processVariableAddress, ValueType.OBJECT_SEQUENCE,
				value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean setValue(IProcessVariableAddress processVariableAddress,
			Enum value) {
		return doSetValue(processVariableAddress, ValueType.ENUM, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsync(
			final IProcessVariableAddress processVariableAddress,
			final ValueType valueType,
			final IProcessVariableValueListener<Double> listener) {

		Runnable r = new Runnable() {
			public void run() {
				doGetCurrentValueAsync(processVariableAddress, valueType,
						listener);
			}
		};

		Thread t = new Thread(r);
		t.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsDouble(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Double> listener) {
		doGetCurrentValueAsync(processVariableAddress, ValueType.DOUBLE,
				listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsDoubleSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<double[]> listener) {
		doGetCurrentValueAsync(processVariableAddress,
				ValueType.DOUBLE_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsEnum(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Enum> listener) {
		doGetCurrentValueAsync(processVariableAddress, ValueType.ENUM, listener);

	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsLong(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Long> listener) {
		doGetCurrentValueAsync(processVariableAddress, ValueType.LONG, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsLongSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<long[]> listener) {
		doGetCurrentValueAsync(processVariableAddress, ValueType.LONG_SEQUENCE,
				listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsObject(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object> listener) {
		doGetCurrentValueAsync(processVariableAddress, ValueType.OBJECT,
				listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsObjectSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<Object[]> listener) {
		doGetCurrentValueAsync(processVariableAddress,
				ValueType.OBJECT_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsString(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String> listener) {
		doGetCurrentValueAsync(processVariableAddress, ValueType.STRING,
				listener);

	}

	/**
	 * {@inheritDoc}
	 */
	public void getValueAsyncAsStringSequence(
			IProcessVariableAddress processVariableAddress,
			IProcessVariableValueListener<String[]> listener) {
		doGetCurrentValueAsync(processVariableAddress,
				ValueType.STRING_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValue(IProcessVariableAddress processVariableAddress,
			ValueType valueType) throws ConnectionException {
		return doGetCurrentValue(processVariableAddress, valueType);
	}

	/**
	 * {@inheritDoc}
	 */
	public double getValueAsDouble(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress, ValueType.DOUBLE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValueAsString(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress, ValueType.STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	public double[] getValueAsDoubleSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress,
				ValueType.DOUBLE_SEQUENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	public Enum getValueAsEnum(IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress, ValueType.ENUM);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getValueAsLong(IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress, ValueType.LONG);
	}

	/**
	 * {@inheritDoc}
	 */
	public long[] getValueAsLongSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress,
				ValueType.LONG_SEQUENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValueAsObject(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress, ValueType.OBJECT);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] getValueAsObjectSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress,
				ValueType.OBJECT_SEQUENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getValueAsStringSequence(
			IProcessVariableAddress processVariableAddress)
			throws ConnectionException {
		return doGetCurrentValue(processVariableAddress,
				ValueType.STRING_SEQUENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void register(IProcessVariableValueListener listener,
			IProcessVariableAddress pv, ValueType valueType) {
		doRegister(pv, valueType, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForDoubleSequenceValues(
			IProcessVariableValueListener<double[]> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.DOUBLE_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */

	public void registerForDoubleValues(
			IProcessVariableValueListener<Double> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.DOUBLE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForEnumValues(
			IProcessVariableValueListener<Enum> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.ENUM, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForLongSequenceValues(
			IProcessVariableValueListener<long[]> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.LONG_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForLongValues(
			IProcessVariableValueListener<Long> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.LONG, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForObjectSequenceValues(
			IProcessVariableValueListener<Object[]> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.OBJECT_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForObjectValues(
			IProcessVariableValueListener<Object> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.OBJECT, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForStringSequenceValues(
			IProcessVariableValueListener<String[]> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.STRING_SEQUENCE, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerForStringValues(
			IProcessVariableValueListener<String> listener,
			IProcessVariableAddress pv) {
		doRegister(pv, ValueType.STRING, listener);
	}

	private synchronized void doGetCurrentValueAsync(
			final IProcessVariableAddress pv, final ValueType valueType,
			final IProcessVariableValueListener listener) {

		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			// FIXME: Momentan fragen wir lokale Werte auch hier synchron ab.
			Object value = LocalChannelPool.getInstance().getChannel(pv,
					valueType).getValue(valueType);
			listener.valueChanged(value);
		} else {

			try {
				final DynamicValueProperty property = createOrGetDalProperty(
						pv, valueType.getDalType());

				if (property != null) {
					try {
						long timeout = System.currentTimeMillis() + 3000;

						while (!property.isConnected()
								&& System.currentTimeMillis() < timeout) {
							// FIXME: Kommt es zu Deadlocks, weil der Thread
							// schlafen geht?
							// try {
							// Thread.sleep(1);
							// } catch (InterruptedException e) {
							// }
						}

						// FIXME: eigene Subklasse für diesen ResponseListener
						ResponseListener responseListener = new ResponseListener() {
							public void responseError(ResponseEvent event) {
								// forward the error
								Exception error = event.getResponse()
										.getError();

								String errorMsg = error != null ? error
										.getMessage() : "Unknown Error!";

								listener.errorOccured(errorMsg);

								cleanup();
							}

							public void responseReceived(ResponseEvent event) {
								Object value = event.getResponse().getValue();
								// forward the value (with the requested type)
								listener.valueChanged(ConverterUtil.convert(
										value, valueType));

								cleanup();
							}

							private void cleanup() {
								// remove the responselistener
								property.removeResponseListener(this);

								// try to dispose the DAL property
								disposeDalProperty(property, pv
										.getControlSystem());
							}
						};

						property.addResponseListener(responseListener);

						if (pv.isCharacteristic()) {
							property.getCharacteristicAsynchronously(pv
									.getCharacteristic());
						} else {
							property.getAsynchronous();
						}
					} catch (DataExchangeException e) {
						listener.errorOccured(e.getMessage());
					}
				}
			} catch (Exception e) {
				listener.errorOccured(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Determines the current value for a process variable. This process runs
	 * synchronous and blocks any other threads. The channel connection will be
	 * closed immediately.
	 * 
	 * @param <E>
	 *            the expected return type
	 * @param pv
	 *            the process variable
	 * @param valueType
	 *            the value type
	 * 
	 * @return the current value or null if none could be obtained
	 */
	@SuppressWarnings("unchecked")
	private <E> E doGetCurrentValue(IProcessVariableAddress pv,
			ValueType valueType) throws ConnectionException {

		E result = null;

		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			result = (E) LocalChannelPool.getInstance().getChannel(pv,
					valueType).getValue(valueType);
		} else {
			DynamicValueProperty property = null;
			try {
				property = createOrGetDalProperty(pv, valueType.getDalType());
			} catch (Exception e) {
				throw new ConnectionException(e);
			}

			if (property != null) {
				try {
					long timeout = System.currentTimeMillis() + 3000;

					while (!property.isConnected()
							&& System.currentTimeMillis() < timeout) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						}
					}

					if (pv.isCharacteristic()) {
						result = (E) property.getCharacteristic(pv
								.getCharacteristic());
					} else {
						result = (E) property.getValue();
					}

				} catch (DataExchangeException e) {
					throw new ConnectionException(e);
				}

				// try to dispose the DAL property
				disposeDalProperty(property, pv.getControlSystem());
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private boolean doSetValue(IProcessVariableAddress pv, ValueType valueType,
			Object value) {
		boolean result = false;

		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			LocalChannelPool.getInstance().getChannel(pv, valueType).setValue(
					value);
			result = true;
		} else {

			DynamicValueProperty property = null;
			try {
				property = createOrGetDalProperty(pv, valueType.getDalType());
			} catch (Exception e) {
				CentralLogger.getInstance().error(null, e);
			}

			if (property != null) {
				try {
					long timeout = System.currentTimeMillis() + 3000;

					while (!property.isConnected()
							&& System.currentTimeMillis() < timeout) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						}
					}

					if (property.isConnected() && property.isSettable()) {
						property.setValue(value);
						result = true;
					}
				} catch (DataExchangeException e) {
					CentralLogger.getInstance().warn(
							null,
							pv + ": data exchange exception occured ("
									+ e.getMessage() + ")");
				}

				// try to dispose the DAL property
				disposeDalProperty(property, pv.getControlSystem());
			}
		}
		return result;

	}

	private AbstractConnector createConnector(IProcessVariableAddress pv,
			ValueType valueType) {
		AbstractConnector result = null;
		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			result = createConnectorForLocal(pv, valueType);
		} else if (pv.getControlSystem().isSupportedByDAL()) {
			result = createConnectorForDal(pv, valueType);
		}

		return result;
	}

	private void doRegister(IProcessVariableAddress pv, ValueType valueType,
			IProcessVariableValueListener listener) {

		// TODO: Verbindungsaufbau nebenläufig in eigenem Thread durchführen
		MapKey key = new MapKey(pv, valueType);

		AbstractConnector connector = (AbstractConnector) _connectors.get(key);

		if (connector == null) {
			connector = createConnector(pv, valueType);

			synchronized (_connectors) {
				// Important: Connector needs to be added here, to prevent
				// the cleanup thread from disposing the connector too early
				connector.addProcessVariableValueListener(listener);
				_connectors.put(key, connector);
			}
		} else {
			connector.addProcessVariableValueListener(listener);
		}
	}

	public void unregister(IProcessVariableValueListener listener) {
		doUnregister(listener);
	}

	private void doUnregister(IProcessVariableValueListener listener) {
		for (AbstractConnector c : _connectors.values()) {
			c.removeProcessVariableValueListener(listener);
		}
	}

	private AbstractConnector createConnectorForLocal(
			IProcessVariableAddress pv, ValueType valueType) {
		LocalConnector connector = new LocalConnector(pv, valueType);
		LocalChannelPool.getInstance().getChannel(pv, valueType).addListener(
				connector);

		return connector;

	}

	private AbstractConnector createConnectorForDal(IProcessVariableAddress pv,
			ValueType valueType) {
		final DalConnector connector = new DalConnector(pv, valueType);

		// get or create a real DAL property
		DynamicValueProperty dynamicValueProperty = null;
		try {
			dynamicValueProperty = createOrGetDalProperty(pv, valueType
					.getDalType());
		} catch (Exception e) {
			connector.forwardError(e.getLocalizedMessage());
		}

		if (dynamicValueProperty != null) {
			// keep the DAL property in mind
			connector.setDalProperty(dynamicValueProperty);

			// add the connector as dynamic value listener on the DAL
			// property
			// (requires workaround)
			new ConnectionWorkarroundLinkListener(dynamicValueProperty,
					connector);
		}

		return connector;
	}

	//
	// /**
	// * Registers the specified listener. We decouple DAL and those listeners
	// * using a {@link DalConnector} construct. In fact, only the
	// * {@link DalConnector} listens to the DAL directly and forwards any
	// events
	// * to appropriate methods on the {@link IProcessVariableValueListener}.
	// *
	// * @param pv
	// * the process variable pointer
	// * @param propertyType
	// * the DAL property type
	// * @param listener
	// * the value listener
	// * @throws Exception
	// * an exception
	// */
	// @SuppressWarnings("unchecked")
	// private void doRegisterForDal(IProcessVariableAddress pv,
	// ValueType valueType, IProcessVariableValueListener listener) {
	//
	// // TODO: Zusammengesetzter Key aus PV und erwartetem Rückgabetyp
	// if (!_dalConnectors.containsKey(pv)) {
	// // get or create a real DAL property
	// DynamicValueProperty dynamicValueProperty = null;
	// dynamicValueProperty = createOrGetDalProperty(pv, valueType
	// .getDalType());
	//
	// if (dynamicValueProperty != null) {
	// // create a new connector
	// final DalConnector connector = new DalConnector(pv);
	//
	// // add the connector as dynamic value listener on the DAL
	// // property
	// // (requires workaround)
	// new ConnectionWorkarroundLinkListener(dynamicValueProperty,
	// connector);
	//
	// // add the connector as link listener on the DAL property
	// dynamicValueProperty.addLinkListener(connector);
	//
	// // send the initial connection state
	// connector.forwardConnectionState(ConnectionState
	// .translate(dynamicValueProperty.getConnectionState()));
	//
	// // keep the DAL property in mind
	// connector.setDalProperty(dynamicValueProperty);
	//
	// synchronized (_dalConnectors) {
	// assert connector.getDalProperty() != null;
	// assert connector.getProcessVariableAddress() != null;
	// // Important: Connector needs to be added here, to prevent
	// // the cleanup thread from disposing the connector too early
	// connector.addProcessVariableValueListener(listener);
	// _dalConnectors.put(pv, connector);
	// }
	// }
	// } else {
	// // connect the connector to the process variable listener
	// DalConnector connector = _dalConnectors.get(pv);
	// connector.addProcessVariableValueListener(listener);
	// }
	// }

	/**
	 * Delivers a real DAL property. The delivered property may already be
	 * connected BUT must not.
	 * 
	 * @param pv
	 *            the process variable address
	 * @param propertyType
	 *            the expected property type
	 * @return a DAL property
	 * @throws InstantiationException
	 * @throws RemoteException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private DynamicValueProperty createOrGetDalProperty(
			IProcessVariableAddress pv, Class propertyType) throws Exception {
		DynamicValueProperty result = null;

		RemoteInfo ri = pv.toDalRemoteInfo();

		PropertyFactory factory = DALPropertyFactoriesProvider.getInstance()
				.getPropertyFactory(pv.getControlSystem());

		boolean exists = factory.getPropertyFamily().contains(ri);

		if (exists) {
			// get the existing property
			result = factory.getProperty(ri);
		} else {
			// create a new property
			try {
				result = factory.getProperty(ri, propertyType, null);
			} catch (ClassCastException e) {
				// TODO: Help Igor fixing this evil bug ;).
				throw new Exception(
						"Cache bug in DAL prevents property from beeing created.");
			}
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	private void disposeConnector(DalConnector connector) {
		DynamicValueProperty property = connector.getDalProperty();

		if (!property.isDestroyed()) {
			// remove link listener
			property.removeLinkListener(connector);

			// remove value listeners
			property.removeDynamicValueListener(connector);

			// try to dispose the DAL property
			disposeDalProperty(property, connector.getProcessVariableAddress()
					.getControlSystem());
		}
	}

	private void disposeDalProperty(DynamicValueProperty property,
			ControlSystemEnum controlSystem) {
		PropertyFactory factory = DALPropertyFactoriesProvider.getInstance()
				.getPropertyFactory(controlSystem);

		if (!property.isDestroyed()) {
			// if the property is not used anymore by other connectors,
			// destroy it
			// FIXME: Dies ist nur ein Workarround. Igor bitten, das
			// Zerstören von Properties tranparent zu gestalten.
			if (property.getDynamicValueListeners().length <= 1
					&& property.getResponseListeners().length <= 0) {
				factory.getPropertyFamily().destroy(property);

				assert !factory.getPropertyFamily().contains(property) : "!getPropertyFactory().getPropertyFamily().contains(property)";
			}
		}

	}

	/**
	 * LinkListener implementation, which adds a DynamicValueListener lazily to
	 * a DynamicValueProperty when the DynamicValueProperty is connected.
	 * 
	 * This is a just a workaround, which is necessary because
	 * DynamicValueListener´s cannot be attached to DynamicValueProperty before
	 * they are connected to a channel. (//TODO: Cosylab! Please fix this!)
	 * 
	 * @author Sven Wende
	 * 
	 */
	@SuppressWarnings("unchecked")
	class ConnectionWorkarroundLinkListener extends LinkAdapter {
		@Override
		public void connectionLost(ConnectionEvent e) {
			_connector.forwardConnectionState(ConnectionState.translate(e
					.getState()));
		}

		@Override
		public void disconnected(ConnectionEvent e) {
			_connector.forwardConnectionState(ConnectionState.translate(e
					.getState()));
		}

		@Override
		public void resumed(ConnectionEvent e) {
			_connector.forwardConnectionState(ConnectionState.translate(e
					.getState()));
		}

		@Override
		public void suspended(ConnectionEvent e) {
			_connector.forwardConnectionState(ConnectionState.translate(e
					.getState()));
		}

		@Override
		public void connectionFailed(ConnectionEvent e) {
			_connector.forwardConnectionState(ConnectionState.translate(e
					.getState()));
		}

		@Override
		public void destroyed(ConnectionEvent e) {
			_connector.forwardConnectionState(ConnectionState.translate(e
					.getState()));
		}

		private DynamicValueProperty _dynamicValueProperty;
		private DalConnector _connector;

		private ConnectionWorkarroundLinkListener(
				DynamicValueProperty dynamicValueProperty,
				DalConnector connector) {
			assert dynamicValueProperty != null;
			assert connector != null;
			_dynamicValueProperty = dynamicValueProperty;
			_connector = connector;

			if (_dynamicValueProperty.isConnected()) {
				// the property is already connected -> we just need to add the
				// dynamic value listener
				_dynamicValueProperty.addDynamicValueListener(_connector);

				init();
			} else {
				// the property is not connected -> we listen and wait for
				// connection events
				_dynamicValueProperty.addLinkListener(this);
			}
		}

		public void connected(ConnectionEvent e) {
			// disconnect this listener
			_dynamicValueProperty.removeLinkListener(this);

			init();
		}

		private void init() {
			initializeValueListeners();
			requestInitialValue();
		}

		private void initializeValueListeners() {
			IProcessVariableAddress pv = _connector.getProcessVariableAddress();

			// we add a ResponseListener in any case, which receives initial
			// dynamic values, which are requested via
			// property.getAsynchronous() as well
			// as values for characteristics, which are requested via
			// property.getCharacteristicAsynchronous()
			_dynamicValueProperty.addResponseListener(_connector);

			// we add a dynamic value listener in case we are listening for
			// dynamic updates of a
			// non-characteristic
			if (!pv.isCharacteristic()) {
				_dynamicValueProperty.addDynamicValueListener(_connector);
			}

			// we add a LinkListener to get informed of connection state changes
			_dynamicValueProperty.addLinkListener(_connector);
		}

		private void requestInitialValue() {
			if (_dynamicValueProperty.isConnectionAlive()) {

				String characteristic = _connector.getProcessVariableAddress()
						.getCharacteristic();

				if (characteristic != null) {
					try {
						_dynamicValueProperty
								.getCharacteristicAsynchronously(characteristic);
					} catch (DataExchangeException e) {
						_connector.forwardError(e.getMessage());
						CentralLogger.getInstance().warn(null, e);
					}
				} else {
					try {
						_dynamicValueProperty.getAsynchronous(_connector);
					} catch (DataExchangeException e) {
						_connector.forwardError(e.getMessage());
						CentralLogger.getInstance().warn(null, e);
					}
				}
			}

			// send initial connection state
			_connector.forwardConnectionState(ConnectionState
					.translate(_dynamicValueProperty.getConnectionState()));
		}
	}

	/**
	 * Cleanup thread, which removes connectors that are not needed anymore.
	 * 
	 * @author swende
	 * 
	 */
	final class CleanupThread extends Thread {

		private long _sleepTime;

		/**
		 * Flag that indicates if the thread should continue its execution.
		 */
		private boolean _running;

		/**
		 * Standard constructor.
		 */
		private CleanupThread() {
			_running = true;
			_sleepTime = 1000;
			start();
		}

		/**
		 * {@inheritDoc}.
		 */
		@Override
		public void run() {
			while (_running) {

				try {
					sleep(_sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				doCleanup();
				yield();
			}
		}

		/**
		 * Stops the execution of this BundelingThread.
		 */
		public void stopExecution() {
			_running = false;
		}

		/**
		 * Performs the cleanup.
		 */
		private synchronized void doCleanup() {
			synchronized (_connectors) {
				List<MapKey> deleteCandidates = new ArrayList<MapKey>();

				Iterator<MapKey> it = _connectors.keySet().iterator();

				while (it.hasNext()) {
					MapKey key = it.next();
					AbstractConnector connector = _connectors.get(key);

					if (connector.isDisposable()) {
						deleteCandidates.add(key);
					}
				}

				for (MapKey key : deleteCandidates) {
					_connectors.remove(key);
				}
			}
		}
	}

	class MapKey {
		private IProcessVariableAddress _processVariableAddress;
		private ValueType _valueType;

		private MapKey(IProcessVariableAddress processVariableAddress,
				ValueType valueType) {
			_processVariableAddress = processVariableAddress;
			_valueType = valueType;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((_processVariableAddress == null) ? 0
							: _processVariableAddress.hashCode());
			result = prime * result
					+ ((_valueType == null) ? 0 : _valueType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = false;

			if (obj != null && obj instanceof MapKey) {
				MapKey other = (MapKey) obj;

				if (other._valueType == _valueType
						&& other._processVariableAddress
								.equals(_processVariableAddress)) {
					result = true;
				}
			}

			return result;
		}

	}

}
