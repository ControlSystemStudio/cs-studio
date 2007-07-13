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
package org.csstudio.platform;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The activator for the platform plugin.
 * 
 * @author Alexander Will, Sven Wende
 */
public class CSSPlatformPlugin extends AbstractCssPlugin {
	/**
	 * The shared instance of this _plugin class.
	 */
	private static CSSPlatformPlugin _plugin;

	/**
	 * This plugin's ID.
	 */
	public static final String ID = "org.csstudio.platform"; //$NON-NLS-1$

	/**
	 * Extension point ID for the <b>controlSystemItemFactories</b> extension
	 * point.
	 */
	public static final String EXTPOINT_CONTROL_SYSTEM_ITEM_FACTORIES = ID
			+ ".controlSystemItemFactories"; //$NON-NLS-1$

	/**
	 * Standard constructor.
	 */
	public CSSPlatformPlugin() {
		_plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doStart(final BundleContext context) throws Exception {
		applySystemPropertyDefaults();
	}

	/**
	 * Applies the default values for system properties set up in the CSS
	 * preferences.
	 */
	private void applySystemPropertyDefaults() {
		// FIXME: fix code duplication (same code is in SystemPropertiesPreferencePage)
		
        IEclipsePreferences platformPrefs = new InstanceScope().getNode(
                CSSPlatformPlugin.getDefault().getBundle().getSymbolicName());
        Preferences systemPropertyPrefs =
            platformPrefs.node("systemProperties");
        try {
            String[] keys = systemPropertyPrefs.keys();
            for (String key : keys) {
                String value = systemPropertyPrefs.get(key, "");
                // the preferences are for defaults, so they are applied only
                // if the property is not already set
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                    CentralLogger.getInstance().debug(this, 
                    		"Setting system property: " + key + "=" + value);
                }
            }
        } catch (BackingStoreException e) {
            // TODO: do something about it?
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doStop(final BundleContext context) throws Exception {
		// do nothing specific
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Return the shared instance.
	 */
	public static CSSPlatformPlugin getDefault() {
		return _plugin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginId() {
		return ID;
	}

}
