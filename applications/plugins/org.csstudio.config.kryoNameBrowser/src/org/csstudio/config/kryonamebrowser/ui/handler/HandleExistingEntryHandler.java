package org.csstudio.config.kryonamebrowser.ui.handler;

import java.util.Iterator;

import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.MainView;
import org.csstudio.config.kryonamebrowser.ui.dialog.KryoNameDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class HandleExistingEntryHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();

		if (selection != null & selection instanceof IStructuredSelection) {

			IStructuredSelection strucSelection = (IStructuredSelection) selection;

			for (Iterator iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				Object element = (Object) iterator.next();

				KryoNameDialog dialog = new KryoNameDialog(
						HandlerUtil.getActiveShell(event),
						(KryoNameResolved) element, shouldEnableEditing());

				IWorkbenchWindow window = HandlerUtil
						.getActiveWorkbenchWindow(event);
				IWorkbenchPage page = window.getActivePage();
				MainView view = (MainView) page.findView(MainView.ID);

				dialog.setLogic(view.getLogic());
				dialog.open();
			}
		}
		return null;
	}
	
	public abstract boolean shouldEnableEditing();

}
