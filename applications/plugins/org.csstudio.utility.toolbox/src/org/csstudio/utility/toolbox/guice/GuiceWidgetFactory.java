package org.csstudio.utility.toolbox.guice;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;

public class GuiceWidgetFactory implements IExecutableExtensionFactory, IExecutableExtension {

	private String extensionId;

	@Override
	public Object create() throws CoreException {
		try {
			Class<?> viewClass = Class.forName(extensionId);
			return DependencyInjector.INSTANCE.getInjector().getInstance(viewClass);
		} catch (ClassNotFoundException e) {
			return new IllegalStateException(e);
		}
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
				throws CoreException {
		extensionId = config.getAttribute("id");
	}

}
