/**
 * 
 */
package org.csstudio.shift.ui;

import org.csstudio.shift.ShiftBuilder;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ShiftBuilderDialogTest {

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		
		Display display = new Display();
		final Shell shell = new Shell(display);
		ShiftBuilderDialog dialog = new ShiftBuilderDialog(shell, ShiftBuilder.withType("this is a test"));
		dialog.setBlockOnOpen(true);
		if (dialog.open() == Window.OK) {
			System.out.println("pressed OK");
		}
	}
}
