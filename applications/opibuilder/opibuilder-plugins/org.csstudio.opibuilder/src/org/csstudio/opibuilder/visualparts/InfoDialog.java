package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/** Information message dialog subclass.
 * Creates new shell, then a dialog with this shell as parent, because on Linux the MessageDialog (e.g. Show Macros) is
 * opened in the background when running a fullscreen OPI.
 * 
 * @author Boris Versic - use InfoDialog (MessageDialog shows behind fullscreen window on Linux)
 */
public class InfoDialog extends MessageDialog {

	public InfoDialog(Shell parent, String title, String message) {
		super(parent, title, null, message, MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0);
	}

	public static void open(Shell parent, String title, String message) {
		
		final Shell shell = new Shell(parent.getDisplay(), SWT.NO_TRIM);
		
		InfoDialog dialog = new InfoDialog(shell, title, message);
		dialog.setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		/* Note:  Using SWT.ON_TOP for the dialog style forces the dialog to have the NO_TRIM style on Linux
		 * (no title bar, no close button) - tested with gtk WM.
		 * Ref. on chosen solution (new shell): https://bugs.eclipse.org/bugs/show_bug.cgi?id=457115#c18
		 * and answer here: https://dev.eclipse.org/mhonarc/lists/platform-swt-dev/msg07717.html
		 * */

		shell.setSize(100, 30);
		Rectangle windowBounds = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getBounds();
		// center horizontally, but place it a little higher vertically
		Point dialogCenter = new Point(windowBounds.x + windowBounds.width / 2, (windowBounds.y + windowBounds.height) / 2);
		Point location = new Point(dialogCenter.x - shell.getBounds().x / 2, dialogCenter.y - shell.getBounds().y / 2);
		shell.setLocation(location);
		dialog.setBlockOnOpen(true);
		dialog.open();
		
		shell.dispose();
	}	
}
