/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveEngineServiceTracker;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/** Plugin Activator
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public class ArchiveEngineActivator extends Plugin {
    /**
     * The id of this Java plug-in (value <code>{@value}</code> as defined in MANIFEST.MF.
     */
    public static final String PLUGIN_ID = "org.csstudio.archive.common.engine"; //$NON-NLS-1$

    /** The shared instance */
    private static ArchiveEngineActivator INSTANCE;

    // FIXME (bknerr) : find out about proper dependency injection for osgi eclipse rcp
    private ArchiveEngineServiceTracker _archiveEngineServiceTracker;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public ArchiveEngineActivator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

    /** @return the shared instance */
    @Nonnull
    public static ArchiveEngineActivator getDefault() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public void start(@Nonnull final BundleContext context) throws Exception {
        super.start(context);

        _archiveEngineServiceTracker = new ArchiveEngineServiceTracker(context);
        _archiveEngineServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(@Nonnull final BundleContext context) throws Exception {

        if (_archiveEngineServiceTracker != null) {
            _archiveEngineServiceTracker.close();
        }

        super.stop(context);
    }


    /**
     * Returns the archive engine config service from the service tracker.
     *
     * Don't use directly but via the service provider injected by GUICE.
     *
     * @return the archive service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException
     */
    @Nonnull
    public IArchiveEngineFacade getArchiveEngineService() throws OsgiServiceUnavailableException {
        final IArchiveEngineFacade service =
            (IArchiveEngineFacade) _archiveEngineServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive engine config service unavailable.");
        }
        return service;
    }

}
