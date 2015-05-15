package org.csstudio.dct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.dct.nameresolution.FieldFunctionExtension;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionPointUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionPointUtil.class);

    public static Map<String, String> getRecordAttributes() {
        Map<String, ServiceExtension<IRecordFunction>> extensions = lookupNamingServiceExtensions(DctActivator.EXTPOINT_RECORD_FUNCTIONS);

        Map<String, String> result = new LinkedHashMap<String, String>();

        for (ServiceExtension<IRecordFunction> extension : extensions.values()) {
            Map<String, String> attributes = extension.getService().getAttributes();

            for (String key : attributes.keySet()) {
                if(result.containsKey(key)) {
                    throw new IllegalArgumentException("Record function attribute [" + key
                            + "] is not unique!");
                } else {
                    result.put(key, attributes.get(key));
                }
            }
        }

        return result;
    }

    public static <E> Map<String, ServiceExtension<E>> lookupNamingServiceExtensions(String extensionPointId) {
        Map<String, ServiceExtension<E>> services = new HashMap<String, ServiceExtension<E>>();

        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] elements = extensionRegistry
                .getConfigurationElementsFor(extensionPointId);

        for (IConfigurationElement element : elements) {
            try {
                String pluginId = element.getNamespaceIdentifier();
                String id = element.getAttribute("id"); //$NON-NLS-1$
                String name = element.getAttribute("name"); //$NON-NLS-1$
                E service = (E) element.createExecutableExtension("class"); //$NON-NLS-1$
                String iconPath = element.getAttribute("icon");
                assert pluginId != null;
                assert id != null;
                assert name != null;
                assert service != null;

                ServiceExtension<E> extension = new ServiceExtension<E>(pluginId,
                                                                        id,
                                                                        name,
                                                                        service,
                                                                        iconPath);
                services.put(id, extension);

            } catch (CoreException e) {
                LOG.warn("Cannot instantiate extension class [{}] for extension point [{}]",
                         elements[0].getAttribute("class"),
                         extensionPointId);
            }
        }

        return services;
    }

    public static List<FieldFunctionExtension> lookupFieldFunctionExtensions() {
        List<FieldFunctionExtension> extensions = new ArrayList<FieldFunctionExtension>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IConfigurationElement[] configurationElements = registry
                .getConfigurationElementsFor(DctActivator.EXTPOINT_FIELDFUNCTIONS);

        for (IConfigurationElement c : configurationElements) {
            String id = c.getAttribute("name"); //$NON-NLS-1$
            String description = c.getAttribute("description"); //$NON-NLS-1$
            IFieldFunction function = null;
            try {
                function = (IFieldFunction) c.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                e.printStackTrace();
            }

            FieldFunctionExtension extension = new FieldFunctionExtension();
            extension.setDescription(description);
            extension.setFunction(function);
            extension.setDescription(description);

            extensions.add(extension);

        }

        return extensions;

    }

}
