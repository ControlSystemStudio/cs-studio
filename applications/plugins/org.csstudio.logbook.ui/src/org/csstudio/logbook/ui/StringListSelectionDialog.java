/**
 * 
 */
package org.csstudio.logbook.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

/**
 * @author shroffk
 * 
 */
public class StringListSelectionDialog extends Dialog {

	protected Shell dialogShell;
	private StringListSelectionWidget stringListSelectionWidget;

	private List<String> initialPossibleValues = new ArrayList<String>();
	private List<String> initialSelectedValues = new ArrayList<String>();

	public StringListSelectionDialog(Shell parent, List<String> possibleValues,
			List<String> selectedValues) {
		super(parent);
		this.initialPossibleValues = possibleValues;
		this.initialSelectedValues = selectedValues;
		createContents();
		populateInitialValues();
	}

	void createContents() {
		dialogShell = new Shell(getParent(), SWT.DIALOG_TRIM);
		dialogShell.setSize(400, 300);
		dialogShell.setText(getText());
		dialogShell.setLayout(new FormLayout());

		// Selection Widget
		stringListSelectionWidget = new StringListSelectionWidget(dialogShell,
				SWT.NONE);
		FormData fd_stringListSelectionWidget = new FormData();
		fd_stringListSelectionWidget.top = new FormAttachment(0, 5);
		fd_stringListSelectionWidget.left = new FormAttachment(0, 5);
		fd_stringListSelectionWidget.right = new FormAttachment(100, -5);
		stringListSelectionWidget.setLayoutData(fd_stringListSelectionWidget);

		Button btnCancel = new Button(dialogShell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateInitialValues();
				dialogShell.close();
			}

		});
		fd_stringListSelectionWidget.bottom = new FormAttachment(btnCancel, -6);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(100, -10);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");

		Button btnApply = new Button(dialogShell, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialogShell.close();
			}
		});
		FormData fd_btnApply = new FormData();
		fd_btnApply.bottom = new FormAttachment(100, -10);
		fd_btnApply.right = new FormAttachment(btnCancel, -5);
		btnApply.setLayoutData(fd_btnApply);
		btnApply.setText("Apply");
		populateInitialValues();
	}

	public void open() {
		createContents();
		dialogShell.open();
		dialogShell.layout();
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public void open(int x, int y) {
		createContents();
		dialogShell.open();
		dialogShell.layout();
		moveTo(x, y);
	}

	private void moveTo(int x, int y) {
		dialogShell.setBounds(
				Math.min(x, dialogShell.getDisplay().getClientArea().width
						- dialogShell.getBounds().width),
				Math.min(y, dialogShell.getDisplay().getClientArea().height
						- dialogShell.getBounds().height),
				dialogShell.getBounds().width, dialogShell.getBounds().height);
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

	private void setPossibleValues(Collection<String> possibleValues) {
		stringListSelectionWidget.setPossibleValues(possibleValues);
	}

	public List<String> getPossibleValues() {
		return stringListSelectionWidget.getPossibleValues();
	}
}
