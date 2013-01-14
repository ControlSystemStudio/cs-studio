package org.csstudio.ui.util.dialogs;

import org.eclipse.swt.widgets.Shell;

public class ExceptionDetailsErrorDialogTest {
	public static void main(String[] args) {
		Shell shell = new Shell();
		ExceptionDetailsErrorDialog.openError(shell, "Title", "Message", new RuntimeException("There was a problem"));
		
		ExceptionDetailsErrorDialog.openError(shell, "Title2", new RuntimeException("This is a problem"));
	}
}
