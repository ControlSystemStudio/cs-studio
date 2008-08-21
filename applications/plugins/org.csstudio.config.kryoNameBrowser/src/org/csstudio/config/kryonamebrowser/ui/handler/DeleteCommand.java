package org.csstudio.config.kryonamebrowser.ui.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.csstudio.config.kryonamebrowser.logic.KryoNameBrowserLogic;
import org.csstudio.config.kryonamebrowser.model.entry.KryoNameEntry;
import org.csstudio.config.kryonamebrowser.model.resolved.KryoNameResolved;
import org.csstudio.config.kryonamebrowser.ui.MainView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Deletes the selected entries. The progress dialog is shown. Deleting of small numbers of entries is fast so the
 * dialog doesn't matter. For deleting large number of entries it takes a little longer since the logic is not optimized
 * for handling large scale deletion. The user is able to abort the operation in the dialog.
 * 
 * 
 * @author Alen Vrecko
 * 
 */
public class DeleteCommand extends AbstractHandler implements IHandler {

	public static final String ID = "deleteEntry.command";
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final Shell shell = HandlerUtil.getActiveShell(event);
		boolean confirm = MessageDialog.openConfirm(shell, "Confirmation",
				"Are you sure?");

		if (!confirm) {
			return null;
		}

		final ISelection selection = HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		if (selection != null & selection instanceof IStructuredSelection) {

			final IStructuredSelection strucSelection = (IStructuredSelection) selection;
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindow(event);
			IWorkbenchPage page = window.getActivePage();
			final MainView view = (MainView) page.findView(MainView.ID);

			final KryoNameBrowserLogic logic = view.getLogic();

			IRunnableWithProgress op = new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					List<KryoNameResolved> list = strucSelection.toList();
					monitor.beginTask("Deleting entries. Please wait...",
							IProgressMonitor.UNKNOWN);

					for (KryoNameResolved element : list) {

						if (monitor.isCanceled()) {

							shell.getDisplay().syncExec(new Runnable() {

								public void run() {
									MessageDialog.openWarning(shell, "Warning",
											"Delete operation aborted by user");

								}
							});

							view.getFilter().updateTable(shell);
							monitor.done();
							return;
						}

						try {
							logic.delete(new KryoNameEntry(element.getId()));

						} catch (final Exception e) {
							shell.getDisplay().syncExec(new Runnable() {

								public void run() {
									MessageDialog.openError(shell, "Error", e
											.getMessage());

								}
							});

							view.getFilter().updateTable(shell);
							monitor.done();
							return;

						}

					}

					shell.getDisplay().syncExec(new Runnable() {

						public void run() {
							MessageDialog.openInformation(shell, "Info",
									"Successfully Deleted");

						}
					});

					view.getFilter().updateTable(shell);
					monitor.done();

				}

			};

			try {
				new ProgressMonitorDialog(HandlerUtil.getActiveShell(event))
						.run(true, true, op);
			} catch (InvocationTargetException e) {
				MessageDialog.openError(HandlerUtil.getActiveShell(event),
						"Error", e.getMessage());
			} catch (InterruptedException e) {
				MessageDialog.openError(HandlerUtil.getActiveShell(event),
						"Error", e.getMessage());
			}

		}

		return null;
	}
}
