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
 package org.csstudio.platform.model.pvs;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;

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
	 * @param controlSystem
	 *            the control system
	 * 
	 * @return a DAL property factory
	 */
	public synchronized PropertyFactory getPropertyFactory(ControlSystemEnum controlSystem) {
		if (!controlSystem.isSupportedByDAL()) {
			throw new IllegalArgumentException(
					"Control System "
							+ controlSystem
							+ " is currently not supported by the data access layer (DAL).");
		} else {
			PropertyFactory result = _propertyFactories.get(controlSystem);

			if (result == null) {
				
				result = DefaultPropertyFactoryService
						.getPropertyFactoryService().getPropertyFactory(
								_applicationContext,
								LinkPolicy.ASYNC_LINK_POLICY,
								controlSystem.getResponsibleDalPlugId());

				_propertyFactories.put(controlSystem, result);
			}

			return result;
		}
	}

	
	/**
	 * Returns the {@link AbstractApplicationContext} to set properties
	 * from DAL plugs to the configuration.
	 * 
	 * @return ApplicationContext
	 */
	public AbstractApplicationContext getApplicationContext() {
	    return _applicationContext;
	}
}
