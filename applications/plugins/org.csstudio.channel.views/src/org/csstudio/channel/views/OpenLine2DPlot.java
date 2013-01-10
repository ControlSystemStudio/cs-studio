package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PartInitException;

public class OpenLine2DPlot extends AbstractAdaptedHandler<ChannelQuery> {

	public OpenLine2DPlot() {
		super(ChannelQuery.class);
	}
	
	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event) throws PartInitException {
		if (!queries.isEmpty()) {
			findView(Line2DPlotView.class, Line2DPlotView.ID)
				.setChannelQuery(queries.get(0));
		}
	}


}
