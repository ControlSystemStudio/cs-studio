package org.remotercp.util.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RemoteExceptionHandler {

	/**
	 * Opens a dialog with the given exception
	 * 
	 * @param e
	 * @param text
	 */
	public static void handleException(Exception e, String text) {

		Display display = Display.getDefault();
		Shell shell = new Shell(display);

		ExceptionHandlingWizard wizard = new ExceptionHandlingWizard(e, text);
		WizardDialog dialog = new WizardDialog(shell, wizard) {
			@Override
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				newShell.setSize(600, 600);
			}
		};

		dialog.create();
		// dialog.setPageSize(300, 200);
		int open = dialog.open();
		if (open == Dialog.OK) {
			wizard.dispose();
			dialog = null;
		}
	}

	public static void handleException(IStatus status) {
		Exception exception = new Exception(status.getMessage(), status
				.getException());
		handleException(exception, status.getMessage());
	}
}
