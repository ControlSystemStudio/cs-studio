package org.csstudio.dct;

import org.csstudio.dct.model.persistence.IPersistenceService;
import org.csstudio.dct.model.persistence.internal.PersistenceService;
import org.csstudio.dct.nameresolution.IFieldFunctionService;
import org.csstudio.dct.nameresolution.internal.FieldFunctionService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public final class DctActivator extends AbstractUIPlugin {
    private IPersistenceService persistenceService;
    private IFieldFunctionService fieldFunctionService;

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.dct";

    /**
     * The ID of the "Exporters" extension point.
     */
    public static final String EXTPOINT_EXPORTERS = PLUGIN_ID + ".exporters"; //$NON-NLS-1$

    public static final String EXTPOINT_FIELDFUNCTIONS = PLUGIN_ID + ".fieldfunctions"; //$NON-NLS-1$

    public static final String EXTPOINT_IO_NAME_SERVICE = PLUGIN_ID + ".ionameservices"; //$NON-NLS-1$

    public static final String EXTPOINT_SENSOR_ID_SERVICES = PLUGIN_ID + ".sensoridservices"; //$NON-NLS-1$

    public static final String EXTPOINT_RECORD_FUNCTIONS = PLUGIN_ID + ".recordfunctions"; //$NON-NLS-1$

    // The shared instance
    private static DctActivator plugin;

    /**
     * The constructor.
     */
    public DctActivator() {
        persistenceService = new PersistenceService();
        fieldFunctionService = new FieldFunctionService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static DctActivator getDefault() {
        return plugin;
    }

    /**
     * Returns the persistence service.
     *
     * @return the persistence service
     */
    public IPersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Returns the field function service.
     *
     * @return the field function service
     */
    public IFieldFunctionService getFieldFunctionService() {
        return fieldFunctionService;
    }
}
