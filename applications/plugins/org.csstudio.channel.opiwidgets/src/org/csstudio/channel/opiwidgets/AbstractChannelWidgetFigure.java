package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for all figures that are based on SWTWidgets and channel finder.
 * 
 * @author carcassi
 *
 * @param <T> the widget type
 */
public abstract class AbstractChannelWidgetFigure<T extends Composite> extends AbstractSWTWidgetFigure<T> {
	
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
		selectionProvider = retrieveSelectionProvider(getSWTWidget());
	}
	
	/**
	 * Returns the selection provider to be used for pop-ups. By default, if the
	 * widget is itself an ISelectionProvider, the widget is returned.
	 * 
	 * @param widget the widget
	 * @return the selection provider or null
	 */
	protected ISelectionProvider retrieveSelectionProvider(T widget) {
		if (widget instanceof ISelectionProvider) {
			return (ISelectionProvider) widget;
		}
		return null;
	}
	
	private final ISelectionProvider selectionProvider;

	
	/**
	 * The selection provider to be used for the pop-up.
	 * 
	 * @return the selection provider or null
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}
	
	public boolean isRunMode() {
		return runmode;
	}
	
}
