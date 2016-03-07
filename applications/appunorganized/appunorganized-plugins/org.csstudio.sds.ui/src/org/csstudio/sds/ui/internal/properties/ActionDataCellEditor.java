/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.ui.internal.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.internal.model.StringProperty;
import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.ui.CheckedUiRunnable;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * A table cell editor for values of type {@link ActionData}.
 *
 * @author Kai Meyer
 */
public final class ActionDataCellEditor extends AbstractDialogCellEditor {

    /**
     * The current map.
     */
    private ActionData _actionData;
    /**
     * A copy of the original map.
     */
    private ActionData _originalActionData;

    /**
     * Creates a new string cell editor parented under the given control. The
     * cell editor value is a Map of Strings.
     *
     * @param parent
     *            The parent table.
     */
    public ActionDataCellEditor(final Composite parent) {
        super(parent, "Widget Actions");
    }

    /**
     * Creates a new string cell editor parented under the given control. The
     * cell editor value is a Map of Strings.
     *
     * @param parent
     *            The parent table.
     * @param title
     *            The title for this CellEditor
     */
    public ActionDataCellEditor(final Composite parent, final String title) {
        super(parent, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void openDialog(final Shell parentShell, final String dialogTitle) {
        ActionDataDialog dialog = new ActionDataDialog(parentShell,
                dialogTitle, "Set the attributes for the action");
        if (dialog != null && dialog.open() == Window.CANCEL) {
            _actionData = _originalActionData;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldFireChanges() {
        return _actionData != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue() {
        return _actionData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue(final Object value) {
        // Assert.isTrue(value instanceof ActionData);
        if (value == null || !(value instanceof ActionData)) {
            _originalActionData = new ActionData();
            _actionData = new ActionData();
        } else {
            _originalActionData = (ActionData) value;
            _actionData = new ActionData();
            for (AbstractWidgetActionModel action : _originalActionData.getWidgetActions()) {
                _actionData.addAction(action.makeCopy());
            }
        }
    }

    /**
     * This class represents a Dialog to add, edit and remove the entries of a
     * Map.
     *
     * @author Kai Meyer
     */
    private final class ActionDataDialog extends Dialog {
        /**
         * The title of the dialog.
         */
        private String _title;
        /**
         * The message to display, or <code>null</code> if none.
         */
        private String _message;
        /**
         * The List for the {@link AbstractWidgetActionModel}s.
         */
        private TableViewer _actionViewer;
        /**
         * The Table for the properties.
         */
        private TableViewer _propertyViewer;
        /**
         * The Action to add a new {@link AbstractWidgetActionModel}.
         */
        private Action _addAction;
        /**
         * The Action to copy the selected {@link AbstractWidgetActionModel}.
         */
        private Action _copyAction;
        /**
         * The Action to delete the selected {@link AbstractWidgetActionModel}.
         */
        private Action _removeAction;
        // /**
        // * The menu for the add-action.
        // */
        // private Menu _actionMenu;
        /**
         * The currently opened CellEditor. If no one is opened this value is
         * null.
         */
        private CellEditor _openedCellEditor = null;
        private Action _moveUpAction;
        private Action _moveDownAction;
        private HashMap<IPropertyDescriptor, WidgetProperty> _descriptorPropertyMap;
        private HashMap<AbstractWidgetActionModel, IPropertyDescriptor[]> _actionDescriptorsMap;

        /**
         * Creates an input dialog with OK and Cancel buttons. Note that the
         * dialog will have no visual representation (no widgets) until it is
         * told to open.
         * <p>
         * Note that the <code>open</code> method blocks for input dialogs.
         * </p>
         *
         * @param parentShell
         *            the parent shell, or <code>null</code> to create a
         *            top-level shell
         * @param dialogTitle
         *            the dialog title, or <code>null</code> if none
         * @param dialogMessage
         *            the dialog message, or <code>null</code> if none
         */
        public ActionDataDialog(final Shell parentShell,
                final String dialogTitle, final String dialogMessage) {
            super(parentShell);
            this.setShellStyle(SWT.MODELESS | SWT.CLOSE | SWT.MAX | SWT.TITLE
                    | SWT.BORDER | SWT.RESIZE);
            _title = dialogTitle;
            _message = dialogMessage;

            _descriptorPropertyMap = new HashMap<IPropertyDescriptor, WidgetProperty>();
            _actionDescriptorsMap = new HashMap<AbstractWidgetActionModel, IPropertyDescriptor[]>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void configureShell(final Shell shell) {
            super.configureShell(shell);
            if (_title != null) {
                shell.setText(_title);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Control createDialogArea(final Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            WorkbenchHelpSystem.getInstance().setHelp(composite,
                    SdsUiPlugin.PLUGIN_ID + ".sds_action_data");
            composite.setLayout(new GridLayout(1, false));

            if (_message != null) {
                Label label = new Label(composite, SWT.WRAP);
                label.setText(_message);
                GridData data = new GridData(GridData.GRAB_HORIZONTAL
                        | GridData.GRAB_VERTICAL
                        | GridData.HORIZONTAL_ALIGN_FILL
                        | GridData.VERTICAL_ALIGN_CENTER);
                data.horizontalSpan = 2;
                data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
                label.setLayoutData(data);
            }

            this.createActions();
            // _actionMenu = this.createMenu(composite, false);
            Composite mainComposite = new Composite(composite, SWT.NONE);
            mainComposite.setLayout(new GridLayout(2, false));
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.heightHint = 200;
            mainComposite.setLayoutData(gridData);
            Composite leftComposite = new Composite(mainComposite, SWT.NONE);
            leftComposite.setLayout(new GridLayout(1, false));
            gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.widthHint = 200;
            leftComposite.setLayoutData(gridData);
            this.createLabel(leftComposite, "The actions:");

            Composite toolBarComposite = new Composite(leftComposite,
                    SWT.BORDER);
            GridLayout gridLayout = new GridLayout(1, false);
            gridLayout.marginLeft = 0;
            gridLayout.marginRight = 0;
            gridLayout.marginBottom = 0;
            gridLayout.marginTop = 0;
            gridLayout.marginHeight = 0;
            gridLayout.marginWidth = 0;
            toolBarComposite.setLayout(gridLayout);
            gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.widthHint = 200;
            toolBarComposite.setLayoutData(gridData);

            ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
            ToolBar toolBar = toolbarManager.createControl(toolBarComposite);
            GridData grid = new GridData();
            grid.horizontalAlignment = GridData.FILL;
            grid.verticalAlignment = GridData.BEGINNING;
            toolBar.setLayoutData(grid);

            toolbarManager.add(_addAction);
            toolbarManager.add(_copyAction);
            toolbarManager.add(_removeAction);
            toolbarManager.add(_moveUpAction);
            toolbarManager.add(_moveDownAction);
            toolbarManager.update(true);

            _actionViewer = this.createActionTableViewer(toolBarComposite);
            _actionViewer.setInput(_actionData.getWidgetActions().toArray(
                    new AbstractWidgetActionModel[0]));
            Composite rightComposite = new Composite(mainComposite, SWT.NONE);
            rightComposite.setLayout(new GridLayout(1, false));
            rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                    true));
            this.createLabel(rightComposite, "The properties for the action");
            _propertyViewer = this.createPropertyTableViewer(rightComposite);
            return composite;
        }

        /**
         * Creates the popup-menu for adding a {@link AbstractWidgetActionModel}.
         *
         * @param control
         *            The {@link Control} for the menu
         * @param withRemoveAction
         *            Indicates if an action to remove a {@link AbstractWidgetActionModel}
         *            should be added
         * @return The resulting menu
         */
        private Menu createMenu(final Control control,
                final boolean withRemoveAction) {
            MenuManager listMenu = new MenuManager();
            for (ActionType type : ActionType.values()) {
                listMenu.add(new TypeAction(type));
            }
            if (withRemoveAction) {
                listMenu.add(new Separator());
                listMenu.add(_removeAction);
            }
            return listMenu.createContextMenu(control);
        }

        /**
         * Creates the actions.
         */
        private void createActions() {
            _addAction = new Action("Add") {
                @Override
                public void run() {
                    // System.out.println(".createActions()");
                }
            };

            _addAction.setMenuCreator(new IMenuCreator() {

                private Menu _menu;

                @Override
                public void dispose() {
                    if (_menu != null) {
                        _menu.dispose();
                        _menu = null;
                    }
                }

                @Override
                public Menu getMenu(final Control parent) {
                    System.out.println("Parent: " + parent);
                    if (_menu != null) {
                        _menu.dispose();
                    }
                    _menu = createMenu(parent, false);
                    return _menu;
                }

                @Override
                public Menu getMenu(final Menu parent) {
                    return null;
                }

            });
            _addAction.setToolTipText("Adds an action");
            _addAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/add.gif"));
            _copyAction = new Action() {
                @Override
                public void run() {
                    IStructuredSelection selection = (IStructuredSelection) _actionViewer
                            .getSelection();
                    if (!selection.isEmpty()
                            && selection.getFirstElement() instanceof AbstractWidgetActionModel) {
                        _actionData.addAction(((AbstractWidgetActionModel) selection
                                .getFirstElement()).makeCopy());
                        refreshActionViewer((AbstractWidgetActionModel) selection
                                .getFirstElement());
                    }
                }
            };
            _copyAction.setText("Copy Action");
            _copyAction.setToolTipText("makes a copy of the selected Action");
            _copyAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/copy.gif"));
            _copyAction.setEnabled(false);
            _removeAction = new Action() {
                @Override
                public void run() {
                    IStructuredSelection selection = (IStructuredSelection) _actionViewer
                            .getSelection();
                    if (!selection.isEmpty()
                            && selection.getFirstElement() instanceof AbstractWidgetActionModel) {
                        _actionData.removeAction((AbstractWidgetActionModel) selection
                                .getFirstElement());
                        refreshActionViewer(null);
                        this.setEnabled(false);
                    }
                }
            };
            _removeAction.setText("Remove Action");
            _removeAction
                    .setToolTipText("Removes the selected Action from the list");
            _removeAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/delete.gif"));
            _removeAction.setEnabled(false);

            _moveUpAction = new Action() {
                @Override
                public void run() {
                    IStructuredSelection selection = (IStructuredSelection) _actionViewer
                            .getSelection();
                    if (!selection.isEmpty()
                            && selection.getFirstElement() instanceof AbstractWidgetActionModel) {
                        AbstractWidgetActionModel widgetAction = (AbstractWidgetActionModel) selection
                                .getFirstElement();
                        _actionData.upAction(widgetAction);
                        refreshActionViewer(widgetAction);
                        this.setEnabled(false);
                    }
                }
            };
            _moveUpAction.setText("Move Up Action");
            _moveUpAction.setToolTipText("Move up the selected Action");
            _moveUpAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/search_prev.gif"));
            _moveUpAction.setEnabled(false);

            _moveDownAction = new Action() {
                @Override
                public void run() {
                    IStructuredSelection selection = (IStructuredSelection) _actionViewer
                            .getSelection();
                    if (!selection.isEmpty()
                            && selection.getFirstElement() instanceof AbstractWidgetActionModel) {
                        AbstractWidgetActionModel widgetAction = (AbstractWidgetActionModel) selection
                                .getFirstElement();
                        _actionData.downAction(widgetAction);
                        refreshActionViewer(widgetAction);
                        this.setEnabled(false);
                    }
                }
            };
            _moveDownAction.setText("Move Down Action");
            _moveDownAction.setToolTipText("Move down the selected Action");
            _moveDownAction.setImageDescriptor(CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/search_next.gif"));
            _moveDownAction.setEnabled(false);
        }

        /**
         * Calculates the input for the table depending on the type of the
         * {@link ActionData}.
         *
         * @param action
         *            The {@link AbstractWidgetActionModel} for the input
         */
        private void calculatePropertyInput(final AbstractWidgetActionModel action) {
            IPropertyDescriptor[] descriptors = new IPropertyDescriptor[0];
            if (!_actionDescriptorsMap.containsKey(action)) {
                if (action != null) {
                    Set<String> propertyKeys = action.getPropertyKeys();
                    descriptors = new IPropertyDescriptor[propertyKeys.size()];
                    int i = 0;
                    for (String propertyKey : propertyKeys) {
                        WidgetProperty property = action.getProperty(propertyKey);
                        IPropertyDescriptorFactory factory = PropertyDescriptorFactoryService
                        .getInstance().getPropertyDescriptorFactory(
                                property.getPropertyType());
                        descriptors[i] = factory.createPropertyDescriptor(
                                propertyKey, property);
                        i++;
                    }
                }
                _actionDescriptorsMap.put(action, descriptors);
            } else {
                descriptors = _actionDescriptorsMap.get(action);
            }
            _propertyViewer.setInput(descriptors);
        }

        /**
         * Creates a label with the given text.
         *
         * @param parent
         *            The parent for the label
         * @param text
         *            The text for the label
         */
        private void createLabel(final Composite parent, final String text) {
            Label label = new Label(parent, SWT.WRAP);
            label.setText(text);
            label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false,
                    false, 2, 1));
        }

        /**
         * Creates and configures a {@link TableViewer}.
         *
         * @param parent
         *            The parent for the table
         * @return The {@link TableViewer}
         */
        private TableViewer createActionTableViewer(final Composite parent) {
            TableViewer viewer = new TableViewer(parent, SWT.V_SCROLL
                    | SWT.H_SCROLL | SWT.BORDER | SWT.SINGLE);
            viewer.setContentProvider(new BaseWorkbenchContentProvider() {
                @Override
                public Object[] getElements(final Object element) {
                    return ((AbstractWidgetActionModel[]) element);
                }
            });
            viewer.setLabelProvider(new WorkbenchLabelProvider() {
                @Override
                protected String decorateText(final String input,
                        final Object element) {
                    int index = _actionData.getWidgetActions().indexOf(element);
                    if (index > -1) {
                        return input + " (Index: " + String.valueOf(index)
                                + ")";
                    }
                    return input;
                }
            });
            viewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(final SelectionChangedEvent event) {
                    refreshActions();
                }
            });
            viewer.getTable().setLayoutData(
                    new GridData(SWT.FILL, SWT.FILL, true, true));
            viewer.getTable().setMenu(this.createMenu(viewer.getTable(), true));
            return viewer;
        }

        /**
         * Refreshes the enabled-state of the actions.
         */
        private void refreshActions() {
            _addAction.setEnabled(_openedCellEditor == null);
            IStructuredSelection selection = (IStructuredSelection) _actionViewer
                    .getSelection();
            if (!selection.isEmpty()
                    && selection.getFirstElement() instanceof AbstractWidgetActionModel
                    && _openedCellEditor == null) {
                _removeAction.setEnabled(true);
                _copyAction.setEnabled(true);
                _moveUpAction.setEnabled(true);
                _moveDownAction.setEnabled(true);
                calculatePropertyInput((AbstractWidgetActionModel) selection
                        .getFirstElement());
            } else {
                _removeAction.setEnabled(false);
                _copyAction.setEnabled(false);
                _moveUpAction.setEnabled(false);
                _moveDownAction.setEnabled(false);
            }
        }

        /**
         * Creates and configures a {@link TableViewer}.
         *
         * @param parent
         *            The parent for the table
         * @return The {@link TableViewer}
         */
        private TableViewer createPropertyTableViewer(final Composite parent) {
            final Table table = new Table(parent, SWT.FULL_SELECTION
                    | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            TableViewer viewer = new TableViewer(table);
            TableViewerColumn tvColumn = new TableViewerColumn(viewer, SWT.NONE);
            tvColumn.getColumn().setText("first");
            tvColumn.getColumn().setMoveable(false);
            tvColumn.getColumn().setWidth(0);
            tvColumn.getColumn().setResizable(false);
            tvColumn = new TableViewerColumn(viewer, SWT.NONE);
            tvColumn.getColumn().setText("Property");
            tvColumn.getColumn().setMoveable(false);
            tvColumn.getColumn().setWidth(100);
            tvColumn = new TableViewerColumn(viewer, SWT.NONE);
            tvColumn.getColumn().setText("Value");
            tvColumn.getColumn().setMoveable(false);
            tvColumn.getColumn().setWidth(300);
            EditingSupport editingSupport = new CustomEditingSupport(viewer,
                    table);
            tvColumn.setEditingSupport(editingSupport);
            viewer.getColumnViewerEditor().addEditorActivationListener(
                    new ColumnViewerEditorActivationListener() {

                        private boolean _deactivationDone = false;

                        @Override
                        public void afterEditorActivated(
                                final ColumnViewerEditorActivationEvent event) {
                            // do nothing
                        }

                        @Override
                        public void afterEditorDeactivated(
                                final ColumnViewerEditorDeactivationEvent event) {
                            if (!_deactivationDone) {
                                _deactivationDone = true;
                                getButton(IDialogConstants.CANCEL_ID)
                                        .setEnabled(true);
                                getButton(IDialogConstants.OK_ID).setEnabled(
                                        true);
                                _openedCellEditor = null;
                                refreshActions();
                            }
                        }

                        @Override
                        public void beforeEditorActivated(
                                final ColumnViewerEditorActivationEvent event) {
                            getButton(IDialogConstants.CANCEL_ID).setEnabled(
                                    false);
                            getButton(IDialogConstants.OK_ID).setEnabled(false);
                            refreshActions();
                            _deactivationDone = false;
                        }

                        @Override
                        public void beforeEditorDeactivated(
                                final ColumnViewerEditorDeactivationEvent event) {
                            // do nothing
                        }
                    });
            viewer.setColumnProperties(new String[] { "first", "Property",
                    "Value" });
            viewer.setUseHashlookup(true);
            viewer.setContentProvider(new ArrayContentProvider());
            viewer.setLabelProvider(new TableLabelProvider());

            viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE,
                    new Transfer[] { TextTransfer.getInstance() },
                    new DropTargetAdapter() {

                        @Override
                        public void dragEnter(final DropTargetEvent event) {
                            for (TransferData transfer : event.dataTypes) {
                                if (TextTransfer.getInstance().isSupportedType(
                                        transfer)) {
                                    if (event.item instanceof TableItem) {
                                        TableItem item = (TableItem) event.item;
                                        IPropertyDescriptor desc = (IPropertyDescriptor) item
                                                .getData();
                                        if (getWidgetProperty(desc) instanceof StringProperty) {
                                            event.detail = DND.DROP_COPY;
                                            break;
                                        }
                                    }
                                }
                            }
                            super.dragEnter(event);
                        }

                        @Override
                        public void drop(final DropTargetEvent event) {
                            if (event.data instanceof String) {
                                IPropertyDescriptor desc = (IPropertyDescriptor) ((TableItem) event.item)
                                        .getData();
                                WidgetProperty widgetProperty = getWidgetProperty(desc);
                                if (widgetProperty instanceof StringProperty) {
                                    widgetProperty.setPropertyValue(event.data);
                                    _propertyViewer.refresh();
                                }
                            }
                        }
                    });

            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2,
                    1);
            gridData.heightHint = 100;
            table.setLayoutData(gridData);
            return viewer;
        }

        /**
         * Returns the {@link WidgetProperty} of the {@link IPropertyDescriptor}.
         *
         * @param descriptor
         *            The {@link IPropertyDescriptor}
         * @return The {@link WidgetProperty} for the given descriptor
         */
        private WidgetProperty getWidgetProperty(
                final IPropertyDescriptor descriptor) {
            if (!_descriptorPropertyMap.containsKey(descriptor)) {
                IStructuredSelection selection = (IStructuredSelection) _actionViewer
                .getSelection();
                if (!selection.isEmpty()
                        && selection.getFirstElement() instanceof AbstractWidgetActionModel) {
                    AbstractWidgetActionModel action = (AbstractWidgetActionModel) selection
                    .getFirstElement();
                    for (String propertyKey : action.getPropertyKeys()) {
                        if (descriptor.getId().equals(propertyKey)) {
                            WidgetProperty widgetProperty = action.getProperty(propertyKey);
                            _descriptorPropertyMap.put(descriptor, widgetProperty);
                            return widgetProperty;
                        }
                    }
                }
                return null;
            } else {
                return _descriptorPropertyMap.get(descriptor);
            }
        }

        /**
         * Refreshes the viewer for the {@link AbstractWidgetActionModel}s.
         *
         * @param action
         *            The action to select (may be null)
         */
        private void refreshActionViewer(final AbstractWidgetActionModel action) {
            new CheckedUiRunnable() {
                @Override
                protected void doRunInUi() {
                    AbstractWidgetActionModel[] input = _actionData.getWidgetActions()
                            .toArray(new AbstractWidgetActionModel[0]);
                    _actionViewer.setInput(input);
                    _actionViewer.refresh();
                    if (action == null) {
                        _actionViewer.setSelection(StructuredSelection.EMPTY);
                    } else {
                        _actionViewer.setSelection(new StructuredSelection(
                                action));
                    }
                    calculatePropertyInput(action);
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean close() {
            if (_openedCellEditor == null) {
                return super.close();
            }
            return false;
        }

        /**
         * The {@link EditingSupport} for the columns of the property table.
         *
         * @author Kai Meyer
         *
         */
        private final class CustomEditingSupport extends EditingSupport {

            /**
             * The {@link Table} where this {@link EditingSupport} is embedded.
             */
            private final Table _table;
            /**
             * A {@link Map} of the already known CllEditors.
             */
            private Map<Object, CellEditor> _cellEditors = new HashMap<Object, CellEditor>();

            /**
             * Constructor.
             *
             * @param viewer
             *            The {@link ColumnViewer} for this
             *            {@link EditingSupport}.
             * @param table
             *            The {@link Table}
             */
            private CustomEditingSupport(final ColumnViewer viewer,
                    final Table table) {
                super(viewer);
                _table = table;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean canEdit(final Object element) {
                return true;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected CellEditor getCellEditor(final Object element) {
                if (!_cellEditors.containsKey(element)) {
                    IStructuredSelection selection = (IStructuredSelection) this
                            .getViewer().getSelection();
                    IPropertyDescriptor descriptor = (IPropertyDescriptor) selection
                            .getFirstElement();
                    if (descriptor != null) {
                        CellEditor editor = descriptor
                                .createPropertyEditor(_table);
                        if (editor != null) {
                            _cellEditors.put(element, editor);
                        }
                    }
                }
                if (_cellEditors.containsKey(element)) {
                    _openedCellEditor = _cellEditors.get(element);
                    return _cellEditors.get(element);
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Object getValue(final Object element) {
                if (element instanceof IPropertyDescriptor) {
                    WidgetProperty prop = getWidgetProperty((IPropertyDescriptor) element);
                    if (prop != null) {
                        return prop.getPropertyValue();
                    }
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void setValue(final Object element, final Object value) {
                if (element instanceof IPropertyDescriptor) {
                    WidgetProperty prop = getWidgetProperty((IPropertyDescriptor) element);
                    if (prop != null) {
                        prop.setPropertyValue(value);
                        _propertyViewer.refresh();
                        refreshActionViewer((AbstractWidgetActionModel) ((IStructuredSelection) _actionViewer
                                .getSelection()).getFirstElement());
                    }
                }
            }
        }

        /**
         * An {@link Action}, which adds a new {@link AbstractWidgetActionModel} of the
         * given {@link ActionType}.
         *
         * @author Kai Meyer
         *
         */
        private final class TypeAction extends Action {
            /**
             * The {@link ActionType}.
             */
            private ActionType _type;

            /**
             * Constructor.
             *
             * @param type
             *            The {@link ActionType} for the action.
             */
            public TypeAction(final ActionType type) {
                _type = type;
                this.setText("Add " + _type.getTitle());
                AbstractWidgetActionModel widgetAction = _type.getActionFactory()
                        .createWidgetActionModel();
                IWorkbenchAdapter adapter = (IWorkbenchAdapter) Platform
                        .getAdapterManager().getAdapter(widgetAction,
                                IWorkbenchAdapter.class);
                if (adapter != null) {
                    this.setImageDescriptor(adapter
                            .getImageDescriptor(widgetAction));
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                AbstractWidgetActionModel widgetAction = _type.getActionFactory()
                        .createWidgetActionModel();
                _actionData.addAction(widgetAction);
                refreshActionViewer(widgetAction);
            }
        }

        /**
         * The {@link LabelProvider} for the table.
         *
         * @author Kai Meyer
         *
         */
        private final class TableLabelProvider extends LabelProvider implements
                ITableLabelProvider {

            /**
             * {@inheritDoc}
             */
            @Override
            public Image getColumnImage(final Object element,
                    final int columnIndex) {
                if (columnIndex == 2 && element instanceof IPropertyDescriptor) {
                    IPropertyDescriptor descriptor = (IPropertyDescriptor) element;
                    Object value = getWidgetProperty(descriptor);
                    if (value != null) {
                        if (descriptor.getLabelProvider() != null) {
                            try {
                                return descriptor.getLabelProvider().getImage(
                                        value);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getColumnText(final Object element,
                    final int columnIndex) {
                if (element instanceof IPropertyDescriptor) {
                    IPropertyDescriptor descriptor = (IPropertyDescriptor) element;
                    if (columnIndex == 0) {
                        return "";
                    }
                    if (columnIndex == 1) {
                        return descriptor.getDisplayName();
                    }
                    WidgetProperty widgetProperty = getWidgetProperty(descriptor);
                    if (widgetProperty != null && descriptor.getLabelProvider() != null) {
                        return descriptor.getLabelProvider().getText(
                                widgetProperty.getPropertyValue());
                    }
                }
                if (element != null) {
                    return element.toString();
                }
                return "Fehler";
            }
        }

    }

}
