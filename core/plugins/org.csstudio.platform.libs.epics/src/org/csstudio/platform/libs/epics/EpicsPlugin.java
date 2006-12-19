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
package org.csstudio.platform.libs.epics;

import gov.aps.jca.jni.JNITargetArch;

import org.csstudio.platform.libs.epics.preferences.PreferenceConstants;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/** The main plugin class to be used in the desktop.
 *  @author Original author unknown
 *  @author Kay Kasemir
  */
@SuppressWarnings("nls")
public class EpicsPlugin extends AbstractCssUiPlugin
{
	public static final String ID = "org.csstudio.platform.libs.epics"; //$NON-NLS-1$
    //The shared instance.
	private static EpicsPlugin plugin;
    private boolean use_pure_java;
	
	/** The constructor. */
	@SuppressWarnings("nls")
    public EpicsPlugin()
    {
        super();
		plugin = this;
        
	}
    
    /** @return <code>true</code> if preferences suggest the use
     *  of pure java CA.
     */
    public boolean usePureJava()
    {   return use_pure_java; }

    /** @return Returns the shared instance. */
    public static EpicsPlugin getDefault()
    {   return plugin;    }
    	
	/** @see org.csstudio.platform.AbstractCssPlugin#getPluginId() */
    @Override
    public String getPluginId()
    {   return ID;    }

    /** @see org.csstudio.platform.AbstractCssPlugin#getPluginId() */
    @Override
	protected void doStart(BundleContext context) throws Exception 
    {
    	/* Still broken:
    	Eclipse magically locates the JCA JNI library as long as it's
		placed under <plugin>/os/<os>/<arch>,
		where the correct values for <os> and <arch> aren't known,
		except that "linux", "x86" and "macosx", "ppc" work.
		No idea what to use for Windows, or OS X on Intel.
		
		Anyway, the (java portion of the) JCA code tries to load
		shared libraries for EPICS base 'Com' and 'ca'.
	    Their location is obtained from the compiled-in
	    JCAProperties.properties file, which points to the EPICS base
	    sources.
	    But for deployment, I'd like to use shared libs that are
	    in the plugin, so I copied them into the same
	    <plugin>/os/<os>/<arch> that has the JNI lib.
	    In here, I (successfully) bend the properties to point to the
	    <plugin>/os/<os>/<arch> directory.
	    With that, the 'Com' library loads OK, but the 'ca' library
	    results in an java.lang.UnsatisfiedLinkError:
	    
	     /home/kasemir/Eclipse/Workbench/org.csstudio.platform.libs.epics/os/linux/x86/libca.so:
	     libCom.so: cannot open shared object file: No such file or directory
	     
	    The only way around:
	    Set environment variables LD_LIBRARY_PATH (Linux)
	    or DYLD_LIBRARY_PATH (OS X) to <plugin>/os/<os>/<arch>.
	     */
    	
        final String jni_target = JNITargetArch.getTargetArch();
        logInfo("JCA JNI Target Arch: " + jni_target);
        try
        {
            // Turn "os/..." path relative to the plugin...
            Bundle bundle = Platform.getBundle(ID);
            Path rel_path = new Path(getOSPath());
            // into an absolute path in the file system ...
            String path = FileLocator.resolve(FileLocator.find(bundle, rel_path, null)).getFile();
            // Chop the final '/' off
            if (path.charAt(path.length()-1) == '/')
                path = path.substring(0, path.length()-1);
            // ... and point the JCA JNI class loader there
            String jni_lib_path_property = "gov.aps.jca.jni.epics."
                                              + jni_target + ".library.path";
            logInfo("Setting " + jni_lib_path_property + "=" + path);
            System.setProperty(jni_lib_path_property, path);
        }
        catch (Exception e)
        {
            logException("Error while setting JNI properties", e);
        }

		installPreferences();
	}

    /** @see org.csstudio.platform.AbstractCssPlugin#getPluginId() */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
		plugin = null;
	}

    private String getOSPath() throws Exception
	{
	    String osname=System.getProperty( "os.name", "" );
	    String osarch=System.getProperty( "os.arch", "" );
	    if (osname.equals("Mac OS X"))
	        return "os/macosx/" + osarch;
	    else if (osname.equals( "Linux" ))
	        return "os/linux/x86";
	    else if (osname.startsWith("Win" ))
	    {
	        // ??
	    }
	    throw new Exception("Cannot determine os/<os>/<arch> path from os.name="
	                    + osname + " and os.arch=" + osarch);
	}

	/** Update the CAJ settings with the data from the
	 *  preference page.
	 *  <p>
	 *  Unfortunately this only takes effect after a restart,
	 *  the current setup seems to remain unaffected.
	 */
	@SuppressWarnings("nls")
	public void installPreferences()
	{
	    try
	    {
	        final Preferences prefs = getDefault().getPluginPreferences();
	        use_pure_java = prefs.getBoolean(PreferenceConstants.constants[0]);
	        // Set the 'CAJ' copy of the settings
	        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", 
	                        prefs.getString(PreferenceConstants.constants[1]));
	        boolean yes_no = prefs.getBoolean(PreferenceConstants.constants[2]);
	        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
	                        (yes_no ? "YES" : "NO")); 
	        System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout",
	                        prefs.getString(PreferenceConstants.constants[3]));
	        System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", 
	                        prefs.getString(PreferenceConstants.constants[4])); 
	        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
	                        prefs.getString(PreferenceConstants.constants[5]));
	        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", 
	                        prefs.getString(PreferenceConstants.constants[6]));
	        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", 
	                        prefs.getString(PreferenceConstants.constants[7]));
	
	        // Set the 'JNI' copy of the settings
	        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", 
	                        prefs.getString(PreferenceConstants.constants[1]));
	        System.setProperty("gov.aps.jca.jni.JNIContext.auto_addr_list",
	                        (yes_no ? "YES" : "NO")); 
	        System.setProperty("gov.aps.jca.jni.JNIContext.connection_timeout",
	                        prefs.getString(PreferenceConstants.constants[3]));
	        System.setProperty("gov.aps.jca.jni.JNIContext.beacon_period", 
	                        prefs.getString(PreferenceConstants.constants[4])); 
	        System.setProperty("gov.aps.jca.jni.JNIContext.repeater_port",
	                        prefs.getString(PreferenceConstants.constants[5]));
	        System.setProperty("gov.aps.jca.jni.JNIContext.server_port", 
	                        prefs.getString(PreferenceConstants.constants[6]));
	        System.setProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", 
	                        prefs.getString(PreferenceConstants.constants[7]));
	    }
	    catch (Exception e)
	    {
	        logException("Cannot set preferences", e);
	    }
	}

	/** Add info to the plugin log. */
    public static void logInfo(String message)
    {
        getDefault().log(IStatus.INFO, message, null);
    }
    
    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     *  @param type
     *  @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type, ID, IStatus.OK, message, e));
    }
}
