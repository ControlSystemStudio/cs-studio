package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.channel.widgets.WaterfallWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * View that allows to create a waterfall plot out of a given PV.
 */
public class WaterfallView extends AbstractChannelQueryView<WaterfallWidget> {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.csstudio.channel.views.WaterfallView";

	/** Memento tag */
	private static final String MEMENTO_PVNAME = "PVName"; //$NON-NLS-1$

	@Override
	public void saveWidgetState(WaterfallWidget widget, IMemento memento) {
		if (widget.getChannelQuery() != null) {
			memento.putString(MEMENTO_PVNAME, widget.getChannelQuery()
					.getQuery());
		}
	}

	@Override
	public void loadWidgetState(WaterfallWidget widget, IMemento memento) {
		if (memento != null && memento.getString(MEMENTO_PVNAME) != null) {
			widget.setChannelQuery(ChannelQuery.query(
					memento.getString(MEMENTO_PVNAME)).build());
		}
	}

	@Override
	protected WaterfallWidget createChannelQueryWidget(Composite parent,
			int style) {
		return new WaterfallWidget(parent, style);
	}

}