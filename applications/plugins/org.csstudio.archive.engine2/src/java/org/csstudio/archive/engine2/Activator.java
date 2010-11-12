/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine2;

import org.csstudio.archive.service.ArchiveServiceTracker;
import org.csstudio.archive.service.IArchiveService;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
    /** Plug-in ID defined in MANIFEST.MF */
    public static final String PLUGIN_ID = "org.csstudio.archive.engine"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator INSTANCE;

    private ArchiveServiceTracker _archiveServiceTracker;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

    /** @return the shared instance */
    public static Activator getDefault()
    {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        _archiveServiceTracker = new ArchiveServiceTracker(context);
        _archiveServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        if (_archiveServiceTracker != null) {
            _archiveServiceTracker.close();
        }
        super.stop(context);
    }

    /**
     * Returns the archive service from the service tracker.
     * @return the archive service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException
     */
    public IArchiveService getArchiveService() throws OsgiServiceUnavailableException
    {
        final IArchiveService service = (IArchiveService) _archiveServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive service unavailable.");
        }
        return service;
    }
}
