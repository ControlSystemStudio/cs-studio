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
 package org.csstudio.utility.ldap;
//
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.platform.AbstractCssPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public final class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.ldap";

	//	 The shared instance
	private static Activator INSTANCE;

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#doStart(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStart(@Nullable final BundleContext context) throws Exception {
	    // Empty
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#doStop(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void doStop(@Nullable final BundleContext context) throws Exception {
	    // Empty
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.AbstractCssPlugin#getPluginId()
	 */
	@Override
	@Nonnull
	public String getPluginId() {
		return PLUGIN_ID;
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the instance
 	 */
	@Nonnull
	public static Activator getDefault() {
	    if (INSTANCE == null) {
	        INSTANCE  = new Activator();
	    }
		return INSTANCE;
	}

    /**
     * Add informational message to the plugin log.
     * @param message info message
     */
    public static void logInfo(@Nonnull final String message) {
        getDefault().log(IStatus.INFO, message, null);
    }

    /**
     * Add error message to the plugin log.
     * @param message error message
     */
    public static void logError(@Nonnull final String message) {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /**
     * Add an exception to the plugin log.
     * @param message error message
     * @param e exception
     */
    public static void logException(@Nonnull final String message, @Nullable final Exception e) {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param severity the severity; one of <code>OK</code>, <code>ERROR</code>,
     * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
     * @param message
     */
    private void log(final int severity, @Nonnull final String message, @Nullable final Exception e) {
        getLog().log(new Status(severity, PLUGIN_ID, IStatus.OK, message, e));
    }
}
