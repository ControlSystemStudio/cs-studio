package org.csstudio.saverestore.ui.browser;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.BaseLevel;
import org.csstudio.saverestore.Engine;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ui.PartInitException;

/**
 *
 * <code>BaseLevelBrowserProvider</code> loads the base level browser from the extension point.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class BaseLevelBrowserProvider {

    public static final String BASE_LEVEL_BROWSER_EXT_POINT = "org.csstudio.saverestore.ui.browser.baselevelbrowser";

    private Optional<BaseLevelBrowser<BaseLevel>> browser;

    /**
     * @return returns the base level browser if it is provided by the extension points (only one can exist)
     */
    @SuppressWarnings("unchecked")
    public Optional<BaseLevelBrowser<BaseLevel>> getBaseLevelBrowser() {
        if (browser == null) {
            BaseLevelBrowser<BaseLevel> bb = null;
            try {
                IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
                IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(BASE_LEVEL_BROWSER_EXT_POINT);
                if (confElements.length > 1) {
                    throw new PartInitException("Cannot instantiate Save and Restore Browser. Only one base level provider can be defined");
                }
                for(IConfigurationElement element : confElements){
                    bb = (BaseLevelBrowser<BaseLevel>) element.createExecutableExtension("browser");
                }

            } catch (CoreException e) {
                Engine.LOGGER.log(Level.SEVERE, "Save and restore base level browser could not be loaded.", e);
                browser = null;
            }
            browser = Optional.ofNullable(bb);

        }
        return browser;
    }
}
