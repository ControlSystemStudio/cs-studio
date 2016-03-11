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
package org.csstudio.sds.ui.internal.properties.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.dal.DynamicValueState;
import org.csstudio.domain.common.LayoutUtil;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.initializers.WidgetInitializationService;
import org.csstudio.sds.ui.internal.dynamicswizard.SimpleChannelPage;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * A wizard, which enables users to configure dynamic settings for a property.
 * This includes the binding of channels and/or macro scripts (e.g. color rules)
 * to properties.
 *
 * @author Sven Wende, Stefan Hofer, Kai Meyer
 * @version $Revision: 1.55 $
 *
 */
public final class DynamicAspectsWizard extends Wizard {

    /**
     * The dynamics descriptor that is edited.
     */
    private DynamicsDescriptor _dynamicsDescriptor;

    /**
     * The property descriptor.
     */
    private IPropertyDescriptor _propertyDescriptor;

    /**
     * Names of existing aliases that can be used in channel names.
     */
    private Map<String, String> _aliases;

    /**
     * The SimpleChannelPage.
     */
    private SimpleChannelPage _simpleChannelPage;
    /**
     * The StatePage.
     */
    private StatePage _statePage;

    private Object _initValue;

    /**
     * Constructs a dynamic aspects wizard for the specified property sheet
     * entry.
     *
     * @param entry
     *            the property sheet entry
     * @param dynamicsDescriptor
     *            the dynamic descriptor or null, if none exists
     * @param aliasNames
     *            names of existing aliases that can be used in channel names.
     * @param propertyDescriptor
     *            The Descriptor for the property
     */
    public DynamicAspectsWizard(final DynamicsDescriptor dynamicsDescriptor, final Map<String, String> aliasNames,
            final IPropertyDescriptor propertyDescriptor, Object currentValue) {

        assert aliasNames != null;
        _aliases = aliasNames;
        _initValue = currentValue;
        _propertyDescriptor = propertyDescriptor;
        _dynamicsDescriptor = dynamicsDescriptor != null ? dynamicsDescriptor.clone() : new DynamicsDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        _dynamicsDescriptor = new DynamicsDescriptor();
        _simpleChannelPage.performFinish(_dynamicsDescriptor);
        _statePage.performFinish(_dynamicsDescriptor);
        return true;
    }

    /**
     * Returns the prepared {@link DynamicsDescriptor}.
     *
     * @return the {@link DynamicsDescriptor}
     */
    public DynamicsDescriptor getDynamicsDescriptor() {
        return _dynamicsDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        _simpleChannelPage = new SimpleChannelPage("Simple Channel", _dynamicsDescriptor, _propertyDescriptor.getPropertyType(), _aliases);
        addPage(_simpleChannelPage);

        _statePage = new StatePage("States", _dynamicsDescriptor.getConnectionStateDependentPropertyValues(), _dynamicsDescriptor
                .getConditionStateDependentPropertyValues());
        addPage(_statePage);
    }

    /**
     * The page for configuring the states.
     *
     * @author Kai Meyer
     */
    private final class StatePage extends WizardPage {
        private Map<ConnectionState, Object> _connectionStateValues = new HashMap<ConnectionState, Object>();
        private Map<DynamicValueState, Object> _conditionStateValues = new HashMap<DynamicValueState, Object>();

        /**
         * A CellEditorListener for the CellEditor.
         */
        private ICellEditorListener _connectionEditorListener;

        /**
         * A CellEditorListener for the CellEditor.
         */
        private ICellEditorListener _conditionEditorListener;

        /**
         * The composite for the ConnectionStates.
         */
        private StateComposite<ConnectionState> _connectionStateComposite;

        /**
         * The composite for the ConditionStates.
         */
        private StateComposite<DynamicValueState> _conditionStateComposite;

        /**
         * Constructor.
         *
         * @param pageName
         *            the name of the page
         */
        protected StatePage(final String pageName, Map<ConnectionState, Object> connectionStateValues,
                Map<DynamicValueState, Object> conditionStateValues) {
            super(pageName);
            assert pageName != null;
            setTitle("State Configurator");
            setDescription("Use this page to configure the states");
            _connectionStateValues = connectionStateValues != null ? connectionStateValues : new HashMap<ConnectionState, Object>();
            _conditionStateValues = conditionStateValues != null ? conditionStateValues : new HashMap<DynamicValueState, Object>();
        }

        /**
         * Creates a new cell editor listener.
         *
         * @param composite
         *            The StateComposite where the CellEditorListener is
         *            registered
         * @return ICellEditorListener A new ICellEditorListener, which sets the
         *         error message on the page
         */
        @SuppressWarnings("unchecked")
        private ICellEditorListener createEditorListener(final StateComposite composite) {
            ICellEditorListener editorListener = new ICellEditorListener() {
                public void cancelEditor() {
                    setErrorMessage(null);
                }

                public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
                    setErrorMessage(composite.getErrorMessage());
                }

                public void applyEditorValue() {
                    setErrorMessage(null);
                }
            };
            return editorListener;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void createControl(final Composite parent) {
            Composite c = new Composite(parent, SWT.None);
            c.setLayout(new GridLayout(1, false));

            // we display only the supported connection states
            Set<ConnectionState> supportedConnectionStates = WidgetInitializationService.getInstance().getSupportedConnectionStates();
            _connectionStateComposite = new StateComposite<ConnectionState>(c, SWT.NONE, "Connection", supportedConnectionStates
                    .toArray(new Enum[supportedConnectionStates.size()])) {

                @Override
                protected Map<ConnectionState, Object> getStateMap() {
                    return _connectionStateValues;
                }

            };
            _connectionEditorListener = this.createEditorListener(_connectionStateComposite);
            _connectionStateComposite.addEditorListener(_connectionEditorListener);

            _conditionStateComposite = new StateComposite<DynamicValueState>(c, SWT.NONE, "Condition", DynamicValueState.values()) {

                @Override
                protected Map<DynamicValueState, Object> getStateMap() {
                    return _conditionStateValues;
                }

            };
            _conditionEditorListener = this.createEditorListener(_conditionStateComposite);
            _conditionStateComposite.addEditorListener(_conditionEditorListener);

            // important for wizards -> set the control
            setControl(c);
        }

        /**
         * Prepare this page for closing.
         */
        public void performFinish(DynamicsDescriptor dynamicsDescriptor) {
            dynamicsDescriptor.setConnectionStateDependentPropertyValues(_connectionStateValues);
            dynamicsDescriptor.setConditionStateDependentPropertyValues(_conditionStateValues);
            _connectionStateComposite.finished();
            _conditionStateComposite.finished();
        }

        /**
         * A Composite, which contains a Table to configure states.
         *
         * @author Kai Meyer
         */
        @SuppressWarnings("unchecked")
        private abstract class StateComposite<ETYPE extends Enum> extends Composite {
            /**
             * The Action to add a state.
             */
            private Action _addStateAction;
            /**
             * The Action to remove a state.
             */
            private Action _removeStateAction;
            /**
             * A table viewer, which is used to configure states.
             */
            private TableViewer _stateTableViewer;
            /**
             * The CellEditor from the PropertyDescriptor.
             */
            private CellEditor _editor;
            /**
             * The title of the States.
             */
            private final String _stateName;
            /**
             * All possible states.
             */
            private final Enum[] _allStates;

            /**
             * Constructor.
             *
             * @param parent
             *            The parent of this Composite
             * @param style
             *            The style for this Composite
             * @param stateName
             *            The title for the States
             * @param allStates
             *            All possible states
             */
            public StateComposite(final Composite parent, final int style, final String stateName, final Enum[] allStates) {
                super(parent, style);
                this.setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell());
                this.setLayout(new GridLayout(1, false));
                _stateName = stateName;
                _allStates = allStates;
                this.makeActions();
                _stateTableViewer = this.createConnectionStateTable(this);
                this.refresh();
            }

            /**
             * Makes the Actions for the Table.
             */
            private void makeActions() {
                _addStateAction = new AddStateAction(_stateName);
                _removeStateAction = new RemoveStateAction(_stateName);
            }

            /**
             * Creates the TableViewer for connection states.
             *
             * @param parent
             *            The parent Composite for the table
             * @return The TableViewer for the connection states
             */
            private TableViewer createConnectionStateTable(final Composite parent) {
                Group group = new Group(parent, SWT.NONE);
                group.setLayout(new GridLayout(1, false));
                group.setText(_stateName + " States");
                group.setLayoutData(LayoutUtil.createGridDataForFillingCell());

                // define column names
                String[] columnNames = new String[] { "FIRST", "PROP_NAME", "PROP_TYPE" }; //$NON-NLS-1$ //$NON-NLS-2$

                // create table
                final Table table = new Table(group, SWT.FULL_SELECTION | SWT.SCROLL_PAGE);
                table.setLinesVisible(true);
                table.setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(150));
                table.setHeaderVisible(true);

                TableColumn column = new TableColumn(table, SWT.CENTER, 0);
                column.setText("First");
                column.setWidth(0);

                column = new TableColumn(table, SWT.LEFT, 1);
                column.setText(_stateName + " State");
                column.setWidth(300);

                column = new TableColumn(table, SWT.LEFT, 2);
                column.setText("Value");
                column.setWidth(140);

                // create viewer
                TableViewer viewer = new TableViewer(table);
                viewer.setUseHashlookup(true);

                // define column properties
                viewer.setColumnProperties(columnNames);

                // configure cell editors
                _editor = _propertyDescriptor.createPropertyEditor(table);
                CellEditor[] editors = new CellEditor[columnNames.length];
                editors[0] = null;
                editors[1] = null;
                editors[2] = _editor;

                viewer.setCellEditors(editors);
                viewer.setCellModifier(new StateCellModifier());
                viewer.setContentProvider(new ArrayContentProvider());
                viewer.setLabelProvider(new StateTableLabelProvider());
                viewer.setInput(this.getStateMap().keySet());

                // create popup menu
                MenuManager popupMenu = new MenuManager();
                popupMenu.add(_addStateAction);
                popupMenu.add(_removeStateAction);
                Menu menu = popupMenu.createContextMenu(viewer.getTable());
                viewer.getTable().setMenu(menu);

                // double click listener
                viewer.getControl().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseDoubleClick(final MouseEvent e) {
                        addState();
                    }
                });

                return viewer;
            }

            /**
             * Removes the current selected State.
             */
            private void removeState() {
                TableItem[] selection = _stateTableViewer.getTable().getSelection();
                for (TableItem item : selection) {
                    this.getStateMap().remove(item.getData());
                }
                this.refresh();
            }

            /**
             * Opens a dialog and adds the selected State to the Table.
             */
            private void addState() {
                StateDialog dialog = new StateDialog(this.getShell(), "Choose the '" + _stateName + " State' you want to add", this
                        .makeStateArray(this.getAllStates()));
                if (dialog.open() == Window.OK) {
                    for (Enum state : dialog.getSelectedStates()) {
                        this.getStateMap().put((ETYPE) state, _initValue);
                    }
                    this.refresh();
                }
            }

            /**
             * Refreshes the Table and the Actions.
             */
            private void refresh() {
                _stateTableViewer.refresh();
                _removeStateAction.setEnabled(_stateTableViewer.getTable().getItemCount() > 0);
                _addStateAction.setEnabled(_stateTableViewer.getTable().getItemCount() < _allStates.length);
            }

            /**
             * returns the Map od states.
             *
             * @return HashMap The Map od states
             */
            protected abstract Map<ETYPE, Object> getStateMap();

            /**
             * Returns all possible states.
             *
             * @return All possible States
             */
            public Enum[] getAllStates() {
                return _allStates;
            }

            /**
             * Generates a Enum[], which entries are not in the Table yet.
             *
             * @param states
             *            Possible Enums
             * @return A Enum[], which entries are not in the Table yet
             */
            private Enum[] makeStateArray(final Enum[] states) {
                List<Enum> stateList = new LinkedList<Enum>();
                Set<ETYPE> keys = this.getStateMap().keySet();
                for (Enum state : states) {
                    if (!keys.contains(state)) {
                        stateList.add(state);
                    }
                }
                return stateList.toArray(new Enum[stateList.size()]);
            }

            /**
             * Returns an error message or null.
             *
             * @return The error Message or null
             */
            public String getErrorMessage() {
                return _editor.getErrorMessage();
            }

            /**
             * Adds the given ICellEditorListener to the CellEditor of the
             * table.
             *
             * @param listener
             *            The ICellEditorListener for the table
             */
            public void addEditorListener(final ICellEditorListener listener) {
                _editor.addListener(listener);
            }

            /**
             * Deactivates all CellEditors to perform last changes.
             */
            public void finished() {
                for (CellEditor editor : _stateTableViewer.getCellEditors()) {
                    if (editor != null) {
                        editor.deactivate();
                    }
                }
            }

            /**
             * An action, which adds a state to the configuration.
             *
             * @author Kai Meyer
             */
            protected final class AddStateAction extends Action {
                /**
                 * Constructor.
                 *
                 * @param stateName
                 *            The Name of the states
                 */
                protected AddStateAction(final String stateName) {
                    super("Add " + stateName + " State");
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    addState();
                }
            }

            /**
             * An action, which removes a state to the configuration.
             *
             * @author Kai Meyer
             */
            protected final class RemoveStateAction extends Action {
                /**
                 * Constructor.
                 *
                 * @param stateName
                 *            The Name of the states
                 */
                protected RemoveStateAction(final String stateName) {
                    super("Remove " + stateName + " State");
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    removeState();
                }
            }

            /**
             * A Dialog, which allows to choose the state, which should be
             * created.
             *
             * @author Kai Meyer
             */
            private final class StateDialog extends TitleAreaDialog {
                /**
                 * A List of the selected states.
                 */
                private List<Enum> _selectedStates = new LinkedList<Enum>();
                /**
                 * The Enums for this dialog.
                 */
                private Enum[] _states;
                /**
                 * The message for this dialog.
                 */
                private String _message;

                /**
                 * The List of Buttons.
                 */
                private final List<Button> _buttonList = new LinkedList<Button>();

                /**
                 * Constructor.
                 *
                 * @param parentShell
                 *            The parent Shell for this Dialog
                 * @param message
                 *            The message for this dialog
                 * @param states
                 *            The available States to display
                 */
                public StateDialog(final Shell parentShell, final String message, final Enum[] states) {
                    super(parentShell);
                    _states = states;
                    _message = message;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected void configureShell(final Shell shell) {
                    super.configureShell(shell);
                    shell.setText("Selected State");
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected Control createDialogArea(final Composite parent) {
                    final Composite composite = (Composite) super.createDialogArea(parent);
                    this.setTitle("Select the State");
                    this.setMessage(_message);
                    Composite comp = new Composite(composite, SWT.NONE);
                    comp.setLayout(new GridLayout(1, false));
                    Label label = new Label(comp, SWT.NONE);
                    label.setText("Available States:");
                    if (_states.length > 0) {
                        this.createRadioButtons(comp);
                    } else {
                        label = new Label(comp, SWT.WRAP);
                        label.setText("No States available");
                    }
                    return composite;
                }

                /**
                 * Creates a Radiobutton for every State.
                 *
                 * @param parent
                 *            The parent composite for the Buttons
                 */
                private void createRadioButtons(final Composite parent) {
                    for (Enum state : _states) {
                        Button button = new Button(parent, SWT.CHECK);
                        button.setText(state.name());
                        button.setData(state);
                        _buttonList.add(button);
                    }
                }

                /**
                 * Gets the selected state.
                 *
                 * @return String The selected state
                 */
                public Enum[] getSelectedStates() {
                    return _selectedStates.toArray(new Enum[_selectedStates.size()]);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                protected void okPressed() {
                    for (Button button : _buttonList) {
                        if (button.getSelection()) {
                            _selectedStates.add((Enum) button.getData());
                        }
                    }
                    super.okPressed();
                }

            }

            /**
             * The CellModifier for States.
             *
             * @author Kai Meyer
             */
            private final class StateCellModifier implements ICellModifier {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean canModify(final Object element, final String property) {
                    return true;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Object getValue(final Object element, final String property) {
                    if (element instanceof ConnectionState || element instanceof DynamicValueState) {
                        return getStateMap().get(element);
                    }
                    return "";
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                @SuppressWarnings("unchecked")
                public void modify(final Object element, final String property, final Object value) {
                    if (element instanceof TableItem) {
                        ETYPE state = (ETYPE) ((TableItem) element).getData();
                        if (value != null) {
                            getStateMap().put((ETYPE) state, value);
                        }

                        _stateTableViewer.refresh();
                    }
                }

            }

            /**
             * A TableLabelProvider for states.
             *
             * @author Kai Meyer
             *
             */
            private final class StateTableLabelProvider extends LabelProvider implements ITableLabelProvider {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public Image getColumnImage(final Object element, final int columnIndex) {
                    if (columnIndex == 2) {
                        Object value = getStateMap().get(element);
                        if (value != null) {
                            if (_propertyDescriptor.getLabelProvider() != null) {
                                try {
                                    return _propertyDescriptor.getLabelProvider().getImage(value);
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
                public String getColumnText(final Object element, final int columnIndex) {
                    if (element == null) {
                        return "Fehler";
                    } else {
                        if (columnIndex == 2) {
                            Object value = getStateMap().get(element);
                            if (value != null) {
                                if (_propertyDescriptor.getLabelProvider() != null) {
                                    try {
                                        return _propertyDescriptor.getLabelProvider().getText(value);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return value.toString();
                            }
                        }
                        return element.toString();
                    }
                }
            }

        }

    }
}
