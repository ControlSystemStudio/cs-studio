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

package org.csstudio.alarm.table;



import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;



/**
 * The main plugin class to be used in the desktop.
 */
public class JmsLogsPlugin extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.alarm.table"; //$NON-NLS-1$

	
	//The shared instance.
	private static JmsLogsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public JmsLogsPlugin() {
		plugin = this;
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void doStart(BundleContext context) throws Exception {
//		super.start(context);
//		this.getWorkbench().getViewRegistry();
//		jmsmAdapterFactory = new JMSMessageAdapterFactory();
//		IAdapterManager mgr = Platform.getAdapterManager();
//		mgr.registerAdapters(jmsmAdapterFactory, adaptable)
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void doStop(BundleContext context) throws Exception {
//		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static JmsLogsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.alarm.table", path); //$NON-NLS-1$
	}
	
	/** Add informational message to the plugin log. */
    public static void logInfo(String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }

    /** Add error message to the plugin log. */
    public static void logError(String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type, PLUGIN_ID, IStatus.OK, message, e));
    }
}
