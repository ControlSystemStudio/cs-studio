package org.csstudio.platform.model.pvs;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal.CssApplicationContext;
import org.epics.css.dal.context.AbstractApplicationContext;
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
	 * Cached property factories for DAL.
	 */
	private Map<ControlSystemEnum, PropertyFactory> _propertyFactories;

	/**
	 * A DAL application context.
	 */
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
	public static synchronized DALPropertyFactoriesProvider getInstance() {
		if (_instance == null) {
			_instance = new DALPropertyFactoriesProvider();
		}

		return _instance;
	}

	/**
	 * Returns a DAL {@link PropertyFactory} for the specified control system.
	 * 
	 * @param controlSystem the control system
	 * 
	 * @return a DAL property factory
	 */
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
