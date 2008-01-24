package org.csstudio.sds.model.initializers;

import java.util.HashMap;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.initializers.ManualSchema;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * A service that provides access to contributions of the
 * <code>controlSystemSchema</code> and the
 * <code>widgetModelInitializers</code> extension points.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public final class ModelInitializationService {

	/**
	 * Token that seperates schema IDs from initializer IDs.
	 */
	private static final String ID_SEPARATOR = ":"; //$NON-NLS-1$

	/**
	 * A proxy for lazy loading of initializers.
	 * 
	 * @author Stefan Hofer
	 * @version $Revision$
	 * 
	 */
	class InitializerDescriptor {

		/**
		 * The configuration element.
		 */
		private final IConfigurationElement _configurationElement;

		/**
		 * Constructor.
		 * 
		 * @param configurationElement
		 *            required
		 */
		public InitializerDescriptor(
				final IConfigurationElement configurationElement) {
			assert configurationElement != null;
			_configurationElement = configurationElement;
		}

		/**
		 * Returns a new initializer instance.
		 * 
		 * @return a new widget model initializer
		 */
		@SuppressWarnings("unchecked")
		public AbstractWidgetModelInitializer createInitializer() {
			AbstractWidgetModelInitializer initializer = null;
			try {
				initializer = (AbstractWidgetModelInitializer) _configurationElement
						.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				CentralLogger.getInstance().error(this, e);
			}
			return initializer;
		}

	}

	/**
	 * A proxy for lazy loading of schema objects.
	 * 
	 * @author Stefan Hofer
	 * @version $Revision$
	 * 
	 */
	public final class ControlSystemSchemaDescriptor {
		/**
		 * The configuration element.
		 */
		private final IConfigurationElement _configurationElement;

		/**
		 * A human readable description of the initialization schema.
		 */
		private String _description;

		/**
		 * Constructor.
		 * 
		 * @param configurationElement
		 *            Required.
		 * @param description
		 *            A human readable description of the initialization schema.
		 */
		public ControlSystemSchemaDescriptor(
				final IConfigurationElement configurationElement,
				final String description) {
			assert configurationElement != null;
			_configurationElement = configurationElement;
			_description = description;
		}

		/**
		 * Returns a new schema instance.
		 * 
		 * @return a new control system schema
		 */
		public AbstractControlSystemSchema createSchema() {
			AbstractControlSystemSchema schema = null;
			try {
				schema = (AbstractControlSystemSchema) _configurationElement
						.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				CentralLogger.getInstance().error(this, e);
			}
			return schema;
		}

		/**
		 * @return The description of the schema.
		 */
		public String getDescription() {
			return _description;
		}
	}

	/**
	 * The singleton instance.
	 */
	private static ModelInitializationService _instance;

	/**
	 * Holds the proxies for lazy loading.
	 */
	private HashMap<String, InitializerDescriptor> _initializerDescriptors;

	/**
	 * Holds the proxies for lazy loading.
	 */
	private HashMap<String, ControlSystemSchemaDescriptor> _schemaDescriptors;

	/**
	 * Private constructor because of singleton pattern. Use
	 * {@link #getInstance()}.
	 */
	private ModelInitializationService() {
		lookupSchema();
		lookupInitializers();
	}

	/**
	 * Reads the extension point registry.
	 * 
	 */
	private void lookupInitializers() {
		_initializerDescriptors = new HashMap<String, InitializerDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsPlugin.EXTPOINT_WIDGET_MODEL_INITIALIZERS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			if (element.getName().equals("widgetModelInitializer")) {
				String schemaId = element.getAttribute("schemaId"); //$NON-NLS-1$
				String widgetId = element.getAttribute("widgetTypeId"); //$NON-NLS-1$

				if (schemaId != null && widgetId != null
						&& schemaId.length() > 0 && widgetId.length() > 0) {
					_initializerDescriptors.put(schemaId + ID_SEPARATOR
							+ widgetId, new InitializerDescriptor(element));
				}
			}
		}
	}

	/**
	 * Reads the extension point registry.
	 * 
	 */
	private void lookupSchema() {
		_schemaDescriptors = new HashMap<String, ControlSystemSchemaDescriptor>();

		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsPlugin.EXTPOINT_WIDGET_MODEL_INITIALIZERS;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			if (element.getName().equals("controlSystemSchema")) {
				String typeId = element.getAttribute("schemaId"); //$NON-NLS-1$
				String description = element.getAttribute("description"); //$NON-NLS-1$

				if (typeId != null) {
					_schemaDescriptors.put(typeId,
							new ControlSystemSchemaDescriptor(element,
									description));
				}
			}
		}
	}

	/**
	 * @return The singleton instance of this class.
	 */
	public static ModelInitializationService getInstance() {
		if (_instance == null) {
			_instance = new ModelInitializationService();
		}
		return _instance;
	}

	/**
	 * Initializes the model.
	 * 
	 * @param model
	 *            The widget model that should be initialized.
	 * @param schemaId
	 *            The ID of the schema that should be used to initialize the
	 *            model.
	 */
	public void initialize(final AbstractWidgetModel model,
			final String schemaId) {
		assert model != null;
		assert schemaId != null;

		if (!ManualSchema.ID.equals(schemaId)) {
			InitializerDescriptor descriptor = _initializerDescriptors
					.get(schemaId + ID_SEPARATOR + model.getTypeID());

			ControlSystemSchemaDescriptor schemaDescriptor = getInitializationSchemaDescriptors()
					.get(schemaId);

			if (schemaDescriptor != null) {
				AbstractControlSystemSchema schema = getInitializationSchemaDescriptors()
						.get(schemaId).createSchema();

				if (schema != null) {
					// let the schema implementation initialize widget defaults
					schema.initializeAliases(model);
					schema.initializeWidget(model);

					if (descriptor != null) {
						AbstractWidgetModelInitializer initializer = descriptor
								.createInitializer();

						if (initializer != null) {
							// inject the model
							initializer.setWidgetModel(model);

							// initialize the widget
							initializer.initialize(schema);
						}
					}
				}
			}
		}
	}

	/**
	 * @return Descriptors of all contributed schema.
	 */
	public HashMap<String, ControlSystemSchemaDescriptor> getInitializationSchemaDescriptors() {
		return _schemaDescriptors;
	}
}
