/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
package org.csstudio.sds.ui.internal.properties.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.dal.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.editparts.AbstractContainerEditPart;
import org.csstudio.sds.ui.editparts.AbstractWidgetEditPart;
import org.csstudio.sds.ui.internal.localization.Messages;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * The PropertySheetViewer displays the properties of objects. The model for the viewer consists of
 * a hierarchy of <code>IPropertySheetEntry</code>.
 * <p>
 * This viewer also supports the optional catogorization of the first level
 * <code>IPropertySheetEntry</code> s by using instances of <code>PropertySheetCategory</code>.
 *
 * @author Sven Wende
 */
final class PropertySheetViewer extends Viewer {
    /**
     * The input objects for the viewer.
     */
    private Object[] _input;

    /**
     * The root entry of the viewer.
     */
    private IPropertySheetEntry _rootEntry;

    /**
     * The current categories.
     */
    private PropertySheetCategory[] _categories;

    /**
     * Tree widget.
     */
    private final Tree _tree;

    /**
     * Tree editor.
     */
    private final TreeEditor _treeEditor;

    /**
     * The column labels of the tree.
     */
    private static String[] _columnLabels = { "Name", "Value" };

    /**
     * Index of the column with cell editor support.
     */
    private final int _columnToEdit = 1;

    /**
     * A cell editor reference.
     */
    private CellEditor _cellEditor;

    /**
     * An entry listener.
     */
    private IPropertySheetEntryListener _entryListener;

    /**
     * A cell editor listener.
     */
    private ICellEditorListener _editorListener;

    /**
     * Flag to indicate if categories (if any) should be shown.
     */
    private boolean _isShowingCategories = true;

    /**
     * Flag to indicate expert properties should be shown.
     */
    private boolean _isShowingExpertProperties = false;

    /**
     * The status line manager for showing messages.
     */
    private IStatusLineManager _statusLineManager;

    /**
     * Cell editor activation listeners.
     */
    private final ListenerList _activationListeners = new ListenerList();

    /**
     * The property sheet sorter.
     */
    private PropertySheetSorter _sorter = new PropertySheetSorter();

    /**
     * A List of IWidgetSelectionListener.
     */
    private final List<IWidgetSelectionListener> _widgetListener = new ArrayList<IWidgetSelectionListener>();

    /**
     * Creates a property sheet viewer on a newly-created tree control under the given parent. The
     * viewer has no input, and no root entry.
     *
     * @param parent
     *            the parent control
     */
    public PropertySheetViewer(final Composite parent) {
        _tree = new Tree(parent, SWT.FULL_SELECTION | SWT.SINGLE | SWT.HIDE_SELECTION);

        // configure the widget
        _tree.setLinesVisible(true);
        _tree.setHeaderVisible(true);

        // configure the columns
        addColumns();

        // add our listeners to the widget
        hookControl();

        // create a new tree editor
        _treeEditor = new TreeEditor(_tree);

        final TreeViewer viewer = new TreeViewer(_tree);

        ProcessVariableExchangeUtil.addProcessVariableAddressDropSupport(viewer.getControl(),
                DND.DROP_COPY | DND.DROP_NONE, new IProcessVariableAdressReceiver() {
                    @Override
                    public void receive(final IProcessVariableAddress[] pvs, final DropTargetEvent event) {
                        if (event.item != null
                                && event.item.getData() instanceof IPropertySheetEntry) {
                            final IPropertySheetEntry entry = (IPropertySheetEntry) event.item.getData();
                            final DynamicsDescriptor[] descriptors = entry.getDynamicsDescriptors();
                            DynamicsDescriptor dynamicsDescriptor;
                            if (descriptors == null || descriptors.length == 0
                                    || descriptors[0] == null) {
                                dynamicsDescriptor = new DynamicsDescriptor();
                            } else {
                                dynamicsDescriptor = entry.getDynamicsDescriptors()[0].clone();
                            }

                            for (final IProcessVariableAddress pv : pvs) {
                                dynamicsDescriptor.addInputChannel(new ParameterDescriptor(pv
                                        .getFullName()));
                            }

                            final DynamicAspectsWizard wizard = entry
                                    .getDynamicsDescriptionConfigurationWizard();

                            if (wizard != null) {
                                if(Window.OK ==ModalWizardDialog.open(Display.getCurrent()
                                        .getActiveShell(), wizard)) {
                                    entry.applyDynamicsDescriptor(wizard.getDynamicsDescriptor());
                                }
                            }
                        }
                    }
                });

        // create the entry and editor listener
        createEntryListener();
        createEditorListener();
    }

    /**
     * Adds a IWidgetSelectionListener.
     *
     * @param listener
     *            The IWidgetSelectionListener, which should be added
     */
    public void addWidgetSelectionListener(final IWidgetSelectionListener listener) {
        if (!_widgetListener.contains(listener)) {
            _widgetListener.add(listener);
        }
    }

    /**
     * Activate a cell editor for the given selected tree item.
     *
     * @param item
     *            the selected tree item
     */
    private void activateCellEditor(final TreeItem item) {
        // ensure the cell editor is visible
        _tree.showSelection();

        // Get the entry for this item
        final IPropertySheetEntry activeEntry = (IPropertySheetEntry) item.getData();

        // Get the cell editor for the entry.
        // Note that the editor parent must be the Tree control
        _cellEditor = activeEntry.getEditor(_tree);

        if (_cellEditor != null) {

            // activate the cell editor
            _cellEditor.activate();

            // if the cell editor has no control we can stop now
            final Control control = _cellEditor.getControl();
            if (control == null) {
                _cellEditor.deactivate();
                _cellEditor = null;
                return;
            }

            // add our editor listener
            _cellEditor.addListener(_editorListener);

            // set the layout of the tree editor to match the cell editor
            final CellEditor.LayoutData layout = _cellEditor.getLayoutData();
            _treeEditor.horizontalAlignment = layout.horizontalAlignment;
            _treeEditor.grabHorizontal = layout.grabHorizontal;
            _treeEditor.minimumWidth = layout.minimumWidth;
            _treeEditor.setEditor(control, item, _columnToEdit);

            // set the error text from the cel editor
            setErrorMessage(_cellEditor.getErrorMessage());

            // give focus to the cell editor
            _cellEditor.setFocus();

            // notify of activation
            fireCellEditorActivated(_cellEditor);
        }
    }

    /**
     * Adds a cell editor activation listener. Has no effect if an identical activation listener is
     * already registered.
     *
     * @param listener
     *            a cell editor activation listener
     */
    void addActivationListener(final ICellEditorActivationListener listener) {
        _activationListeners.add(listener);
    }

    /**
     * Add columns to the tree and set up the layout manager accordingly.
     */
    private void addColumns() {

        // create the columns
        final TreeColumn[] columns = _tree.getColumns();
        for (int i = 0; i < _columnLabels.length; i++) {
            final String string = _columnLabels[i];
            if (string != null) {
                TreeColumn column;
                if (i < columns.length) {
                    column = columns[i];
                } else {
                    column = new TreeColumn(_tree, 0);
                }
                column.setText(string);
            }
        }

        _tree.addControlListener(new ControlAdapter() {
            private int _counter = 0;

            @Override
            public void controlResized(final ControlEvent e) {
                final Rectangle area = _tree.getClientArea();
                final TreeColumn[] cols = _tree.getColumns();

                if (area.width > 0) {
                    cols[0].setWidth(area.width * 60 / 100);
                    cols[1].setWidth(area.width * 40 / 100);

                    // Workarround: this is ugly, but it
                    // seems,
                    // there is a "smell" in the SWT port, which prevents
                    // the viewer
                    // from beeing initialized with the right size
                    _counter++;
                    if (_counter > 3) {
                        _tree.removeControlListener(this);
                    }
                }
            }
        });

    }

    /**
     * Asks the entry currently being edited to apply its current cell editor value.
     */
    private void applyEditorValue() {
        final TreeItem treeItem = _treeEditor.getItem();
        // treeItem can be null when view is opened
        if (treeItem == null || treeItem.isDisposed()) {
            return;
        }
        final IPropertySheetEntry entry = (IPropertySheetEntry) treeItem.getData();
        entry.applyEditorValue();
    }

    /**
     * Creates the child items for the given widget (item or tree). This method is called when the
     * item is expanded for the first time or when an item is assigned as the root of the tree.
     *
     * @param widget
     *            TreeItem or Tree to create the children in.
     */
    private void createChildren(final Widget widget) {
        // get the current child items
        final TreeItem[] childItems = getChildItems(widget);

        if (childItems.length > 0) {
            final Object data = childItems[0].getData();
            if (data != null) {
                // children already there!
                return;
            }
            // remove the dummy
            childItems[0].dispose();
        }

        // get the children and create their tree items
        final Object node = widget.getData();
        final List children = getChildren(node);
        if (children.isEmpty()) {
            // this item does't actually have any children
            return;
        }
        for (int i = 0; i < children.size(); i++) {
            // create a new tree item
            createItem(children.get(i), widget, i);
        }
    }

    /**
     * Creates a new cell editor listener.
     */
    private void createEditorListener() {
        _editorListener = new ICellEditorListener() {
            @Override
            public void cancelEditor() {
                deactivateCellEditor();
            }

            @Override
            public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
                // Do nothing
            }

            @Override
            public void applyEditorValue() {
                // Do nothing
            }
        };
    }

    /**
     * Creates a new property sheet entry listener.
     */
    private void createEntryListener() {
        _entryListener = new IPropertySheetEntryListener() {
            @Override
            public void childEntriesChanged(final IPropertySheetEntry entry) {
                // update the children of the given entry
                if (entry == _rootEntry) {
                    updateChildrenOf(entry, _tree);
                } else {
                    final TreeItem item = findItem(entry);
                    if (item != null) {
                        updateChildrenOf(entry, item);
                    }
                }
            }

            @Override
            public void valueChanged(final IPropertySheetEntry entry) {
                // update the given entry
                final TreeItem item = findItem(entry);
                if (item != null) {
                    updateEntry(entry, item);
                }
            }

            @Override
            public void errorMessageChanged(final IPropertySheetEntry entry) {
                // update the error message
                setErrorMessage(entry.getErrorText());
            }
        };
    }

    /**
     * Creates a new tree item, sets the given entry or category (node)in its user data field, and
     * adds a listener to the node if it is an entry.
     *
     * @param node
     *            the entry or category associated with this item
     * @param parent
     *            the parent widget
     * @param index
     *            indicates the position to insert the item into its parent
     */
    private void createItem(final Object node, final Widget parent, final int index) {
        // create the item
        TreeItem item;
        if (parent instanceof TreeItem) {
            item = new TreeItem((TreeItem) parent, SWT.NONE, index);
        } else {
            item = new TreeItem((Tree) parent, SWT.NONE, index);
        }

        // set the user data field
        item.setData(node);

        // add our listener
        if (node instanceof IPropertySheetEntry) {
            ((IPropertySheetEntry) node).addPropertySheetEntryListener(_entryListener);
        }

        // update the visual presentation
        if (node instanceof IPropertySheetEntry) {
            updateEntry((IPropertySheetEntry) node, item);
        } else {
            updateCategory((PropertySheetCategory) node, item);
        }
    }

    /**
     * Deactivate the currently active cell editor.
     */
    void deactivateCellEditor() {
        _treeEditor.setEditor(null, null, _columnToEdit);
        if (_cellEditor != null) {
            _cellEditor.deactivate();
            fireCellEditorDeactivated(_cellEditor);
            _cellEditor.removeListener(_editorListener);
            _cellEditor = null;
        }
        // clear any error message from the editor
        setErrorMessage(null);
    }

    /**
     * Sends out a selection changed event for the entry tree to all registered listeners.
     */
    private void entrySelectionChanged() {
        final SelectionChangedEvent changeEvent = new SelectionChangedEvent(this, getSelection());
        fireSelectionChanged(changeEvent);
    }

    /**
     * Return a tree item in the property sheet that has the same entry in its user data field as
     * the supplied entry. Return <code>null</code> if there is no such item.
     *
     * @param entry
     *            the entry to serach for
     * @return the TreeItem for the entry or <code>null</code> if there isn't one.
     */
    protected TreeItem findItem(final IPropertySheetEntry entry) {
        // Iterate through treeItems to find item
        final TreeItem[] items = _tree.getItems();
        for (final TreeItem item : items) {
            final TreeItem findItem = findItem(entry, item);
            if (findItem != null) {
                return findItem;
            }
        }
        return null;
    }

    /**
     * Return a tree item in the property sheet that has the same entry in its user data field as
     * the supplied entry. Return <code>null</code> if there is no such item.
     *
     * @param entry
     *            the entry to search for
     * @param item
     *            the item look in
     * @return the TreeItem for the entry or <code>null</code> if there isn't one.
     */
    private TreeItem findItem(final IPropertySheetEntry entry, final TreeItem item) {
        // compare with current item
        if (entry == item.getData()) {
            return item;
        }

        // recurse over children
        final TreeItem[] items = item.getItems();
        for (final TreeItem childItem : items) {
            final TreeItem findItem = findItem(entry, childItem);
            if (findItem != null) {
                return findItem;
            }
        }
        return null;
    }

    /**
     * Notifies all registered cell editor activation listeners of a cell editor activation.
     *
     * @param activatedCellEditor
     *            the activated cell editor
     */
    private void fireCellEditorActivated(final CellEditor activatedCellEditor) {
        final Object[] listeners = _activationListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            ((ICellEditorActivationListener) listeners[i]).cellEditorActivated(activatedCellEditor);
        }
    }

    /**
     * Notifies all registered cell editor activation listeners of a cell editor deactivation.
     *
     * @param activatedCellEditor
     *            the deactivated cell editor
     */
    private void fireCellEditorDeactivated(final CellEditor activatedCellEditor) {
        final Object[] listeners = _activationListeners.getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            ((ICellEditorActivationListener) listeners[i])
                    .cellEditorDeactivated(activatedCellEditor);
        }
    }

    /**
     * Returns the active cell editor of this property sheet viewer or <code>null</code> if no cell
     * editor is active.
     *
     * @return the active cell editor
     */
    public CellEditor getActiveCellEditor() {
        return _cellEditor;
    }

    /**
     * Gets child items.
     *
     * @param widget
     *            the widget
     * @return child tree items
     */
    private TreeItem[] getChildItems(final Widget widget) {
        if (widget instanceof Tree) {
            return ((Tree) widget).getItems();
        } else if (widget instanceof TreeItem) {
            return ((TreeItem) widget).getItems();
        }
        // shouldn't happen
        return new TreeItem[0];
    }

    /**
     * Returns the sorted children of the given category or entry.
     *
     * @param node
     *            a category or entry
     * @return the children of the given category or entry (element type
     *         <code>IPropertySheetEntry</code> or <code>PropertySheetCategory</code>)
     */
    private List getChildren(final Object node) {
        // cast the entry or category
        IPropertySheetEntry entry = null;
        PropertySheetCategory category = null;
        if (node instanceof IPropertySheetEntry) {
            entry = (IPropertySheetEntry) node;
        } else {
            category = (PropertySheetCategory) node;
        }

        // get the child entries or categories
        List children;
        if (category == null) {
            children = getChildren(entry);
        } else {
            children = getChildren(category);
        }

        return children;
    }

    /**
     * Returns the child entries of the given entry.
     *
     * @param entry
     *            The entry to search
     *
     * @return the children of the given entry (element type <code>IPropertySheetEntry</code>)
     */
    private List getChildren(final IPropertySheetEntry entry) {
        // if the entry is the root and we are showing categories, and we have
        // more than the
        // defualt category, return the categories
        if (entry == _rootEntry && _isShowingCategories) {
            if (_categories.length > 1
                    || _categories.length == 1 && !_categories[0].getCategoryName().equals(
                            Messages.PropertyViewer_misc)) {
                return Arrays.asList(_categories);
            }
        }

        // return the sorted & filtered child entries
        return getSortedEntries(getFilteredEntries(entry.getChildEntries()));
    }

    /**
     * Returns the child entries of the given category.
     *
     * @param category
     *            The category to search
     *
     * @return the children of the given category (element type <code>IPropertySheetEntry</code>)
     */
    private List getChildren(final PropertySheetCategory category) {
        return getSortedEntries(getFilteredEntries(category.getChildEntries()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control getControl() {
        return _tree;
    }

    /**
     * Returns the entries which match the current filter.
     *
     * @param entries
     *            the entries to filter
     * @return the entries which match the current filter (element type
     *         <code>IPropertySheetEntry</code>)
     */
    @SuppressWarnings("unchecked")
    private List getFilteredEntries(final IPropertySheetEntry[] entries) {
        // if no filter just return all entries
        if (_isShowingExpertProperties) {
            return Arrays.asList(entries);
        }

        // check each entry for the filter
        final List filteredEntries = new ArrayList(entries.length);
        for (final IPropertySheetEntry entry : entries) {
            if (entry != null) {
                final String[] filters = entry.getFilters();
                boolean expert = false;
                if (filters != null) {
                    for (final String filter : filters) {
                        if (filter.equals(IPropertySheetEntry.FILTER_ID_EXPERT)) {
                            expert = true;
                            break;
                        }
                    }
                }
                if (!expert) {
                    filteredEntries.add(entry);
                }
            }
        }
        return filteredEntries;
    }

    /**
     * Returns a sorted list of <code>IPropertySheetEntry</code> entries.
     *
     * @param unsortedEntries
     *            unsorted list of <code>IPropertySheetEntry</code>
     * @return a sorted list of the specified entries
     */
    @SuppressWarnings("unchecked")
    private List getSortedEntries(final List unsortedEntries) {
        final IPropertySheetEntry[] propertySheetEntries = (IPropertySheetEntry[]) unsortedEntries
                .toArray(new IPropertySheetEntry[unsortedEntries.size()]);
        _sorter.sort(propertySheetEntries);
        return Arrays.asList(propertySheetEntries);
    }

    /**
     * @return The <code>PropertySheetViewer</code> implementation of this method declared on
     *         <code>IInputProvider</code> returns the objects for which the viewer is currently
     *         showing properties. It returns an <code>Object[]</code> or <code>null</code>.
     */
    @Override
    public Object getInput() {
        return _input;
    }

    /**
     * Returns the root entry for this property sheet viewer. The root entry is not visible in the
     * viewer.
     *
     * @return the root entry or <code>null</code>.
     */
    public IPropertySheetEntry getRootEntry() {
        return _rootEntry;
    }

    /**
     * @return The <code>PropertySheetViewer</code> implementation of this
     *         <code>ISelectionProvider</code> method returns the result as a
     *         <code>StructuredSelection</code>.
     *         <p>
     *         Note that this method only includes <code>IPropertySheetEntry</code> in the selection
     *         (no categories).
     *         </p>
     */
    @SuppressWarnings("unchecked")
    @Override
    public ISelection getSelection() {
        if (_tree.getSelectionCount() == 0) {
            return StructuredSelection.EMPTY;
        }
        final TreeItem[] sel = _tree.getSelection();
        final List entries = new ArrayList(sel.length);
        for (final TreeItem ti : sel) {
            final Object data = ti.getData();
            if (data instanceof IPropertySheetEntry) {
                entries.add(data);
            }
        }
        return new StructuredSelection(entries);
    }

    /**
     * Selection in the viewer occurred. Check if there is an active cell editor. If yes, deactivate
     * it and check if a new cell editor must be activated.
     *
     * @param selection
     *            the TreeItem that is selected
     */
    protected void handleSelect(final TreeItem selection) {
        // deactivate the current cell editor
        if (_cellEditor != null) {
            applyEditorValue();
            deactivateCellEditor();
        }

        // get the new selection
        final TreeItem[] sel = new TreeItem[] { selection };
        if (sel.length == 0) {
            setMessage(null);
            setErrorMessage(null);
        } else {
            final Object object = sel[0].getData(); // assume single selection
            if (object instanceof IPropertySheetEntry) {
                // get the entry for this item
                final IPropertySheetEntry activeEntry = (IPropertySheetEntry) object;

                // display the description for the item
                setMessage(activeEntry.getDescription());

                // activate a cell editor on the selection
                activateCellEditor(sel[0]);
            }
        }
        entrySelectionChanged();
    }

    /**
     * The expand icon for a node in this viewer has been selected to collapse a subtree. Deactivate
     * the cell editor
     *
     * @param event
     *            the SWT tree event
     */
    protected void handleTreeCollapse(final TreeEvent event) {
        if (_cellEditor != null) {
            applyEditorValue();
            deactivateCellEditor();
        }
    }

    /**
     * The expand icon for a node in this viewer has been selected to expand the subtree. Create the
     * children 1 level deep.
     * <p>
     * Note that we use a "dummy" item (no user data) to show a "+" icon beside an item which has
     * children before the item is expanded now that it is being expanded we have to create the real
     * child items
     * </p>
     *
     * @param event
     *            the SWT tree event
     */
    protected void handleTreeExpand(final TreeEvent event) {
        createChildren(event.item);
    }

    /**
     * Hides the categories.
     */
    void hideCategories() {
        _isShowingCategories = false;
        _categories = null;
        refresh();
    }

    /**
     * Hides the expert properties.
     */
    void hideExpert() {
        _isShowingExpertProperties = false;
        refresh();
    }

    /**
     * Establish this viewer as a listener on the control.
     */
    private void hookControl() {
        // Handle selections in the Tree
        // Part1: Double click only (allow traversal via keyboard without
        // activation
        _tree.addSelectionListener(new SelectionAdapter() {
            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // The viewer only owns the status line when there is
                // no 'active' cell editor
                if (_cellEditor == null || !_cellEditor.isActivated()) {
                    updateStatusLine(e.item);
                }
            }

            /*
             * (non-Javadoc)
             *
             * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                handleSelect((TreeItem) e.item);
            }
        });
        // Part2: handle single click activation of cell editor
        _tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(final MouseEvent event) {
                // only activate if there is a cell editor
                if (event.button == 1 && event.stateMask == 0) {
                    final Point pt = new Point(event.x, event.y);
                    final TreeItem item = _tree.getItem(pt);
                    if (item != null) {
                        handleSelect(item);
                    }
                }else  if (event.button == 1 && event.stateMask == SWT.MOD1) {
                    final Point pt = new Point(event.x, event.y);
                    final TreeItem item = _tree.getItem(pt);
                    openDynamicWizard(item);
                }
            }
        });

        // Add a tree listener to expand and collapse which
        // allows for lazy creation of children
        _tree.addTreeListener(new TreeListener() {
            @Override
            public void treeExpanded(final TreeEvent event) {
                handleTreeExpand(event);
            }

            @Override
            public void treeCollapsed(final TreeEvent event) {
                handleTreeCollapse(event);
            }
        });

        // Refresh the tree when F5 pressed
        _tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.character == SWT.ESC) {
                    deactivateCellEditor();
                } else if (e.keyCode == SWT.F5) {
                    // The following will simulate a reselect
                    setInput(getInput());
                } else if (e.stateMask==SWT.MOD1&&(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR)) {
                    final TreeItem[] selection = _tree.getSelection();
                    if(selection!=null&&selection.length>0) {
                        openDynamicWizard(selection[0]);
                    }
                }
            }
        });
    }

    /**
     * Update the status line based on the data of item.
     *
     * @param item
     *            the item
     */
    protected void updateStatusLine(final Widget item) {
        setMessage(null);
        setErrorMessage(null);

        // Update the status line
        if (item != null) {
            if (item.getData() instanceof PropertySheetEntry) {
                final PropertySheetEntry psEntry = (PropertySheetEntry) item.getData();

                // For entries, show the description if any, else show the label
                final String desc = psEntry.getDescription();
                if (desc != null && desc.length() > 0) {
                    setMessage(psEntry.getDescription());
                } else {
                    setMessage(psEntry.getDisplayName());
                }
            } else if (item.getData() instanceof PropertySheetCategory) {
                final PropertySheetCategory psCat = (PropertySheetCategory) item.getData();
                setMessage(psCat.getCategoryName());
            }
        }
    }

    /**
     * Updates all of the items in the tree.
     * <p>
     * Note that this means ensuring that the tree items reflect the state of the model (entry tree)
     * it does not mean telling the model to update itself.
     * </p>
     */
    @Override
    public void refresh() {
        if (_rootEntry != null) {
            updateChildrenOf(_rootEntry, _tree);
        }
    }

    /**
     * Removes the given cell editor activation listener from this viewer. Has no effect if an
     * identical activation listener is not registered.
     *
     * @param listener
     *            a cell editor activation listener
     */
    void removeActivationListener(final ICellEditorActivationListener listener) {
        _activationListeners.remove(listener);
    }

    /**
     * Remove the given item from the tree. Remove our listener if the item's user data is a an
     * entry then set the user data to null
     *
     * @param item
     *            the item to remove
     */
    private void removeItem(final TreeItem item) {
        final Object data = item.getData();
        if (data instanceof IPropertySheetEntry) {
            ((IPropertySheetEntry) data).removePropertySheetEntryListener(_entryListener);
        }
        item.setData(null);
        item.dispose();
    }

    /**
     * Reset the selected properties to their default values.
     */
    public void resetProperties() {
        // Determine the selection
        final IStructuredSelection selection = (IStructuredSelection) getSelection();

        // Iterate over entries and reset them
        final Iterator itr = selection.iterator();
        while (itr.hasNext()) {
            ((IPropertySheetEntry) itr.next()).resetPropertyValue();
        }
    }

    /**
     * Sets the error message to be displayed in the status line.
     *
     * @param errorMessage
     *            the message to be displayed, or <code>null</code>
     */
    protected void setErrorMessage(final String errorMessage) {
        // show the error message
        if (_statusLineManager != null) {
            _statusLineManager.setErrorMessage(errorMessage);
        }
    }

    /**
     * The <code>PropertySheetViewer</code> implementation of this method declared on
     * <code>Viewer</code> method sets the objects for which the viewer is currently showing
     * properties.
     * <p>
     * The input must be an <code>Object[]</code> or <code>null</code>.
     * </p>
     *
     * @param newInput
     *            the input of this viewer, or <code>null</code> if none
     */
    @Override
    public void setInput(final Object newInput) {
        // need to save any changed value when user clicks elsewhere
        applyEditorValue();
        // deactivate our cell editor
        deactivateCellEditor();

        // set the new input to the root entry
        final Object[] objects = (Object[]) newInput;
        // if (objects==null || (objects.length>0 && (objects[0] instanceof
        // DisplayEditPart || objects[0] instanceof AbstractWidgetEditPart)) ) {
        _input = objects;
        if (_input != null
                && _input.length > 0
                && (_input[0] instanceof AbstractWidgetEditPart || _input[0] instanceof AbstractContainerEditPart)) {
            for (final IWidgetSelectionListener listener : _widgetListener) {
                listener.handleWidgetSelection(_input);
            }
        } else {
            for (final IWidgetSelectionListener listener : _widgetListener) {
                listener.handleWidgetSelection(null);
            }
        }
        if (_input == null) {
            _input = new Object[0];
        }

        if (_rootEntry != null) {
            _rootEntry.setValues(_input);
            // ensure first level children are visible
            updateChildrenOf(_rootEntry, _tree);
        }
        // }
    }

    /**
     * Sets the message to be displayed in the status line. This message is displayed when there is
     * no error message.
     *
     * @param message
     *            the message to be displayed, or <code>null</code>
     */
    private void setMessage(final String message) {
        // show the message
        if (_statusLineManager != null) {
            _statusLineManager.setMessage(message);
        }
    }

    /**
     * Sets the root entry for this property sheet viewer. The root entry is not visible in the
     * viewer.
     *
     * @param root
     *            the root entry
     */
    public void setRootEntry(final IPropertySheetEntry root) {
        // If we have a root entry, remove our entry listener
        if (_rootEntry != null) {
            _rootEntry.removePropertySheetEntryListener(_entryListener);
        }

        _rootEntry = root;

        // Set the root as user data on the tree
        _tree.setData(_rootEntry);

        // Add an IPropertySheetEntryListener to listen for entry change
        // notifications
        _rootEntry.addPropertySheetEntryListener(_entryListener);

        // Pass our input to the root, this will trigger entry change
        // callbacks to update this viewer
        setInput(_input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(final ISelection selection, final boolean reveal) {
        // Do nothing by default
    }

    /**
     * Sets the sorter for this viewer.
     * <p>
     * The default sorter sorts categories and entries alphabetically. A viewer update needs to be
     * triggered after the sorter has changed.
     * </p>
     *
     * @param sorter
     *            the sorter to set (<code>null</code> will reset to the default sorter)
     * @since 3.1
     */
    public void setSorter(final PropertySheetSorter sorter) {
        if (null == sorter) {
            _sorter = new PropertySheetSorter();
        } else {
            _sorter = sorter;
        }
    }

    /**
     * Sets the status line manager this view will use to show messages.
     *
     * @param manager
     *            the status line manager
     */
    public void setStatusLineManager(final IStatusLineManager manager) {
        _statusLineManager = manager;
    }

    /**
     * Shows the categories.
     */
    void showCategories() {
        _isShowingCategories = true;
        refresh();
    }

    /**
     * Shows the expert properties.
     */
    void showExpert() {
        _isShowingExpertProperties = true;
        refresh();
    }

    /**
     * Updates the categories. Reuses old categories if possible.
     */
    @SuppressWarnings("unchecked")
    private void updateCategories() {
        // lazy initialize
        if (_categories == null) {
            _categories = new PropertySheetCategory[0];
        }

        // get all the filtered child entries of the root
        final List childEntries = getFilteredEntries(_rootEntry.getChildEntries());

        // if the list is empty, just set an empty categories array
        if (childEntries.size() == 0) {
            _categories = new PropertySheetCategory[0];
            return;
        }

        // cache old categories by their descriptor name
        final Map categoryCache = new HashMap(_categories.length * 2 + 1);
        for (final PropertySheetCategory _categorie : _categories) {
            _categorie.removeAllEntries();
            categoryCache.put(_categorie.getCategoryName(), _categorie);
        }

        // create a list of categories to get rid of
        final List categoriesToRemove = new ArrayList(Arrays.asList(_categories));

        // Determine the categories
        PropertySheetCategory misc = (PropertySheetCategory) categoryCache
                .get(Messages.PropertyViewer_misc);
        if (misc == null) {
            misc = new PropertySheetCategory(Messages.PropertyViewer_misc);
        }
        boolean addMisc = false;

        for (int i = 0; i < childEntries.size(); i++) {
            final IPropertySheetEntry childEntry = (IPropertySheetEntry) childEntries.get(i);
            final String categoryName = childEntry.getCategory();
            if (categoryName == null) {
                misc.addEntry(childEntry);
                addMisc = true;
                categoriesToRemove.remove(misc);
            } else {
                PropertySheetCategory category = (PropertySheetCategory) categoryCache
                        .get(categoryName);
                if (category == null) {
                    category = new PropertySheetCategory(categoryName);
                    categoryCache.put(categoryName, category);
                } else {
                    categoriesToRemove.remove(category);
                }
                category.addEntry(childEntry);
            }
        }

        // Add the PSE_MISC category if it has entries
        if (addMisc) {
            categoryCache.put(Messages.PropertyViewer_misc, misc);
        }

        // Sort the categories.
        // Rather than just sorting categoryCache.values(), we'd like the
        // original order to be preserved
        // (with misc added at the end, if needed) before passing to the sorter.
        final ArrayList categoryList = new ArrayList();
        final Set seen = new HashSet(childEntries.size());
        for (int i = 0; i < childEntries.size(); i++) {
            final IPropertySheetEntry childEntry = (IPropertySheetEntry) childEntries.get(i);
            final String categoryName = childEntry.getCategory();
            if (categoryName != null && !seen.contains(categoryName)) {
                seen.add(categoryName);
                final PropertySheetCategory category = (PropertySheetCategory) categoryCache
                        .get(categoryName);
                if (category != null) {
                    categoryList.add(category);
                }
            }
        }
        if (addMisc && !seen.contains(Messages.PropertyViewer_misc)) {
            categoryList.add(misc);
        }

        final PropertySheetCategory[] categoryArray = (PropertySheetCategory[]) categoryList
                .toArray(new PropertySheetCategory[categoryList.size()]);
        _sorter.sort(categoryArray);
        _categories = categoryArray;
    }

    /**
     * Update the category (but not its parent or children).
     *
     * @param category
     *            the category to update
     * @param item
     *            the tree item for the given entry
     */
    private void updateCategory(final PropertySheetCategory category, final TreeItem item) {
        // ensure that backpointer is correct
        item.setData(category);

        // Update the name and value columns
        item.setText(0, category.getCategoryName());
        item.setText(1, ""); //$NON-NLS-1$

        // update the "+" icon
        if (category.getAutoExpand()) {
            // we auto expand categories when they first appear
            createChildren(item);
            item.setExpanded(true);
            category.setAutoExpand(false);
        } else {
            // we do not want to auto expand categories if the user has
            // collapsed them
            updatePlus(category, item);
        }
    }

    /**
     * Update the child entries or categories of the given entry or category. If the given node is
     * the root entry and we are showing categories then the child entries are categories, otherwise
     * they are entries.
     *
     * @param node
     *            the entry or category whose children we will update
     * @param widget
     *            the widget for the given entry, either a <code>TableTree</code> if the node is the
     *            root node or a <code>TreeItem</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    protected void updateChildrenOf(final Object node, final Widget widget) {
        // cast the entry or category
        IPropertySheetEntry entry = null;
        PropertySheetCategory category = null;
        if (node instanceof IPropertySheetEntry) {
            entry = (IPropertySheetEntry) node;
        } else {
            category = (PropertySheetCategory) node;
        }

        // get the current child tree items
        TreeItem[] childItems = getChildItems(widget);

        // optimization! prune collapsed subtrees
        TreeItem item = null;
        if (widget instanceof TreeItem) {
            item = (TreeItem) widget;
        }
        if (item != null && !item.getExpanded()) {
            // remove all children
            for (final TreeItem childItem : childItems) {
                if (childItem.getData() != null) {
                    removeItem(childItem);
                }
            }

            // append a dummy if necessary
            if (category != null || entry.hasChildEntries()) {
                // may already have a dummy
                // It is either a category (which always has at least one child)
                // or an entry with chidren.
                // Note that this test is not perfect, if we have filtering on
                // then there in fact may be no entires to show when the user
                // presses the "+" expand icon. But this is an acceptable
                // compromise.
                childItems = getChildItems(widget);
                if (childItems.length == 0) {
                    new TreeItem(item, SWT.NULL);
                }
            }
            return;
        }

        // get the child entries or categories
        if (node == _rootEntry && _isShowingCategories) {
            // update the categories
            updateCategories();
        }
        final List children = getChildren(node);

        // remove items
        final Set set = new HashSet(childItems.length * 2 + 1);

        for (final TreeItem childItem : childItems) {
            final Object data = childItem.getData();
            if (data != null) {
                final Object e = data;
                final int ix = children.indexOf(e);
                if (ix < 0) { // not found
                    removeItem(childItem);
                } else { // found
                    set.add(e);
                }
            } else if (data == null) { // the dummy
                childItem.dispose();
            }
        }

        // WORKAROUND
        int oldCnt = -1;
        if (widget == _tree) {
            oldCnt = _tree.getItemCount();
        }

        // add new items
        final int newSize = children.size();
        for (int i = 0; i < newSize; i++) {
            final Object el = children.get(i);
            if (!set.contains(el)) {
                createItem(el, widget, i);
            }
        }

        // WORKAROUND
        if (widget == _tree && oldCnt == 0 && _tree.getItemCount() == 1) {
            _tree.setRedraw(false);
            _tree.setRedraw(true);
        }

        // get the child tree items after our changes
        childItems = getChildItems(widget);

        // update the child items
        // This ensures that the children are in the correct order
        // are showing the correct values.
        for (int i = 0; i < newSize; i++) {
            final Object el = children.get(i);
            if (el instanceof IPropertySheetEntry) {
                updateEntry((IPropertySheetEntry) el, childItems[i]);
            } else {
                updateCategory((PropertySheetCategory) el, childItems[i]);
                updateChildrenOf(el, childItems[i]);
            }
        }
        // The tree's original selection may no longer apply after the update,
        // so fire the selection changed event.
        entrySelectionChanged();
    }

    /**
     * Update the given entry (but not its children or parent).
     *
     * @param entry
     *            the entry we will update
     * @param item
     *            the tree item for the given entry
     */
    protected void updateEntry(final IPropertySheetEntry entry, final TreeItem item) {
        // ensure that backpointer is correct
        item.setData(entry);

        // update the name and value columns
        if (entry.isDynamicallySampled()) {
            item.setImage(0, CustomMediaFactory.getInstance().getImageFromPlugin(
                    SdsUiPlugin.PLUGIN_ID, "icons/dynamic.png"));
        } else {
            item.setImage(0, null);

        }
        item.setText(0, entry.getDisplayName());
        // if (entry.getValueAsString().equals(Boolean.toString(Boolean.FALSE)))
        // {
        // //System.out.println("PropertySheetViewer.updateEntry() FALSE
        // "+item);
        // item.setText(1, "F");
        // item.setImage(1,
        // CustomMediaFactory.getInstance().getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
        // "icons/unchecked.gif"));
        // } else
        // if (entry.getValueAsString().equals(Boolean.toString(Boolean.TRUE)))
        // {
        // //System.out.println("PropertySheetViewer.updateEntry() TRUE "+item);
        // item.setText(1, "T");
        // item.setImage(1,
        // CustomMediaFactory.getInstance().getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
        // "icons/checked.gif"));
        // } else {
        item.setText(1, entry.getValueAsString());
        item.setText(2, "" + entry.isDynamicallySampled());

        final Image image = entry.getImage();
        if (item.getImage(1) != image) {
            item.setImage(1, image);
        }

        // update the "+" icon
        updatePlus(entry, item);
    }

    /**
     * Updates the "+"/"-" icon of the tree item from the given entry or category.
     *
     * @param node
     *            the entry or category
     * @param item
     *            the tree item being updated
     */
    private void updatePlus(final Object node, final TreeItem item) {
        // cast the entry or category
        IPropertySheetEntry entry = null;
        PropertySheetCategory category = null;
        if (node instanceof IPropertySheetEntry) {
            entry = (IPropertySheetEntry) node;
        } else {
            category = (PropertySheetCategory) node;
        }

        final boolean hasPlus = item.getItemCount() > 0;
        final boolean needsPlus = category != null || entry.hasChildEntries();
        boolean removeAll = false;
        boolean addDummy = false;

        if (hasPlus != needsPlus) {
            if (needsPlus) {
                addDummy = true;
            } else {
                removeAll = true;
            }
        }
        if (removeAll) {
            // remove all children
            final TreeItem[] items = item.getItems();
            for (final TreeItem item2 : items) {
                removeItem(item2);
            }
        }

        if (addDummy) {
            new TreeItem(item, SWT.NULL); // append a dummy to create the
            // plus sign
        }
    }
    private void openDynamicWizard(final TreeItem item) {
        if (item != null) {
            final IPropertySheetEntry entry = (IPropertySheetEntry) item.getData();
            // Open Wizard
            if (entry != null) {
                final DynamicAspectsWizard wizard = entry.getDynamicsDescriptionConfigurationWizard();

                if (wizard != null) {
                    if(Window.OK ==ModalWizardDialog.open(Display.getCurrent()
                            .getActiveShell(), wizard)) {
                        entry.applyDynamicsDescriptor(wizard.getDynamicsDescriptor());
                    }
                }
            }
        }
    }

}
