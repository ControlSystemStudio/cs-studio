package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetFigure;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for all figures that are based on SWTWidgets and channel finder.
 * 
 * @author carcassi
 *
 * @param <T> the widget type
 */
public abstract class AbstractChannelWidgetFigure<T extends Composite> extends AbstractSelectionWidgetFigure<T> {
	
	/**
	 * Creates a new figure based on the give swt widget and the selection provider.
	 * 
	 * @param composite pass through
	 * @param parentModel pass through
	 * @param swtWidget the SWT widget
	 * @param selectionProvider a corresponding selection provider
	 */
	public AbstractChannelWidgetFigure(AbstractBaseEditPart editPart) {
		super(editPart);
	}
	
}
