package org.csstudio.alarm.table.preferences;

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
 * addition the items in the table are editable.
 * 
 * @author jhatje
 * 
 */
public class PreferenceTopicTableEditor extends PreferenceTableEditor {

	private ExchangeablePreferenceColumnTableEditor _preferenceColumnTableEditor = null;

	private TopicTableEditorMouseListener _topicTableEditorMouseListener;

	/**
	 * Creates a new list field editor
	 */
	protected PreferenceTopicTableEditor() {
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
	 * @param preferenceColumnTableEditor
	 */
	public PreferenceTopicTableEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * Notifies that the Add button has been pressed. A new tableItem is set at
	 * the end of the table with initial stings that the user has to adjust.
	 */
	void addPressed() {
		setPresentsDefaultValue(false);
		int itemNumber = tableViewer.getTable().getItemCount();
		TableItem item = new TableItem(tableViewer.getTable(), SWT.NONE,
				itemNumber);
		item.setText(1, "Topics");
		item.setText(2, "Name");
		item.setText(3, "false");
		item.setText(4, "false");
		item.setText(5, "Tahoma,0,8");
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
			// Is topic set to default
			if (tableItem.getChecked()) {
				preferenceString.append("default");
			}
			preferenceString.append("?");
			// Set of topics
			preferenceString.append(tableItem.getText(1));
			preferenceString.append("?");
			// Name for set of topics
			preferenceString.append(tableItem.getText(2));
			preferenceString.append("?");
			// Is pop up mode set to true or false
			preferenceString.append(tableItem.getText(3));
			preferenceString.append("?");
			// Is auto start mode set to true or false
			preferenceString.append(tableItem.getText(4));
			preferenceString.append("?");
			// Selected font
			preferenceString.append(tableItem.getText(5));
			preferenceString.append(";");
		}
		return preferenceString.toString();
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
			int defaultColumn = 0;
			String s = getPreferenceStore().getString(getPreferenceName());
			String[] array = parseString(s);
			TableItem item;
			for (int i = 0; i < array.length; i++) {
				item = new TableItem(tableViewer.getTable(), SWT.NONE);
				String[] tableRowFromPreferences = array[i].split("\\?");
				if (tableRowFromPreferences[0].equals("default")) {
					tableRowFromPreferences[0] = "";
					item.setChecked(true);
					defaultColumn = i;
				}
				item.setText(tableRowFromPreferences);
			}
		}
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
	 * Returns this field editor's table control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public TableViewer getTableControl(Composite parent) {
		if (tableViewer == null) {
			int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.CHECK;
			Table table = new Table(parent, style);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			TableColumn column;
			column = new TableColumn(table, SWT.LEFT, 0);
			column.setText("Default");
			column.setWidth(40);
			column = new TableColumn(table, SWT.LEFT, 1);
			column.setText("Topics");
			column.setWidth(150);
			column = new TableColumn(table, SWT.LEFT, 2);
			column.setText("Name");
			column.setWidth(150);
			column = new TableColumn(table, SWT.LEFT, 3);
			column.setText("PupUp Mode");
			column.setWidth(80);
			column = new TableColumn(table, SWT.LEFT, 4);
			column.setText("Auto Start");
			column.setWidth(80);
			column = new TableColumn(table, SWT.LEFT, 5);
			column.setText("Font");
			column.setWidth(100);

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
			_topicTableEditorMouseListener = new TopicTableEditorMouseListener(
					editor, this);
			table.addMouseListener(_topicTableEditorMouseListener);

			table.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// just the selection of 'checkbox' (detail 32) is of
					// interest.
					if (e.detail != 32) {
						return;
					}
					super.widgetSelected(e);
					Table table = tableViewer.getTable();
					for (int i = 0; i < table.getItemCount(); i++) {
						if (table.getItem(i).getChecked()) {
							table.getItem(i).setChecked(false);
						}
					}
					TableItem item = (TableItem) e.item;
					item.setChecked(true);
				}
			});
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
		return 6;
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


	public void setColumnTableReference(
			ExchangeablePreferenceColumnTableEditor preferenceColumnTableEditor) {
		_preferenceColumnTableEditor = preferenceColumnTableEditor;
	}

	public void setRowOfTopicSelection(int i, String topicTitle) {
		_preferenceColumnTableEditor.setSelectionToColumnEditor(i, topicTitle);
	}

	public void updateTopicTitle(String text) {
		_preferenceColumnTableEditor.updateTopicTitle(text);
	}
}
