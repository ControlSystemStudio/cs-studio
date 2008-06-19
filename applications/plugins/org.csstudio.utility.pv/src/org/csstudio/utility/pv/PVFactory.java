package org.csstudio.utility.pv;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** PV Factory
 *  <p>
 *  Locates the one and only expected implementation of the IPVFactory
 *  via an extension to the pvfactory extension point
 *  and creates the PV through it.
 *  
 *  <pre>
    // Create PV
    final PV pv = PVFactory.createPV(pv_name);
    // Register listener for updates
    pv.addListener(new PVListener()
    {
        public void pvDisconnected(PV pv)
        {
            System.out.println(pv.getName() + " is disconnected");
        }
        public void pvValueUpdate(PV pv)
        {
            System.out.println(pv.getName() + " = " + pv.getValue());
        }
    });
    // Start the PV
    pv.start();
    </pre>
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVFactory
{
    /** ID of the extension point */
    private static final String PVFACTORY_EXT_ID =
        "org.csstudio.utility.pv.pvfactory";

    /** Lazyly intialized PV factory found in extension registry */
    private static IPVFactory pv_factory = null;
    
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
    private static final void locatePVFactory() throws Exception
    {
        // Get extension point info from registry
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
            .getConfigurationElementsFor(PVFACTORY_EXT_ID);
        // Allow one and only implementation
        if (configs.length != 1)
            throw new Exception("Found " + configs.length
                            + " instead of one " + PVFACTORY_EXT_ID);
        Plugin.getLogger().debug("Found PVFactory implementation "
                        + configs[0].getContributor().getName());
        // Create instance of that implementation
        pv_factory = (IPVFactory) configs[0].createExecutableExtension("class");
    }
}
