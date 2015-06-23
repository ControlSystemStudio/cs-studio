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
 */
public class ContextPropertyWidgetFactory implements PropertyWidgetFactory {

    /**
     * @wbp.parser.entryPoint
     */
    public ContextPropertyWidgetFactory() {

    }

    @Override
    public AbstractPropertyWidget create(Composite parent, int SWT,
        LogEntryChangeset logEntryChangeset, boolean editable) {
    return new ContextPropertyWidget(parent, SWT, logEntryChangeset, editable);
    }

}
