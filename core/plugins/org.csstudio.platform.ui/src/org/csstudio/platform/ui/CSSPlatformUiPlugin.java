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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.platform.CSSPlatformPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator for the CSS platform UI plugin.
 * 
 * @author Alexander Will
 */
public class CSSPlatformUiPlugin extends AbstractCssUiPlugin {
    
    
	/**
	 * This _plugin's ID.
	 */
	public static final String ID = "org.csstudio.platform.ui"; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(ID);

	/**
	 * The shared instance of this plugin class.
	 */
	private static CSSPlatformUiPlugin _plugin;

	/**
	 * The preference store to access the css core preferences.
	 */
	private static IPreferenceStore _preferenceStore;

	/**
	 * Standard constructor.
	 */
	public CSSPlatformUiPlugin() {
		_plugin = this;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Return the shared instance.
	 */
	public static CSSPlatformUiPlugin getDefault() {
		return _plugin;
	}

	/**
	 * Return the preference store of the css core plugin.
	 * 
	 * @return The preference store of the css core plugin.
	 */
	public static IPreferenceStore getCorePreferenceStore() {
		if (_preferenceStore == null) {
			_preferenceStore = new ScopedPreferenceStore(new InstanceScope(),
					CSSPlatformPlugin.getDefault().getBundle()
							.getSymbolicName());

			_preferenceStore
					.addPropertyChangeListener(new IPropertyChangeListener() {
						@Override
                        public void propertyChange(
								final PropertyChangeEvent event) {
						    
						    Object[] args = new Object[] {event.getProperty(), event.getOldValue(), event.getNewValue()};
							LOG.log(Level.INFO,"Property [{}] changed from [{}] to [{}]", args); //$NON-NLS-1$
						}
					});
		}

		return _preferenceStore;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doStart(final BundleContext context) throws Exception {
		// do nothing specific
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doStop(final BundleContext context) throws Exception {
		// do nothing specific
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginId() {
		return ID;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            The path
	 * @return an image descriptor.
	 */
    public static ImageDescriptor getImageDescriptor(final String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
    }
}
