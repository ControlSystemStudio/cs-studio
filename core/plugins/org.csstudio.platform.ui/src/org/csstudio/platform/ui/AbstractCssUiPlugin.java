/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui;

import org.csstudio.platform.internal.logging.CssLogListener;
import org.csstudio.platform.internal.xmlprefs.CssPreferences;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.xmlprefs.XMLPreferenceStore;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * Common super class for all CSS UI plugin classes.
 * 
 * @author Alexander Will
 */
public abstract class AbstractCssUiPlugin extends AbstractUIPlugin {
	/**
	 * Log listener that catches log events and redirects them to the central
	 * log service.
	 */
	private ILogListener _logListener;
	
	/**
	 * The preferences used by this plugin.
	 */
	private Preferences _preferences;
	
	/**
	 * The preference store used by this plugin.
	 */
	private IPreferenceStore _preferenceStore;

	/**
	 * Standard constructor.
	 */
	public AbstractCssUiPlugin() {
		_logListener = new CssLogListener();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start(final BundleContext context) throws Exception {
		super.start(context);
		getLog().addLogListener(_logListener);
		doStart(context);
		CentralLogger.getInstance().info(this,
				"Plugin with ID " + getPluginId() + " started"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Hook method that is called from
	 * {@link AbstractCssUiPlugin#start(BundleContext)}.
	 * 
	 * @param context
	 *            the bundle context for this plug-in
	 * @exception Exception
	 *                if this plug-in did not start up properly
	 */
	protected abstract void doStart(final BundleContext context)
			throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop(final BundleContext context) throws Exception {
		super.stop(context);
		savePluginPreferences();
		doStop(context);
		CentralLogger.getInstance().info(this,
				"Plugin with ID " + getPluginId() + " stopped"); //$NON-NLS-1$ //$NON-NLS-2$			
		getLog().removeLogListener(_logListener);
	}

	/**
	 * Hook method that is called from
	 * {@link AbstractCssUiPlugin#stop(BundleContext)}.
	 * 
	 * @param context
	 *            the bundle context for this plug-in
	 * @exception Exception
	 *                if this method fails to shut down this plug-in
	 */
	protected abstract void doStop(final BundleContext context)
			throws Exception;

	/**
	 * Return the ID of this plugin.
	 * 
	 * @return The ID of this plugin.
	 */
	public abstract String getPluginId();
	
	/**
	 * Returns the preference store for this plugin. For CSS plugins, this
	 * method should be used instead of {@code getPluginPreferences()}.
	 * 
	 * @return the preferences for this plugin.
	 * @deprecated use the Eclipse Preferences Service instead.
	 */
	@Deprecated
	public Preferences getCssPluginPreferences() {
		if (_preferences == null) {
			if (useXmlPreferences()) {
				_preferences = new CssPreferences(getPluginId());
			} else {
				_preferences = getPluginPreferences();
			}
		}
		return _preferences;
	}
	
//	/**
//	 * Returns the preference store for this UI-plugin.
//	 * 
//	 * @return the preference store for this plugin.
//	 */
//	@Override
//	public IPreferenceStore getPreferenceStore() {
//		if (_preferenceStore == null) {
//			if (useXmlPreferences()) {
//				_preferenceStore = new XMLPreferenceStore(getPluginId());
//			} else {
//				_preferenceStore = super.getPreferenceStore();
//			}
//		}
//		return _preferenceStore;
//	}

	/**
	 * Returns the preference store for the plugin with
	 * <code>pluginID</code>.
	 * 
	 * @param pluginID of the plugin to get the preference store from
	 * 
	 * @return the preference store for this plugin with
	 * <code>pluginID</code>.
	 */
	@Deprecated
	public IPreferenceStore getPreferenceStore(String pluginID) {
		if (_preferenceStore == null) {
			if (useXmlPreferences()) {
				_preferenceStore = new XMLPreferenceStore(pluginID);
			} else {
				_preferenceStore = new ScopedPreferenceStore( new InstanceScope(),
						pluginID);
			}
		}
		return _preferenceStore;
	}

	
	/**
	 * Returns whether to use the XML-based preference store implementation.
	 * 
	 * @return <code>true</code> if the XML-based preference store should be
	 *         used, <code>false</code> otherwise.
	 */
	private boolean useXmlPreferences() {
		return false;
	}
}
