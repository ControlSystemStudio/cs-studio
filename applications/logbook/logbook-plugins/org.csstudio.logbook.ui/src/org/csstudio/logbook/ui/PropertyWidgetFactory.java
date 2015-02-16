/**
 * 
 */
package org.csstudio.logbook.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * A factory for creating a composite which will handle the creation and viewing
 * of logbook properties
 * 
 * @author shroffk
 * 
 */
public interface PropertyWidgetFactory {

	public AbstractPropertyWidget create(Composite parent, int SWT,
			LogEntryChangeset logEntryChangeset, boolean editable);
}
