package org.csstudio.platform.model.pvs;

import java.util.HashMap;
import java.util.Map;

import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.impl.CssApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

/**
 * A property factory provider.
 * 
 * @author Sven Wende, Alexander Will
 * 
 */
public final class DALPropertyFactoriesProvider {
	/**
	 * The singleton instance.
	 */
	private static DALPropertyFactoriesProvider _instance;

	/**
	 * A cached property factory, which is based on DAL´s EPICs plug.
	 */
	private PropertyFactory _epicsPropertyFactory;

	/**
	 * A cached property factory, which is based on DAL´s TINE plug.
	 */
	private PropertyFactory _tinePropertyFactory;

	/**
	 * A cached property factory, which is based on DAL´s simulator plug.
	 */
	private PropertyFactory _simulatorPropertyFactory;

	private Map<ControlSystemEnum, PropertyFactory> _propertyFactories;

	private AbstractApplicationContext _applicationContext;

	/**
	 * Constructor.
	 */
	private DALPropertyFactoriesProvider() {
		_propertyFactories = new HashMap<ControlSystemEnum, PropertyFactory>();
		_applicationContext = new CssApplicationContext("CSS"); //$NON-NLS-1$
	}

	/**
	 * Gets the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static DALPropertyFactoriesProvider getInstance() {
		if (_instance == null) {
			_instance = new DALPropertyFactoriesProvider();
		}

		return _instance;
	}

	public PropertyFactory getPropertyFactory(ControlSystemEnum controlSystem) {
		PropertyFactory result = _propertyFactories.get(controlSystem);

		if (result == null) {
			result = DefaultPropertyFactoryService.getPropertyFactoryService()
					.getPropertyFactory(_applicationContext,
							LinkPolicy.ASYNC_LINK_POLICY,
							controlSystem.getResponsibleDalPlugId());
			
			_propertyFactories.put(controlSystem, result);
		}

		return result;
	}
}
