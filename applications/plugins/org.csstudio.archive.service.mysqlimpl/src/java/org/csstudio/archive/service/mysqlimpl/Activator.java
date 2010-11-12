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
package org.csstudio.archive.service.mysqlimpl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.csstudio.archive.service.ArchiveServiceTracker;
import org.csstudio.archive.service.IArchiveService;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    final public static String PLUGIN_ID = "org.csstudio.archive.service.mysqlimpl";

    private static final Logger LOG = CentralLogger.getInstance().getLogger(Activator.class);

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

    /**
     * Returns the singleton instance.
     *
     * @return the instance
     */
    public static Activator getDefault() {
        return INSTANCE;
    }

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(final BundleContext context) throws Exception {

	    final Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("service.vendor", "SNS");
        props.put("service.description", "MySQL archive service implementation");
        LOG.info("Register Oracle archive service");

        context.registerService(IArchiveService.class.getName(),
                                MySQLArchiveServiceImpl.INSTANCE,
                                props);

        _archiveServiceTracker = new ArchiveServiceTracker(context);
        _archiveServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(final BundleContext bundleContext) throws Exception {
	    // Service is automatically unregistered

	    if (_archiveServiceTracker != null) {
	        _archiveServiceTracker.close();
	    }
	}

    /**
     * Returns the archive service from the service tracker.
     * @return the archive  service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException if the service cannot be retrieved
     */
    public IArchiveService getArchiveService() throws OsgiServiceUnavailableException
    {
        final IArchiveService service = (IArchiveService) _archiveServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive service tracker returned null.");
        }
        return service;
    }
}
