/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.preferences;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class is a copy from the abstract eclipse class 'ListEditor' with
 * changes that now the items are not displayed in a 'List' but in a 'Table'. In
 * addition the items in the table are editable.
 *
 * @author jhatje
 *
 */
public class PreferenceTopicTableEditor extends PreferenceTableEditor {

	private static final String ITEM_SEPARATOR = ";";
    private static final String INNER_ITEM_SEPARATOR_AS_REGEX = "\\?";
    private static final String INNER_ITEM_SEPARATOR = "?";

    private ExchangeablePreferenceColumnTableEditor _preferenceColumnTableEditor = null;

	private TopicTableEditorMouseListener _topicTableEditorMouseListener;

	private final List<ColumnDescription> _columnDescriptions;

	public PreferenceTopicTableEditor(@Nonnull final List<ColumnDescription> columnDescriptions) {
	    super();
	    _columnDescriptions = new ArrayList<ColumnDescription>();
	    for (ColumnDescription columnDescription : columnDescriptions) {
	        _columnDescriptions.add(columnDescription);
	    }
    }


    /**
     * Initializes the field editor.
     *
     * @param name
     *            the name of the preference this field editor works on
     * @param labelText
     *            the label text of the field editor
     * @param parent
     *            the parent of the field editor's control
     */
    public void init(@Nonnull final String name,
                     @Nonnull final String labelText,
                     @Nonnull final Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

	/**
	 * Notifies that the Add button has been pressed. A new tableItem is set at
	 * the end of the table with initial strings that the user has to adjust.
	 */
	void addPressed() {
		setPresentsDefaultValue(false);
		int itemNumber = tableViewer.getTable().getItemCount();
		TableItem item = new TableItem(tableViewer.getTable(), SWT.NONE,
				itemNumber);
		for (ColumnDescription columnDescription : _columnDescriptions) {
		    String defaultValue = columnDescription.getDefaultValue();
		    if (defaultValue != null) {
		        item.setText(_columnDescriptions.indexOf(columnDescription), defaultValue);
            }
        }
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(final int numColumns) {
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
	@Nonnull
    protected String createList(@Nonnull final TableItem[] items) {
        StringBuffer preferenceString = new StringBuffer();
        for (TableItem tableItem : items) {
            for (ColumnDescription columnDescription : _columnDescriptions) {
                appendStringForColumn(preferenceString, tableItem, columnDescription);
                appendSeparator(preferenceString, columnDescription);
            }
        }
        return preferenceString.toString();
    }

    private void appendStringForColumn(@Nonnull final StringBuffer preferenceString,
                                       @Nonnull final TableItem tableItem,
                                       @Nonnull final ColumnDescription columnDescription) {
        if (columnDescription == ColumnDescription.IS_DEFAULT_ENTRY) {
            // Special case for the checkbox
            if (tableItem.getChecked()) {
                preferenceString.append("default");
            }
        } else {
            preferenceString.append(tableItem.getText(columnDescription.getColumnIndex()));
        }
    }


	private void appendSeparator(@Nonnull final StringBuffer preferenceString, @Nonnull final ColumnDescription columnDescription) {
        if (columnDescription.isLast()) {
            preferenceString.append(ITEM_SEPARATOR);
        } else {
            preferenceString.append(INNER_ITEM_SEPARATOR);
        }
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
			String s = getPreferenceStore().getString(getPreferenceName());
			String[] array = parseString(s);
			TableItem item;
			for (String element : array) {
				item = new TableItem(tableViewer.getTable(), SWT.NONE);
				String[] tableRowFromPreferences = element.split(INNER_ITEM_SEPARATOR_AS_REGEX);
				if (tableRowFromPreferences[0].equals("default")) {
					tableRowFromPreferences[0] = "";
					item.setChecked(true);
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
	public TableViewer getTableControl(final Composite parent) {
		if (tableViewer == null) {
			int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.CHECK;
			Table table = new Table(parent, style);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			for (ColumnDescription columnDescription : _columnDescriptions) {
			    TableColumn column = new TableColumn(table, SWT.LEFT, _columnDescriptions.indexOf(columnDescription));
			    column.setText(columnDescription.getTitle());
			    column.setWidth(columnDescription.getColumnWidth());
            }

			// Create an editor object to use for text editing
			final TableEditor editor = new TableEditor(table);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;

			tableViewer = new TableViewer(table);
			tableViewer.getTable().setFont(parent.getFont());
			tableViewer.getTable().addSelectionListener(getSelectionListener());
			tableViewer.getTable().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					tableViewer = null;
				}
			});
			_topicTableEditorMouseListener = new TopicTableEditorMouseListener(
					editor, this);
			table.addMouseListener(_topicTableEditorMouseListener);

			table.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
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
		return _columnDescriptions.size();
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
	protected String[] parseString(final String stringList) {
		return stringList.split(ITEM_SEPARATOR);
	}


	public void setColumnTableReference(
			final ExchangeablePreferenceColumnTableEditor preferenceColumnTableEditor) {
		_preferenceColumnTableEditor = preferenceColumnTableEditor;
	}

	public void setRowOfTopicSelection(final int i, final String topicTitle) {
		_preferenceColumnTableEditor.setSelectionToColumnEditor(i, topicTitle);
	}

	public void updateTopicTitle(final String text) {
		_preferenceColumnTableEditor.updateTopicTitle(text);
	}
}
