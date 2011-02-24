/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.reader;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveReaderServiceTracker;
import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator for reader bundle.
 *
 * @author bknerr
 * @since 03.02.2011
 */
public class Activator implements BundleActivator {

    /**
     * The id of this Java plug-in (value <code>{@value}</code>.
     */
    public static final String PLUGIN_ID = "org.csstudio.archive.common.reader";

    private static Activator INSTANCE;
    private static BundleContext CONTEXT;


    // Anti pattern galore - but minimally invasive
    private ArchiveReaderServiceTracker _tracker;

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

    /**
     * Returns the singleton instance.
     *
     * @return the instance
     */
    @Nonnull
    public static Activator getDefault() {
        return INSTANCE;
    }

    /**
     * Is nonnull if the framework doesn't freak it out.
     * @return
     */
	@Nonnull
	static BundleContext getContext() {
		return CONTEXT;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(@Nonnull final BundleContext bundleContext) throws Exception {
		Activator.CONTEXT = bundleContext;

		_tracker = new ArchiveReaderServiceTracker(bundleContext);
		_tracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(@Nonnull final BundleContext bundleContext) throws Exception {
		Activator.CONTEXT = null;
		_tracker.close();
	}

    /**
     * Returns the archive reader service from the service tracker.
     * @return the archive service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException
     */
	@Nonnull
    public IArchiveReaderService getArchiveReaderService() throws OsgiServiceUnavailableException {
        final IArchiveReaderService service =
            (IArchiveReaderService) _tracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive reader service unavailable.");
        }
        return service;
    }
}
