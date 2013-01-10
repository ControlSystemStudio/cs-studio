package org.csstudio.channel.views;

import org.csstudio.channel.widgets.TunerWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

public class ChannelOrchestratorView extends AbstractChannelQueryView<TunerWidget> {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.ChannelOrchestratorView";
	
	@Override
	public void saveWidgetState(TunerWidget widget, IMemento memento) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadWidgetState(TunerWidget widget, IMemento memento) {
		// TODO Auto-generated method stub

	}

	@Override
	protected TunerWidget createChannelQueryWidget(Composite parent, int style) {
		return new TunerWidget(parent, style);
	}

}
