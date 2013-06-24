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
public class FileWidgetFactory implements PropertyWidgetFactory {

    public FileWidgetFactory() {
    }

    @Override
    public AbstractPropertyWidget create(Composite parent, int SWT,
	    LogEntryChangeset logEntryChangeset) {
	return new FileWidget(parent, SWT, logEntryChangeset);
    }

}
