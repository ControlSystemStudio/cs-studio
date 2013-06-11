package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to edit PVItem and FormulaItem.
 * @author Takashi Nakamoto
 */
public class EditItemsDialog extends Dialog {
	/**
	 * Edit result of this dialog. 
	 * @author Takashi Nakamoto 
	 */
	public class Result {
		private String strDisplayName;
		
		/**
		 * Initialize the instance that represents the result of EditItemsDialog.
		 * @param strDisplayName Display name.
		 */
		public Result(String strDisplayName) {
			this.strDisplayName = strDisplayName;
		}
		
		/**
		 * @return Display name. Returns null if the entered name is empty.
		 */
		public String getDisplayName() {
			if (strDisplayName == null || strDisplayName.isEmpty())
				return null;
			else
				return strDisplayName;
		}
	}
	
	/** The instance that represents the result of this dialog. */
	private Result result = null;
	
	/** Subjected items that will be edited by this dialog. */
	private ModelItem[] items;
	
	private Text textDisplayName = null;
	
	/**
	 * Initialize this dialog.
	 * @param parent Parent shell for dialog.
	 * @param items Subjected items that will be edited by this dialog.
	 */
	public EditItemsDialog(Shell parent, ModelItem[] items) {
		super(parent);
		this.items = items;
	}
	
	protected Point getInitialSize() {
		// TODO: Adjust the size of this dialog more appropriately.
		return new Point(500, 300);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		// Set the title of this dialog.
		newShell.setText(Messages.EditItem);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		
		Label labelDisplayName = new Label(composite, SWT.NONE);
		labelDisplayName.setText("Display Name");
		
		textDisplayName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textDisplayName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (items.length == 1)
			textDisplayName.setText(items[0].getDisplayName());

		return composite;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		// Save the result for later use.
		if (IDialogConstants.OK_ID == buttonId) {
			result = new Result(textDisplayName.getText());
		}
		
		super.buttonPressed(buttonId);
	}
	
	/**
	 * Get the result of this dialog. This method returns null if the dialog is not closed yet,
	 * or if the dialog is closed with "Cancel" button.
	 * @return The instance of result.
	 */
	public Result getResult() {
		return result;
	}
}
