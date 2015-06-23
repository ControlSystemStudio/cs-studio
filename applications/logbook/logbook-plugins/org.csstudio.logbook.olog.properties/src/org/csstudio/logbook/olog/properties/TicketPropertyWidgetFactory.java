/**
 *
 */
package org.csstudio.logbook.olog.properties;

import org.csstudio.logbook.ui.AbstractPropertyWidget;
import org.csstudio.logbook.ui.LogEntryChangeset;
import org.csstudio.logbook.ui.PropertyWidgetFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 *         TODO a public constructor is mandated by the extension framework.
 *
 */
public class TicketPropertyWidgetFactory implements PropertyWidgetFactory {

    public TicketPropertyWidgetFactory() {
    }

    @Override
    public AbstractPropertyWidget create(Composite parent, int SWT,
        LogEntryChangeset logEntryChangeset, boolean editable) {
    return new TicketPropertyWidget(parent, SWT, logEntryChangeset, editable);
    }
}
