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

import javax.annotation.Nonnull;

import org.eclipse.core.runtime.Assert;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
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
public abstract class AbstractPreferenceTableEditor extends FieldEditor {

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

	/**
	 * The selection listener.
	 */
	private SelectionListener _selectionListener;

	/**
	 * Creates a new list field editor
	 */
	protected AbstractPreferenceTableEditor() {
	    // EMPTY
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
		_selectionListener = new SelectionAdapter() {
			@Override
            public void widgetSelected(final SelectionEvent event) {
				final Widget widget = event.widget;
				if (widget == _addButton) {
					addPressed();
				} else if (widget == _removeButton) {
					removePressed();
				} else if (widget == _upButton) {
					upPressed();
				} else if (widget == _downButton) {
					downPressed();
				} else if (widget == _tableViewer.getTable()) {
					selectionChanged();
				}
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
	public Composite getButtonBoxControl(final Composite parent) {
		if (_buttonBox == null) {
			_buttonBox = new Composite(parent, SWT.NULL);
			final GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			_buttonBox.setLayout(layout);
			createButtons(_buttonBox);
			_buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					_addButton = null;
					_removeButton = null;
					_upButton = null;
					_downButton = null;
					_buttonBox = null;
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
    private void createButtons(final Composite box) {
    	_addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
    	_removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
    	_upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
    	_downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
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
    private Button createPushButton(final Composite parent, final String key) {
    	final Button button = new Button(parent, SWT.PUSH);
    	button.setText(JFaceResources.getString(key));
    	button.setFont(parent.getFont());
    	final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    	final int widthHint = convertHorizontalDLUsToPixels(button,
    			IDialogConstants.BUTTON_WIDTH);
    	data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
    			SWT.DEFAULT, true).x);
    	button.setLayoutData(data);
    	button.addSelectionListener(getSelectionListener());
    	return button;
    }

    /**
     * Returns this field editor's table control.
     *
     * @param parent
     *            the parent control
     * @return the list control
     */
    public abstract TableViewer getTableControl(Composite parent);

    /*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
    public abstract int getNumberOfControls();

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
    public void setEnabled(final boolean enabled, final Composite parent) {
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
    abstract void addPressed();

    /**
     * Notifies that the Remove button has been pressed.
     */
    private void removePressed() {
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
    private void upPressed() {
    	swap(true);
    }

    /**
     * Notifies that the Down button has been pressed.
     */
    private void downPressed() {
    	swap(false);
    }

    /**
	 * Notifies that the list selection has changed.
	 */
	private void selectionChanged() {

		final int index = _tableViewer.getTable().getSelectionIndex();
		final int size = _tableViewer.getTable().getItemCount();

		_removeButton.setEnabled(index >= 0);
		_upButton.setEnabled((size > 1) && (index > 0));
		_downButton.setEnabled((size > 1) && (index >= 0) && (index < size - 1));
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

		if (index >= 0) {
			final TableItem[] selectedTableItems = _tableViewer.getTable().getSelection();
			Assert.isTrue(selectedTableItems.length == 1);

			final String[] tableItemBackup = createBackupOfTableItem(selectedTableItems[0]);
			_tableViewer.getTable().remove(index);

			final int target = up ? index - 1 : index + 1;
			createTableItemFromBackup(target, tableItemBackup);

			_tableViewer.getTable().setSelection(target);
		}
		selectionChanged();
	}

    private void createTableItemFromBackup(final int target, @Nonnull final String[] tableItemBackup) {
        final TableItem item = new TableItem(_tableViewer.getTable(), SWT.NONE, target);
        item.setText(tableItemBackup);
    }

	@Nonnull
	private String[] createBackupOfTableItem(@Nonnull final TableItem tableItem) {
        final String[] tableRow = new String[getNumberOfControls()];
        for (int i = 0; i < tableRow.length; i++) {
            tableRow[i] = tableItem.getText(i);
        }
        return tableRow;
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
    protected abstract String createList(TableItem[] items);

    /**
     * Creates and returns a new item for the list.
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @return a new item
     */
    protected abstract String getNewInputObject();

    @Override
    protected void doFillIntoGrid(final Composite parent, final int numColumns) {
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
    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected abstract void doLoad();

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doLoadDefault() {
    	// there are no defaults for the quickstart menu.
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected abstract void doStore();

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
    	if (_addButton == null) {
    		return null;
    	}
    	return _addButton.getShell();
    }

    /**
     * Returns this field editor's selection listener. The listener is created
     * if necessary.
     *
     * @return the selection listener
     */
    protected SelectionListener getSelectionListener() {
    	if (_selectionListener == null) {
    		createSelectionListener();
    	}
    	return _selectionListener;
    }
}
