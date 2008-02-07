/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.diag.IOCremoteManagement;


import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.csstudio.utility.ioc_socket_communication.IOCAnswer;
import org.csstudio.utility.ioc_socket_communication.RMTControl;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.IOCremoteManagement"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}



	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	protected void doStart(final BundleContext context) throws Exception {
		System.out.println("inDoStart IOCRemote");
		/*
//		super.start(context);
		File defaultFile = new File(getPluginPreferences().getString(SampleService.IOCremoteManagement_XML_FILE_PATH));
//		System.out.println(" defFileName="+getPluginPreferences().getString(SampleService.IOCremoteManagement_XML_FILE_PATH));
		if(!defaultFile.isFile()){
			if(defaultFile.isDirectory()){
				defaultFile = new File(defaultFile,"IOCremoteManagement.xml"); //$NON-NLS-1$
			}
			else{
				defaultFile = new File(getPluginPreferences().getDefaultString(SampleService.IOCremoteManagement_XML_FILE_PATH));
			}
			getPluginPreferences().setValue(SampleService.IOCremoteManagement_XML_FILE_PATH, defaultFile.toString());
			if(defaultFile.createNewFile()){
				// Albert WriteDefaultXML.writeDefault(defaultFile);
			}
		}
		*/
	}

	@Override
	protected void doStop(final BundleContext context) throws Exception {
		System.out.println("inDoStop IOCRemote");
//TODO		
//		RMTControl.getInstance().close();
		plugin = null;
	}


	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	/** Add informational message to the plugin log. */
    public static void logInfo(final String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }

    /** Add error message to the plugin log. */
    public static void logError(final String message)
    {
        getDefault().log(IStatus.ERROR, message, null);
    }

    /** Add an exception to the plugin log. */
    public static void logException(final String message, final Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     * @param type
     * @param message
     */
    private void log(final int type, final String message, final Exception e)
    {
        getLog().log(new Status(type, PLUGIN_ID, IStatus.OK, message, e));
    }
    
	public static void  errorPrint(String ... argString) {
		String s=PLUGIN_ID + ": Error: ";
		for(String x : argString) s += x+" "; 
		//System.out.println(s);
		logError(s); 
	}
}
