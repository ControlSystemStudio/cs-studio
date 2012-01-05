package org.csstudio.channel.views;

import gov.bnl.channelfinder.api.ChannelQuery;

import java.util.List;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenPVTableByProperty extends AbstractAdaptedHandler<ChannelQuery> {
	
	public OpenPVTableByProperty() {
		super(ChannelQuery.class);
	}
	
	@Override
	protected void execute(List<ChannelQuery> queries, ExecutionEvent event) {
		try {
			IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
			if (queries.size() > 0) {
				PVTableByPropertyView pvTable = (PVTableByPropertyView) page
				.showView(PVTableByPropertyView.ID);
				pvTable.setPVName(queries.get(0).getQuery());
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
