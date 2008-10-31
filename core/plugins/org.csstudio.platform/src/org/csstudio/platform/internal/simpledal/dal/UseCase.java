package org.csstudio.platform.internal.simpledal.dal;

import org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ProcessVariableValueAdapter;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.Timestamp;

public class UseCase {
	public static void main(String[] args) {
		
		
		
		// get a service instance (all applications using the same shared instance will share channels, too)
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		
		// get a factory for process variable addresses 
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance();
		
		// create a process variable address
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress("dal-epics://myproperty");
		
		// read value synchronously
		try {
			Double value = service.readValueSynchronously(pv, ValueType.DOUBLE);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		
		// write value synchronously
		try {
			service.writeValueSynchronously(pv, 98.0, ValueType.DOUBLE);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		// write value asynchronously
		service.writeValueAsynchronously(pv, 98.0, ValueType.DOUBLE);
		
		// create a listener
		IProcessVariableValueListener<Double> listener = new ProcessVariableValueAdapter<Double>() {
			@Override
			public void valueChanged(Double value, Timestamp timestamp) {
				System.out.println(value);
			}

			@Override
			public void errorOccured(String error) {
				System.out.println(error);
			}
		};
		
		// use listener for asynchronous calls
		service.readValueAsynchronously(pv, ValueType.DOUBLE, listener);
		
		// register listener for permanent updates
		service.register(listener, pv, ValueType.DOUBLE);
		
		// unregister a listener explicitly
		service.unregister(listener);
		
		// unregister a service implicitly
		listener = null;
		
	}
}
