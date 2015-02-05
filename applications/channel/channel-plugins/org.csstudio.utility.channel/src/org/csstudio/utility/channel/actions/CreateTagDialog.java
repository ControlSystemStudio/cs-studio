package org.csstudio.utility.channel.actions;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateTagDialog extends TitleAreaDialog {

	private String tagName;
	private String tagOwner;
	private Text textTagName;
	private Text textTagOwner;
	

	/**
	 * Create a dialog with the an initial tag name <tt>tagName</tt>
	 * 
	 * @param parentShell
	 * @param tagName
	 */
	public CreateTagDialog(Shell parentShell, String tagName) {
		super(parentShell);
		this.tagName = tagName;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param parent
	 *            the parent composite
	 * @return Control
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		// Set the title
		setTitle("Create a New Tag");

		// Set the message
		setMessage("The selected tag is not present in the channelfinder service.\n Create a new Tag with the following credentials.");

		return contents;
	}

	/**
	 * Creates the gray area
	 * 
	 * @param parent
	 *            the parent composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(2, false));
		
		Label lblTagName = new Label(composite, SWT.NONE);
		lblTagName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTagName.setText("Tag Name: ");
		
		textTagName = new Text(composite, SWT.BORDER);
		textTagName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textTagName.setText(tagName);
		
		Label lblTagOwner = new Label(composite, SWT.NONE);
		lblTagOwner.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTagOwner.setText("Tag Owner: ");
		
		textTagOwner = new Text(composite, SWT.BORDER);
		textTagOwner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return composite;	
	}
	
	protected void okPressed() {
		tagName = textTagName.getText();
		tagOwner = textTagOwner.getText();
		super.okPressed();
	}
	
	public String getTagName(){
		return this.tagName;
	}
	
	public String getTagOwner(){
		return this.tagOwner;
	}
}
