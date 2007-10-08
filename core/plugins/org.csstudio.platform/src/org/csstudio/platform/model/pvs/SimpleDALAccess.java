package org.csstudio.platform.model.pvs;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * Simple access to the control systems via DAL.
 * (Proposed by Igor Kriznar) 
 * 
 * @author jhatje
 *
 */
public class SimpleDALAccess {

	private static PropertyFactory factory;
	
	public static DynamicValueProperty<?> getProperty(String name) throws RemoteException, InstantiationException {
		IProcessVariableAddress pv =ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(name);
		factory = DALPropertyFactoriesProvider.getInstance().getPropertyFactory(pv.getControlSystem());
	
		DynamicValueProperty property = factory.getProperty(name);
		
		return property;
	}
	
	public static void dispose(DynamicValueProperty property) {
		if (property==null) return;
		factory.getPropertyFamily().destroy(property);
	}
}
