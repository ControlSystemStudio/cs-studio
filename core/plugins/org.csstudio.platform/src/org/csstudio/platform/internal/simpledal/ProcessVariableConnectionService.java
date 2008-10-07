/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
import org.csstudio.platform.simpledal.ConnectorIdentification;
import org.csstudio.platform.simpledal.IConnectorStatistic;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
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
 * TODO: Schreiben von Werten erm�glichen!
 * 
 * TODO: Sync/Async Lesen von Werten (x)
 * 
 * TODO: Ping-Funktion
 * 
 */
public class ProcessVariableConnectionService implements
		IProcessVariableConnectionService {

	private Map<ConnectorIdentification, AbstractConnector> _connectors;

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
		_connectors = new HashMap<ConnectorIdentification, AbstractConnector>();
		_cleanupThread = new CleanupThread();
		_cleanupThread.start();
	}

	public static synchronized IProcessVariableConnectionService getInstance() {
		if (_instance == null) {
			_instance = new ProcessVariableConnectionService();
		}

		return _instance;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getConnectorCount() {
		return _connectors.keySet().size();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IConnectorStatistic> getConnectorStatistic() {
		List<IConnectorStatistic> result = new ArrayList<IConnectorStatistic>();
		result.addAll(_connectors.values());
		return result;
		
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

	private void doGetCurrentValueAsync(final IProcessVariableAddress pv,
			final ValueType valueType,
			final IProcessVariableValueListener listener) {

		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			// FIXME: Momentan fragen wir lokale Werte auch hier synchron ab.
			Object value = LocalChannelPool.getInstance().getChannel(pv,
					valueType).getValue();
			listener.valueChanged(ConverterUtil.convert(value, valueType), new Timestamp());
		} else {

			// there is one connector for each pv-type-combination
			ConnectorIdentification key = new ConnectorIdentification(pv, valueType);
			final DalConnector connector;
			final IProcessVariableValueListener llistener= new IProcessVariableValueListener () {
				public void connectionStateChanged(
						ConnectionState connectionState) {
					//
				}
				public void errorOccured(String error) {
					//
				}
				public void valueChanged(Object value, Timestamp timestamp) {
					//
				}
			};
			
			synchronized (_connectors) {
				if (_connectors.containsKey(key)) {
					connector = (DalConnector) _connectors.get(key);					
				} else {
					connector = createConnectorForDal(pv, valueType);
					_connectors.put(key, connector);
				}
				connector.addProcessVariableValueListener(null,llistener);
			}
			
			connector.watiTillConnected(3000);

			final DynamicValueProperty property = connector.getDalProperty();

			try {

				if (property != null) {
					try {

						ResponseListener responseListener = new ResponseListener() {
							public void responseError(ResponseEvent event) {
								// forward the error
								Exception error = event.getResponse().getError();
								String errorMsg = error != null ? error
										.getMessage() : "Unknown Error!";
								listener.errorOccured(errorMsg);
								connector.removeProcessVariableValueListener(llistener);
							}

							//jhatje 18.07.2008, add timestamp
							public void responseReceived(ResponseEvent event) {
								Object value = event.getResponse().getValue();
								Timestamp timestamp = event.getResponse().getTimestamp();
								listener.valueChanged(ConverterUtil.convert(
										value, valueType), timestamp);
								connector.removeProcessVariableValueListener(llistener);
								System.out.println("AGET-RETURN "+pv.toString()+" "+valueType+" "+value);
							}

						};

						if (pv.isCharacteristic()) {
							property.getCharacteristicAsynchronously(pv
									.getCharacteristic(),responseListener);
						} else {
							property.getAsynchronous(responseListener);
						}
						
						System.out.println("AGET '"+pv.toString()+"' "+valueType);

					} catch (DataExchangeException e) {
						listener.errorOccured(e.getMessage());
						connector.removeProcessVariableValueListener(llistener);
					}
				} else {
					connector.removeProcessVariableValueListener(llistener);
				}
			} catch (Exception e) {
				listener.errorOccured(e.getLocalizedMessage());
				connector.removeProcessVariableValueListener(llistener);
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
			Object value = LocalChannelPool.getInstance().getChannel(pv,
					valueType).getValue();
			//result = (E) value;
			result = (E) ConverterUtil.convert(value, valueType);
		} else {
			// there is one connector for each pv-type-combination
			ConnectorIdentification key = new ConnectorIdentification(pv, valueType);
			DalConnector connector=null;
			IProcessVariableValueListener listener= new IProcessVariableValueListener () {
				public void connectionStateChanged(
						ConnectionState connectionState) {
					//
				}
				public void errorOccured(String error) {
					//
				}
				public void valueChanged(Object value, Timestamp timestamp) {
					//
				}
			};
			
			synchronized (_connectors) {
				connector = (DalConnector) _connectors
						.get(key);
				if (connector == null) {
					connector = createConnectorForDal(pv, valueType);
					_connectors.put(key, connector);
				}
				connector.addProcessVariableValueListener(null,listener);
			}
			
			connector.watiTillConnected(3000);

			DynamicValueProperty property = connector.getDalProperty();
			
			if (property != null) {
				try {

					if (pv.isCharacteristic()) {
						result = (E) DalConnector.getCharacteristic(pv.getCharacteristic(), property, valueType);
					} else {
						Object value= property.getValue();
						result = (E) ConverterUtil.convert(value, valueType);
					}

				} catch (DataExchangeException e) {
					throw new ConnectionException(e);
				} finally {
					connector.removeProcessVariableValueListener(listener);
				}

			} else {
				connector.removeProcessVariableValueListener(listener);
			}
		}

		System.out.println("GET '"+pv.toString()+"' "+valueType+" "+result);

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
			
			// there is one connector for each pv-type-combination
			ConnectorIdentification key = new ConnectorIdentification(pv, valueType);
			final DalConnector connector;
			final IProcessVariableValueListener listener= new IProcessVariableValueListener () {
				public void connectionStateChanged(
						ConnectionState connectionState) {
					//
				}
				public void errorOccured(String error) {
					//
				}
				public void valueChanged(Object value, Timestamp timestamp) {
					//
				}
			};
			
			synchronized (_connectors) {
				if (_connectors.containsKey(key)) {
					connector = (DalConnector) _connectors.get(key);					
				} else {
					connector = createConnectorForDal(pv, valueType);
					_connectors.put(key, connector);
				}
				connector.addProcessVariableValueListener(null,listener);
			}
			
			connector.watiTillConnected(3000);

			DynamicValueProperty property = connector.getDalProperty();

			if (property!=null && property.isSettable()) {
				try {
					property.setAsynchronous(ConverterUtil.convert(value, valueType), new ResponseListener() {
					
						public void responseReceived(ResponseEvent event) {
							CentralLogger.getInstance().debug(null, event.getResponse().toString());
							connector.removeProcessVariableValueListener(listener);
						}
					
						public void responseError(ResponseEvent event) {
							CentralLogger.getInstance().error(null, event.getResponse().getError());
							connector.removeProcessVariableValueListener(listener);
						}
					
					});
				} catch (DataExchangeException e) {
					e.printStackTrace();
					connector.removeProcessVariableValueListener(listener);
				}
			} else {
				connector.removeProcessVariableValueListener(listener);
				CentralLogger.getInstance().debug(null, "Property "+property.getUniqueName()+" is not settable");
			}	
			
/*			WriteValueLinkListener listener = new WriteValueLinkListener(pv,
					valueType, value);
*/		
		}
		System.out.println("SET '"+pv.toString()+"' "+valueType+" "+value);
		return result;

	}

	private AbstractConnector createConnector(IProcessVariableAddress pv,
			ValueType valueType) {
		AbstractConnector result = null;
		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			result = createConnectorForLocal(pv, valueType);
		} else if (pv.getControlSystem().isSupportedByDAL()
				|| !pv.getControlSystem().isSupportedByDAL()) {
			result = createConnectorForDal(pv, valueType);
		} else {
			result = new DalConnector(pv, valueType);
			result.forwardError("Control System " + pv.getControlSystem()
					+ " is not supported yet.");
		}

		return result;
	}

	private void doRegister(IProcessVariableAddress pv, ValueType valueType,
			IProcessVariableValueListener listener) {

		// there is one connector for each pv-type-combination
		ConnectorIdentification key = new ConnectorIdentification(pv, valueType);

		synchronized (_connectors) {
			AbstractConnector connector = (AbstractConnector) _connectors
					.get(key);

			if (connector == null) {
				connector = createConnector(pv, valueType);

				// Important: Connector needs to be added here, to prevent
				// the cleanup thread from disposing the connector too early
				if (pv.isCharacteristic()) {
					connector.addProcessVariableValueListener(pv.getCharacteristic(),listener);
				} else {
					connector.addProcessVariableValueListener(null,listener);
				}
				_connectors.put(key, connector);

			} else {
				if (pv.isCharacteristic()) {
					connector.addProcessVariableValueListener(pv.getCharacteristic(),listener);
				} else {
					connector.addProcessVariableValueListener(null,listener);
				}
			}
			System.out.println("REGISTER '"+pv.getFullName()+"' '"+pv.getCharacteristic()+"' "+valueType+" "+connector.getLatestConnectionState());
		}
	}

	public void unregister(IProcessVariableValueListener listener) {
		// we remove the listener from all connectors
		synchronized (_connectors) {
			for (AbstractConnector c : _connectors.values()) {
				if (c.removeProcessVariableValueListener(listener)) {
					System.out.println("UNREGISTER '"+c.getName());
				}
			}
		}
	}

	private AbstractConnector createConnectorForLocal(
			IProcessVariableAddress pv, ValueType valueType) {
		LocalConnector connector = new LocalConnector(pv, valueType);
		
		// send initial connection state (local channels are always connected)
		connector.forwardConnectionState(ConnectionState.CONNECTED);
		
		
		LocalChannelPool.getInstance().getChannel(pv, valueType).addListener(
				connector);

		return connector;

	}

	private DalConnector createConnectorForDal(IProcessVariableAddress pv,
			ValueType valueType) {
		final DalConnector connector = new DalConnector(pv, valueType);

		// get or create a real DAL property
		DynamicValueProperty dynamicValueProperty = null;
		try {
			dynamicValueProperty = createOrGetDalProperty(pv, valueType
					.getDalType());
		} catch (Throwable e) {
			connector.forwardError(e.getLocalizedMessage());
		}

		if (dynamicValueProperty != null) {
			connector.setDalProperty(dynamicValueProperty);
		}

		return connector;
	}

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

		
		// Igor: this does nto work anyhow, always is true, RDFM.
		//boolean exists = factory.getPropertyFamily().contains(ri);

		/*
		igor: because xsista is allways FALSE, it always makes new property for same type.
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
		}*/
		
		// TODO: test this, it should work, for characteristic property type has no effect 

		if (pv.isCharacteristic()) {
			// first we try to get from DAL cache property of any type
			result= factory.getPropertyFamily().getProperty(ri.getName());
			
			if (result==null) {
				// second we try to create any property with requested remote name
				result = factory.getProperty(ri);
			}
		} else {
			result = factory.getProperty(ri, propertyType, null);
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	private void disposeConnector(DalConnector connector) {
		
		System.out.println("DISPOSE "+connector.getPVAdress().toString());

		DynamicValueProperty property = connector.getDalProperty();

		connector.setDalProperty(null);
		
		if (property != null && !property.isDestroyed()) {
			// remove link listener
			property.removeLinkListener(connector);

			// remove value listeners
			property.removeDynamicValueListener(connector);

			// remove response listeners
			property.removeResponseListener(connector);

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
			// Zerst�ren von Properties tranparent zu gestalten.
			if (property.getDynamicValueListeners().length <= 1
					&& property.getResponseListeners().length <= 0) {
				
				System.out.println("DESTROY "+property.getUniqueName());

				factory.getPropertyFamily().destroy(property);
				

				// <**** Workarround (FIXME: Remove, when DAL is fixed) ***
				// DAL caches a reference to a former ResponseListener
				// via its latestResponse and latestRequest fields on
				// DynamicValuePropertyImpl.class
				// ********************************************************
				/*try {
					Object e = property.getLatestResponse();

					property.getAsynchronous(null);

					while (e == property.getLatestResponse()) {
						Thread.sleep(1);
					}

				} catch (DataExchangeException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				// **** Workarround (Remove, when DAL is fixed)************>

				assert !factory.getPropertyFamily().contains(property) : "!getPropertyFactory().getPropertyFamily().contains(property)";
			}
		}

	}

	class WriteValueLinkListener /*extends LinkAdapter*/ {
		private IProcessVariableAddress _processVariableAddress;
		private ValueType _valueType;
		private DynamicValueProperty _dynamicValueProperty;
		private Object _valueToSet;

		private WriteValueLinkListener(
				IProcessVariableAddress processVariableAddress,
				ValueType valueType, Object valueToSet) {
			assert processVariableAddress != null;
			assert valueType != null;
			assert valueToSet != null;

			_processVariableAddress = processVariableAddress;
			_valueType = valueType;
			_valueToSet = valueToSet;

			// create or get the DAL property
			try {
				_dynamicValueProperty = createOrGetDalProperty(
						_processVariableAddress, _valueType.getDalType());
			} catch (Exception e) {
				CentralLogger.getInstance().error(null, e);
			}
			
			DalConnector.waitTillConnected(_dynamicValueProperty, 3000);

			//if (_dynamicValueProperty != null
			//		&& _dynamicValueProperty.isConnectionAlive()) {
				
			
			doSetValue();
			
			
			//} else {
				// the property is not connected -> we listen and wait for
				// connection events
				//_dynamicValueProperty.addLinkListener(this);
			//}

			// TODO: Timeout???
		}

		/*@Override
		public void connectionFailed(ConnectionEvent e) {
			_dynamicValueProperty.removeLinkListener(this);
		}

		@Override
		public void connectionLost(ConnectionEvent e) {
			_dynamicValueProperty.removeLinkListener(this);
		}

		@Override
		public void destroyed(ConnectionEvent e) {
			_dynamicValueProperty.removeLinkListener(this);
		}

		@Override
		public void disconnected(ConnectionEvent e) {
			_dynamicValueProperty.removeLinkListener(this);
		}

		@Override
		public void suspended(ConnectionEvent e) {
			_dynamicValueProperty.removeLinkListener(this);
		}

		public void connected(ConnectionEvent e) {
			// disconnect this listener
			_dynamicValueProperty.removeLinkListener(this);
			doSetValue();
		}*/

		private void doSetValue() {
			assert _dynamicValueProperty.isConnected() : "_dynamicValueProperty.isConnected()";

			if (_dynamicValueProperty.isSettable()) {
				try {
					_dynamicValueProperty.setAsynchronous(ConverterUtil.convert(_valueToSet, _valueType), new ResponseListener() {
					
						public void responseReceived(ResponseEvent event) {
							CentralLogger.getInstance().debug(null, event.getResponse().toString());
						}
					
						public void responseError(ResponseEvent event) {
							CentralLogger.getInstance().error(null, event.getResponse().getError());
						}
					
					});
				} catch (DataExchangeException e) {
					e.printStackTrace();
				}
			} else {
				CentralLogger.getInstance().debug(null, "Property "+_dynamicValueProperty.getUniqueName()+" is not settable");
			}

			// try to dispose the DAL property
			// FIXME: Igor: this should be solved differently, creating ad disposing like this is not efficient
			/*disposeDalProperty(_dynamicValueProperty, _processVariableAddress
					.getControlSystem());*/
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
		CleanupThread() {
			super("ProcessVariableConnectionService#CleanupThread");
			// Have to be a daemon to be automatically stopped on a system
			// shutdown.
			this.setDaemon(true);
			_running = true;
			_sleepTime = 1000;
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
		private void doCleanup() {
			synchronized (_connectors) {
				List<ConnectorIdentification> deleteCandidates = new ArrayList<ConnectorIdentification>();

				Iterator<ConnectorIdentification> it = _connectors.keySet().iterator();

				while (it.hasNext()) {
					ConnectorIdentification key = it.next();
					AbstractConnector connector = _connectors.get(key);

					if (connector.isDisposable()) {
						deleteCandidates.add(key);
					}
				}

				for (ConnectorIdentification key : deleteCandidates) {
					AbstractConnector connector = _connectors.remove(key);

					if (connector instanceof DalConnector) {
						disposeConnector((DalConnector) connector);
					} else if (connector instanceof LocalConnector) {
						LocalChannelPool.getInstance().getChannel(
								connector.getProcessVariableAddress(),
								connector.getValueType()).removeListener(
								(LocalConnector) connector);
					}
					// TODO: Local
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public SettableState isSettable(IProcessVariableAddress pv) {
		SettableState result = SettableState.UNKNOWN;

		if (pv.getControlSystem() == ControlSystemEnum.LOCAL) {
			result = SettableState.SETTABLE;
		} else {
			try {
				ValueType valueType = pv.getValueTypeHint() != null ? pv
						.getValueTypeHint() : ValueType.DOUBLE;

				DynamicValueProperty p = createOrGetDalProperty(pv, valueType
						.getDalType());


				long timeout = System.currentTimeMillis() + 1000;

				while (!p.isConnected()
						&& System.currentTimeMillis() < timeout) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
				
				// DAL encapsulates the detection of the current user internally
				// (probably via global system properties)
				if(p.isConnected()) {
					result = p.isSettable() ? SettableState.SETTABLE : SettableState.NOT_SETTABLE;
				}
			} catch (Exception e) {
				CentralLogger.getInstance().error(
						this,
						"We could not check the settable-state of ["
								+ pv.toString() + "]", e);
				result = SettableState.UNKNOWN;
			}
		}

		return result;
	}

}
