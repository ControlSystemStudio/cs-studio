package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.Arrays;
import java.util.Collection;

import org.csstudio.channel.widgets.ChannelQueryAdaptable;
import org.csstudio.channel.widgets.ConfigurableWidget;
import org.csstudio.channel.widgets.ConfigurableWidgetAdaptable;
import org.csstudio.csdata.ProcessVariable;
import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.ui.util.AdapterUtil;

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
    M extends AbstractWidgetModel> extends AbstractWidgetEditPart
    implements ConfigurableWidgetAdaptable, ChannelQueryAdaptable {
	
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
	
	@Override
	public ConfigurableWidget toConfigurableWidget() {
		Collection<ConfigurableWidget> adapted = selectionToType(ConfigurableWidget.class);
		if (adapted != null && adapted.size() == 1) {
			return adapted.iterator().next();
		}
		return null;
	}
	
	@Override
	public Collection<ChannelQuery> toChannelQueries() {
		return selectionToType(ChannelQuery.class);
	}
	
	private <T> Collection<T> selectionToType(Class<T> clazz) {
		if (getFigure().getSelectionProvider() == null)
			return null;
		return Arrays.asList(AdapterUtil.convert(getFigure().getSelectionProvider().getSelection(), clazz));
	}
	
	@Override
	public Collection<Channel> toChannels() {
		return selectionToType(Channel.class);
	}
	
	@Override
	public Collection<ProcessVariable> toProcesVariables() {
		return selectionToType(ProcessVariable.class);
	}
}
