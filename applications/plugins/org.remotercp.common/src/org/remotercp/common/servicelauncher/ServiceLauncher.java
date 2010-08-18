package org.remotercp.common.servicelauncher;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * This class is used to create executables for the extension point
 * "org.remotercp.remoteService".
 * 
 * @author eugrei
 * 
 */
public class ServiceLauncher {
    private final static Logger logger = Logger.getLogger(ServiceLauncher.class
                                                          .getName());
    
    public static void startRemoteServices() {
        final IExtensionPoint extensionPoint =
            Platform.getExtensionRegistry().getExtensionPoint("org.remotercp.remoteService");
        Assert.isNotNull(extensionPoint);
        
        final IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
        
        for (final IConfigurationElement element : configurationElements) {
            try {
                final Object executableExtension = element
                .createExecutableExtension("class");
                Assert.isNotNull(executableExtension);
                
                if (executableExtension instanceof IRemoteServiceLauncher) {
                    final IRemoteServiceLauncher launcher = (IRemoteServiceLauncher) executableExtension;
                    launcher.startServices();
                }
            } catch (final CoreException e) {
                logger
                .severe("Unable to create executable Extension for element: "
                        + element.toString());
                e.printStackTrace();
            }
        }
    }
    
}
