package org.csstudio.dct.nameresolution.internal;

import java.util.Collections;
import java.util.List;

import org.csstudio.dct.DctActivator;
import org.csstudio.dct.IoNameService;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * Implementation for the ioname() function.
 * 
 * @author Sven Wende
 * 
 */
public final class IoNameFieldFunction implements IFieldFunction {

	/**
	 * Constructor.
	 * 
	 * @param ioNameService
	 *            an IO name service
	 */
	public IoNameFieldFunction() {
	}

	/**
	 *{@inheritDoc}
	 */
	public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
		IoNameService service = resolveNameService();
		
		if(service==null) {
			throw new IllegalArgumentException("name service not available");
		}else {
			return service.getEpicsAddress(parameters[0], fieldName);
		}
	}

	private IoNameService resolveNameService() {
		IoNameService service = null;

		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IConfigurationElement[] configurationElementsFor = extensionRegistry.getConfigurationElementsFor(DctActivator.PLUGIN_ID
				+ ".ioNameService");
		if (configurationElementsFor.length == 1) {
			try {
				service = (IoNameService) configurationElementsFor[0].createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return service;
	}
	
	public List<IContentProposal> getParameterProposal(int parameter, IRecord record) {
		return Collections.EMPTY_LIST;
	}
}
