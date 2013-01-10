
/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.archive.sdds.server;

import javax.annotation.Nonnull;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class SddsServerActivator extends Plugin {

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.csstudio.archive.sdds.server";

    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(SddsServerActivator.class);

    /** The shared instance */
    private static SddsServerActivator PLUGIN;

    private GenericServiceTracker<ISessionService> _genericServiceTracker;

    /**
     * The constructor
     */
    public SddsServerActivator() {
        PLUGIN = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    @Nonnull
    public static SddsServerActivator getDefault() {
        return PLUGIN;
    }

    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public void addSessionServiceListener(@Nonnull final IGenericServiceListener<ISessionService> sessionServiceListener) {
        _genericServiceTracker.addServiceListener(sessionServiceListener);
    }

    @Override
    public void start(@Nonnull final BundleContext context) throws Exception {
        super.start(context);
        LOG.info(PLUGIN_ID + " is starting.");
        _genericServiceTracker = new GenericServiceTracker<ISessionService>(
                context, ISessionService.class);
        _genericServiceTracker.open();
    }

    @Override
    public void stop(@Nonnull final BundleContext context) throws Exception {
        super.stop(context);
        PLUGIN = null;
        LOG.info(PLUGIN_ID + " is stopping.");
    }
}
