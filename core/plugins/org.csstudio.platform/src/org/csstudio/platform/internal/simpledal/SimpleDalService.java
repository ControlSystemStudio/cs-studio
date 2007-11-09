package org.csstudio.platform.internal.simpledal;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ISimpleDalListener;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.context.RemoteInfo;
import org.epics.css.dal.spi.PropertyFactory;

public class SimpleDalService {
	private Map<IProcessVariableAddress, DalConnectorListener> _dalConnectorListeners;

	SimpleDalService() {
		_dalConnectorListeners = new HashMap<IProcessVariableAddress, DalConnectorListener>();

	}

	public void registerForIntValues(ISimpleDalListener<Integer> listener,
			IProcessVariableAddress pv) throws Exception {
		doRegister(pv, LongProperty.class, listener);
	}

	public void registerForDoubleValues(ISimpleDalListener<Double> listener,
			IProcessVariableAddress pv) throws Exception {
		doRegister(pv, DoubleProperty.class, listener);
	}
	
	private void doRegister(IProcessVariableAddress pv, Class<? extends DynamicValueProperty> propertyType, ISimpleDalListener simpleDalListener) throws Exception {
		if (!_dalConnectorListeners.containsKey(pv)) {
			final DalConnectorListener connectorListener = new DalConnectorListener();
			
			_dalConnectorListeners.put(pv, connectorListener);

			final DynamicValueProperty property = createProperty(pv, propertyType);
			
			// auf DAL anmelden
			property.addLinkListener(new LinkListener(){

				public void connected(ConnectionEvent e) {
					property.addDynamicValueListener(connectorListener);
				}

				public void connectionFailed(ConnectionEvent e) {
					
				}

				public void connectionLost(ConnectionEvent e) {
					
				}

				public void destroyed(ConnectionEvent e) {
					
				}

				public void disconnected(ConnectionEvent e) {
					
				}

				public void resumed(ConnectionEvent e) {
					
				}

				public void suspended(ConnectionEvent e) {
					
				}
			});
			
		}

		DalConnectorListener dalConnectorListener = _dalConnectorListeners
				.get(pv);

		// SimpleDal Listener anmelden
		
		dalConnectorListener.addSimpleDalListener(simpleDalListener);
	}

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
			result = factory.getProperty(ri, propertyType, null);
		}
		
		return result;

	}

}
