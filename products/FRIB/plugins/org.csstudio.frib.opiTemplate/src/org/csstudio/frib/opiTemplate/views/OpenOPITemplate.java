package org.csstudio.frib.opiTemplate.views;

import java.util.List;

import gov.bnl.channelfinder.api.ChannelQuery;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;

public class OpenOPITemplate extends AbstractAdaptedHandler<ChannelQuery>{

	public OpenOPITemplate() {
		super(ChannelQuery.class);
	}

	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event)
			throws Exception {
		if (!queries.isEmpty()) {
			findView(OPITemplate.class, OPITemplate.ID)
					.setChannelQuery(queries.get(0));
		}
		
	}

}
