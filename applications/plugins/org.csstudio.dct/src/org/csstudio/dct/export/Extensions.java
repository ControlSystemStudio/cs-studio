package org.csstudio.dct.export;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.DctActivator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class Extensions {
	
	public static List<ExporterDescriptor>  lookupExporterExtensions() {
		List<ExporterDescriptor> descriptors = new ArrayList<ExporterDescriptor>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(DctActivator.EXTPOINT_EXPORTERS);

		for (IConfigurationElement c : configurationElements) {
			String id = c.getAttribute("id"); //$NON-NLS-1$
			String description = c.getAttribute("description"); //$NON-NLS-1$
			String icon = c.getAttribute("icon"); //$NON-NLS-1$
			String pluginId = c.getDeclaringExtension().getNamespaceIdentifier();
			IExporter exporter = null;
			try {
				exporter = (IExporter) c.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			ExporterDescriptor descriptor = new ExporterDescriptor();
			descriptor.setId(id);
			descriptor.setDescription(description);
			descriptor.setExporter(exporter);
			descriptor.setIcon(icon);
			descriptor.setPluginId(pluginId);

			descriptors.add(descriptor);

		}
		
		return descriptors;

	}

}
