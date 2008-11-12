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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractNameHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();

		final Shell shell = HandlerUtil.getActiveShell(event);

		if (selection != null & selection instanceof IStructuredSelection) {

			IStructuredSelection strucSelection = (IStructuredSelection) selection;

			for (Iterator iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				Object element = (Object) iterator.next();

				KryoNameDialog dialog = getDialog(shell,
						(KryoNameResolved) element);

				IWorkbenchWindow window = HandlerUtil
						.getActiveWorkbenchWindow(event);
				IWorkbenchPage page = window.getActivePage();
				final MainView view = (MainView) page.findView(MainView.ID);

				dialog.setLogic(view.getLogic());
				dialog.open();

				if (dialog.isCallUpdate()) {
					view.getFilter().updateTable(
							HandlerUtil.getActiveShell(event));
				}

				return null;
			}
		}
		return null;
	}

	public abstract KryoNameDialog getDialog(Shell shell,
			KryoNameResolved element);

}
