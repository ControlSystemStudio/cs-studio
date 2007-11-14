package org.csstudio.platform.internal.simpledal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkAdapter;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.spi.PropertyFactory;

public class ProcessVariableConnectionService implements IProcessVariableConnectionService {
	private Map<IProcessVariableAddress, DalConnector> _dalConnectors;

	private Thread _cleanupThread;

	public ProcessVariableConnectionService() {
		_dalConnectors = new HashMap<IProcessVariableAddress, DalConnector>();

		_cleanupThread = new CleanupThread();
	}

	/**
	 * Returns all active connectors.
	 * 
	 * @return a map with all active connectors
	 */
	Map<IProcessVariableAddress, DalConnector> getConnectors() {
		return _dalConnectors;
	}

	void removeConnector(IProcessVariableAddress pv) {
		synchronized (_dalConnectors) {
			if (_dalConnectors.containsKey(pv)) {
				DalConnector connector = _dalConnectors.remove(pv);
				destroyProperty(connector);
				CentralLogger.getInstance().info(null,
						"Connection to " + pv.toString() + " closed!");
			}
		}
	}

	public void registerForIntValues(
			IProcessVariableValueListener<Integer> listener,
			IProcessVariableAddress pv) throws Exception {
		doRegister(pv, LongProperty.class, listener);
	}

	public void registerForDoubleValues(
			IProcessVariableValueListener<Double> listener,
			IProcessVariableAddress pv) throws Exception {
		doRegister(pv, DoubleProperty.class, listener);
	}

	@SuppressWarnings("unchecked")
	private void doRegister(IProcessVariableAddress pv,
			Class<? extends DynamicValueProperty<?>> propertyType,
			IProcessVariableValueListener<?> processVariableValueListener)
			throws Exception {
		if (!_dalConnectors.containsKey(pv)) {
			// create a new connector
			final DalConnector connector = new DalConnector();

			// get or create a real DAL property
			final DynamicValueProperty<?> dynamicValueProperty = createProperty(
					pv, propertyType);

			// add the connector as dynamic value listener on the DAL property
			// (requires workaround)
			new ConnectionWorkarroundLinkListener(dynamicValueProperty,
					connector);

			// add the connector as link listener on the DAL property
			dynamicValueProperty.addLinkListener(connector);

			// TODO: send the initial connection state

			// keep the DAL property in mind
			connector.setDalProperty(dynamicValueProperty);

			// keep the PV in mind
			connector.setProcessVariableAddress(pv);

			synchronized (_dalConnectors) {
				assert connector.getDalProperty() != null;
				assert connector.getProcessVariableAddress() != null;
				_dalConnectors.put(pv, connector);
			}
		}

		// connect the connector to the process variable listener
		DalConnector connector = _dalConnectors.get(pv);
		connector.addProcessVariableValueListener((IProcessVariableValueListener<Double>) processVariableValueListener);

		assert connector.getDalProperty() != null;
		assert connector.getProcessVariableAddress() != null;
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
	 * @throws Exception
	 */
	private DynamicValueProperty createProperty(IProcessVariableAddress pv,
			Class propertyType) throws Exception {
		DynamicValueProperty result = null;

		RemoteInfo ri = pv.toDalRemoteInfo();

		PropertyFactory factory = DALPropertyFactoriesProvider.getInstance()
				.getPropertyFactory(pv.getControlSystem());

		boolean exists = factory.getPropertyFamily().contains(ri);

		if (exists) {
			// get the existing property
			try {
				// with the right type
				result = factory.getProperty(ri);
			} catch (RemoteException e) {
				CentralLogger.getInstance().info(null,
						"A remote exception occured: " + e.getMessage());
			}
		} else {
			// create a new property
			result = factory.getProperty(ri, propertyType, null);
		}

		return result;

	}

	private void destroyProperty(DalConnector connector) {
		DynamicValueProperty property = connector.getDalProperty();
		IProcessVariableAddress pv = connector.getProcessVariableAddress();
		PropertyFactory factory = DALPropertyFactoriesProvider.getInstance()
				.getPropertyFactory(pv.getControlSystem());

		if (!property.isDestroyed()) {
			// remove link listener
			property.removeLinkListener(connector);

			// remove value listeners
			property.removeDynamicValueListener(connector);

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
		private DynamicValueProperty _dynamicValueProperty;
		private DynamicValueListener _dynamicValueListener;

		private ConnectionWorkarroundLinkListener(
				DynamicValueProperty dynamicValueProperty,
				DynamicValueListener dynamicValueListener) {
			assert dynamicValueProperty != null;
			assert dynamicValueListener != null;
			_dynamicValueProperty = dynamicValueProperty;
			_dynamicValueListener = dynamicValueListener;

			if (_dynamicValueProperty.isConnected()) {
				// the property is already connected -> we just need to add the
				// dynamic value listener
				_dynamicValueProperty
						.addDynamicValueListener(_dynamicValueListener);
			} else {
				// the property is not connected -> we listen and wait for
				// connection events
				_dynamicValueProperty.addLinkListener(this);
			}
		}

		public void connected(ConnectionEvent e) {
			// connect the dynamic value listener
			_dynamicValueProperty
					.addDynamicValueListener(_dynamicValueListener);

			// disconnect this listener
			_dynamicValueProperty.removeLinkListener(this);
		}

	}

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
		 * Process the complete queue.
		 */
		private synchronized void doCleanup() {
			synchronized (_dalConnectors) {
				List<IProcessVariableAddress> deleteCandidates = new ArrayList<IProcessVariableAddress>();
				
				Iterator<IProcessVariableAddress> it = _dalConnectors.keySet()
						.iterator();

				while (it.hasNext()) {
					IProcessVariableAddress pv = it.next();
					DalConnector connector = _dalConnectors.get(pv);

					if (connector.isDisposable()) {
						deleteCandidates.add(pv);
					}
				}
				
				for(IProcessVariableAddress pv : deleteCandidates) {
					removeConnector(pv);
				}
			}
		}
	}

}
