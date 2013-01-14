package org.csstudio.utility.quickstart.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

/**
 * This class is a copy from the abstract eclipse class 'ListEditor' with
 * changes that now the items are not displayed in a 'List' but in a 'Table'. In
 * addition it is possible to edit the items in the table.
 * 
 * @author jhatje
 * 
 */
public class PreferenceTableEditor extends FieldEditor {

	/**
	 * The table for the items in the menu. (Currently the options of the JFace
	 * component are not used. The TableViewer is just a container for the SWT
	 * table. Maybe the TableViewer can be replaced with the SWT table.)
	 */
	private TableViewer tableViewer;

	/**
	 * The button box containing the Add, Remove, Up, and Down buttons;
	 * <code>null</code> if none (before creation or after disposal).
	 */
	private Composite buttonBox;

	/**
	 * The Add button.
	 */
	private Button addButton;

	/**
	 * The Remove button.
	 */
	private Button removeButton;

	/**
	 * The Up button.
	 */
	private Button upButton;

	/**
	 * The Down button.
	 */
	private Button downButton;

	/**
	 * The selection listener.
	 */
	private SelectionListener selectionListener;

	/**
	 * Creates a new list field editor
	 */
	protected PreferenceTableEditor() {
	}

	/**
	 * Creates a list field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	protected PreferenceTableEditor(String name, String labelText,
			Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	/**
	 * Notifies that the Add button has been pressed. A new tableItem is set at
	 * the end of the table with initial stings that the user has to adjust.
	 */
	private void addPressed() {
		setPresentsDefaultValue(false);
		int itemNumber = tableViewer.getTable().getItemCount();
		TableItem item = new TableItem(tableViewer.getTable(), SWT.NONE,
				itemNumber);
		item.setText(0, "Path to file");
		item.setText(1, "Name for Menu");
		item.setText(2, "false");
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) tableViewer.getTable().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box) {
		addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
		removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
		upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
		downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
	}

	/**
	 * Combines the given list of items into a single string. This method is the
	 * converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	protected String createList(TableItem[] items) {
		StringBuffer preferenceString = new StringBuffer();
		for (TableItem tableItem : items) {
			// The path to the file, mandatory
			if (tableItem.getText(0) == null) {
				preferenceString.append("");
			} else {
				preferenceString.append(tableItem.getText(0));
			}
			preferenceString.append("?");
			// The name for quickstart menu, optional
			if (tableItem.getText(1) == null) {
				preferenceString.append("");
			} else {
				preferenceString.append(tableItem.getText(1));
			}
			preferenceString.append("?");
			// Should the display opens with CSS startup
			if (tableItem.getText(2) == null) {
				preferenceString.append("false");
			} else {
				preferenceString.append(tableItem.getText(2));
			}
			preferenceString.append(";");
		}
		return preferenceString.toString();
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(JFaceResources.getString(key));
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button,
				IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == upButton) {
					upPressed();
				} else if (widget == downButton) {
					downPressed();
				} else if (widget == tableViewer.getTable()) {
					selectionChanged();
				}
			}
		};
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		tableViewer = getTableControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		tableViewer.getTable().setLayoutData(gd);

		buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}

	/**
	 * Set the file path and menu name set by the user from preferences in the
	 * table rows.
	 */
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoad() {
		if (tableViewer != null) {
		    PreferenceValidator prefernecValidator = new PreferenceValidator();
			String s = getPreferenceStore().getString(getPreferenceName());
			String[] array = parseString(s);
			TableItem item;
			for (int i = 0; i < array.length; i++) {
				item = new TableItem(tableViewer.getTable(), SWT.NONE);
				String[] tableRowFromPreferences = array[i].split("\\?");
				item.setText(prefernecValidator.checkPreferenceValidity(tableRowFromPreferences));
			}
		}
	}

	

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doLoadDefault() {
		// there are no defaults for the quickstart menu.
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore() {
		String s = createList(tableViewer.getTable().getItems());
		if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	private void downPressed() {
		swap(false);
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					addButton = null;
					removeButton = null;
					upButton = null;
					downButton = null;
					buttonBox = null;
				}
			});

		} else {
			checkParent(buttonBox, parent);
		}

		selectionChanged();
		return buttonBox;
	}

	/**
	 * Returns this field editor's table control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public TableViewer getTableControl(Composite parent) {
		if (tableViewer == null) {
			int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
			Table table = new Table(parent, style);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			TableColumn column;
			column = new TableColumn(table, SWT.LEFT, 0);
			column.setText("Path");
			column.setWidth(100);
			column = new TableColumn(table, SWT.LEFT, 1);
			column.setText("Menu name");
			column.setWidth(100);
			column = new TableColumn(table, SWT.LEFT, 2);
			column.setText("Auto Start");
			column.setWidth(60);

			// Create an editor object to use for text editing
			final TableEditor editor = new TableEditor(table);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;

			tableViewer = new TableViewer(table);
			tableViewer.getTable().setFont(parent.getFont());
			tableViewer.getTable().addSelectionListener(getSelectionListener());
			tableViewer.getTable().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					tableViewer = null;
				}
			});
			table.addMouseListener(new TableEditorMouseListener(editor,
					tableViewer.getTable()));

		} else {
			checkParent(tableViewer.getTable(), parent);
		}
		return tableViewer;
	}

	/**
	 * Creates and returns a new item for the list.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return a new item
	 */
	protected String getNewInputObject() {
		return null;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		return 3;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if necessary.
	 * 
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener() {
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell() {
		if (addButton == null) {
			return null;
		}
		return addButton.getShell();
	}

	/**
	 * Splits the given string into a list of strings. This method is the
	 * converse of <code>createList</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param stringList
	 *            the string
	 * @return an array of <code>String</code>
	 * @see #createList
	 */
	protected String[] parseString(String stringList) {
		return stringList.split(";");
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	private void removePressed() {
		setPresentsDefaultValue(false);
		int index = tableViewer.getTable().getSelectionIndex();
		if (index >= 0) {
			tableViewer.getTable().remove(index);
			selectionChanged();
		}
	}

	/**
	 * Notifies that the list selection has changed.
	 */
	private void selectionChanged() {

		int index = tableViewer.getTable().getSelectionIndex();
		int size = tableViewer.getTable().getItemCount();

		removeButton.setEnabled(index >= 0);
		upButton.setEnabled(size > 1 && index > 0);
		downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if (tableViewer.getTable() != null) {
			tableViewer.getTable().setFocus();
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 * 
	 * @param up
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	private void swap(boolean up) {
		setPresentsDefaultValue(false);
		int index = tableViewer.getTable().getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			TableItem[] selection = tableViewer.getTable().getSelection();
			Assert.isTrue(selection.length == 1);
			String[] tableRow = new String[3];
			tableRow[0] = selection[0].getText(0);
			tableRow[1] = selection[0].getText(1);
			tableRow[2] = selection[0].getText(2);
			tableViewer.getTable().remove(index);
			TableItem item = new TableItem(tableViewer.getTable(), SWT.NONE,
					target);
			item.setText(tableRow);
			tableViewer.getTable().setSelection(target);
		}
		selectionChanged();
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	private void upPressed() {
		swap(true);
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getTableControl(parent).getTable().setEnabled(enabled);
		addButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		upButton.setEnabled(enabled);
		downButton.setEnabled(enabled);
	}
}
