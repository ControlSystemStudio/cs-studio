/**
 * 
 */
package org.csstudio.logbook.olog.ui;

import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.ui.PropertyWidgetFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * @author shroffk
 * 
 */
public class ContextPropertyWidgetFactory implements PropertyWidgetFactory {

	private String propertyName;

	/**
	 * @wbp.parser.entryPoint
	 */
	public ContextPropertyWidgetFactory() {

	}

	@Override
	public AbstractPropertyWidget create(Composite parent, int SWT,
			LogEntryChangeset logEntryChangeset) {
		return new ContextPropertyWidget(parent, SWT, logEntryChangeset);
	}

}
