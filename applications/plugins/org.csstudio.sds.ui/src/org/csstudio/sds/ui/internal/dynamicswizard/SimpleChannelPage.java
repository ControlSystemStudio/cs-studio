package org.csstudio.sds.ui.internal.dynamicswizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.ui.dnd.rfc.IProcessVariableAdressReceiver;
import org.csstudio.platform.ui.dnd.rfc.IShowControlSystemDialogStrategy;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariableExchangeUtil;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.csstudio.platform.ui.util.SelectionUtil;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.model.logic.RuleDescriptor;
import org.csstudio.sds.model.logic.RuleService;
import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.properties.ActionDataCellEditor;
import org.csstudio.sds.ui.internal.properties.DoubleCellEditor;
import org.csstudio.sds.ui.internal.properties.IntegerCellEditor;
import org.csstudio.sds.ui.internal.properties.MultipleLineTextCellEditor;
import org.csstudio.sds.ui.internal.properties.RGBCellEditor;
import org.csstudio.sds.ui.internal.properties.ResourceCellEditor;
import org.csstudio.sds.ui.internal.properties.view.IPropertyDescriptor;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.csstudio.sds.util.ChannelReferenceValidationUtil;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
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
	private boolean _isLinkOutput = true;

	/**
	 * The dynamics descriptor that is edited.
	 */
	private DynamicsDescriptor _dynamicsDescriptor;

	/**
	 * The model for the channel table.
	 */
	private InputChannelTableModel _inputChannelTableModel;

	/**
	 * The descriptor of the property which is to be configured.
	 */
	private IPropertyDescriptor _propertyDescriptor;

	/**
	 * Names of existing aliases that can be used in channel names.
	 */
	private Map<String, String> _aliases;
	
	/**
	 * A tree viewer, which shows the available rules.
	 */
	private TreeViewer _rulesViewer;
	/**
	 * The selected rule.
	 */
	private RuleDescriptor _selectedRule;
	
	@SuppressWarnings("unchecked")
	private static final Map<Class, CellEditor> CELLEDITORS = new HashMap<Class, CellEditor>();

	/**
	 * Creates the widgets to display the alias informations.
	 * @param parent The parent for the widgets
	 */
	private void createAliasInformation(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		c.setLayout(LayoutUtil.createGridLayout(2, 0, 5, 5));

		Label header = new Label(c, SWT.NONE);
		GridData gd = LayoutUtil.createGridDataForFillingCell();
		gd.horizontalSpan = 2;
		header.setLayoutData(gd);
		header.setFont(CustomMediaFactory.getInstance()
				.getDefaultFont(SWT.BOLD));
		header.setText("Available Aliases / Macros");

		for (String alias : _aliases.keySet()) {
			Label left = new Label(c, SWT.NONE);
			left.setLayoutData(LayoutUtil.createGridData());
			left.setForeground(CustomMediaFactory.getInstance().getColor(
					new RGB(0, 0, 255)));
			left.setText("$" + alias + "$");

			Label right = new Label(c, SWT.NONE);
			right.setLayoutData(LayoutUtil.createGridDataForFillingCell());
			right.setText("--> " + _aliases.get(alias));
		}
	}

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
			IStructuredSelection sel = (IStructuredSelection) _channelTableViewer
					.getSelection();
			if ((sel != null) && (sel.getFirstElement() != null)) {
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
		private DalPropertyTypes _typeHint;

		/**
		 * Constructor.
		 * @param typeHint The {@link DalPropertyTypes}
		 */
		protected AppendTypeHintAction(final DalPropertyTypes typeHint) {
			super(typeHint.toString(), CustomMediaFactory
					.getInstance().getImageDescriptorFromPlugin(
							SdsUiPlugin.PLUGIN_ID, "icons/getas.gif"));
			assert typeHint != null;
			_typeHint = typeHint;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			IStructuredSelection sel = (IStructuredSelection) _channelTableViewer
					.getSelection();

			if (sel != null && sel.getFirstElement() != null && !sel.isEmpty()) {
				for (Object o : sel.toArray()) {
					InputChannelTableRow row = (InputChannelTableRow) o;

					String rowValue = row.getChannel();

					if (rowValue != null && rowValue.length() > 0) {
						row.setChannel(applyTypeHint(rowValue));
					}
				}
				
				_channelTableViewer.refresh();
			}
		}
		
		/**
		 * Adds the type hint to the channel url.
		 * @param channel the existing channel url
		 * @return a enriched channel url
		 */
		private String applyTypeHint(final String channel) {
			String result = null;

			// try to replace existing type hint
			String newPattern = ", "+_typeHint.toPortableString();
			for(DalPropertyTypes dalType : DalPropertyTypes.values()) {
				String oldPattern = ", " + dalType.toPortableString();
				
				if(dalType!=_typeHint && channel.indexOf(oldPattern)>0) {
					result = channel.replace(oldPattern, newPattern);
				}
			}
			
			// if there was no existing type hint, just add the new one
			if(result==null) {
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
			Object element = cell.getElement();
			int index = cell.getColumnIndex();
			cell.setText(getText(element, index));
			Image image = getImage(element, index);
			cell.setImage(image);
			cell.setBackground(getBackground(element));
			cell.setForeground(getForeground(element, index));
			cell.setFont(getFont(element, index));
		}

		/**
		 * Returns the text to display. 
		 * @param element the current element
		 * @param columnIndex the current column index
		 * @return The text to display in the viewer
		 */
		private String getText(final Object element, final int columnIndex) {
			InputChannelTableRow row = (InputChannelTableRow) element;
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
		public String getToolTipText(final Object element) {
			String tooltip = "";
			InputChannelTableRow row = (InputChannelTableRow) element;

			String rawChannel = row.getChannel();

			try {
				// try to resolve the name (this should replace all aliases)
				String channel = ChannelReferenceValidationUtil
						.createCanonicalName(rawChannel, _aliases);

				tooltip = channel + " [STRING]";
			} catch (ChannelReferenceValidationException e) {
				tooltip = e.getMessage();
			}

			return tooltip;
		}

		/**
		 * {@inheritDoc}
		 */
		public Point getToolTipShift(final Object object) {
			return new Point(5, 5);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getToolTipDisplayDelayTime(final Object object) {
			return 100;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getToolTipTimeDisplayed(final Object object) {
			return 10000;
		}

		/**
		 * Returns the font, which is used to display the channel informations.
		 * @param element The current element
		 * @param column The current column index
		 * @return The font
		 */
		private Font getFont(final Object element, final int column) {
			int style = SWT.BOLD;

			InputChannelTableRow row = (InputChannelTableRow) element;

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
		 * @param element The current element
		 * @param column The current column index
		 * @return The foreground color 
		 */
		private Color getForeground(final Object element, final int column) {
			RGB rgb = new RGB(0, 0, 0);

			if (column == 1) {
				InputChannelTableRow row = (InputChannelTableRow) element;

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
		 * @param element The current element
		 * @param index The current column index
		 * @return The Image for the cell
		 */
		private Image getImage(final Object element, final int index) {
			Image result = null;

			if (index == 0) {
				InputChannelTableRow row = (InputChannelTableRow) element;

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
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {

		}

		/**
		 * {@inheritDoc}
		 */
		public Object[] getElements(final Object parent) {
			return _inputChannelTableModel.getAllRows().toArray();
		}

		/**
		 * {@inheritDoc}
		 */
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
		public final boolean canModify(final Object element,
				final String property) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		public final Object getValue(final Object element, final String property) {
			Object result = null;

			InputChannelTableRow row = (InputChannelTableRow) element;

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
				String rawInput = (String) value;

				String channel = "";
				// We take the plain raw input by default. If the entered name
				// does not reference any aliases, we try to
				// validate it using the process variable name parsers, which
				// might automatically add a control system prefix.
				boolean valid = ChannelReferenceValidationUtil
						.testValidity(rawInput);

				if (valid) {
					List<String> requiredAliasNames = ChannelReferenceValidationUtil
							.getRequiredAliasNames(rawInput);

					if (requiredAliasNames.isEmpty()) {
						IProcessVariableAddress pv = ProcessVariableExchangeUtil
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
			Object[] columnHeaders = _channelTableViewer.getColumnProperties();
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
	 * @author Kai Meyer
	 *
	 */
	private final class CustomEditingSupport extends EditingSupport {
		
		/**
		 * The {@link Table} where this {@link EditingSupport} is embedded.
		 */
		private final Table _table;
		/**
		 * A {@link Map} of the already known CellEditors.
		 */
		private Map<Object, CellEditor> _cellEditors = new HashMap<Object, CellEditor>();
		/**
		 * Determines if this {@link EditingSupport} is for the <i>Channel</i> column. 
		 */
		private boolean _channelColumn;
		
		/**
		 * Constructor.
		 * @param viewer The {@link ColumnViewer} for this {@link EditingSupport}.
		 * @param table The {@link Table} 
		 * @param channelColumn True if this {@link EditingSupport} is for the <i>Channel</i> column
		 */
		private CustomEditingSupport(final ColumnViewer viewer, final Table table, final boolean channelColumn) {
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
			if (_channelColumn) {
				return new TextCellEditor(_table);
			}
			if (!_cellEditors.containsKey(element)) {
				InputChannelTableRow row = (InputChannelTableRow) element;
				if (row!=null) {
					Class valueType = row.getValueType();
					CellEditor editor = CELLEDITORS.get(valueType);
					//editor = new TextCellEditor(_table);
					if (editor!=null) {
						_cellEditors.put(element, editor);
					}
				}
			}
			if (_cellEditors.containsKey(element)) {
				return _cellEditors.get(element);
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Object getValue(final Object element) {
			if (element instanceof InputChannelTableRow) {
				InputChannelTableRow row = (InputChannelTableRow)element;
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
					((InputChannelTableRow)element).setChannel(value.toString());
				} else {
					((InputChannelTableRow)element).setDefaultValue(value);
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
	 * 			  the {@link DynamicsDescriptor}
	 * @param propertyDescriptor
	 * 			  the {@link IPropertyDescriptor}
	 * @param aliases 
	 * 			  the aliases
	 */
	public SimpleChannelPage(final String pageName,
			final DynamicsDescriptor dynamicsDescriptor,
			final IPropertyDescriptor propertyDescriptor, final Map<String, String> aliases) {
		super(pageName);
		assert dynamicsDescriptor != null;
		assert aliases != null;
		assert propertyDescriptor != null;
		setTitle("Dynamics Wizard");
		setDescription("Use this wizard to configure the dynamic behaviour of your properties");
		_dynamicsDescriptor = dynamicsDescriptor;
		_propertyDescriptor = propertyDescriptor;
		_aliases = aliases;
	}
	
	/**
	 * Initializes the <code>CELLEDITORS</code>-map with known {@link CellEditor}s.
	 * @param parent The parent composite for the {@link CellEditor}s
	 */
	private void fillCellEditorMap(final Composite parent) {
		CELLEDITORS.put(Object.class, new TextCellEditor(parent));
		CELLEDITORS.put(RGB.class, new RGBCellEditor(parent));
		CELLEDITORS.put(ActionData.class, new ActionDataCellEditor(parent));
		CELLEDITORS.put(Boolean.class, new CheckboxCellEditor(parent));
		CELLEDITORS.put(Double.class, new DoubleCellEditor(parent));
		CELLEDITORS.put(Integer.class, new IntegerCellEditor(parent));
		CELLEDITORS.put(String.class, new MultipleLineTextCellEditor(parent));
		CELLEDITORS.put(IPath.class, new ResourceCellEditor(parent, new String[] {"*.*"}));
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(final Composite parent) {
		Composite c = new Composite(parent, SWT.None);
		c.setLayout(LayoutUtil.createGridLayout(1, 0, 5, 0));

		// create widgets
		_rulesViewer = createRuleControl(c);
		_channelTableViewer = createInputChannelsTable(c);
		
		createAliasInformation(c);

		// initialize the widgets
		if (_dynamicsDescriptor.getRuleId() != null) {
			_selectedRule = RuleService.getInstance().getRuleDescriptor(
					_dynamicsDescriptor.getRuleId());
			if (_selectedRule != null) {
				_rulesViewer.setSelection(new StructuredSelection(_selectedRule));
			}
			updateChannelTableModel();
		}

		// important for wizards -> set the control
		setControl(c);
		this.fillCellEditorMap(c);
	}

	/**
	 * Creates a control, which contains a filtered list of rules.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the control
	 */
	private TreeViewer createRuleControl(final Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGridLayout(2, 3, 5, 0));
		group.setText("Rules / Scripts");
		group.setLayoutData(LayoutUtil
				.createGridDataForHorizontalFillingCell(120));

		TreeViewer viewer = new TreeViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY
				| SWT.SCROLL_LINE | SWT.V_SCROLL | SWT.H_SCROLL);

		viewer.getControl().setLayoutData(
				LayoutUtil.createGridDataForFillingCell());

		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setContentProvider(new BaseWorkbenchContentProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public Object[] getElements(final Object element) {
				return ((Collection<RuleDescriptor>) element).toArray();
			}
		});
		
		List<RuleDescriptor> rules = RuleService.getInstance()
		.getRegisteredRuleDescriptors();
		
		Collections.sort(rules, new Comparator<RuleDescriptor>(){
			public int compare(final RuleDescriptor r1, final RuleDescriptor r2) {
				return r1.getDescription().compareTo(r2.getDescription());
			}
		});
		viewer.setInput(rules);

		// setup a filter for the rules
		viewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(final Viewer viewer, final Object parentElement,
					final Object element) {
				RuleDescriptor ruleDescriptor = (RuleDescriptor) element;
				
				Class ruleReturnType = ruleDescriptor.getReturnType();
				
				boolean result = _propertyDescriptor
						.isCompatibleWith(ruleReturnType);
				
				return result;
			}

		});

		// add a selection listener that updates the channel table when the rule
		// changes
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event
						.getSelection();

				if (sel.getFirstElement() != null) {
					RuleDescriptor descriptor = (RuleDescriptor) sel
							.getFirstElement();
					_selectedRule = descriptor;

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
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0));
		group.setText("Input Channels");
		group.setLayoutData(LayoutUtil
				.createGridDataForHorizontalFillingCell(300));

		final TableViewer viewer = createChannelTable(group);

		// enable Tooltip support
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

		// cell modifiers
		ICellModifier cellModifier = new ChannelTableCellModifier() {
			@Override
			protected void setChannelHook(final ParameterDescriptor descriptor,
					final String channel) {
				if (_isLinkOutput) {
					if ((_dynamicsDescriptor.getOutputChannel() != null)
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
	 * Creates the popup menu.
	 * @param control The parent control for the menu
	 * @return The {@link Menu}
	 */
	private Menu createPopupMenu(final Control control) {
		MenuManager popupMenu = new MenuManager();
		
		// channel actions
		IAction a = new AddInputChannelAction();
		popupMenu.add(a);
		a = new AddOutputChannelAction();
		popupMenu.add(a);
		a = new RemoveChannelAction();
		popupMenu.add(a);

		// "get as" actions
		MenuManager subMenu = new MenuManager("Get As");
		popupMenu.add(subMenu);
		
		for (DalPropertyTypes type : DalPropertyTypes.values()) {
			a = new AppendTypeHintAction(type);
			subMenu.add(a);
		}

		Menu menu = popupMenu.createContextMenu(control);
		control.setMenu(menu);
		return menu;
	}

	/**
	 * Creates the model for the channel table.
	 * @param dynamicsDescriptor The {@link DynamicsDescriptor}
	 * @return the created {@link InputChannelTableModel}
	 */
	private static InputChannelTableModel createChannelTableModel(
			final DynamicsDescriptor dynamicsDescriptor) {
		InputChannelTableModel model = new InputChannelTableModel();

		// input channels
		for (ParameterDescriptor descriptor : dynamicsDescriptor
				.getInputChannels()) {
			String inputChannel = new String(descriptor.getChannel());
			model.addRowForInputChannel(new InputChannelTableRow(
					ParameterType.IN, "", inputChannel));
		}

		// output channel
		if (dynamicsDescriptor.getOutputChannel() != null) {
			String outputChannel = new String(dynamicsDescriptor
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
						_selectedRule.getParameterDescriptions()[i], _selectedRule.getParameterTypes()[i]);
				if (_selectedRule.getRuleId().equals(_dynamicsDescriptor.getRuleId()) && _dynamicsDescriptor.getInputChannels().length>i) {
					_inputChannelTableModel.setInputChannelValue(i, _dynamicsDescriptor.getInputChannels()[i].getValue());
				} else {
					_inputChannelTableModel.setInputChannelValue(i, null);
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
		String[] columnNames = new String[] { "PROP_DESCRIPTION", "PROP_NAME", "PROP_VALUE"}; //$NON-NLS-1$ //$NON-NLS-2$ 

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
		EditingSupport editingSupport = new CustomEditingSupport(viewer, table, true);
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
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(
				viewer, new FocusCellOwnerDrawHighlighter(viewer));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				viewer) {
			protected boolean isEditorActivationEvent(
					final ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2)
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
						for (IProcessVariableAddress pv : pvs) {
							addInputChannel(pv.getFullName());
						}
					}
				}, new IShowControlSystemDialogStrategy() {

					public boolean showControlSystem(final String rawName) {
						// only popup the dialog if there are no aliases used
						// within the raw string
						boolean show = ChannelReferenceValidationUtil
								.getRequiredAliasNames(rawName).isEmpty();
						return show;
					}

				});

		ProcessVariableExchangeUtil.addProcessVariableAdressDragSupport(viewer
				.getControl(), DND.DROP_MOVE | DND.DROP_COPY,
				new IProcessVariableAdressProvider() {
					public List<IProcessVariableAddress> getProcessVariableAdresses() {
						List<ParameterDescriptor> parameterDescriptors = SelectionUtil
								.getInstance().getObjectsFromSelection(
										viewer.getSelection());

						List<IProcessVariableAddress> result = new ArrayList<IProcessVariableAddress>();

						for (ParameterDescriptor d : parameterDescriptors) {
							IProcessVariableAddress pv = ProcessVariableAdressFactory
									.getInstance().createProcessVariableAdress(
											d.getChannel());
							result.add(pv);
						}
						return result;
					}

					public IProcessVariableAddress getPVAdress() {
						List<IProcessVariableAddress> all = getProcessVariableAdresses();
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
		ParameterDescriptor descriptor = new ParameterDescriptor();
		descriptor.setChannel(channelName);
		_dynamicsDescriptor.setOutputChannel(descriptor);

//		if (_isLinkOutput) {
//			if (!_dynamicsDescriptor.hasInputChannel(descriptor)) {
//				// _dynamicsDescriptor.addInputChannel(descriptor);
//			}
//		}

		InputChannelTableRow row = new InputChannelTableRow(ParameterType.OUT,
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
		IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance()
				.createProcessVariableAdress(channelName);

		InputChannelTableRow row = new InputChannelTableRow(ParameterType.IN,
				"", pv.getFullName());
		_inputChannelTableModel.addRowForInputChannel(row);
		_channelTableViewer.refresh();
	}

	/**
	 * Finishes the editing.
	 * @param dynamicsDescriptor The {@link DynamicsDescriptor}
	 */
	public void performFinish(final DynamicsDescriptor dynamicsDescriptor) {
		// setup rule
		if (_selectedRule != null) {
			dynamicsDescriptor.setRuleId(_selectedRule.getRuleId());
		}
		// setup IN channels
		for (InputChannelTableRow row : _inputChannelTableModel
				.getRowsWithContent(ParameterType.IN)) {
			dynamicsDescriptor.addInputChannel(new ParameterDescriptor(row.getChannel(), row.getValueType(), row.getDefaultValue()));
		}

		// setup OUT channels
		// FIXME: Sobald es mehere OUT-Parameter geben kann, hier entsprechend
		// anpassen
		List<InputChannelTableRow> outParameter = _inputChannelTableModel
				.getRowsWithContent(ParameterType.OUT);
		if (outParameter.size() > 0) {
			InputChannelTableRow row = outParameter.get(0); 
			dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(
					row.getChannel(), row.getValueType(), row.getDefaultValue()));
		}
		// for (String value :
		// _inputChannelTableModel.getRowsWithContent(ParameterType.OUT)) {
		// dynamicsDescriptor.setOutputChannel(new ParameterDescriptor(value));
		// }
	}
}
