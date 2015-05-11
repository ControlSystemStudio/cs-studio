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
package org.csstudio.sds.model.initializers;

import java.util.HashMap;
import java.util.Set;

import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.initializers.ManualSchema;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service that can initialize widgets using contributions of the
 * <code>controlSystemSchema</code> and the
 * <code>widgetModelInitializers</code> extension points.
 *
 * @author Stefan Hofer & Sven Wende
 * @version $Revision: 1.5 $
 *
 */
public final class WidgetInitializationService {
    private static final Logger LOG = LoggerFactory.getLogger(WidgetInitializationService.class);

    /**
     * Separator token.
     */
    private static final String ID_SEPARATOR = ":"; //$NON-NLS-1$

    /**
     * A proxy for lazy loading of initializers.
     *
     * @author Stefan Hofer
     * @version $Revision: 1.5 $
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
        public AbstractWidgetModelInitializer createInitializer() {
            AbstractWidgetModelInitializer initializer = null;
            try {
                initializer = (AbstractWidgetModelInitializer) _configurationElement
                        .createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                LOG.error(e.toString());
            }
            return initializer;
        }

    }

    /**
     * A proxy for lazy loading of schema objects.
     *
     * @author Stefan Hofer
     * @version $Revision: 1.5 $
     *
     */
    public final class ControlSystemSchemaDescriptor {
        /**
         * The configuration element.
         */
        private final IConfigurationElement _configurationElement;

        private String _id;

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
                final String id, final String description) {
            assert id != null;
            assert description != null;
            assert configurationElement != null;
            _id = id;
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
                LOG.error(e.toString());
            }
            return schema;
        }

        /**
         * @return The description of the schema.
         */
        public String getDescription() {
            return _description;
        }

        public String getId() {
            return _id;
        }
    }

    /**
     * The singleton instance.
     */
    private static WidgetInitializationService _instance;

    /**
     * Holds the proxies for lazy loading.
     */
    private HashMap<String, InitializerDescriptor> _initializerDescriptors;

    /**
     * Holds the proxies for lazy loading.
     */
    private HashMap<String, ControlSystemSchemaDescriptor> _schemaDescriptors;

    /**
     * Property ID for the schema setting.
     */
    public static final String PROP_SCHEMA = "schema"; //$NON-NLS-1$

    /**
     * Private constructor because of singleton pattern. Use
     * {@link #getInstance()}.
     */
    private WidgetInitializationService() {
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
                            new ControlSystemSchemaDescriptor(element, typeId,
                                    description));
                }
            }
        }
    }

    /**
     * @return The singleton instance of this class.
     */
    public static WidgetInitializationService getInstance() {
        if (_instance == null) {
            _instance = new WidgetInitializationService();
        }
        return _instance;
    }

    /**
     * Initializes the specified widget using the schema that is configured via
     * the according preference pages.
     *
     * @param widget
     *            the widget
     */
    public void initialize(final AbstractWidgetModel widget) {
        ControlSystemSchemaDescriptor descriptor = getPreferredSchema();

        if (descriptor != null) {
            initialize(widget, descriptor);
        }
    }

    /**
     * Initializes the specified widget using the specified schema.
     *
     * @param widget
     */
    public void initialize(final AbstractWidgetModel widget, String schemaId) {
        assert widget != null;
        assert schemaId != null;
        ControlSystemSchemaDescriptor descriptor = getInitializationSchemaDescriptors()
                .get(schemaId);

        if (descriptor != null) {
            initialize(widget, descriptor);
        }
    }

    /**
     * Returns the currently selected schema. A schema is selected via
     * preference page.
     *
     * @return the currently selected schema
     */
    private ControlSystemSchemaDescriptor getPreferredSchema() {
        String schemaId = Platform.getPreferencesService().getString(
                SdsPlugin.PLUGIN_ID, WidgetInitializationService.PROP_SCHEMA,
                ManualSchema.ID, null);

        ControlSystemSchemaDescriptor schemaDescriptor = null;

        schemaDescriptor = getInitializationSchemaDescriptors().get(schemaId);

        return schemaDescriptor;
    }

    /**
     * Initializes the model.
     *
     * @param widget
     *            The widget model that should be initialized.
     * @param schemaDescriptor
     *            The ID of the schema that should be used to initialize the
     *            model.
     */
    private void initialize(final AbstractWidgetModel widget,
            ControlSystemSchemaDescriptor schemaDescriptor) {
        assert widget != null;

//        CentralLogger.getInstance().info(null, "Initialization has been skipped. Initializers will be removed completely, soon!!");
//        return;

        if (schemaDescriptor != null) {
            AbstractControlSystemSchema schema = schemaDescriptor
                    .createSchema();

            InitializerDescriptor descriptor = _initializerDescriptors
                    .get(schemaDescriptor.getId() + ID_SEPARATOR
                            + widget.getTypeID());

            if (schema != null) {
                // let the schema implementation initialize widget defaults
                schema.setWidgetModel(widget);
                schema.initialize();

                if (descriptor != null) {
                    AbstractWidgetModelInitializer initializer = descriptor
                            .createInitializer();

                    if (initializer != null) {
                        // inject the model
                        initializer.setWidgetModel(widget);

                        // initialize the widget
                        initializer.initialize(schema);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<ConnectionState> getSupportedConnectionStates() {
        AbstractControlSystemSchema schema = getPreferredSchema()
                .createSchema();

        return schema.getSupportedConnectionStates();
    }

    /**
     * @return Descriptors of all contributed schema.
     */
    public HashMap<String, ControlSystemSchemaDescriptor> getInitializationSchemaDescriptors() {
        return _schemaDescriptors;
    }
}
