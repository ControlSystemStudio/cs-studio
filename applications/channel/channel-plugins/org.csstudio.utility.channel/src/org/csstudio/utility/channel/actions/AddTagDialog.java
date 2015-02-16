package org.csstudio.utility.channel.actions;

import java.util.Collection;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AddTagDialog extends TitleAreaDialog {
	private Collection<String> tagNames;
	private String tagName;
	private Combo combo;


	protected AddTagDialog(Shell parentShell, Collection<String> tagNames) {
		super(parentShell);
		this.tagNames = tagNames;
	}

	/**
	   * Creates the dialog's contents
	   * 
	   * @param parent the parent composite
	   * @return Control
	   */
	  protected Control createContents(Composite parent) {
	    Control contents = super.createContents(parent);

	    // Set the title
	    setTitle("Add Tag");

	    // Set the message
	    setMessage("Add the following selected tag to all the selection channel");

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
		Composite composite = (Composite) super.createDialogArea(parent);
						new Label(composite, SWT.NONE);
						
						Label tagNameLabel = new Label(composite, SWT.NONE);
						tagNameLabel.setText("Tag Name:");
				
						combo = new Combo(composite, SWT.NONE);
						combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
						combo.setItems(tagNames.toArray(new String[tagNames.size()]));
						combo.addSelectionListener(new SelectionListener() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								tagName = combo.getText();
							}

							@Override
							public void widgetDefaultSelected(SelectionEvent e) {
							}
						});
						
						combo.addModifyListener(new ModifyListener() {
							
							@Override
							public void modifyText(ModifyEvent e) {
								tagName = ((Combo) e.getSource()).getText();
							}
						});
		return composite;
	}

	public String getValue() {
		return this.tagName;
	}

}
