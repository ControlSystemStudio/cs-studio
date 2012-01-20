package org.csstudio.channel.views;

import java.util.List;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PartInitException;


public class OpenChannelsView extends AbstractAdaptedHandler<ChannelQuery> {

	public OpenChannelsView() {
		super(ChannelQuery.class);
	}
	
	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event) throws PartInitException {
		if (!queries.isEmpty()) {
			findView(ChannelsView.class, ChannelsView.ID)
					.setChannelQuery(queries.get(0));
		}
	}

}
