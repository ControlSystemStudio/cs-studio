package org.csstudio.sds.internal.connection.dal;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.sds.internal.connection.Connector;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.ResponseEvent;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * Connector implementation that deals with the Data Access Layer (DAL).
 * 
 * @author Sven Wende
 * 
 */
public final class DalConnector extends Connector {

	/**
	 * The DAL property which provides access to the control system.
	 */
	private DynamicValueProperty _cachedProperty = null;

	/**
	 * Overridden, just to provide a log statement. {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		execute(new RunActionWithProperty("finalizing connector") {
			@Override
			protected void run(DynamicValueProperty property) throws Exception {
				// do nothing
			}
		});
	}

	/**
	 * Constructor.
	 * 
	 * @param processVariableAddress
	 *            the adress of the process variable this connector should
	 *            connect to
	 */
	public DalConnector(final IProcessVariableAddress processVariableAddress) {
		super(processVariableAddress);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doProcessManualValueChange(final Object newValue) {
		execute(new RunActionWithProperty("applying manual value change") {
			@Override
			protected void run(DynamicValueProperty property) throws Exception {
				if (property.isConnected() && property.isSettable()) {
					property.setValue(newValue.toString());
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doDisconnect() {
		// create the property
		execute(new RunActionWithProperty("disconnecting") {

			@Override
			protected void run(DynamicValueProperty property) throws Exception {
				if (!property.isDestroyed()) {
					// remove link listener
					property.removeLinkListener(getLinkListener());

					// remove value listeners
					property.removeResponseListener(getResponseListener());
					property
							.removeDynamicValueListener(getDynamicValueListener());

					// if the property is not used anymore by other connectors,
					// destroy it
					// FIXME: Dies ist nur ein Workarround. Igor bitten, das
					// Zerstören von Properties tranparent zu gestalten.
					if (property.getDynamicValueListeners().length <= 1
							&& property.getResponseListeners().length <= 0) {
						getPropertyFactory().getPropertyFamily().destroy(
								property);

						assert !getPropertyFactory().getPropertyFamily()
								.contains(property) : "!getPropertyFactory().getPropertyFamily().contains(property)";
					}
				}
			}

		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doConnect() {
		// get the DAL property
		try {
			_cachedProperty = createAndLinkDalProperty();
		} catch (RemoteException e) {
			logError("DAL property could not be created", e);
		} catch (InstantiationException e) {
			logError("DAL property could not be created", e);
		}

		if (_cachedProperty == null) {
			// forward initial connection state
			forwardConnectionStateChange(ConnectionState.CONNECTION_FAILED);
		} else {
			// forward a initial connection state
			forwardConnectionStateChange(_cachedProperty.getConnectionState());

			if (_cachedProperty.isConnected()
					&& _cachedProperty.isConnectionAlive()) {
				requestInitialValue();
			}

		}
	}

	/**
	 * Request an initial property value.
	 * 
	 * @param property
	 *            the DAL property
	 */
	private void requestInitialValue() {
		boolean withCharacteristic = getProcessVariable().getCharacteristic() != null;

		execute(new RunActionWithProperty("requesting "
				+ (withCharacteristic ? "characteristic ("
						+ getProcessVariable().getCharacteristic() + ")"
						: "initial value")) {

			@Override
			protected void run(DynamicValueProperty property) throws Exception {
				assert property.isConnected() : "property.isConnected()";
				if (property.isConnectionAlive()) {
					String characteristic = getProcessVariable()
							.getCharacteristic();

					if (characteristic != null) {
						// CharacteristicsResponseListener listener = new
						// CharacteristicsResponseListener(
						// characteristic);
						// property.addResponseListener(listener);
						property
								.getCharacteristicAsynchronously(characteristic);
					} else {
						 property.getAsynchronous(new ResponseListener() {
							public void responseError(final ResponseEvent event) {

							}

							public void responseReceived(
									final ResponseEvent event) {
								forwardValueChange(event.getResponse()
										.getValue());
							}
						});
					}
				}
			}
		});
	}

	private PropertyFactory getPropertyFactory() {
		return DALPropertyFactoriesProvider.getInstance().getPropertyFactory(
				getProcessVariable().getControlSystem());
	}

	private boolean _valueListenersInitialized = false;

	@Override
	protected void handleConnectionStateTransition(
			final ConnectionState oldState, final ConnectionState newState) {
		execute(new RunActionWithProperty("handling state transition ["
				+ oldState + "->" + newState + "]") {

			@Override
			protected void run(DynamicValueProperty property) throws Exception {
				if (!_valueListenersInitialized
						&& newState == ConnectionState.CONNECTED) {

					if (getProcessVariable().isCharacteristic()) {
						property.addResponseListener(getResponseListener());
					} else {
						property
								.addDynamicValueListener(getDynamicValueListener());

					}

					_valueListenersInitialized = true;
				}

				// request the initial value, as soon as the channel is
				// connected
				if (newState == ConnectionState.CONNECTED) {
					requestInitialValue();
				}
			}

		});

	}

	private boolean createWasCalled = false;

	/**
	 * Creates and returns a DAL property. The method does already add the
	 * {@link LinkListener} to the property.
	 * 
	 * Important: This method might also return null, in case of unsupported
	 * control systems
	 * 
	 * Important: This method MUST be called only ONCE in the lifecycle of a
	 * connector.
	 * 
	 * @return a DAL property or null
	 */
	private synchronized DynamicValueProperty createAndLinkDalProperty()
			throws RemoteException, InstantiationException {
		// make sure, this method only gets called once
		if (createWasCalled) {
			throw new IllegalAccessError(
					"Method should only be called once in the lifecycle of this connector.");
		} else {
			createWasCalled = true;
		}

		DynamicValueProperty result = null;

		if (getProcessVariable().getControlSystem().isSupportedByDAL()) {
			RemoteInfo pv = getProcessVariable().toDalRemoteInfo();

			PropertyFactory factory = getPropertyFactory();

			boolean exists = factory.getPropertyFamily().contains(
					pv.getRemoteName());

			if (exists) {
				// get the existing property
				try {
					// with the right type
					result = factory.getProperty(pv);

					// register a link listener
					result.addLinkListener(getLinkListener());

					// forward a initial connection state
					forwardConnectionStateChange(result.getConnectionState());

				} catch (RemoteException e) {
					CentralLogger.getInstance().info(null, "A remote exception occured: "+e.getMessage());
				}

			} else {
				result = factory.getProperty(pv, determineDalPropertyType(),
						getLinkListener());
			}
		}

		return result;
	}

	/**
	 * Determines the type which is used to establish the DAL connection.
	 * 
	 * @return the DAL property type
	 */
	private Class determineDalPropertyType() {
		Class<? extends DynamicValueProperty> dalType = null;

		// 1. choice, is there a type hint directly on the pv ?
		ValueType typeHint = getProcessVariable().getValueTypeHint();
		if (typeHint != null) {
			dalType = typeHint.getDalType();
		}

		// 2nd choice, take the type hint, provided by the widget
		// property
		if (dalType == null) {
			dalType = getPropertyType().getTypeHint().getDalType();
		}

		// 3rd choice, take double
		if (dalType == null) {
			dalType = DoubleProperty.class;
		}

		return dalType;
	}

	private void execute(RunActionWithProperty action) {
		if (_cachedProperty != null) {
			action.execute();
		}
	}

	abstract class RunActionWithProperty {
		private String _message;

		public RunActionWithProperty(String message) {
			assert message != null;
			_message = message;
		}

		protected abstract void run(DynamicValueProperty property)
				throws Exception;

		public void execute() {
			if (_cachedProperty != null) {
				logInfo(_message);
				try {
					run(_cachedProperty);
				} catch (Exception e) {
					logError(e.getMessage(), e);
				}
			}
		}
	}
}
