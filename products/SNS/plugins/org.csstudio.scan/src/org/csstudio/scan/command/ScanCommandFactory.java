package org.csstudio.scan.command;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/** Implementation of the {@link SimpleScanCommandFactory} for Eclipse code
 * 
 *  <p>Utilizes the extension point registry.
 *  
 *  @see SimpleScanCommandFactory
 *   
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandFactory extends SimpleScanCommandFactory
{
    final public static String COMMAND_EXT_POINT = "org.csstudio.scan.command";
    
    /** Create a {@link ScanCommand} for a command ID
     * 
     *  <p>This is the derived implementation that uses the Eclipse registry.
     *  Non-Eclipse code that does not have access to the Eclipse registry
     *  should use the {@link SimpleScanCommandFactory}
     * 
     *  @param id ID of the command
     *  @return ScanCommand
     *  @throws Exception on error
     */
    @Override
    public ScanCommand createCommandForID(final String id) throws Exception
    {
        final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(COMMAND_EXT_POINT);
        final IConfigurationElement[] configs = point.getConfigurationElements();

        for (IConfigurationElement config : configs)
        {
            if (id.equals(config.getAttribute("id")))
                return (ScanCommand) config.createExecutableExtension("class");
        }
        throw new Exception("Unknown command type '" + id + "'");
    }
}
