package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**A dialog with a tip and a check box to not show this dialog again.
 * @author Xihui Chen
 *
 */
public class TipDialog extends MessageDialog {
	
	private boolean showAgain = true;
	
	public TipDialog(Shell parentShell, String dialogTitle,
			String dialogMessage) {
		super(parentShell, dialogTitle, null, dialogMessage,
				MessageDialog.INFORMATION, new String[] { JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY)}, 0);
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		final Button checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText("Do not show this dialog again");
		checkbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showAgain = !checkbox.getSelection();
			}
		});
		return checkbox;
	}
	
	
	public boolean isShowThisDialogAgain(){
		return showAgain;
	}
	
}
