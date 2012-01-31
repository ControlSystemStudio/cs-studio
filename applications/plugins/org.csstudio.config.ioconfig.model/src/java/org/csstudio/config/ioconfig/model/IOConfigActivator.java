package org.csstudio.config.ioconfig.model;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.service.IDocumentService;
import org.csstudio.config.ioconfig.model.service.IInternId2IONameService;
import org.csstudio.config.ioconfig.model.service.IProcessVariable2IONameService;
import org.csstudio.config.ioconfig.model.service.internal.InternId2IONameImplemation;
import org.csstudio.config.ioconfig.model.service.internal.LogbookDocumentService;
import org.csstudio.config.ioconfig.model.service.internal.ProcessVariable2IONameImplemation;
import org.csstudio.config.ioconfig.model.service.internal.ProfiBusIoNameService;
import org.csstudio.config.ioconfig.model.service.internal.ProfibusSensorService;
import org.csstudio.dct.ISensorIdService;
import org.csstudio.dct.IoNameService;
import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.servicelocator.ServiceLocatorFactory;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class IOConfigActivator extends AbstractCssPlugin {

    /**
     *  The plug-in ID.
     */
    public static final String PLUGIN_ID = "org.csstudio.config.ioconfig.model";

    /**
     *  The shared instance
     */
    private static IOConfigActivator INSTANCE;

    /**
     * The constructor
     */
    public IOConfigActivator() {
        if (INSTANCE != null) { // ENSURE SINGLETON
            throw new IllegalStateException("Class " + PLUGIN_ID + " already exists.");
        }
        INSTANCE = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    @Nonnull
    public static IOConfigActivator getDefault() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStart(@Nonnull final BundleContext context) throws Exception {
        ServiceLocatorFactory.registerServiceWithTracker("LogbookDocumentService", context, IDocumentService.class, new LogbookDocumentService());
        ServiceLocatorFactory.registerServiceWithTracker("InternId2IONameImplemation", context, IInternId2IONameService.class, new InternId2IONameImplemation());
        ServiceLocatorFactory.registerServiceWithTracker("ProcessVariable2IONameImplemation", context, IProcessVariable2IONameService.class, new ProcessVariable2IONameImplemation());
        ServiceLocatorFactory.registerServiceWithTracker("ProfiBusIoNameService", context, IoNameService.class, new ProfiBusIoNameService());
        ServiceLocatorFactory.registerServiceWithTracker("ProfibusSensorService", context, ISensorIdService.class, new ProfibusSensorService());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doStop(@Nonnull final BundleContext context) throws Exception {
        // nothing to stop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }

}
