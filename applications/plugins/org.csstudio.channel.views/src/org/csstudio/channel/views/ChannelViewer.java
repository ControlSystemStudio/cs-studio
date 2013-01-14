package org.csstudio.channel.views;

import org.csstudio.channel.widgets.ChannelViewerWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

public class ChannelViewer extends AbstractChannelQueryView<ChannelViewerWidget> {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.ChannelViewer";

	@Override
	public void saveWidgetState(ChannelViewerWidget widget, IMemento memento) {
	}

	@Override
	public void loadWidgetState(ChannelViewerWidget widget, IMemento memento) {
	}

	@Override
	protected ChannelViewerWidget createChannelQueryWidget(Composite parent,
			int style) {
		return new ChannelViewerWidget(parent, style);
	}
}
