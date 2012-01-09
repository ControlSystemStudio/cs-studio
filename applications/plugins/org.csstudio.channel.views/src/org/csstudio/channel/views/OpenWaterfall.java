package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;

/**
 * Opens the waterfall view.
 * 
 * @author carcassi
 */
public class OpenWaterfall extends AbstractAdaptedHandler<ChannelQuery> {
	
	public OpenWaterfall() {
		super(ChannelQuery.class);
	}

	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event)
			throws Exception {
		if (!queries.isEmpty()) {
			findView(WaterfallView.class, WaterfallView.ID)
				.setPVName(queries.get(0).getQuery());
		}
	}

}
