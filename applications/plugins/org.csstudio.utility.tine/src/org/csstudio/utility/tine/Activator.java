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
 package org.csstudio.utility.tine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.utility.tine.preference.PreferenceConstants;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {
//public class Activator extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.utility.tine";

    //   The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
        plugin = this;
        System.out.println("Activate "+PLUGIN_ID);
    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.AbstractCssPlugin#doStart(org.osgi.framework.BundleContext)
     */
    @Override
    protected void doStart(BundleContext context) throws Exception {
        System.out.println("Start "+PLUGIN_ID+" \t init tine" );
        String configFileName = "cshosts.csv";
        // Slash(/) is the main path of the plugin Plugins
        String prop = System.getProperty("TINE_HOME");
        if(prop == null){
            System.setProperty("TINE_HOME",getPluginPreferences().getString(PreferenceConstants.TINE_CONFIG_PATH));
            prop = System.getProperty("TINE_HOME");
        }
        File path = new File( prop);
        System.out.println("-----");
        System.out.println("path: "+path.getAbsolutePath());
        System.out.println("-----");
        if(!path.isDirectory()){
            System.setProperty("TINE_HOME",getPluginPreferences().getDefaultString(PreferenceConstants.TINE_CONFIG_PATH));
            prop = System.getProperty("TINE_HOME");
            path = new File( prop);
        }
        File configFile = new File(path,configFileName);
        System.out.println("configFile: "+configFile.getAbsolutePath());
        System.out.println("-----");
        try {
            // Copy the default configfile when dosen't exist
            if(!(configFile.isFile()&&configFile.canRead())){
                if(!path.isDirectory()&&!path.mkdirs()){
                    System.out.println("Can not create path!!!");
                }
                if(!configFile.createNewFile()){
                    System.out.println("Can not create file!!!");
                }
                URL pluginURL = getBundle().getEntry("/");
                String plugInPath;
                plugInPath = FileLocator.resolve(pluginURL).getPath();
                File defaultFile = new File(plugInPath,configFileName);
                System.out.println("defaultFile: "+defaultFile.getAbsolutePath());
                FileInputStream in = new FileInputStream(defaultFile);
                FileOutputStream out = new FileOutputStream(configFile);
                try {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException e) {
                    System.err.println(e.toString());
                } finally{
                    if (in != null) {
                        try {
                            in.close();
                        }
                        finally {
                            if (out != null) {
                                out.close();
                            }
                        }
                    }
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


    }

    /* (non-Javadoc)
     * @see org.csstudio.platform.AbstractCssPlugin#doStop(org.osgi.framework.BundleContext)
     */
    @Override
    protected void doStop(BundleContext context) throws Exception { }

    /* (non-Javadoc)
     * @see org.csstudio.platform.AbstractCssPlugin#getPluginId()
     */
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public static Activator getDefault() {
        return plugin;
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
