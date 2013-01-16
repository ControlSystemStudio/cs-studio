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

/**
 * @author shroffk
 * 
 *         TODO a public constructor is mandated by the extension framework.
 * 
 */
public class TracPropertyWidgetFactory implements PropertyWidgetFactory {

	public TracPropertyWidgetFactory() {
	}

	@Override
	public AbstractPropertyWidget create(Composite parent, int SWT,
			LogEntryChangeset logEntryChangeset) {
		return new TracPropertyWidget(parent, SWT, logEntryChangeset);
	}
}
