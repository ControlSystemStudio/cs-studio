/**
 * 
 */
package org.csstudio.logbook.olog.property.shift;

import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.ui.PropertyWidgetFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 * TODO a public constructor is mandated by the extension framework.
 * 
 */
public class ShiftPropertyWidgetFactory implements PropertyWidgetFactory {

    public ShiftPropertyWidgetFactory() {
    }

    @Override
    public AbstractPropertyWidget create(Composite parent, int SWT,
	    LogEntryChangeset logEntryChangeset) {
	return new ShiftPropertyWidget(parent, SWT, logEntryChangeset);
    }
}
