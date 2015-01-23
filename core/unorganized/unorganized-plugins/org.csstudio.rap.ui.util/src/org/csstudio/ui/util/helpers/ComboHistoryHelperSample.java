package org.csstudio.ui.util.helpers;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

public class ComboHistoryHelperSample {
	private static Text text;
	private static Combo combo;
	private static ComboViewer comboViewer;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

		
		text = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(10, 44, 412, 201);
		
		comboViewer = new ComboViewer(shell, SWT.NONE);
		combo = comboViewer.getCombo();
		combo.setBounds(10, 10, 412, 28);

		new ComboHistoryHelper(null, "tag", combo) {
			@Override
			public void newSelection(final String pvName) {
				// Need to use \r\n. Unbelievable!!!
				String newText = text.getText() + pvName + "\r\n";
				text.setText(newText);
				
			}
		};		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
