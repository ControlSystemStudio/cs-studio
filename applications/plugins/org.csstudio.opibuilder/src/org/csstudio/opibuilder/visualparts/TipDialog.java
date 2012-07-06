package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
	
	/**
	 * 
	 * @param parentShell
	 * @param kind
	 *            the kind of dialog to open, one of {@link MessageDialog#ERROR},
	 *            {@link MessageDialog#INFORMATION}, {@link MessageDialog#QUESTION}, {@link MessageDialog#WARNING},
	 *            {@link MessageDialog#CONFIRM}, or {@link MessageDialog#QUESTION_WITH_CANCEL}.
	 * @param dialogTitle
	 * @param dialogMessage
	 */
	public TipDialog(Shell parentShell, int kind, String dialogTitle,
			String dialogMessage) {	
		super(parentShell, dialogTitle, null, dialogMessage,
				kind, getButtonLabels(kind), 0); //$NON-NLS-1$
	}
	
	public TipDialog(Shell parentShell, String dialogTitle,
			String dialogMessage) {	
		this(parentShell, MessageDialog.INFORMATION, dialogTitle, dialogMessage);
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
	
	/**
	 * @param kind
	 * @return
	 */
	static String[] getButtonLabels(int kind) {
		String[] dialogButtonLabels;
		switch (kind) {
		case ERROR:
		case INFORMATION:
		case WARNING: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
			break;
		}
		case CONFIRM: {
			dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL,
					IDialogConstants.CANCEL_LABEL };
			break;
		}
		case QUESTION: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL };
			break;
		}
		case QUESTION_WITH_CANCEL: {
			dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL };
			break;
		}
		default: {
			throw new IllegalArgumentException(
					"Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
		}
		}
		return dialogButtonLabels;
	}
	
}
