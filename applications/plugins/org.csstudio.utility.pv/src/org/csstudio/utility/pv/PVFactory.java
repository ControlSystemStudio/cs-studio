package org.csstudio.utility.pv;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/** PV Factory
 *  <p>
 *  Locates the one and only expected implementation of the IPVFactory
 *  via an extension to the pvfactory extension point
 *  and creates the PV through it.
 *  
 *  @author Kay Kasemir
 */
public class PVFactory
{
    /** ID of the extension point */
    private static final String PVFACTORY_EXT_ID = "pvfactory"; //$NON-NLS-1$

    /** Name of the config element under that extension point */
    private static final String PV_FACTORY_CONFIG = "PVFactory"; //$NON-NLS-1$
 
    /** Lazyly intialized PV factory found in extension registry */
    private static IPVFactory pv_factory = null;
    
    /** Does the application have a user interface?
     *  <p>
     *  Especially the EPICS PV Tree application which performs many
     *  connects and disconnects ran into deadlocks with JNI JCA:
     *  While the application tried to start or stop a channel from
     *  the UI thread, another channel might receive meta data and
     *  try to subscribe.
     *  These concurrent calls into CA from different threads created
     *  deadlocks. They were hard to debug beyond the borders of Java,
     *  but channeling all the CA callbacks back into a UI thread
     *  before performing further CA calls solved the problem.
     *  <p>
     *  Of course that only works if there is a UI thread.
     *  For JUnit tests or non-UI applications, that option is
     *  not available.
     *  Trying to use Display.getDefault() would sometimes still
     *  return a display, and the existence of a plugin mechanism
     *  also didn't work out, so now it's a public flag that non-UI
     *  environments (JUnit tests, non-UI apps) need to clear.
     */
    public static boolean use_ui_thread = true;

    /** Create a PV for the given channel name.
     *  @param name Channel name
     *  @return PV
     *  @exception Exception on error
     */
    public static final PV createPV(final String name) throws Exception
    {
        if (pv_factory == null)
            locatePVFactory();
        return pv_factory.createPV(name);
    }

    /** Locate the PV factory via the extension point registry */
    @SuppressWarnings("nls")
    private static final void locatePVFactory() throws Exception
    {
        // Get extension point info from registry
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IExtensionPoint ext_point =
            registry.getExtensionPoint(org.csstudio.utility.pv.Plugin.ID,
                                       PVFACTORY_EXT_ID);
        // Get available implementations
        final IExtension[] extensions = ext_point.getExtensions ();
        if (extensions.length != 1)
            throw new Exception("Found " + extensions.length
                            + " instead of one PVFactory extensions");
        // We only handle one and only implementation
        final IExtension extension = extensions[0];
        if (!extension.isValid ())
            throw new Exception(PV_FACTORY_CONFIG
                                + extension.getContributor().getName()
                                + " is invalid");
        Plugin.logInfo("Found PVFactory implementation "
                        + extension.getContributor().getName());
        // Create instance of that implementation
        final IConfigurationElement[] configs =
                                         extension.getConfigurationElements();
        // Should have one config element "PVFactory"
        if (configs.length != 1)
            throw new Exception("Found " + configs.length
                              + " instead of one PVFactory extension configs");
        final IConfigurationElement config = configs[0];
        if (! config.getName().equals(PV_FACTORY_CONFIG))
            throw new Exception("Expected " + PV_FACTORY_CONFIG + ", found "
                                + config.getName());
        // Should have attributes 'name' (ignored) and 'class'
        for (String attr : config.getAttributeNames())
            Plugin.logInfo("Attribute '" + attr + "' = '" + config.getAttribute(attr) + "'");
        pv_factory = (IPVFactory) config.createExecutableExtension("class");
    }
}
