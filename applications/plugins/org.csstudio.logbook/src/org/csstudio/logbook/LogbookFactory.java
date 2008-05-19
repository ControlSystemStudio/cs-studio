package org.csstudio.logbook;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/** Factory class for obtaining a logbook.
 *  @author nypaver
 *  @author Kay Kasemir
 */
public class LogbookFactory
{
    /** Connect to a logbook
     *  @param user User name used when connecting to logbook (Oracle, ...)
     *  @param password password that goes with the user
     *  @return Logbook interface
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public static ILogbook connect(String user, String password)
            throws Exception
    {
        final IConfigurationElement[] configs = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(ILogbookFactory.EXTENSION_ID);
        if (configs.length != 1)
            throw new Exception("Got " + configs.length + " instead of 1 "
                    + ILogbookFactory.EXTENSION_ID + " implementations");

        final ILogbookFactory factory = (ILogbookFactory) configs[0]
                .createExecutableExtension("class");

        return factory.connect(user, password);
    }
}
