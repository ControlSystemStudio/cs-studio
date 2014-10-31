/**
 * 
 */
package org.csstudio.ui.util.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.widgets.StringListSelectionWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author shroffk
 * 
 */
public class StringListSelectionDialog extends Dialog {

	protected Shell dialogShell;
	private StringListSelectionWidget stringListSelectionWidget;

	private List<String> initialPossibleValues = new ArrayList<String>();
	private List<String> initialSelectedValues = new ArrayList<String>();

	private final String title;

	/**
	 * Create a string list selection dialog. 
	 * 
	 * @param parent
	 * @param possibleValues -  a List of Values
	 * @param selectedValues - a List of selected Values
	 * @param title
	 */
	public StringListSelectionDialog(Shell parent, List<String> possibleValues,
			List<String> selectedValues, String title) {
		super(parent);
		setShellStyle(SWT.RESIZE | SWT.DIALOG_TRIM);
		this.initialPossibleValues = possibleValues;
		this.initialSelectedValues = selectedValues;
		this.title = title;
	}

	@Override
	public void create() {
		super.create();
		// Set the title
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(title);
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		stringListSelectionWidget = new StringListSelectionWidget(container,
				SWT.NONE);
		stringListSelectionWidget.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, true, true, 1, 1));
		populateInitialValues();
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Apply", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
	}

	private void populateInitialValues() {
		setPossibleValues(initialPossibleValues);
		setSelectedValues(initialSelectedValues);

	}

	private void setSelectedValues(List<String> selectedValues) {
		stringListSelectionWidget.setSelectedValues(selectedValues);
	}

	public List<String> getSelectedValues() {
		return stringListSelectionWidget.getSelectedValues();
	}

	private void setPossibleValues(List<String> possibleValues) {
		stringListSelectionWidget.setPossibleValues(possibleValues);
	}

	public List<String> getPossibleValues() {
		return stringListSelectionWidget.getPossibleValues();
	}
}
