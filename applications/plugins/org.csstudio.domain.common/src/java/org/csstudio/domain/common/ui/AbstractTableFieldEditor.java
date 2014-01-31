/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.domain.common.ui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class is a copy from the abstract eclipse class 'ListEditor' with
 * changes that now the items are not displayed in a 'List' but in a 'Table'. In
 * addition the items in the table are editable.
 *
 * @author jhatje
 * @author $Author: $
 * @since 16.03.2011
 */
public abstract class AbstractTableFieldEditor extends FieldEditor {
    protected static final int EDITABLECOLUMN = 1;

    // the string to separate the columns.
    private static final String COLUMN_SEPARATOR = "?";

    // the string to separate the rows.
    private static final String ROW_SEPARATOR = ";";


    /**
     * The table for the items in the menu. (Currently the options of the JFace
     * component are not used. The TableViewer is just a container for the SWT
     * table. Maybe the TableViewer can be replaced with the SWT table.)
     */
    private TableViewer _tableViewer;

    /**
     * The button box containing the Add, Remove, Up, and Down buttons;
     * <code>null</code> if none (before creation or after disposal).
     */
    private Composite _buttonBox;

    /**
     * The Add button.
     */
    private Button _addButton;

    /**
     * The Remove button.
     */
    private Button _removeButton;

    /**
     * The Up button.
     */
    private Button _upButton;

    /**
     * The Down button.
     */
    private Button _downButton;

    private List<List<String>> _parseString;

    private SelectionListener _addButtonSelectionListner;

    private SelectionListener _downButtonSelectionListner;

    private SelectionListener _upButtonSelectionListner;

    private SelectionListener _removeButtonSelectionListner;

    private SelectionAdapter _tableSelectionChangeListner;

    /**
     * Creates a new list field editor
     */
    protected AbstractTableFieldEditor() {
        // NOP
    }

    public void init(@Nonnull final String name,
                     @Nonnull final String labelText,
                     @Nonnull final Composite parent) {
        init(name, labelText);
        createSelectionListener();
        createControl(parent);
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
     * Creates a selection listener.
     */
    public void createSelectionListener() {
        _downButtonSelectionListner = new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable final SelectionEvent event) {
                downPressed();
            }
        };
        _upButtonSelectionListner = new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable final SelectionEvent event) {
                upPressed();
            }
        };
        _addButtonSelectionListner = new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable final SelectionEvent event) {
                addPressed();
            }
        };
        _removeButtonSelectionListner = new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable final SelectionEvent event) {
                removePressed();
            }
        };
        _tableSelectionChangeListner = new SelectionAdapter() {
            @Override
            public void widgetSelected(@Nullable final SelectionEvent event) {
                selectionChanged();
            }
        };
    }

    /**
     * Returns this field editor's button box containing the Add, Remove, Up,
     * and Down button.
     *
     * @param parent
     *            the parent control
     * @return the button box
     */
    @Nonnull
    public Composite getButtonBoxControl(@Nonnull final Composite parent) {
        if (_buttonBox == null) {
            _buttonBox = new Composite(parent, SWT.NULL);
            final GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            _buttonBox.setLayout(layout);
            createButtons(_buttonBox);
            _buttonBox.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(@Nullable final DisposeEvent event) {
                    disposeButtonBox();
                }
            });

        } else {
            checkParent(_buttonBox, parent);
        }

        selectionChanged();
        return _buttonBox;
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box
     *            the box for the buttons
     */
    private void createButtons(@Nonnull final Composite box) {
        _addButton = createPushButton(box, "ListEditor.add", _addButtonSelectionListner);//$NON-NLS-1$
        _removeButton = createPushButton(box, "ListEditor.remove", _removeButtonSelectionListner);//$NON-NLS-1$
        _upButton = createPushButton(box, "ListEditor.up", _upButtonSelectionListner);//$NON-NLS-1$
        _downButton = createPushButton(box, "ListEditor.down", _downButtonSelectionListner);//$NON-NLS-1$
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
    @Nonnull
    private Button createPushButton(@Nonnull final Composite parent,
                                    @Nonnull final String key,
                                    @Nonnull final SelectionListener selectionListener) {
        final Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        final GridData data = new GridData(GridData.FILL_HORIZONTAL);
        final int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        button.addSelectionListener(selectionListener);
        return button;
    }

    /**
     * Returns this field editor's table control.
     *
     * @param parent
     *            the parent control
     * @return the list control
     */
    @Nonnull
    public TableViewer getTableControl(@Nonnull final Composite parent) {
        if (!hasTableViewer()) {
            final int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
            final Table table = new Table(parent, style);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            setTableViewer(new TableViewer(table));
            createColumns();
            getTable().setFont(parent.getFont());
            getTable().addSelectionListener(_tableSelectionChangeListner);
            getTable().addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(@Nullable final DisposeEvent event) {
                    removeTableViewer();
                }
            });
        } else {
            checkParent(getTable(), parent);
        }
        return getTableViewer();
    }

    /**
     * @param table
     */
    public abstract void createColumns();

    @Override
    public int getNumberOfControls() {
        // One for the Table one for the Buttons
        return 2;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    public void setFocus() {
        final Table table = _tableViewer.getTable();
        if (table != null) {
            table.setFocus();
        }
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    @Override
    public void setEnabled(final boolean enabled, @Nullable final Composite parent) {
        super.setEnabled(enabled, parent);
        getTableControl(parent).getTable().setEnabled(enabled);
        _addButton.setEnabled(enabled);
        _removeButton.setEnabled(enabled);
        _upButton.setEnabled(enabled);
        _downButton.setEnabled(enabled);
    }

    /**
     * Notifies that the Add button has been pressed. A new tableItem is set at
     * the end of the table with initial stings that the user has to adjust.
     */
    protected void addPressed() {
        setPresentsDefaultValue(false);
        final List<String> newRowList = new ArrayList<String>();
        newRowList.add("<typ>");
        newRowList.add("<path>");
        _parseString.add(newRowList);
        getTableViewer().refresh(_parseString);
    }

    /**
     * Notifies that the Remove button has been pressed.
     */
    protected void removePressed() {
        setPresentsDefaultValue(false);
        final int index = _tableViewer.getTable().getSelectionIndex();
        if (index >= 0) {
            _tableViewer.getTable().remove(index);
            selectionChanged();
        }
    }

    /**
     * Notifies that the Up button has been pressed.
     */
    protected void upPressed() {
        swap(true);
    }

    /**
     * Notifies that the Down button has been pressed.
     */
    protected void downPressed() {
        swap(false);
    }

    /**
     * Notifies that the list selection has changed.
     */
    protected void selectionChanged() {

        final int index = _tableViewer.getTable().getSelectionIndex();
        final int size = _tableViewer.getTable().getItemCount();

        _removeButton.setEnabled(index >= 0);
        _upButton.setEnabled(size > 1 && index > 0);
        _downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }

    /**
     * Moves the currently selected item up or down.
     *
     * @param up
     *            <code>true</code> if the item should move up, and
     *            <code>false</code> if it should move down
     */
    private void swap(final boolean up) {
        setPresentsDefaultValue(false);
        final int index = _tableViewer.getTable().getSelectionIndex();
        if (index >= 0 && index<_parseString.size()) {
            List<String> movedRow = _parseString.get(index);
            int newIndex;
            newIndex = up ? index-1 : index+1;
            movedRow = _parseString.set(newIndex, movedRow);
            _parseString.set(index, movedRow);
            _tableViewer.refresh(_parseString);
            getTable().setSelection(newIndex);
            selectionChanged();
        }
    }

    @Nonnull
    protected final TableViewer getTableViewer() {
        assert hasTableViewer() : "_tableViewer must not be null";
        return _tableViewer;
    }

    protected final boolean hasTableViewer() {
        return _tableViewer != null;
    }

    protected final void setTableViewer(@Nonnull final TableViewer tableViewer) {
        _tableViewer = tableViewer;
    }

    protected final void removeTableViewer() {
        _tableViewer = null;

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
        final int columnCount = getTable().getColumnCount();
        final StringBuilder preferenceString = new StringBuilder();
        if (columnCount > 0) {
            for (final TableItem tableItem : items) {
                for (int i = 0; i < columnCount; i++) {
                    preferenceString.append(tableItem.getText(i));
                    preferenceString.append(COLUMN_SEPARATOR);
                }
                preferenceString.reverse();
                preferenceString.append(ROW_SEPARATOR);
            }
        }
        return preferenceString.toString();
    }

    @Override
    protected void doFillIntoGrid(@Nullable final Composite parent, final int numColumns) {
        final Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        _tableViewer = getTableControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        _tableViewer.getTable().setLayoutData(gd);

        _buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        _buttonBox.setLayoutData(gd);
    }

    /**
     * Set the file path and menu name set by the user from preferences in the
     * table rows.
     */
    @Override
    protected void doLoad() {
        final String s = getPreferenceStore().getString(getPreferenceName());
        final List<List<String>> parseString = parseString(s);
        setPreferenceStructure(parseString);
        getTableViewer().setInput(parseString);
    }

    /**
     * @param parseString
     */
    public void setPreferenceStructure(@Nonnull final List<List<String>> parseString) {
        _parseString = parseString;
    }

    /**
     * @param s
     * @return
     */
    @Nonnull
    public static List<List<String>> parseString(@Nonnull final String s) {
        final List<List<String>> rowsList = new ArrayList<List<String>>();

        final String[] rows = s.split(ROW_SEPARATOR);
        for (final String row : rows) {
            final String[] columns = row.split("\\" + COLUMN_SEPARATOR);
            final List<String> columnsList = new ArrayList<String>();
            for (final String columnWidthSet : columns) {
                columnsList.add(columnWidthSet);
            }
            rowsList.add(columnsList);
        }
        return rowsList;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doLoadDefault() {
        _parseString.clear();
        getTableViewer().refresh(_parseString);
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doStore() {
        setTableSettingsToPreferenceString();
        final StringBuffer buffer = new StringBuffer();
        for (final List<String> columnSetting : _parseString) {
            for (final String string : columnSetting) {
                buffer.append(string);
                buffer.append(COLUMN_SEPARATOR);
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append(ROW_SEPARATOR);
        }
        buffer.deleteCharAt(buffer.length() - 1);
        final String string = buffer.toString();
        getPreferenceStore().setValue(getPreferenceName(), string);
    }

    /**
     * @param table
     */
    public abstract void setTableSettingsToPreferenceString();

    protected void setAddButton(@Nullable final Button addButton) {
        _addButton = addButton;
    }

    @CheckForNull
    protected Button getAddButton() {
        return _addButton;
    }

    /**
     * @return
     * @return
     */
    @CheckForNull
    public Table getTable() {
        final TableViewer tableViewer = getTableViewer();
        return tableViewer.getTable();
    }

    /**
     *
     */
    protected void disposeButtonBox() {
        setAddButton(null);
        _removeButton = null;
        _upButton = null;
        _downButton = null;
        _buttonBox = null;
    }

}
