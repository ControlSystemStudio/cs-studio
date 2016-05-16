/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore.ui.browser;

import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.saverestore.data.BaseLevel;
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

    private Optional<BaseLevelBrowser<BaseLevel>> browser;

    /**
     * Returns the base level browser if one is provided by an extension point. If none is defined, empty object is
     * returned. If more than one are defined, an exception is logged and an empty object is returned.
     *
     * @return returns the base level browser if it is defined
     */
    @SuppressWarnings("unchecked")
    public Optional<BaseLevelBrowser<BaseLevel>> getBaseLevelBrowser() {
        if (browser == null) {
            BaseLevelBrowser<BaseLevel> bb = null;
            try {
                IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
                IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(BaseLevelBrowser.EXT_POINT);
                if (confElements.length > 1) {
                    throw new PartInitException(
                        "Cannot properly instantiate Save and Restore Browser. Only one base level provider can be "
                            + "defined but there were " + confElements.length + ".");
                }
                for (IConfigurationElement element : confElements) {
                    bb = (BaseLevelBrowser<BaseLevel>) element.createExecutableExtension("browser");
                }
            } catch (CoreException e) {
                SaveRestoreService.LOGGER.log(Level.SEVERE, "Save and restore base level browser could not be loaded.",
                    e);
                browser = null;
            }
            browser = Optional.ofNullable(bb);
        }
        return browser;
    }
}
