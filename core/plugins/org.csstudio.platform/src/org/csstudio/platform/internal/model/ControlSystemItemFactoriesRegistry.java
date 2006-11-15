package org.csstudio.platform.internal.model;

import java.util.HashMap;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.AbstractControlSystemItemFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * A registry, which provides access to extension that have been provided for
 * the extension point the <b>org.ccstudio.platform.controlSystemItemFactories</b>
 * extension point. For further details please refer to the documentation of
 * that extension point.
 * 
 * @author Sven Wende
 * 
 */
public final class ControlSystemItemFactoriesRegistry {
	/**
	 * The singleton instance.
	 */
	private static ControlSystemItemFactoriesRegistry _instance;

	/**
	 * Contains the factory descriptors.
	 */
	private HashMap<String, FactoryDescriptor> _factories;

	/**
	 * Private constructor.
	 */
	private ControlSystemItemFactoriesRegistry() {
		lookup();
	}

	/**
	 * @return The singleton instance.
	 */
	public static ControlSystemItemFactoriesRegistry getInstance() {
		if (_instance == null) {
			_instance = new ControlSystemItemFactoriesRegistry();
		}

		return _instance;
	}

	/**
	 * Internal lookup of the Eclipse extension registry.
	 */
	private void lookup() {
		_factories = new HashMap<String, FactoryDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = CSSPlatformPlugin.EXTPOINT_CONTROL_SYSTEM_ITEM_FACTORIES;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			String typeId = element.getAttribute("typeId"); //$NON-NLS-1$

			if (_factories.containsKey(typeId)) {
				throw new IllegalArgumentException(
						"Only one item factory for the type >>" + typeId
								+ "<< should be registered.");
			}
			_factories.put(typeId, new FactoryDescriptor(element));
		}
	}

	/**
	 * Gets the control system item factory, which has been registrated for the
	 * specified type id.
	 * 
	 * @param typeId
	 *            the type id
	 * @return a factory, that is responsible for control system items of the
	 *         specified type or null, if no corresponding factory was
	 *         registered as plugin extension
	 */
	public AbstractControlSystemItemFactory getControlSystemItemFactory(
			final String typeId) {
		AbstractControlSystemItemFactory result = null;

		FactoryDescriptor descriptor = _factories.get(typeId);

		if (descriptor != null) {
			result = descriptor.getFactory();
			assert result != null : "type id was valid, but no factory was instantiated";
		}

		return result;
	}

	/**
	 * Descriptor for factory extension, which is used to implement the lazy
	 * loading pattern that is recommended by the Eclipse plugin mechanism.
	 * 
	 * @author Sven Wende
	 * 
	 */
	class FactoryDescriptor {
		/**
		 * Reference to a configuration element of the Eclipse plugin registry.
		 */
		private IConfigurationElement _configurationElement;

		/**
		 * A control system item factory, which is instantiated lazy.
		 */
		private AbstractControlSystemItemFactory _factory;

		/**
		 * Constructs a descriptor, which is based on the specified
		 * configuration element.
		 * 
		 * @param configurationElement
		 *            the configuration element
		 */
		public FactoryDescriptor(
				final IConfigurationElement configurationElement) {
			_configurationElement = configurationElement;
		}

		/**
		 * @return The control system item factory.
		 */
		public AbstractControlSystemItemFactory getFactory() {
			if (_factory == null) {
				try {
					_factory = (AbstractControlSystemItemFactory) _configurationElement
							.createExecutableExtension("class");
				} catch (CoreException e) {
					CentralLogger.getInstance().error(this, e);
				}
			}

			return _factory;
		}
	}

}
