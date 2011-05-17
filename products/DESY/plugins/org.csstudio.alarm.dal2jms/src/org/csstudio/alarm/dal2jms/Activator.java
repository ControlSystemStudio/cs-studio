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
package org.csstudio.alarm.dal2jms;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 02.06.2010
 */
public class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.alarm.dal2jms";

	// The shared instance
	private static Activator INSTANCE;

    private IAlarmService _alarmService;
    
	private GenericServiceTracker<ISessionService> _genericServiceTracker;


	/**
	 * The constructor
	 */
	public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
	}

	@Override
	public String getPluginId() {
	    return PLUGIN_ID;
	}

	@Override
	public void doStart(@Nullable final BundleContext context) throws Exception {

		_alarmService = getService(context, IAlarmService.class);
		_genericServiceTracker = new GenericServiceTracker<ISessionService>(
				context, ISessionService.class);
		_genericServiceTracker.open();
	}

	@Override
	public void doStop(@Nullable final BundleContext context) throws Exception {
		INSTANCE = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	@Nonnull
	public static Activator getDefault() {
		return INSTANCE;
	}

    /**
     * @return the alarm service or null
     */
	@CheckForNull
    public IAlarmService getAlarmService() {
        return _alarmService;
    }

	public void addSessionServiceListener(
			IGenericServiceListener<ISessionService> sessionServiceListener) {
		_genericServiceTracker.addServiceListener(sessionServiceListener);
	}

}
