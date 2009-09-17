package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**The dialog for editing multiline text.
 * @author Xihui Chen
 *
 */
public class MultilineTextEditDialog extends Dialog {
	
	private String title;	
	private String contents;
	private Text text;

	
	

	protected MultilineTextEditDialog(Shell parentShell, String stringValue, String dialogTitle) {
		super(parentShell);
		this.title = dialogTitle;
		this.contents = stringValue;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		final Composite mainComposite = new Composite(parent_Composite, SWT.None);			
		mainComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 400;
		gridData.heightHint = 200;
		text = new Text(mainComposite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL |SWT.V_SCROLL);
		text.setLayoutData(gridData);
		text.setText(contents);
		return parent_Composite;
	}
	
	@Override
	protected void okPressed() {
		contents = text.getText();
		super.okPressed();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	public String getResult() {
			
		return contents;
	}
	
	
	
}
