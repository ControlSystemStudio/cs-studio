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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/** The main plugin class to be used in the desktop.
 *  @author Original author unknown
 *  @author Kay Kasemir
 *  @author Sergei Chevtsov
  */
public class EpicsPlugin extends Plugin
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

    /**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("nls")
    @Override
	public final void start(final BundleContext context) throws Exception {
		super.start(context);

		installPreferences();
		
		if (!use_pure_java)
		{
			final String jni_target = JNITargetArch.getTargetArch();
			// this property must be unset, because JCA might mistakenly use it
			final String path = "gov.aps.jca.jni.epics."
			                     .concat(jni_target).concat(".library.path");
            System.setProperty(path, "");
            // In case we have a dependency to Com and ca,
            // try to load those.
            // Stricly speaking, loadLibray() might only apply to
            // real JNI libs, and not ordinary shared libs.
            // So the preferred method is to build a JCA JNILIB
            // without further dependencies, in which case it's
            // OK for the following two calls to fail:
			try
			{
				System.loadLibrary("Com");
				System.loadLibrary("ca");
            }
            catch (Throwable e)
            {
                // Only info, not necessarily an error
                log(Status.INFO,
                    "Cannot load Com and ca libraries. "
                     + "Could be a problem if JCA binary depends on them", e);
            }
            // Load the JCA library.
            // This better works out OK.
            try
            {
				System.loadLibrary("jca");
			}
			catch (Throwable e)
			{
				log(Status.ERROR, "Cannot load JCA binary",	e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
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
	        // TODO Avoid getPluginPreferences(), directly use IPreferencesService?
	        // final IPreferencesService prefs = Platform.getPreferencesService();
	        // ...
	        final Preferences prefs = getDefault().getPluginPreferences();
	        use_pure_java = prefs.getBoolean(PreferenceConstants.PURE_JAVA);
	        // Set the 'CAJ' copy of the settings
	        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", 
	                        prefs.getString(PreferenceConstants.ADDR_LIST));
	        final boolean auto_addr = prefs.getBoolean(PreferenceConstants.AUTO_ADDR_LIST);
	        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                            Boolean.toString(auto_addr)); 
	        System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout",
	                        prefs.getString(PreferenceConstants.TIMEOUT));
	        System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", 
	                        prefs.getString(PreferenceConstants.BEACON_PERIOD)); 
	        System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
	                        prefs.getString(PreferenceConstants.REPEATER_PORT));
	        System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", 
	                        prefs.getString(PreferenceConstants.SERVER_PORT));
	        System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", 
	                        prefs.getString(PreferenceConstants.MAX_ARRAY_BYTES));
	
	        // Set the 'JNI' copy of the settings
	        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", 
	                        prefs.getString(PreferenceConstants.ADDR_LIST));
	        System.setProperty("gov.aps.jca.jni.JNIContext.auto_addr_list",
	                        Boolean.toString(auto_addr)); 
	        System.setProperty("gov.aps.jca.jni.JNIContext.connection_timeout",
	                        prefs.getString(PreferenceConstants.TIMEOUT));
	        System.setProperty("gov.aps.jca.jni.JNIContext.beacon_period", 
	                        prefs.getString(PreferenceConstants.BEACON_PERIOD)); 
	        System.setProperty("gov.aps.jca.jni.JNIContext.repeater_port",
	                        prefs.getString(PreferenceConstants.REPEATER_PORT));
	        System.setProperty("gov.aps.jca.jni.JNIContext.server_port", 
	                        prefs.getString(PreferenceConstants.SERVER_PORT));
	        System.setProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", 
	                        prefs.getString(PreferenceConstants.MAX_ARRAY_BYTES));

	        // Select the QueuedEventDispatcher, because that avoids
	        // deadlocks when calling JCA while receiving JCA callbacks
	        System.setProperty("gov.aps.jca.jni.JNIContext.event_dispatcher", 
                            "gov.aps.jca.event.QueuedEventDispatcher");
	    }
	    catch (Exception e)
	    {
	        log(IStatus.ERROR, "Cannot set preferences", e);
	    }
	}
  
    /** Add a message to the log.
     *  @param type
     *  @param message
     *  @param e Exception or <code>null</code>
     */
    private static void log(int type, String message, Throwable ex)
    {
      if (plugin == null)
      {
            System.out.println(message);
            if (ex != null)
                ex.printStackTrace();
      }
      else
          plugin.getLog().log(new Status(type, ID, IStatus.OK, message, ex));
    }
}
