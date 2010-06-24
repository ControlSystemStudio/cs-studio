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
 *
 * $Id$
 */
package org.csstudio.alarm.table.preferences;

import java.util.ArrayList;
import java.util.Collections;
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
public class PreferenceTopicTableEditor extends AbstractPreferenceTableEditor {

	private static final String ITEM_SEPARATOR = ";";
    private static final String INNER_ITEM_SEPARATOR_AS_REGEX = "\\?";
    private static final String INNER_ITEM_SEPARATOR = "?";

    private ExchangeablePreferenceColumnTableEditor _preferenceColumnTableEditor = null;

	private TopicTableEditorMouseListener _topicTableEditorMouseListener;

	private final List<ColumnDescription> _columnDescriptions;

	public PreferenceTopicTableEditor(@Nonnull final List<ColumnDescription> columnDescriptions) {
	    super();
	    _columnDescriptions = new ArrayList<ColumnDescription>(columnDescriptions);
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
	@Override
    void addPressed() {
		setPresentsDefaultValue(false);
		final int itemNumber = _tableViewer.getTable().getItemCount();
		final TableItem item = new TableItem(_tableViewer.getTable(), SWT.NONE,
				itemNumber);
		int i = 0;
		for (final ColumnDescription columnDescription : _columnDescriptions) {
		    final String defaultValue = columnDescription.getDefaultValue();
		    if (defaultValue != null) {
		        item.setText(i, defaultValue);
		    }
		    i++;
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void adjustForNumColumns(final int numColumns) {
		final Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) _tableViewer.getTable().getLayoutData()).horizontalSpan = numColumns - 1;
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
	@Override
    @Nonnull
    protected String createList(@Nonnull final TableItem[] items) {
        final StringBuilder prefBuilder = new StringBuilder();
        for (final TableItem tableItem : items) {
            int i = 0;
            for (final ColumnDescription columnDescription : _columnDescriptions) {
                prefBuilder.append(getStringForColumn(tableItem, columnDescription, i));
                prefBuilder.append(getColSeparator(isLast(i)));
                i++;
            }
        }
        return prefBuilder.toString();
    }

	@Nonnull
	private String getStringForColumn(@Nonnull final TableItem tableItem,
                                      @Nonnull final ColumnDescription columnDescription,
                                      final int columnIndex) {

	    if (columnDescription == ColumnDescription.IS_DEFAULT_ENTRY) {
	        // Special case for the checkbox
	        if (tableItem.getChecked()) {
	            return "default";
	        }
            return "";
	    }
        return tableItem.getText(columnIndex);
	}

	@Nonnull
	private String getColSeparator(final boolean isLast) {
        if (isLast) {
            return ITEM_SEPARATOR;
        }
        return INNER_ITEM_SEPARATOR;
	}

	private boolean isLast(final int columnIndex) {
	    return columnIndex == (_columnDescriptions.size() - 1);
	}

    /**
	 * Set the file path and menu name set by the user from preferences in the
	 * table rows.
	 */
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    protected void doLoad() {
		if (_tableViewer != null) {
			final String s = getPreferenceStore().getString(getPreferenceName());
			final String[] array = s.split(ITEM_SEPARATOR);
			for (final String element : array) {
			    final TableItem item = new TableItem(_tableViewer.getTable(), SWT.NONE);
				final String[] tableRowFromPreferences = element.split(INNER_ITEM_SEPARATOR_AS_REGEX);
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
	@Override
    protected void doStore() {
		final String s = createList(_tableViewer.getTable().getItems());
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
	@Override
	@Nonnull
    public TableViewer getTableControl(final Composite parent) {
		if (_tableViewer == null) {
			final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.CHECK;
			final Table table = new Table(parent, style);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			int i = 0;
			for (final ColumnDescription columnDescription : _columnDescriptions) {
			    final TableColumn column = new TableColumn(table, SWT.LEFT, i++);
			    column.setText(columnDescription.getTitle());
			    column.setWidth(columnDescription.getColumnWidth());
            }

			// Create an editor object to use for text editing
			final TableEditor editor = new TableEditor(table);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;

			_tableViewer = new TableViewer(table);
			_tableViewer.getTable().setFont(parent.getFont());
			_tableViewer.getTable().addSelectionListener(getSelectionListener());
			_tableViewer.getTable().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					_tableViewer = null;
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
					final Table innerTable = _tableViewer.getTable();
					for (int j = 0; j < innerTable .getItemCount(); j++) {
						if (innerTable .getItem(j).getChecked()) {
						    innerTable .getItem(j).setChecked(false);
						}
					}
					final TableItem item = (TableItem) e.item;
					item.setChecked(true);
				}
			});
		} else {
			checkParent(_tableViewer.getTable(), parent);
		}
		return _tableViewer;
	}

	/**
	 * Creates and returns a new item for the list.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 *
	 * @return a new item
	 */
	@Override
    protected String getNewInputObject() {
		return null;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public int getNumberOfControls() {
		return _columnDescriptions.size();
	}

	@Nonnull
	public List<ColumnDescription> getColumnDescriptions() {
        return Collections.unmodifiableList(_columnDescriptions);
    }


	public final void setColumnTableReference(@Nonnull final ExchangeablePreferenceColumnTableEditor preferenceColumnTableEditor) {
		_preferenceColumnTableEditor = preferenceColumnTableEditor;
	}

	public void setRowOfTopicSelection(final int i, @Nonnull final String topicTitle) {
		_preferenceColumnTableEditor.setSelectionToColumnEditor(i, topicTitle);
	}

	public void updateTopicTitle(@Nonnull final String text) {
		_preferenceColumnTableEditor.updateTopicTitle(text);
	}
}
