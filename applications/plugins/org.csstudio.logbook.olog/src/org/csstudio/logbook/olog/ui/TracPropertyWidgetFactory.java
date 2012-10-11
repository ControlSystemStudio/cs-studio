/**
 * 
 */
package org.csstudio.logbook.olog.ui;

import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.PropertyWidgetFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 *         TODO a public constructor is mandated by the extension framework.
 * 
 */
public class TracPropertyWidgetFactory implements PropertyWidgetFactory,
		IExecutableExtensionFactory, IExecutableExtension {

	private String propertyName;

	public TracPropertyWidgetFactory() {
	}

	public TracPropertyWidgetFactory(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * 
	 */
	@Override
	public Object create() throws CoreException {
		return new TracPropertyWidgetFactory(
				propertyName);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.propertyName = config.getAttribute("propertyName");
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public AbstractPropertyWidget create(Composite parent, int style) {
		return new TracPropertyWidget(parent, style, this.propertyName);
	}

}
