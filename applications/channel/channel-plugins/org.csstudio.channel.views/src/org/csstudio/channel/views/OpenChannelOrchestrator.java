package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PartInitException;

public class OpenChannelOrchestrator extends AbstractAdaptedHandler<ChannelQuery> {

	public OpenChannelOrchestrator() {
		super(ChannelQuery.class);
	}

	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event)
			throws PartInitException {
		if (!queries.isEmpty()) {
			findView(ChannelOrchestratorView.class, ChannelOrchestratorView.ID).setChannelQuery(
					queries.get(0));
		}
	}

}
