package org.csstudio.config.kryonamebrowser.ui.handler;

import java.sql.SQLException;
import java.util.Iterator;

import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.entry.KryoObjectEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.MainView;
import org.csstudio.config.kryonamebrowser.ui.dialog.KryoNameDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class DeleteCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();

		if (selection != null & selection instanceof IStructuredSelection) {

			IStructuredSelection strucSelection = (IStructuredSelection) selection;

			for (Iterator iterator = strucSelection.iterator(); iterator
					.hasNext();) {
				KryoNameResolved element = (KryoNameResolved) iterator.next();

				boolean confirm = MessageDialog
						.openConfirm(HandlerUtil.getActiveShell(event),
								"Confirmation", "Are you sure?");

				if (confirm) {

					IWorkbenchWindow window = HandlerUtil
							.getActiveWorkbenchWindow(event);
					IWorkbenchPage page = window.getActivePage();
					MainView view = (MainView) page.findView(MainView.ID);

					try {
						view.getLogic().delete(
								new KryoNameEntry(element.getId()));
					} catch (SQLException e) {
						MessageDialog
								.openConfirm(HandlerUtil.getActiveShell(event),
										"Error", e.getMessage());
					}

				}

			}
		}

		return null;
	}

}
