package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.internal.model.ActionDataProperty;
import org.csstudio.sds.model.ActionData;
import org.csstudio.sds.model.ActionType;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.AbstractWidgetActionModel;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ColumnConfig;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ConvenienceTableWrapper;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ITableRow;
import org.csstudio.sds.ui.internal.properties.PropertyDescriptorFactoryService;
import org.csstudio.sds.ui.properties.IPropertyDescriptor;
import org.csstudio.sds.ui.properties.IPropertyDescriptorFactory;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Section for {@link ActionDataProperty}.
 * 
 * @author Kai Meyer (C1 WPS)
 *
 */
public class ActionDataSection extends AbstractBaseSection<ActionDataProperty> {

	private TableViewer tableViewer;
	private List<TableEditor> tableEditors = new ArrayList<TableEditor>();

	public ActionDataSection(String propertyId) {
		super(propertyId);
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public int getMinimumHeight() {
		return 150;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doCreateControls(final Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(parent);

		// .. table for viewing and editing the entries
		Composite tableComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().hint(STANDARD_WIDGET_WIDTH * 3, 100)
				.span(5, 1).applyTo(tableComposite);

		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		Table table = getWidgetFactory().createTable(tableComposite,
				SWT.FULL_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);

		tableViewer = ConvenienceTableWrapper.equip(table, 
				new ColumnConfig("action", "Action", 100, 10, false),
				new ColumnConfig("remove", "Remove", 30, -1, true),
				new ColumnConfig("up", "Up", 30, -1, true));

		// .. button to add new entries to the table
		final Hyperlink addHyperLink = getWidgetFactory().createHyperlink(parent, "Add Action...", SWT.NONE);
		addHyperLink.setUnderlined(false);

		addHyperLink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				ActionDataProperty property = getMainWidgetProperty();

				if (property != null) {
					ActionData data = property.<ActionData>getPropertyValue().clone();

					if (data != null) {
						AbstractWidgetActionModel widgetAction = ActionType.OPEN_DISPLAY
								.getActionFactory().createWidgetActionModel();
						ActionDataDialog dialog = new ActionDataDialog(
								addHyperLink.getShell(), widgetAction);
						if (Window.OK == dialog.open()) {
							widgetAction = dialog.getActionModel();
							printAction(widgetAction);
							data.addAction(widgetAction);
							applyPropertyChange(data);
						}
					}
				}
			}
		});

	}
	
	private void printAction(AbstractWidgetActionModel widgetAction) {
		System.out.println("WidgetAction:");
		System.out.println("	"+ widgetAction.getActionLabel());
		for (WidgetProperty prop : widgetAction.getProperties()) {
			System.out.println("	" + prop.getDescription() + ": " + prop.getPropertyValue());
		}
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	protected void doRefreshControls(ActionDataProperty widgetProperty) {
		// .. (re)create the table editors used for removing single lines of the
		// table
		if (tableEditors != null) {
			// .. dispose existing editors
			for (TableEditor editor : tableEditors) {
				if (editor.getEditor() != null) {
					editor.getEditor().dispose();
				}
				editor.dispose();

			}

			tableEditors.clear();
		}
		// .. create new editors
		if (widgetProperty != null && tableViewer.getContentProvider() != null) {
			ActionData original = widgetProperty.getPropertyValue();
			ActionData data = original.clone();
			List<ITableRow> rows = new ArrayList<ITableRow>();

			for (AbstractWidgetActionModel action : data.getWidgetActions()) {
				rows.add(new ActionDataRowAdapter(data, action));
			}
			tableViewer.setInput(rows);

			TableItem[] items = tableViewer.getTable().getItems();
			for (int i = 0; i < items.length; i++) {
				final TableItem item = items[i];

				TableEditor deleteTableEditor = new TableEditor(tableViewer
						.getTable());
				Button deleteButton = new Button(tableViewer.getTable(),
						SWT.FLAT);
				deleteButton.setImage(CustomMediaFactory.getInstance()
						.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
								"icons/delete.gif"));
				deleteButton.pack();
				deleteTableEditor.minimumWidth = deleteButton.getSize().x;
				deleteTableEditor.horizontalAlignment = SWT.LEFT;
				deleteTableEditor.setEditor(deleteButton, item, 1);
				deleteButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ActionDataRowAdapter data = (ActionDataRowAdapter) item
								.getData();
						data.setValue(1, "true");
					}
				});
				tableEditors.add(deleteTableEditor);

				TableEditor upTableEditor = new TableEditor(tableViewer
						.getTable());
				final Button upButton = new Button(tableViewer.getTable(), SWT.FLAT);
				upButton.setImage(CustomMediaFactory.getInstance()
						.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
								"icons/search_prev.gif"));
				upButton.pack();
				upTableEditor.minimumWidth = upButton.getSize().x;
				upTableEditor.horizontalAlignment = SWT.LEFT;
				upTableEditor.setEditor(upButton, item, 2);
				upButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						ActionDataRowAdapter data = (ActionDataRowAdapter) item
								.getData();
						data.setValue(2, "true");
					}
				});

				tableEditors.add(upTableEditor);
			}
		}
	}

	/**
	 * 
	 * @author Kai Meyer (C1 WPS)
	 * 
	 */
	private class ActionDataRowAdapter implements ITableRow {

		private final AbstractWidgetActionModel _actionModel;
		private final ActionData _actionData;

		public ActionDataRowAdapter(ActionData actionData,
				AbstractWidgetActionModel action) {
			_actionData = actionData;
			_actionModel = action;
		}

		public boolean canModify(int column) {
			return true;
		}

		public RGB getBackgroundColor(int column) {
			return null;
		}

		public CellEditor getCellEditor(int column, Composite parent) {
			switch (column) {
			case 0:
				return new ActionDataCellEditor(parent, _actionModel);
			default:
				return null;
			}
		}

		public String getDisplayValue(int column) {
			switch (column) {
			case 0:
				return _actionModel.getActionLabel();
			default:
				return null;
			}
		}

		public String getEditingValue(int column) {
			return _actionModel.getActionLabel();
		}

		public Font getFont(int column) {
			return CustomMediaFactory.getInstance().getFont("Arial", 10,
					SWT.NORMAL);
		}

		public RGB getForegroundColor(int column) {
			return null;
		}

		public Image getImage(int column) {
			if (column == 0) {
				ImageDescriptor imageDescriptor = CustomMediaFactory
						.getInstance().getImageDescriptorFromPlugin(
								SdsUiPlugin.PLUGIN_ID,
								_actionModel.getType().getIcon());
				return imageDescriptor.createImage();
			}
			return null;
		}

		public String getTooltip() {
			return null;
		}

		public void setValue(int column, Object value) {
			switch (column) {
			case 0:
				AbstractWidgetActionModel newModel = (AbstractWidgetActionModel) value;
				_actionData.replaceActionModels(_actionModel, newModel);
				applyPropertyChange(_actionData);
				break;
			case 1:
				_actionData.removeAction(_actionModel);
				applyPropertyChange(_actionData);
				break;
			case 2:
				_actionData.upAction(_actionModel);
				applyPropertyChange(_actionData);
				break;
			default:
				break;
			}
		}

		public int compareTo(ITableRow o) {
			return 0;
		}

	}

	/**
	 * 
	 * @author Kai Meyer (C1 WPS)
	 *
	 */
	private class ActionDataCellEditor extends CellEditor {

		private final Shell _shell;
		private AbstractWidgetActionModel _actionModel;

		public ActionDataCellEditor(Composite parent,
				AbstractWidgetActionModel actionModel) {
			super(parent, SWT.NONE);
			_actionModel = actionModel;
			_shell = parent.getShell();
		}

		@Override
		protected Control createControl(Composite parent) {
			return null;
		}

		@Override
		public void activate() {
			ActionDataDialog dialog = new ActionDataDialog(_shell, _actionModel);
			dialog.open();
			_actionModel = dialog.getActionModel();
			fireApplyEditorValue();
		}

		@Override
		protected Object doGetValue() {
			return _actionModel;
		}

		@Override
		protected void doSetFocus() {
			// nothing to do
		}

		@Override
		protected void doSetValue(Object value) {
			// nothing to do
		}

	}

	/**
	 * 
	 * @author Kai Meyer (C1 WPS)
	 *
	 */
	private class ActionDataDialog extends Dialog {

		private TableViewer _propertyViewer;
		private CellEditor _openedCellEditor = null;
		private AbstractWidgetActionModel _actionModel;
		private final HashMap<IPropertyDescriptor, WidgetProperty> _descriptorPropertyMap;

		protected ActionDataDialog(Shell parentShell,
				AbstractWidgetActionModel actionModel) {
			super(parentShell);
			_actionModel = actionModel;
			_descriptorPropertyMap = new HashMap<IPropertyDescriptor, WidgetProperty>();
			parentShell.setText("Action Properties");
		}

		public AbstractWidgetActionModel getActionModel() {
			return _actionModel;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite area = (Composite) super.createDialogArea(parent);

			ComboViewer typeCombo = new ComboViewer(area, SWT.READ_ONLY);
			typeCombo.setContentProvider(new ArrayContentProvider());
			typeCombo.setLabelProvider(new LabelProvider());
			typeCombo.setInput(ActionType.values());

			StructuredSelection selection = new StructuredSelection(
					_actionModel.getType());
			typeCombo.setSelection(selection, true);

			typeCombo
					.addSelectionChangedListener(new ISelectionChangedListener() {
						public void selectionChanged(SelectionChangedEvent event) {
							IStructuredSelection selection = (IStructuredSelection) event
									.getSelection();
							ActionType newType = (ActionType) selection
									.getFirstElement();
							_actionModel = newType.getActionFactory()
									.createWidgetActionModel();
							_propertyViewer
									.setInput(generateInputProperties(_actionModel));
						}
					});

			_propertyViewer = createPropertyTableViewer(area);
			_propertyViewer.setInput(generateInputProperties(_actionModel));
			return area;
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
							}
						}

						@Override
						public void beforeEditorActivated(
								final ColumnViewerEditorActivationEvent event) {
							getButton(IDialogConstants.CANCEL_ID).setEnabled(
									false);
							getButton(IDialogConstants.OK_ID).setEnabled(false);
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

			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2,
					1);
			gridData.heightHint = 100;
			table.setLayoutData(gridData);
			return viewer;
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
		 * Calculates the input for the table depending on the type of the
		 * {@link ActionData}.
		 * 
		 * @param action
		 *            The {@link AbstractWidgetActionModel} for the input
		 */
		private IPropertyDescriptor[] generateInputProperties(
				final AbstractWidgetActionModel action) {
			IPropertyDescriptor[] descriptors = new IPropertyDescriptor[0];
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
			return descriptors;
		}

		/**
		 * Returns the {@link WidgetProperty} of the {@link IPropertyDescriptor}
		 * .
		 * 
		 * @param descriptor
		 *            The {@link IPropertyDescriptor}
		 * @return The {@link WidgetProperty} for the given descriptor
		 */
		private WidgetProperty getWidgetProperty(
				final IPropertyDescriptor descriptor) {
			if (!_descriptorPropertyMap.containsKey(descriptor)) {
				for (String propertyKey : _actionModel.getPropertyKeys()) {
					if (descriptor.getId().equals(propertyKey)) {
						WidgetProperty widgetProperty = _actionModel
								.getProperty(propertyKey);
						_descriptorPropertyMap.put(descriptor, widgetProperty);
						return widgetProperty;
					}
				}
				return null;
			} else {
				return _descriptorPropertyMap.get(descriptor);
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
					}
				}
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
					if (widgetProperty != null
							&& descriptor.getLabelProvider() != null) {
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
