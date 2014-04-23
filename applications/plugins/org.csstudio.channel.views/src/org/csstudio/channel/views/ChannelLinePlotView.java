package org.csstudio.channel.views;

import org.csstudio.channel.widgets.ChannelLinePlotWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

public class ChannelLinePlotView extends AbstractChannelQueryView<ChannelLinePlotWidget> {

	public static final String ID = "org.csstudio.channel.views.ChannelLinePlotView";

	@Override
	public void saveWidgetState(ChannelLinePlotWidget widget, IMemento memento) {
		widget.saveState(memento);
	}

	@Override
	public void loadWidgetState(ChannelLinePlotWidget widget, IMemento memento) {
		widget.loadState(memento);
	}

	@Override
	protected ChannelLinePlotWidget createChannelQueryWidget(Composite parent, int style) {
		return new ChannelLinePlotWidget(parent, style);
	}

}
