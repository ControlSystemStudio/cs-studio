package org.csstudio.dct.nameresolution.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.nameresolution.FieldFunctionExtension;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class Extensions {
	
	public static List<FieldFunctionExtension>  lookupExtensions() {
		List<FieldFunctionExtension> extensions = new ArrayList<FieldFunctionExtension>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(DctActivator.EXTPOINT_FIELDFUNCTIONS);

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
