package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.csstudio.saverestore.SaveRestoreService;
import org.csstudio.ui.fx.util.InputValidator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ui.PartInitException;

/**
 *
 * <code>ExtensionPointLoader</code> is the utility class that loads the {@link ValueImporter}s and
 * {@link ParametersProvider}.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class ExtensionPointLoader {

    private static final String BASE_LEVEL_VALIDATOR_EXT_POINT = "org.csstudio.saverestore.ui.baselevelvalidator";

    private List<ValueImporterWrapper> importers;
    private Optional<ParametersProvider> parametersProvider;
    private Optional<InputValidator<String>> baseLevelValidator;

    private static final ExtensionPointLoader INSTANCE = new ExtensionPointLoader();

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance
     */
    public static ExtensionPointLoader getInstance() {
        return INSTANCE;
    }

    private ExtensionPointLoader() {
    }

    /**
     * Returns an unmodifiable the list of all registered value importers.
     *
     * @return returns the list of all value importers
     */
    public synchronized List<ValueImporterWrapper> getValueImporters() {
        if (importers == null) {
            importers = new ArrayList<>();
            try {
                IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
                IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(ValueImporter.EXT_POINT);
                for (IConfigurationElement element : confElements) {
                    ValueImporter importer = (ValueImporter) element.createExecutableExtension("importer");
                    String name = (String) element.getAttribute("name");
                    importers.add(new ValueImporterWrapper(importer, name));
                }
            } catch (CoreException e) {
                SaveRestoreService.LOGGER.log(Level.SEVERE, "Save and restore value importers could not be loaded.", e);
            }
            importers = Collections.unmodifiableList(importers);
        }
        return importers;
    }

    /**
     * Returns the registered parameters provider if it exists.
     *
     * @return returns parameters provider
     */
    public synchronized Optional<ParametersProvider> getParametersProvider() {
        if (parametersProvider == null) {
            ParametersProvider finder = null;
            try {
                IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
                if (extReg == null) {
                    //only happens during test execution
                    return Optional.empty();
                }
                IConfigurationElement[] confElements = extReg.getConfigurationElementsFor(ParametersProvider.EXT_POINT);
                if (confElements.length > 1) {
                    throw new PartInitException(
                        "Cannot instantiate readback provider. Only one provider can be defined, but "
                            + confElements.length + " were found.");
                }
                for (IConfigurationElement element : confElements) {
                    finder = (ParametersProvider) element.createExecutableExtension("parametersprovider");
                }
            } catch (CoreException e) {
                SaveRestoreService.LOGGER.log(Level.SEVERE, "Save and restore readback provider could not be loaded.",
                    e);
            }
            parametersProvider = Optional.ofNullable(finder);
        }
        return parametersProvider;
    }

    /**
     * Loads the extension point providing the base level validator. If non defined an empty object is returned.
     *
     * @return the registered base level validator if any
     */
    @SuppressWarnings("unchecked")
    public synchronized Optional<InputValidator<String>> getBaseLevelValidator() {
        if (baseLevelValidator == null) {
            InputValidator<String> bb = null;
            try {
                IExtensionRegistry extReg = org.eclipse.core.runtime.Platform.getExtensionRegistry();
                IConfigurationElement[] confElements = extReg
                    .getConfigurationElementsFor(BASE_LEVEL_VALIDATOR_EXT_POINT);
                for (IConfigurationElement element : confElements) {
                    bb = (InputValidator<String>) element.createExecutableExtension("validator");
                }

            } catch (CoreException e) {
                SaveRestoreService.LOGGER.log(Level.SEVERE, "Save and restore base level browser could not be loaded.",
                    e);
                baseLevelValidator = null;
            }
            baseLevelValidator = Optional.ofNullable(bb);

        }
        return baseLevelValidator;
    }

}
