package org.csstudio.channel.views;

import org.csstudio.channel.widgets.Line2DPlotWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

public class Line2DPlotView extends AbstractChannelQueryView<Line2DPlotWidget> {

	public static final String ID = "org.csstudio.channel.views.Line2DPlotView";

	@Override
	public void saveWidgetState(Line2DPlotWidget widget, IMemento memento) {
		widget.saveState(memento);
	}

	@Override
	public void loadWidgetState(Line2DPlotWidget widget, IMemento memento) {
		widget.loadState(memento);
	}

	@Override
	protected Line2DPlotWidget createChannelQueryWidget(Composite parent, int style) {
		return new Line2DPlotWidget(parent, style);
	}

}
