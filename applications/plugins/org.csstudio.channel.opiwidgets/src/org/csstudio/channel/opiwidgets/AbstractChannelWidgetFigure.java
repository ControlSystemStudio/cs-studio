package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.widgets.figures.AbstractSWTWidgetFigure;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for all figures that are based on SWTWidgets and channel finder.
 * 
 * @author carcassi
 *
 * @param <T> the widget type
 */
public abstract class AbstractChannelWidgetFigure<T extends Composite> extends AbstractSWTWidgetFigure {
	
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
		widget = createWidget(composite);
		selectionProvider = retrieveSelectionProvider(widget);
	}

	/**
	 * Implement to create the widget to be wrapped.
	 * 
	 * @param parent the widget parent
	 * @return the new widget
	 */
	protected abstract T createWidget(Composite parent);
	
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
	
	private final T widget;
	private final ISelectionProvider selectionProvider;

	@Override
	public T getSWTWidget() {
		return widget;
	}
	
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
