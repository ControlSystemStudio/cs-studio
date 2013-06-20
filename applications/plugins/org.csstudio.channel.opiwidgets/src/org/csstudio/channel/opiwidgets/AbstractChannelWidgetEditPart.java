package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.Channel;
import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.Collection;

import org.csstudio.channel.widgets.ChannelQueryAdaptable;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.extra.AbstractSelectionWidgetEditpart;

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
    M extends AbstractWidgetModel> extends AbstractSelectionWidgetEditpart<F, M>
    implements ChannelQueryAdaptable {
	
	@Override
	public Collection<ChannelQuery> toChannelQueries() {
		return selectionToTypeCollection(ChannelQuery.class);
	}
	
	@Override
	public Collection<Channel> toChannels() {
		return selectionToTypeCollection(Channel.class);
	}
}
