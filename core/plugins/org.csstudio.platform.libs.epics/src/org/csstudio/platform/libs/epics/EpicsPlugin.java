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

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IPreferencesService;
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

    /** How should subscriptions be established? */
    public enum MonitorMask
    {
        /** Listen to changes in value */
        VALUE(1),
        
        /** Listen to changes in value beyond archive limit */
        ARCHIVE(2),
        
        /** Listen to changes in alarm state */
        ALARM(4);
        
        final private int mask;
        
        private MonitorMask(final int mask)
        {
            this.mask = mask;
        }
        
        /** @return Mask bits used in underlying CA call */
        public int getMask()
        {
            return mask;
        }
    }

    private MonitorMask monitor_mask = MonitorMask.VALUE;
	
	/** The constructor. */
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

    /** @return Mask used to create CA monitors (subscriptions) */
    public MonitorMask getMonitorMask()
    {   return monitor_mask; }

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
            Throwable com_ca_exception = null;
			try
			{
				System.loadLibrary("Com");
				System.loadLibrary("ca");
            }
            catch (Throwable ex)
            {
                // Remember the error because it might explain a follow-up
                // jca load error.
                // On the other hand, if jca loads OK, we can ignore this one.
                com_ca_exception = ex;
            }
            // Load the JCA library.
            // This better works out OK.
            try
            {
				System.loadLibrary("jca");
			}
			catch (Throwable ex)
			{
			    if (com_ca_exception != null)
			        CentralLogger.getInstance().getLogger(this).info(
		                "Cannot load Com and ca libraries. "
		                + "Could be a problem if JCA binary depends on them",
		                com_ca_exception);
			    // This is an error for sure:
			    CentralLogger.getInstance().getLogger(this)
			        .error("Cannot load JCA binary", ex);
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
	        final IPreferencesService prefs = Platform.getPreferencesService();
	        use_pure_java =
	            prefs.getBoolean(ID, PreferenceConstants.PURE_JAVA, true, null);
	        monitor_mask = MonitorMask.valueOf(
                prefs.getString(ID, PreferenceConstants.MONITOR, "VALUE", null));
	        
	        // Set the 'CAJ' and 'JNI' copies of the settings
	        setSystemProperty("com.cosylab.epics.caj.CAJContext.use_pure_java", Boolean.toString(use_pure_java));
	        final String addr_list =
	            prefs.getString(ID, PreferenceConstants.ADDR_LIST, null, null);
			setSystemProperty("com.cosylab.epics.caj.CAJContext.addr_list", 
	                          addr_list);
            setSystemProperty("gov.aps.jca.jni.JNIContext.addr_list", 
                              addr_list);

			final String auto_addr = Boolean.toString(
			    prefs.getBoolean(ID, PreferenceConstants.AUTO_ADDR_LIST, true, null));
	        setSystemProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                              auto_addr); 
            setSystemProperty("gov.aps.jca.jni.JNIContext.auto_addr_list",
                              auto_addr); 
	        
	        final String timeout =
	            prefs.getString(ID, PreferenceConstants.TIMEOUT, "30.0", null);
			setSystemProperty("com.cosylab.epics.caj.CAJContext.connection_timeout",
	                        timeout);
            setSystemProperty("gov.aps.jca.jni.JNIContext.connection_timeout",
                    timeout);
			
			final String beacon_period =
			    prefs.getString(ID, PreferenceConstants.BEACON_PERIOD, "15.0", null);
			setSystemProperty("com.cosylab.epics.caj.CAJContext.beacon_period", 
	                        beacon_period); 
            setSystemProperty("gov.aps.jca.jni.JNIContext.beacon_period", 
                    beacon_period); 

            final String repeater_port =
                prefs.getString(ID, PreferenceConstants.REPEATER_PORT, "5065", null);
			setSystemProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
	                        repeater_port);
            setSystemProperty("gov.aps.jca.jni.JNIContext.repeater_port",
                    repeater_port);

            final String server_port =
                prefs.getString(ID, PreferenceConstants.SERVER_PORT, "5064", null);
			setSystemProperty("com.cosylab.epics.caj.CAJContext.server_port", 
	                        server_port);
            setSystemProperty("gov.aps.jca.jni.JNIContext.server_port", 
                    server_port);
	        
			final String max_array_bytes =
			    prefs.getString(ID, PreferenceConstants.MAX_ARRAY_BYTES, "16384", null);
			setSystemProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", 
	                        max_array_bytes);
	        setSystemProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", 
	                        max_array_bytes);

	        // Select the QueuedEventDispatcher, because that avoids
	        // deadlocks when calling JCA while receiving JCA callbacks
	        //setSystemProperty("gov.aps.jca.jni.JNIContext.event_dispatcher", 
            //                "gov.aps.jca.event.QueuedEventDispatcher");
	        // Select the DirectEventDispatcher:
	        // As long as the PV library that uses JCA avoids deadlocks,
	        // this is faster than the QueuedEventDispatcher
	        setSystemProperty("gov.aps.jca.jni.ThreadSafeContext.event_dispatcher",
	        		"gov.aps.jca.event.DirectEventDispatcher");
	    }
	    catch (Exception ex)
	    {
	        CentralLogger.getInstance().getLogger(this)
	            .error("Preferences Error", ex);
	    }
	    
	}
  
	/** Sets property from preferences to System properties only if property
	 *  value is not null or empty string.
	 *  @param prop System property name
	 *  @param value CSS preference name
	 */
    private void setSystemProperty(final String prop, final String value)
    {
    	if (value == null  ||  value.length()<=0)
    	    return;
    	CentralLogger.getInstance().getLogger(this).debug(prop + "=" + value); //$NON-NLS-1$
		System.setProperty(prop, value);
    }
}
