package org.csstudio.channel.views;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Opens the waterfall view.
 * 
 * @author carcassi
 */
public class ConfigureView extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ChannelTreeByPropertyView view = (ChannelTreeByPropertyView) HandlerUtil.getActivePart(event);
			view.configure();
//			ISelection selection = HandlerUtil.getActiveMenuSelection(event);
//
//			IWorkbench workbench = PlatformUI.getWorkbench();
//			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
//			IWorkbenchPage page = window.getActivePage();
//			ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
//			
//			if (pvs.length > 0) {
//				WaterfallView waterfall = (WaterfallView) page
//				.showView(WaterfallView.ID);
//				waterfall.setPVName(pvs[0].getName());
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
