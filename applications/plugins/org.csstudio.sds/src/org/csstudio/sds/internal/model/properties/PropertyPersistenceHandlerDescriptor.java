package org.csstudio.sds.internal.model.properties;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Descriptor for extensions of the <code>propertyPersistenceHandlers</code>
 * extension point.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class PropertyPersistenceHandlerDescriptor {
	/**
	 * Reference to a configuration element of the Eclipse plugin registry.
	 */
	private IConfigurationElement _configurationElement;

	/**
	 * A lazy instantiated property persistence handler.
	 */
	private AbstractPropertyPersistenceHandler _persistenceHandler;

	/**
	 * Constructs a descriptor, which is based on the specified configuration
	 * element.
	 * 
	 * @param configurationElement
	 *            the configuration element
	 */
	public PropertyPersistenceHandlerDescriptor(
			final IConfigurationElement configurationElement) {
		_configurationElement = configurationElement;
	}

	/**
	 * Instantiate and return the persistence handler.
	 * 
	 * @return The property persistence handler.
	 */
	public AbstractPropertyPersistenceHandler getPersistenceHandler() {
		if (_persistenceHandler == null) {
			try {
				_persistenceHandler = (AbstractPropertyPersistenceHandler) _configurationElement
						.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				CentralLogger.getInstance().error(this, e);
			}
		}

		return _persistenceHandler;
	}
}
