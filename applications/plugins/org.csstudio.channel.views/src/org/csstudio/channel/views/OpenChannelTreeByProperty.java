package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PartInitException;

public class OpenChannelTreeByProperty extends AbstractAdaptedHandler<ChannelQuery> {
	
	public OpenChannelTreeByProperty() {
		super(ChannelQuery.class);
	}
	
	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event) throws PartInitException {
		if (!queries.isEmpty()) {
			findView(ChannelTreeByPropertyView.class, ChannelTreeByPropertyView.ID)
					.setChannelQuery(queries.get(0));
		}
	}
}
