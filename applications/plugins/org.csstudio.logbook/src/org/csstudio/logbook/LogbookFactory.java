package org.csstudio.logbook;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Factory class for obtaining a logbook factory.
 *  @author nypaver
 *  @author Kay Kasemir
 */
public class LogbookFactory
{
    /** Get a logbook factory
     *  @return ILogbookFactory interface
     *  @throws Exception when finding anything but exactly one ELog implementation 
     */
    @SuppressWarnings("nls")
    public static ILogbookFactory getInstance() throws Exception
    {
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(ILogbookFactory.EXTENSION_ID);
        if (configs.length != 1)
            throw new Exception("Got " + configs.length + " instead of 1 "
                    + ILogbookFactory.EXTENSION_ID + " implementations");
        return (ILogbookFactory) configs[0].createExecutableExtension("class");
    }
}
