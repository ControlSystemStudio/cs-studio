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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

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
		// TODO: generating a log message lets console messages appear on the
		// Eclipse internal console instead of the CSS console
		// getLog()
		// .log(
		// new Status(IStatus.INFO, ID, 0,
		// "CSS core plugin started", null)); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doStop(final BundleContext context) throws Exception {
		// generate a sample eclipse log message...
		getLog()
				.log(
						new Status(IStatus.INFO, ID, 0,
								"CSS core plugin stopped", null)); //$NON-NLS-1$
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Return the shared instance.
	 */
	public static CSSPlatformPlugin getDefault() {
		return _plugin;
	}

}
