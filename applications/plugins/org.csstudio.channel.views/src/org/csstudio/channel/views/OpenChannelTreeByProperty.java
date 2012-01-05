package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenChannelTreeByProperty extends AbstractAdaptedHandler<ChannelQuery> {
	
	public OpenChannelTreeByProperty() {
		super(ChannelQuery.class);
	}
	
	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event) {
		try {
			IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			if (queries.size() > 0) {
				ChannelTreeByPropertyView channelTree;
					channelTree = (ChannelTreeByPropertyView) page
					.showView(ChannelTreeByPropertyView.ID);
				channelTree.setChannelQuery(queries.get(0));
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
