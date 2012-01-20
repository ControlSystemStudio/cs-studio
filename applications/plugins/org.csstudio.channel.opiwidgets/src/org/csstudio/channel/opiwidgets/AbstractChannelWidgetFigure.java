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
	}
	
	// TODO: this should be changed to have an abstract method to be implemented,
	// instead of having people set it in the constructor.
	protected T widget;
	protected ISelectionProvider selectionProvider;

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
	
	// TODO: these most likely are better moved to the edit part, and converted
	// to use the adaptable interfaces we are building.
	
	/**
	 * Takes the selection and converts it to a Channel array.
	 * 
	 * @return the adapted object or null
	 */
	public Channel[] getSelectedChannels() {
		if (selectionProvider == null)
			return null;
		return AdapterUtil.convert(getSelectionProvider().getSelection(), Channel.class);
	}

	/**
	 * Takes the selection and converts it to a ChannelQuery array.
	 * 
	 * @return the adapted object or null
	 */
	public ChannelQuery[] getSelectedChannelQuery() {
		if (selectionProvider == null)
			return null;
		return AdapterUtil.convert(getSelectionProvider().getSelection(), ChannelQuery.class);
	}

	/**
	 * Takes the selection and converts it to a ProcessVariable array.
	 * 
	 * @return the adapted object or null
	 */
	public ProcessVariable[] getSelectedProcessVariables() {
		if (selectionProvider == null)
			return null;
		return AdapterUtil.convert(getSelectionProvider().getSelection(), ProcessVariable.class);
	}
	
	public boolean isRunMode() {
		return runmode;
	}
	
}
