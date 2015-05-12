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
package org.csstudio.sds.ui.internal.dynamicswizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.csstudio.dal.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.dal.ui.dnd.rfc.IShowControlSystemDialogStrategy;
import org.csstudio.dal.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.domain.common.LayoutUtil;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.sds.internal.rules.NullRule;
import org.csstudio.sds.internal.rules.ParameterDescriptor;
import org.csstudio.sds.internal.rules.RuleDescriptor;
import org.csstudio.sds.internal.rules.RuleService;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.csstudio.sds.util.SelectionUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * WizardPage implementation, which enables the user to configure a simple
 * channel.
 *
 * @author Sven Wende
 */
public final class SimpleChannelPage extends WizardPage {

    /**
     * A table viewer, which is used to configure input parameters.
     */
    private TableViewer _channelTableViewer;

    /**
     * A table viewer, which is used to configure output parameters.
     */
    private TableViewer _outputChannelTableViewer;

    /**
     * Flag that signals if the definition of output channels should
     * automatically lead to the definition of an input channel.
     */
    private final boolean _isLinkOutput = true;

    /**
     * The dynamics descriptor that is edited.
     */
    private final DynamicsDescriptor _dynamicsDescriptor;

    /**
     * The model for the channel table.
     */
    private InputChannelTableModel _inputChannelTableModel;

    /**
     * Names of existing aliases that can be used in channel names.
     */
    private final Map<String, String> _aliases;

    /**
     * A tree viewer, which shows the available rules.
     */
    private TreeViewer _rulesViewer;
    /**
     * Checkbox for using only the connection states.
     */
    private Button _useOnlyConnectionsCheckBox;
    /**
     * The selected rule.
     */
    private RuleDescriptor _selectedRule;

    private Text _rulePattern;

    private final PropertyTypesEnum _propertyType;

    private Text _ruleDescriptionText;

    /**
     * An action, which removes an input channel from the configuration.
     *
     * @author Sven Wende
     */
    private final class RemoveChannelAction extends Action {
        /**
         * Constructor.
         */
        public RemoveChannelAction() {
            super("Remove Parameter", CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/parameter_remove.gif"));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            final IStructuredSelection sel = (IStructuredSelection) _channelTableViewer
                    .getSelection();
            if (sel != null && sel.getFirstElement() != null) {
                _inputChannelTableModel.removeRow((InputChannelTableRow) sel
                        .getFirstElement());
                _channelTableViewer.refresh();
            }
        }
    }

    /**
     * An action, which adds a type hint to the selected channel parameter.
     *
     * @author Sven Wende
     */
    protected final class AppendTypeHintAction extends Action {
        /**
         * The {@link DalPropertyTypes}.
         */
        private final DalPropertyTypes _typeHint;

        /**
         * Constructor.
         *
         * @param typeHint
         *            The {@link DalPropertyTypes}
         */
        protected AppendTypeHintAction(final DalPropertyTypes typeHint) {
            super(typeHint.toString(), CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/getas.gif"));
            assert typeHint != null;
            _typeHint = typeHint;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            final IStructuredSelection sel = (IStructuredSelection) _channelTableViewer
                    .getSelection();

            if (sel != null && sel.getFirstElement() != null && !sel.isEmpty()) {
                for (final Object o : sel.toArray()) {
                    final InputChannelTableRow row = (InputChannelTableRow) o;

                    final String rowValue = row.getChannel();

                    if (rowValue != null && rowValue.length() > 0) {
                        row.setChannel(applyTypeHint(rowValue));
                    }
                }

                _channelTableViewer.refresh();
            }
        }

        /**
         * Adds the type hint to the channel url.
         *
         * @param channel
         *            the existing channel url
         * @return a enriched channel url
         */
        private String applyTypeHint(final String channel) {
            String result = null;

            // try to replace existing type hint
            final String newPattern = ", " + _typeHint.toPortableString();
            for (final DalPropertyTypes dalType : DalPropertyTypes.values()) {
                final String oldPattern = ", " + dalType.toPortableString();

                if (dalType != _typeHint && channel.indexOf(oldPattern) > 0) {
                    result = channel.replace(oldPattern, newPattern);
                }
            }

            // if there was no existing type hint, just add the new one
            if (result == null) {
                result = channel + newPattern;
            }

            return result;
        }
    }

    /**
     * An action, which adds an input channel to the configuration.
     *
     * @author Sven Wende
     */
    protected final class AddInputChannelAction extends Action {
        /**
         * Constructor.
         */
        protected AddInputChannelAction() {
            super("Add Parameter", CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/parameter_in.gif"));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            addInputChannel("<<Channel>>");
        }
    }

    /**
     * An action, which adds an output channel to the configuration.
     *
     * @author Alexander Will
     */
    protected final class AddOutputChannelAction extends Action {
        /**
         * Constructor.
         */
        protected AddOutputChannelAction() {
            super("Add Output Channel", CustomMediaFactory.getInstance()
                    .getImageDescriptorFromPlugin(SdsUiPlugin.PLUGIN_ID,
                            "icons/parameter_out.gif"));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            addOutputChannel("<<Channel>>");
        }
    }

    /**
     * Label provider for the channel tables.
     *
     * @author Sven Wende
     *
     */
    protected final class ChannelTableLabelProvider extends ColumnLabelProvider {

        /**
         * FIXME: Momentan ist update() nur wegen eines Workarrounds
         * überschrieben. Umstellen des Labelproviders auf
         * TableColumnViewerLabelProvider.class, sobald diese public ist.
         * {@inheritDoc}
         */
        @Override
        public void update(final ViewerCell cell) {
            final Object element = cell.getElement();
            final int index = cell.getColumnIndex();
            cell.setText(getText(element, index));
            final Image image = getImage(element, index);
            cell.setImage(image);
            cell.setBackground(getBackground(element));
            cell.setForeground(getForeground(element, index));
            cell.setFont(getFont(element, index));
        }

        /**
         * Returns the text to display.
         *
         * @param element
         *            the current element
         * @param columnIndex
         *            the current column index
         * @return The text to display in the viewer
         */
        private String getText(final Object element, final int columnIndex) {
            final InputChannelTableRow row = (InputChannelTableRow) element;
            String result = "";
            switch (columnIndex) {
            case 0:
                result = row.getDescription();
                break;
            case 1:
                result = row.getChannel();
                break;
            case 2:
                result = row.getDefaultValueAsString();
                break;
            default:
                break;
            }
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getToolTipText(final Object element) {
            String tooltip = "";
            final InputChannelTableRow row = (InputChannelTableRow) element;

            final String rawChannel = row.getChannel();

            try {
                // try to resolve the name (this should replace all aliases)
                final String channel = ChannelReferenceValidationUtil
                        .createCanonicalName(rawChannel, _aliases);

                tooltip = channel + " [STRING]";
            } catch (final ChannelReferenceValidationException e) {
                tooltip = e.getMessage();
            }

            return tooltip;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Point getToolTipShift(final Object object) {
            return new Point(5, 5);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getToolTipDisplayDelayTime(final Object object) {
            return 100;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getToolTipTimeDisplayed(final Object object) {
            return 10000;
        }

        /**
         * Returns the font, which is used to display the channel informations.
         *
         * @param element
         *            The current element
         * @param column
         *            The current column index
         * @return The font
         */
        private Font getFont(final Object element, final int column) {
            int style = SWT.BOLD;

            final InputChannelTableRow row = (InputChannelTableRow) element;

            if (column == 1) {
                if (row.getDescription() == null
                        || row.getDescription().equals("")) {
                    style = SWT.ITALIC;
                } else {
                    style = SWT.NONE;
                }
            }
            return CustomMediaFactory.getInstance().getDefaultFont(style);
        }

        /**
         * returns the foreground color for a cell.
         *
         * @param element
         *            The current element
         * @param column
         *            The current column index
         * @return The foreground color
         */
        private Color getForeground(final Object element, final int column) {
            RGB rgb = new RGB(0, 0, 0);

            if (column == 1) {
                final InputChannelTableRow row = (InputChannelTableRow) element;

                if (row.getDescription() == null
                        || row.getDescription().equals("")) {
                    rgb = new RGB(200, 200, 200);
                } else {
                    if (!ChannelReferenceValidationUtil.testValidity(row
                            .getChannel())) {
                        rgb = new RGB(255, 160, 160);
                    }
                }
            }
            return CustomMediaFactory.getInstance().getColor(rgb);
        }

        /**
         * Returns the Image for a cell.
         *
         * @param element
         *            The current element
         * @param index
         *            The current column index
         * @return The Image for the cell
         */
        private Image getImage(final Object element, final int index) {
            Image result = null;

            if (index == 0) {
                final InputChannelTableRow row = (InputChannelTableRow) element;

                if (row.getParameterType() == ParameterType.IN) {
                    result = CustomMediaFactory.getInstance()
                            .getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
                                    "icons/parameter_in.gif"); //$NON-NLS-1$
                } else if (row.getParameterType() == ParameterType.OUT) {
                    result = CustomMediaFactory.getInstance()
                            .getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
                                    "icons/parameter_out.gif"); //$NON-NLS-1$
                }
            }

            return result;
        }
    }

    /**
     * Content provider for the input channel table.
     *
     * @author Sven Wende
     *
     */
    protected final class ChannelTableContentProvider implements
            IStructuredContentProvider {
        /**
         * {@inheritDoc}
         */
        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput,
                final Object newInput) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getElements(final Object parent) {
            return _inputChannelTableModel.getAllRows().toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {

        }
    }

    /**
     * Cell modifier for the channel tables.
     *
     * @author Sven Wende
     *
     */
    protected class ChannelTableCellModifier implements ICellModifier {
        /**
         * Constructor.
         */
        protected ChannelTableCellModifier() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean canModify(final Object element,
                final String property) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Object getValue(final Object element, final String property) {
            Object result = null;

            final InputChannelTableRow row = (InputChannelTableRow) element;

            switch (findColumnIndex(property)) {
            case 0:
                result = row.getChannel();
                break;
            case 1:
                result = row.getChannel();
                break;
            default:
                break;
            }
            assert result != null : "result!=null"; //$NON-NLS-1$;

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final void modify(final Object element, final String property,
                final Object value) {
            InputChannelTableRow row;

            // @see ICellModifier#modify(element, property, value)
            if (element instanceof Item) {
                row = (InputChannelTableRow) ((Item) element).getData();
            } else {
                row = (InputChannelTableRow) element;
            }

            switch (findColumnIndex(property)) {
            case 1:
                final String rawInput = (String) value;

                String channel = "";
                // We take the plain raw input by default. If the entered name
                // does not reference any aliases, we try to
                // validate it using the process variable name parsers, which
                // might automatically add a control system prefix.
                final boolean valid = ChannelReferenceValidationUtil
                        .testValidity(rawInput);

                if (valid) {
                    final List<String> requiredAliasNames = ChannelReferenceValidationUtil
                            .getRequiredAliasNames(rawInput);

                    if (requiredAliasNames.isEmpty()) {
                        final IProcessVariableAddress pv = ProcessVariableExchangeUtil
                                .parseProcessVariableAdress(rawInput, true);

                        if (pv != null) {
                            channel = pv.getFullName();
                        }
                    } else {
                        channel = rawInput;
                    }
                } else {
                    channel = rawInput;
                }

                // setChannelHook(row, channel);
                row.setChannel(channel);
                break;
            default:
                break;
            }

            _channelTableViewer.refresh();
            _channelTableViewer.setSelection(null);
        }

        /**
         * Hook method that is called before the channel name is actually set.
         * Subclasses may overwrite in order to perform custom actions.
         *
         * @param descriptor
         *            the parameter descriptor.
         * @param channel
         *            the new channel name.
         */
        protected void setChannelHook(final ParameterDescriptor descriptor,
                final String channel) {
        }

        /**
         * Gets the colunmn index for the specified property.
         *
         * @param property
         *            the property
         *
         * @return the column index
         */
        private int findColumnIndex(final String property) {
            int result = 0;
            // Find the index of the column
            final Object[] columnHeaders = _channelTableViewer.getColumnProperties();
            for (int i = 0; i < columnHeaders.length; i++) {
                if (columnHeaders[i].equals(property)) {
                    result = i;
                }
            }

            return result;
        }
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
         * Determines if this {@link EditingSupport} is for the <i>Channel</i>
         * column.
         */
        private final boolean _channelColumn;

        /**
         * Constructor.
         *
         * @param viewer
         *            The {@link ColumnViewer} for this {@link EditingSupport}.
         * @param table
         *            The {@link Table}
         * @param channelColumn
         *            True if this {@link EditingSupport} is for the <i>Channel</i>
         *            column
         */
        private CustomEditingSupport(final ColumnViewer viewer,
                final Table table, final boolean channelColumn) {
            super(viewer);
            _table = table;
            _channelColumn = channelColumn;
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
            return new TextCellEditor(_table);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object getValue(final Object element) {
            if (element instanceof InputChannelTableRow) {
                final InputChannelTableRow row = (InputChannelTableRow) element;
                if (_channelColumn) {
                    return row.getChannel();
                }
                return row.getDefaultValue();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void setValue(final Object element, final Object value) {
            if (element instanceof InputChannelTableRow) {
                if (_channelColumn) {
                    ((InputChannelTableRow) element).setChannel(value
                            .toString());
                    _channelTableViewer.getCellModifier().modify(element,
                            "PROP_NAME", value);
                } else {
                    String newValue = "";
                    if (value != null) {
                        newValue = value.toString();
                    }
                    ((InputChannelTableRow) element).setDefaultValue(newValue);
                }
            }
            this.getViewer().refresh();
        }
    }

    /**
     * Constructor.
     *
     * @param pageName
     *            the name of the page
     * @param dynamicsDescriptor
     *            the {@link DynamicsDescriptor}
     * @param propertyDescriptor
     *            the {@link IPropertyDescriptor}
     * @param aliases
     *            the aliases
     */
    public SimpleChannelPage(final String pageName,
            final DynamicsDescriptor dynamicsDescriptor,
            final PropertyTypesEnum propertyType,
            final Map<String, String> aliases) {
        super(pageName);
        assert dynamicsDescriptor != null;
        assert aliases != null;
        assert propertyType != null : "propertyType != null";
        _propertyType = propertyType;
        setTitle("Dynamics Wizard");
        setDescription("Use this wizard to configure the dynamic behaviour of your properties");
        _dynamicsDescriptor = dynamicsDescriptor;
        _aliases = aliases;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(final Composite parent) {
        final Composite c = new Composite(parent, SWT.None);
        c.setLayout(new GridLayout(1, false));

        _useOnlyConnectionsCheckBox = new Button(c, SWT.CHECK);
        _useOnlyConnectionsCheckBox.setText("Use only connection states");
        _useOnlyConnectionsCheckBox
                .addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(final SelectionEvent e) {
                        executeCheckBoxStateChange(_useOnlyConnectionsCheckBox
                                .getSelection());
                    }
                });

        _rulePattern = new Text(c, SWT.SEARCH);
        _rulePattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _rulePattern.setMessage("Rule Pattern");

        _rulesViewer = createRuleControl(c);
        _channelTableViewer = createInputChannelsTable(c);

        createAliasInformation(c);

        // initialize the widgets
        if (_dynamicsDescriptor.getRuleId() != null) {
            _selectedRule = RuleService.getInstance().getRuleDescriptor(
                    _dynamicsDescriptor.getRuleId());
            if (_selectedRule != null) {
                _rulesViewer
                        .setSelection(new StructuredSelection(_selectedRule));
            }
            updateChannelTableModel();
        }

        _useOnlyConnectionsCheckBox.setSelection(_dynamicsDescriptor
                .isUsingOnlyConnectionStates());
        this.executeCheckBoxStateChange(_useOnlyConnectionsCheckBox
                .getSelection());

        // important for wizards -> set the control
        setControl(c);
    }

    /**
     * @param c
     * @return
     */
    private Text createDescriptionControl(final Composite c) {
        final Text text = new Text(c, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER|SWT.READ_ONLY);
//        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        text.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        text.setText("");
        return text;
    }

    /**
     * Updates the viewer for the rules depending on given choice.
     *
     * @param choice
     *            <code>true</code> if the viewer should be disabled,
     *            <code>false</code> otherwise.
     */
    private void executeCheckBoxStateChange(final boolean choice) {
        _rulesViewer.getTree().setEnabled(!choice);
        if (choice) {
            _selectedRule = RuleService.getInstance().getRuleDescriptor(
                    NullRule.ID);
        } else {
            _selectedRule = RuleService.getInstance().getRuleDescriptor(
                    _dynamicsDescriptor.getRuleId());
        }
        updateChannelTableModel();
    }

    /**
     * Creates a control, which contains a filtered list of rules.
     *
     * @param parent
     *            the parent composite
     * @return the control
     */
    private TreeViewer createRuleControl(final Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText("Rules / Scripts");
        group.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, 120).create());

        final TreeViewer viewer = new TreeViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY
                | SWT.SCROLL_LINE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

        viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
        _ruleDescriptionText = createDescriptionControl(group);


        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setContentProvider(new BaseWorkbenchContentProvider() {
            @SuppressWarnings("unchecked")
            @Override
            public Object[] getElements(final Object element) {
                return ((Collection<RuleDescriptor>) element).toArray();
            }
        });
        ColumnViewerToolTipSupport.enableFor(viewer);

        final List<RuleDescriptor> rules = RuleService.getInstance()
                .getRegisteredRuleDescriptors();

        Collections.sort(rules, new Comparator<RuleDescriptor>() {
            @Override
            public int compare(final RuleDescriptor r1, final RuleDescriptor r2) {
                return r1.getDescription().compareTo(r2.getDescription());
            }
        });
        viewer.setInput(rules);

        // setup a filter for the rules
        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer,
                    final Object parentElement, final Object element) {
                final RuleDescriptor ruleDescriptor = (RuleDescriptor) element;

                final PropertyTypesEnum[] ruleReturnType = ruleDescriptor.getCompatiblePropertyTypes();

                for (final PropertyTypesEnum type : ruleReturnType) {
                    if (type.equals(_propertyType)) {
                        return true;
                    }
                }

                return false;
            }

        });

        // Filter for Pattern set by User.
        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                final RuleDescriptor ruleDescriptor = (RuleDescriptor) element;
                final String pattern = _rulePattern.getText();
                if (pattern == null || pattern.length() < 1) {
                    return true;
                }
                return ruleDescriptor.getDescription().toLowerCase().matches(
                        pattern.replace("$", "\\$").replace(".", "\\.").replace("*", ".*").replace(
                                "?", ".?").toLowerCase()
                                + ".*");
            }

        });

        // listener to update rule list for new rule patterns set by user.
        _rulePattern.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(final KeyEvent e) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                viewer.refresh();
            }

        });


        // add a selection listener that updates the channel table when the rule
        // changes
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection sel = (IStructuredSelection) event
                        .getSelection();

                if (sel.getFirstElement() != null) {
                    final RuleDescriptor descriptor = (RuleDescriptor) sel
                            .getFirstElement();
                    _selectedRule = descriptor;

                    String description = descriptor.getRule().getDescription();
                    if(description == null || description.isEmpty()) {
                        description = "no description available";
                    }
                    _ruleDescriptionText.setText(description);
                    updateChannelTableModel();
                }
            }

        });

        return viewer;
    }

    /**
     * Creates a table viewer, which enables the user to enter typed input
     * channels.
     *
     * @param parent
     *            the parent composite
     *
     * @return the created viewer
     */
    private TableViewer createInputChannelsTable(final Composite parent) {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0));
        group.setText("Input Channels");
        group.setLayoutData(LayoutUtil
                .createGridDataForHorizontalFillingCell(300));

        final TableViewer viewer = createChannelTable(group);

        // enable Tooltip support
        ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

        // cell modifiers
        final ICellModifier cellModifier = new ChannelTableCellModifier() {
            @Override
            protected void setChannelHook(final ParameterDescriptor descriptor,
                    final String channel) {
                if (_isLinkOutput) {
                    if (_dynamicsDescriptor.getOutputChannel() != null
                            && _dynamicsDescriptor.getOutputChannel().equals(
                                    descriptor)) {
                        _dynamicsDescriptor.getOutputChannel().setChannel(
                                channel);
                    }
                }

                viewer.setSelection(null);
            }

        };

        viewer.setCellModifier(cellModifier);

        viewer.setContentProvider(new ChannelTableContentProvider());
        viewer.setLabelProvider(new ChannelTableLabelProvider());

        // Input aufbereiten
        _inputChannelTableModel = createChannelTableModel(_dynamicsDescriptor);
        viewer.setInput(_inputChannelTableModel);

        createPopupMenu(viewer.getControl());
        return viewer;
    }

    /**
     * Creates the widgets to display the alias informations.
     *
     * @param parent
     *            The parent for the widgets
     */
    private void createAliasInformation(final Composite parent) {
        final Composite c = new Composite(parent, SWT.NONE);
        c.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        c.setLayout(new GridLayout(2, false));

        final Label header = new Label(c, SWT.NONE);
        final GridData gd = GridDataFactory.fillDefaults().grab(true, true).create();
        gd.horizontalSpan = 2;
        header.setLayoutData(gd);
        header.setFont(CustomMediaFactory.getInstance()
                .getDefaultFont(SWT.BOLD));
        header.setText("Available Aliases / Macros");

        for (final String alias : _aliases.keySet()) {
            final Label left = new Label(c, SWT.NONE);
            left.setLayoutData(GridDataFactory.fillDefaults().create());
            left.setForeground(CustomMediaFactory.getInstance().getColor(
                    new RGB(0, 0, 255)));
            left.setText("$" + alias + "$");

            final Label right = new Label(c, SWT.NONE);
            right.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
            right.setText("--> " + _aliases.get(alias));
        }
    }

    /**
     * Creates the popup menu.
     *
     * @param control
     *            The parent control for the menu
     * @return The {@link Menu}
     */
    private Menu createPopupMenu(final Control control) {
        final MenuManager popupMenu = new MenuManager();

        // channel actions
        IAction a = new AddInputChannelAction();
        popupMenu.add(a);
        a = new AddOutputChannelAction();
        popupMenu.add(a);
        a = new RemoveChannelAction();
        popupMenu.add(a);

        // "get as [TYPE]" actions
        final MenuManager subMenu = new MenuManager("Get As");
        popupMenu.add(subMenu);

        for (final DalPropertyTypes type : DalPropertyTypes.values()) {
            a = new AppendTypeHintAction(type);
            subMenu.add(a);
        }

        final Menu menu = popupMenu.createContextMenu(control);
        control.setMenu(menu);
        return menu;
    }

    /**
     * Creates the model for the channel table.
     *
     * @param dynamicsDescriptor
     *            The {@link DynamicsDescriptor}
     * @return the created {@link InputChannelTableModel}
     */
    private static InputChannelTableModel createChannelTableModel(
            final DynamicsDescriptor dynamicsDescriptor) {
        final InputChannelTableModel model = new InputChannelTableModel();

        // input channels
        for (final ParameterDescriptor descriptor : dynamicsDescriptor
                .getInputChannels()) {
            final String inputChannel = new String(descriptor.getChannel());
            model.addRowForInputChannel(new InputChannelTableRow(
                    ParameterType.IN, "", inputChannel));
        }

        // output channel
        if (dynamicsDescriptor.getOutputChannel() != null) {
            final String outputChannel = new String(dynamicsDescriptor
                    .getOutputChannel().getChannel());

            if (outputChannel != null && outputChannel.length() > 0) {
                model.addRowForOutputChannel(new InputChannelTableRow(
                        ParameterType.OUT, "Output Channel", outputChannel));
            }
        }

        return model;
    }

    /**
     * Updates the model.
     */
    private void updateChannelTableModel() {
        if (_selectedRule != null) {
            _inputChannelTableModel.clearInputChannelDescriptions();
            for (int i = 0; i < _selectedRule.getParameterDescriptions().length; i++) {
                _inputChannelTableModel.setInputChannelDescription(i,
                        _selectedRule.getParameterDescriptions()[i]);
                if (_selectedRule.getRuleId().equals(
                        _dynamicsDescriptor.getRuleId())
                        && _dynamicsDescriptor.getInputChannels().length > i) {
                    _inputChannelTableModel.setInputChannelValue(i,
                            _dynamicsDescriptor.getInputChannels()[i]
                                    .getValue());
                } else {
                    _inputChannelTableModel.setInputChannelValue(i, "");
                }
            }
            _channelTableViewer.refresh();
        }
    }

    /**
     * Creates a table viewer for managing channels.
     *
     * @param parent
     *            The parent composite.
     *
     * @return The created viewer.
     */
    private TableViewer createChannelTable(final Composite parent) {
        // define column names
        final String[] columnNames = new String[] {
                "PROP_DESCRIPTION", "PROP_NAME", "PROP_VALUE" }; //$NON-NLS-1$ //$NON-NLS-2$

        // create table
        final Table table = new Table(parent, SWT.FULL_SELECTION
                | SWT.HIDE_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE);
        table.setLinesVisible(true);
        table.setLayoutData(LayoutUtil.createGridDataForFillingCell());
        table.setHeaderVisible(true);

        // create viewer
        final TableViewer viewer = new TableViewer(table);

        TableViewerColumn tvColumn;
        tvColumn = new TableViewerColumn(viewer, SWT.NONE);
        tvColumn.getColumn().setText("Description");
        tvColumn.getColumn().setMoveable(false);
        tvColumn.getColumn().setWidth(200);
        tvColumn = new TableViewerColumn(viewer, SWT.NONE);
        tvColumn.getColumn().setText("Channel");
        tvColumn.getColumn().setMoveable(false);
        tvColumn.getColumn().setWidth(300);
        EditingSupport editingSupport = new CustomEditingSupport(viewer, table,
                true);
        tvColumn.setEditingSupport(editingSupport);
        tvColumn = new TableViewerColumn(viewer, SWT.NONE);
        tvColumn.getColumn().setText("Default Value");
        tvColumn.getColumn().setMoveable(false);
        tvColumn.getColumn().setWidth(200);
        editingSupport = new CustomEditingSupport(viewer, table, false);
        tvColumn.setEditingSupport(editingSupport);

        viewer.setUseHashlookup(true);

        // define column properties
        viewer.setColumnProperties(columnNames);

        // configure keyboard support
        final TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(
                viewer, new FocusCellOwnerDrawHighlighter(viewer));

        final ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
                viewer) {
            @Override
            protected boolean isEditorActivationEvent(
                    final ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
                        || event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };

        TableViewerEditor.create(viewer, focusCellManager, actSupport,
                ColumnViewerEditor.TABBING_HORIZONTAL
                        | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.TABBING_VERTICAL
                        | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        // DnD
        ProcessVariableExchangeUtil.addProcessVariableAddressDropSupport(viewer
                .getControl(), DND.DROP_MOVE | DND.DROP_COPY,
                new IProcessVariableAdressReceiver() {
                    public void receive(final IProcessVariableAddress[] pvs,
                            final DropTargetEvent event) {
                        for (final IProcessVariableAddress pv : pvs) {
                            addInputChannel(pv.getFullName());
                        }
                    }
                }, new IShowControlSystemDialogStrategy() {

                    public boolean showControlSystem(final String rawName) {
                        // only popup the dialog if there are no aliases used
                        // within the raw string
                        final boolean show = ChannelReferenceValidationUtil
                                .getRequiredAliasNames(rawName).isEmpty();
                        return show;
                    }

                });

        ProcessVariableExchangeUtil.addProcessVariableAdressDragSupport(viewer
                .getControl(), DND.DROP_MOVE | DND.DROP_COPY,
                new IProcessVariableAdressProvider() {
                    @Override
                    public List<IProcessVariableAddress> getProcessVariableAdresses() {
                        final List<ParameterDescriptor> parameterDescriptors = SelectionUtil
                                .getInstance().getObjectsFromSelection(
                                        viewer.getSelection());

                        final List<IProcessVariableAddress> result = new ArrayList<IProcessVariableAddress>();

                        for (final ParameterDescriptor d : parameterDescriptors) {
                            final IProcessVariableAddress pv = ProcessVariableAdressFactory
                                    .getInstance().createProcessVariableAdress(
                                            d.getChannel());
                            result.add(pv);
                        }
                        return result;
                    }

                    @Override
                    public IProcessVariableAddress getPVAdress() {
                        final List<IProcessVariableAddress> all = getProcessVariableAdresses();
                        if (all.size() > 0) {
                            return all.get(0);
                        }
                        return null;
                    }

                });

        return viewer;
    }

    /**
     * Add a output channel.
     *
     * @param channelName
     *            The output channel name.
     */
    private void addOutputChannel(final String channelName) {
        final ParameterDescriptor descriptor = new ParameterDescriptor();
        descriptor.setChannel(channelName);
        _dynamicsDescriptor.setOutputChannel(descriptor);

        final InputChannelTableRow row = new InputChannelTableRow(ParameterType.OUT,
                "OUT", channelName);
        _inputChannelTableModel.addRowForOutputChannel(row);
        _channelTableViewer.refresh();

        _outputChannelTableViewer.refresh();
    }

    /**
     * Add a input channel.
     *
     * @param channelName
     *            The input channel name.
     */
    private void addInputChannel(final String channelName) {
        final IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance()
                .createProcessVariableAdress(channelName);

        final InputChannelTableRow row = new InputChannelTableRow(ParameterType.IN,
                "", pv.getFullName());
        _inputChannelTableModel.addRowForInputChannel(row);
        _channelTableViewer.refresh();
    }

    /**
     * Finishes the editing.
     *
     * @param dynamicsDescriptor
     *            The {@link DynamicsDescriptor}
     */
    public void performFinish(final DynamicsDescriptor dynamicsDescriptor) {
        // setup rule
        if (_selectedRule != null) {
            dynamicsDescriptor.setRuleId(_selectedRule.getRuleId());
        }
        final boolean selection = _useOnlyConnectionsCheckBox.getSelection();
        dynamicsDescriptor.setUsingOnlyConnectionStates(selection);
        // setup IN channels
        for (final InputChannelTableRow row : _inputChannelTableModel
                .getRowsWithContent(ParameterType.IN)) {
            dynamicsDescriptor.addInputChannel(new ParameterDescriptor(row
                    .getChannel(), row.getDefaultValue()));
        }

        // setup OUT channels
        final List<InputChannelTableRow> outParameter = _inputChannelTableModel
                .getRowsWithContent(ParameterType.OUT);
        if (outParameter.size() > 0) {
            final InputChannelTableRow row = outParameter.get(0);
            dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(row
                    .getChannel(), row.getDefaultValue()));
        }
    }
}
