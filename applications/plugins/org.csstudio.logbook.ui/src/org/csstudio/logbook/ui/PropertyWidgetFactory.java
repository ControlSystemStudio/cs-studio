/**
 * 
 */
package org.csstudio.logbook.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 *
 */
public interface PropertyWidgetFactory {

	public AbstractPropertyWidget create(Composite parent, int SWT);
}
