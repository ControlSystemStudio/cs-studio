package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;

/**
 * Abstract class for channel based widgets. Here we put the functionality that is common to
 * all channel based widgets, some of which may in the future be pushed to the BOY base classes.
 * <p>
 * Generics is used to avoid casting figures and widgets.
 * 
 * @author carcassi
 *
 * @param <F> the figure type
 * @param <M> the model type
 */
public abstract class AbstractChannelWidgetEditPart<F extends AbstractChannelWidgetFigure<?>,
    M extends AbstractChannelWidgetModel> extends AbstractWidgetEditPart {
	
	@Override
	protected abstract F doCreateFigure();
	
	@Override
	public F getFigure() {
		@SuppressWarnings("unchecked")
		F figure = (F) super.getFigure();
		return figure;
	}
	
	@Override
	public M getWidgetModel() {
		@SuppressWarnings("unchecked")
		M widgetModel = (M) super.getWidgetModel();
		return widgetModel;
	}

}
