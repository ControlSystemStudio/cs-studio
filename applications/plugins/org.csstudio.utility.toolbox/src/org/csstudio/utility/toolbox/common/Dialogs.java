package org.csstudio.utility.toolbox.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

public final class Dialogs {

	private Dialogs() {}
	
	public static boolean question(String title, String message) {
		return MessageDialog.openQuestion(PlatformUI.getWorkbench().getModalDialogShellProvider().getShell(),
					title, message);
	}

	public static void message(String title, String message) {
		MessageDialog.open(MessageDialog.INFORMATION, PlatformUI.getWorkbench().getModalDialogShellProvider()
					.getShell(), title, message, SWT.NONE);
	}
	
	public static void exception(String title, Exception e) {
		IStatus status = new Status(IStatus.ERROR, Constant.PLUGIN_ID, 1, e.getLocalizedMessage(), e.getCause());
		ErrorDialog.openError(PlatformUI.getWorkbench().getModalDialogShellProvider()
					.getShell(), title, null,status);
	}

	
}
