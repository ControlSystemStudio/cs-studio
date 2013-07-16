package org.csstudio.askap.sb;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.ui.SourceSelectionDialog;
import org.csstudio.askap.sb.util.CalibrationSource;
import org.csstudio.askap.sb.util.ParamDataModel;
import org.csstudio.askap.sb.util.SBParameter;
import org.csstudio.askap.sb.util.SBTemplate;
import org.csstudio.askap.sb.util.SBTemplateDataModel;
import org.csstudio.askap.sb.util.SchedulingBlock;
import org.csstudio.askap.sb.util.SchedulingBlock.SBState;
import org.csstudio.askap.utility.AskapEditorInput;
import org.csstudio.askap.utility.AskapHelper;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public class SBMaintenanceView extends EditorPart {

	public static final String ID = "org.csstudio.askap.sb.SBMaintenanceView";
	private static Logger logger = Logger.getLogger(SBMaintenanceView.class.getName());

	private static final String DATE_FORMAT = "ddMMyyyy-HHmmss";
	private static final int NUM_OF_COLUMNS = 4;
	private static final int BAR_SPACING = 10;
	private static final int EXPAND_ITEM_HEIGHT = 150;

	private SBTemplateDataModel sbTemplateDataModel = null;
	private ParamDataModel userConfigModel = new ParamDataModel();
	private ParamDataModel fixedConfigModel = new ParamDataModel();

	private TableViewer fixedParamTable;
	private TableViewer userParamTable;
	
	private Button exportTemplate;
	private Button stateTransitionButton;
	private Button createSBButton;

	private ExpandItem fixedParamExpandItem;
	private ExpandItem scriptExpandItem;

	private Text scriptText;

	class ParameterValueEditor extends EditingSupport {
		
		TableViewer viewer;

		public ParameterValueEditor(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			
			
	/**
	 * only need this if we are using Ephemerides Service
	 *  
			
			if (element instanceof SBParameter) {
				final SBParameter param = (SBParameter) element;
				if (param.getParam().contains(
						"." + Preferences.CALIBRATION_SOURCE_NAME)) {
					return new DialogCellEditor(viewer.getTable()) {
						@Override
						protected Object openDialogBox(Control cellEditorWindow) {
							return selectSource(param.getParam());
						}
					};
				}

			}
		**/
			return new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			SBParameter sb = (SBParameter) element;
			return sb.getStrValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			try {
				SBParameter sb = (SBParameter) element;
				sb.setValue(value.toString());
				viewer.refresh();
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not set value to " + value + ": " + e.getMessage());
	            ExceptionDetailsErrorDialog.openError(getShell(),
	                    "ERROR",
	                    "Could not set value to " + value,
	                    e);

			}
		}
		
	}
	
	public Shell getShell() {
		return this.getSite().getShell();
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		GridLayout layout = new GridLayout(NUM_OF_COLUMNS, false);
		parent.setLayout(layout);

		exportTemplate = new Button(parent, SWT.PUSH);
		exportTemplate.setText("Export Template to File");
		exportTemplate.setEnabled(false);
		
		exportTemplate.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent event) {
				try {
					exportTemplate();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Could not export schema to file", e);
		            ExceptionDetailsErrorDialog.openError(getShell(),
		                    "ERROR",
		                    "Could not export schema to file",
		                    e);
				}				
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		Label l = new Label(parent, SWT.NONE);
		GridData g = new GridData();
		g.horizontalAlignment = GridData.FILL;	
		g.grabExcessHorizontalSpace = true;
		l.setLayoutData(g);

		stateTransitionButton = new Button(parent, SWT.PUSH);
		stateTransitionButton.setText("Deprecate");
		stateTransitionButton.setVisible(false);
		stateTransitionButton.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent arg0) {
				transitionSB();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		
		// if 'Create' is clicked, user is required to specify a filename if this is not a load
		createSBButton = new Button(parent, SWT.PUSH);
		createSBButton.setText("Create Scheduling Block");
		createSBButton.setEnabled(false);
		createSBButton.addSelectionListener(new SelectionListener() {			
			public void widgetSelected(SelectionEvent arg0) {
				createSB();
			}
			
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		final ExpandBar bar = new ExpandBar(parent, SWT.NO_SCROLL);
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;	
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = NUM_OF_COLUMNS;
		bar.setLayoutData(gridData);
		
		// First item
		Composite script = new Composite(bar, SWT.NONE);
		GridLayout gridLayout1 = new GridLayout(1, false);
		script.setLayout(gridLayout1);
		
		scriptText = new Text(script, SWT.MULTI | SWT.V_SCROLL);
		scriptText.setEditable(false);
		scriptText.setSize(SWT.DEFAULT, 100);
		GridData g1 = new GridData();
		g1.horizontalAlignment = GridData.FILL;	
		g1.grabExcessHorizontalSpace = true;
		g1.verticalAlignment = GridData.FILL;	
		g1.grabExcessVerticalSpace = true;
		scriptText.setLayoutData(g1);
				
		scriptExpandItem = new ExpandItem (bar, SWT.V_SCROLL, 0);
		scriptExpandItem.setText("Python script");
		scriptExpandItem.setHeight(EXPAND_ITEM_HEIGHT);
		scriptExpandItem.setControl(script);
		
		scriptExpandItem.setExpanded(true);
		
		// Second item
		Composite fixedParam = new Composite(bar, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout(1, false);
		fixedParam.setLayout(gridLayout2);
		
		fixedParamTable = createParamTable(fixedParam, false);
		GridData tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;	
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		fixedParamTable.getControl().setLayoutData(tableGridData);
		
		fixedParamExpandItem = new ExpandItem (bar, SWT.V_SCROLL, 1);
		fixedParamExpandItem.setText("Fixed Parameters");
		fixedParamExpandItem.setHeight(EXPAND_ITEM_HEIGHT);
		fixedParamExpandItem.setControl(fixedParam);
		
		fixedParamExpandItem.setExpanded(true);
		
		fixedParam.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize(fixedParamTable.getTable());
			}
		});
		
		bar.addExpandListener(new ExpandListener() {

			public void itemExpanded(ExpandEvent e) {
				ExpandItem item = (ExpandItem) e.item;					
				int h=item.getHeaderHeight();
				item.setHeight(EXPAND_ITEM_HEIGHT);
				
				if (item == scriptExpandItem) {
					// if item0 is being expanded, then we have to look at if item1 is expanded
					if (fixedParamExpandItem.getExpanded())
						gridData.heightHint = EXPAND_ITEM_HEIGHT*2+h*2+BAR_SPACING*3;
					else
						gridData.heightHint = EXPAND_ITEM_HEIGHT+h*2+BAR_SPACING*3;
				} else {
					if (scriptExpandItem.getExpanded())
						gridData.heightHint = EXPAND_ITEM_HEIGHT*2+h*2+BAR_SPACING*3;
					else
						gridData.heightHint = EXPAND_ITEM_HEIGHT+h*2+BAR_SPACING*3;
				}
				
				parent.layout(true, true);
			}

			public void itemCollapsed(ExpandEvent e) {
				ExpandItem item = (ExpandItem) e.item;
				int h=item.getHeaderHeight();
                item.setHeight(h);
				if (item == scriptExpandItem) {
					if (fixedParamExpandItem.getExpanded())
						gridData.heightHint = EXPAND_ITEM_HEIGHT+h*2+BAR_SPACING*3;
					else
						gridData.heightHint = h*2+BAR_SPACING*3;
				} else {
					if (scriptExpandItem.getExpanded())
						gridData.heightHint = EXPAND_ITEM_HEIGHT+h*2+BAR_SPACING*3;
					else
						gridData.heightHint = h*2+BAR_SPACING*3;
				}
				parent.layout(true, true);
                
			}
		});		
		
		bar.setSpacing(BAR_SPACING);
		
		// user configurable parameters
		Label userParamLabel = new Label(parent, SWT.NONE);
		userParamLabel.setText("User Configurable Parameters");
		GridData g3 = new GridData();
		g3.horizontalSpan = NUM_OF_COLUMNS;
		userParamLabel.setLayoutData(g3);
		
		userParamTable = createParamTable(parent, true);
		tableGridData = new GridData();
		tableGridData.horizontalSpan = NUM_OF_COLUMNS;
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;	
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		userParamTable.getControl().setLayoutData(tableGridData);
				
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize(userParamTable.getTable());
			}
		});
	}


	private TableViewer createParamTable(Composite parent, boolean isEditable) {
		TableViewer table = new TableViewer (parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		table.getTable().setLinesVisible (true);
		table.getTable().setHeaderVisible (true);
		
		createColumns(parent, table, isEditable);
		
		table.setContentProvider(new ArrayContentProvider());

		return table;
	}

	protected void createSB() {
		SBTemplate template = (SBTemplate) createSBButton.getData();
		try {
			if (template!=null) {
				// get SB name from dialog box
				String sbName = template.getName() + "-" 
									+ AskapHelper.getFormatedData(null, DATE_FORMAT);
				
				InputDialog sbDialog = new InputDialog(this.getSite().getShell(),
						"Please enter an alias name",
						"Please enter an alias name:", 
						sbName, 
						new IInputValidator() {								
							@Override
							public String isValid(String newText) {
								if (newText==null || newText.trim().length()==0)
									return "Please enter a reason for Deprecation!";
								
								return null;
							}
						});
				if (sbDialog.open() != Window.OK)
					return;
				
				sbName = sbDialog.getValue();				
				SchedulingBlock sb = sbTemplateDataModel.createSB(sbName.trim(), template, userConfigModel.getParamValues());
				
				SBTemplateView view = (SBTemplateView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SBTemplateView.ID);				
				view.refreshAndSelect(sb);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not create Scheduling Block", e);
            ExceptionDetailsErrorDialog.openError(getShell(),
                    "ERROR",
                    "Could not create Scheduling Block",
                    e);
		}
		
	}

	protected void transitionSB() {
		SchedulingBlock sb = (SchedulingBlock) stateTransitionButton.getData();
		try {
			if (sb!=null) {
				if (sb.getState().equals(SBState.SUBMITTED)) {
					// transition to error, must enter reason
					InputDialog sbDialog = new InputDialog(this.getSite().getShell(),
							"Please enter reason for Deprecation",
							"Please enter a reason:", 
							"Deprecate due to major version upgrade", 
							new IInputValidator() {								
								@Override
								public String isValid(String newText) {
									if (newText==null || newText.trim().length()==0)
										return "Please enter a reason for Deprecation!";
									
									return null;
								}
							});
					if (sbDialog.open() != Window.OK)
						return;
					
					String reason = sbDialog.getValue();					
					sbTemplateDataModel.setSBState(sb.getId(), SBState.ERRORED, reason);
					
				} else if (sb.getState().equals(SBState.ERRORED)){
					sbTemplateDataModel.setSBState(sb.getId(), SBState.SUBMITTED);
				}
				SBTemplateView view = (SBTemplateView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SBTemplateView.ID);				
				view.refreshAndSelect(sb);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not " + stateTransitionButton.getText() + " Scheduling Block " + sb.getId(), e);
            ExceptionDetailsErrorDialog.openError(getShell(),
                    "ERROR",
                    "Could not " + stateTransitionButton.getText() + " Scheduling Block",
                    e);
		}
	}

	protected void exportTemplate() {
		// TODO Auto-generated method stub
		
	}


	public void display(SchedulingBlock sb, SBTemplateDataModel dataModel) {
		sbTemplateDataModel = dataModel;
		SBTemplate latestTemplate = sbTemplateDataModel.getLatestVersion(sb.getTemplateName());
		boolean isLatest = (sb.getMajorVersion()==latestTemplate.getMajorVersion());
		createSBButton.setEnabled(false);
		stateTransitionButton.setVisible(false);
		
		try {
			loadTemplate(sbTemplateDataModel.getLatestVersion(sb.getTemplateName(), sb.getMajorVersion()), isLatest);
			loadSB(sb, isLatest); 
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not load SchedulingBlock " + sb.getAliasName(), e);
            ExceptionDetailsErrorDialog.openError(getShell(),
                    "ERROR",
                    "Could not load SchedulingBlock " + sb.getAliasName(),
                    e);
		}
	}

	public void display(SBTemplate template, SBTemplateDataModel dataModel) {
		sbTemplateDataModel = dataModel;
		SBTemplate latestTemplate = sbTemplateDataModel.getLatestVersion(template.getName());
		boolean isLatest = (template.getVersion().equals(latestTemplate.getVersion()));
		createSBButton.setEnabled(false);
		stateTransitionButton.setVisible(false);
		
		try {
			loadTemplate(template, isLatest);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not load Template " + template.getDisplayName(), e);
            ExceptionDetailsErrorDialog.openError(getShell(),
                    "ERROR",
                    "Could not load Template " + template.getDisplayName(),
                    e);
		}
	}

	private void loadSB(SchedulingBlock sb, boolean canCreateSB) throws Exception {
		userConfigModel.loadValue(sb.getParameterMap());
		populateTable(userParamTable, userConfigModel, true);
		
		if (sb.getState().equals(SBState.ERRORED)) {
			if (canCreateSB) {
				stateTransitionButton.setText("Resubmit");
				stateTransitionButton.setVisible(true);
				stateTransitionButton.setData(sb);
			}
		} else if (sb.getState().equals(SBState.SUBMITTED)) {
			stateTransitionButton.setText("Deprecate");
			stateTransitionButton.setVisible(true);
			stateTransitionButton.setData(sb);
		}
	}

	private void populateTable(TableViewer tableViewer,
			ParamDataModel dataModel, boolean isEditable) {
		
		tableViewer.setInput(dataModel.getParamters());
		
	}

	private void loadTemplate(SBTemplate template, boolean canCreateSB) throws Exception {
		userConfigModel.clear();
		fixedConfigModel.clear();
				
		scriptText.setText(template.getPythonScript());
		ParamDataModel.loadParamModel(template.getParameterMap(), userConfigModel, fixedConfigModel);

		populateTable(userParamTable, userConfigModel, true);
		populateTable(fixedParamTable, fixedConfigModel, false);
		
		exportTemplate.setEnabled(true);
		exportTemplate.setData(template);
		
		createSBButton.setEnabled(canCreateSB);
		createSBButton.setData(template);		
	}

	
	private void createColumns(final Composite parent, final TableViewer table, boolean isEditable) {		
		
		TableViewerColumn col = createTableViewerColumn("Name", table);		
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return sb.getName();
			}
		});	
		
		col = createTableViewerColumn("Description", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return sb.getDescription();
			}
		});	
		
		col = createTableViewerColumn("Type", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return sb.getType();
			}
		});	

		col = createTableViewerColumn("Min Val", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return sb.getMin()==null ? "" : sb.getMin().toString();
			}
		});	

		col = createTableViewerColumn("Max Val", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return sb.getMax()==null ? "" : sb.getMax().toString();
			}
		});	

		col = createTableViewerColumn("Size", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return "" + sb.getSize();
			}
		});	

		col = createTableViewerColumn("Enum", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return sb.getEnumStr();
			}
		});	

		col = createTableViewerColumn("Step", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return "" + sb.getStep();
			}
		});	
		
		col = createTableViewerColumn("Unit", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return "" + sb.getUnit();
			}
		});	

		col = createTableViewerColumn("Value", table);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SBParameter sb = (SBParameter) element;
				return "" + sb.getStrValue();
			}
		});
		
		if (isEditable)
			col.setEditingSupport(new ParameterValueEditor(table));
	}

	private TableViewerColumn createTableViewerColumn(String title, TableViewer table) {
		TableViewerColumn viewerColumn = new TableViewerColumn(table,
				SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		
		return viewerColumn;
	}
	
	
	protected void setTableSize(Table table) {
		Rectangle area = table.getParent().getClientArea();
		Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = table.getVerticalBar();
		int width = area.width - table.computeTrim(0, 0, 0, 0).width
				- vBar.getSize().x;
		if (size.y > area.height + table.getHeaderHeight()) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = vBar.getSize();
			width -= vBarSize.x;
		}
		Point oldSize = table.getSize();
		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			table.getColumn(0).setWidth(width * 15 / 100);
			if (table.getColumnCount() > 10)
				table.getColumn(1).setWidth(width * 20 / 100);
			else
				table.getColumn(1).setWidth(width * 25 / 100);

			table.getColumn(2).setWidth(width * 5 / 100);
			table.getColumn(3).setWidth(width * 5 / 100);
			table.getColumn(4).setWidth(width * 5 / 100);
			table.getColumn(5).setWidth(width * 5 / 100);
			table.getColumn(6).setWidth(width * 5 / 100);
			table.getColumn(7).setWidth(width * 5 / 100);
			table.getColumn(8).setWidth(width * 5 / 100);
			table.getColumn(9).setWidth(width * 25 / 100);
			if (table.getColumnCount() > 10)
				table.getColumn(10).setWidth(width * 5 / 100);
			table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			table.setSize(area.width, area.height);
			table.getColumn(0).setWidth(width * 15 / 100);

			if (table.getColumnCount() > 10)
				table.getColumn(1).setWidth(width * 20 / 100);
			else
				table.getColumn(1).setWidth(width * 25 / 100);
			table.getColumn(2).setWidth(width * 5 / 100);
			table.getColumn(3).setWidth(width * 5 / 100);
			table.getColumn(4).setWidth(width * 5 / 100);
			table.getColumn(5).setWidth(width * 5 / 100);
			table.getColumn(6).setWidth(width * 5 / 100);
			table.getColumn(7).setWidth(width * 5 / 100);
			table.getColumn(8).setWidth(width * 5 / 100);
			table.getColumn(9).setWidth(width * 25 / 100);
			if (table.getColumnCount() > 10)
				table.getColumn(10).setWidth(width * 5 / 100);
		}
		table.pack();
	}
	
	
	protected String selectSource(String param) {
		String sourceName = "";
		SourceSelectionDialog sourceSelectionDialog = new SourceSelectionDialog(
				getShell());
		CalibrationSource source = sourceSelectionDialog.open();
		if (source != null) {
			for (TableItem tableItem : userParamTable.getTable().getItems()) {
				SBParameter sb = (SBParameter) tableItem.getData();
				try {
					// source.name
					if (sb.getParam().equals(param)) {
						sb.setValue(source.name);
						tableItem.setText(9, source.name);
						sourceName = source.name;
					} else if (sb.getParam().equals(
							param.replace(Preferences.CALIBRATION_SOURCE_NAME,
									Preferences.CALIBRATION_SOURCE_FRAME))) {
						sb.setValue(source.frame);
						tableItem.setText(9, source.frame);
					} else if (sb.getParam().equals(
							param.replace(Preferences.CALIBRATION_SOURCE_NAME,
									Preferences.CALIBRATION_SOURCE_C1))) {
						sb.setValue(source.C1);
						tableItem.setText(9, source.C1);
					} else if (sb.getParam().equals(
							param.replace(Preferences.CALIBRATION_SOURCE_NAME,
									Preferences.CALIBRATION_SOURCE_C2))) {
						sb.setValue(source.C2);
						tableItem.setText(9, source.C2);
					}
				} catch (Exception e) {
					// we really should not be here
					logger.log(Level.WARNING,
							"Got exception while trying to set source", e);
				}
			}
		}
		
		return sourceName;
	}

	
	public SBMaintenanceView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
        setSite(site);
        setPartName(input.getName());
    	setInput(input);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public static SBMaintenanceView openSBMaintenanceView() {
		try {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			final IWorkbenchWindow window = workbench
					.getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();

			return (SBMaintenanceView) page.openEditor(new AskapEditorInput(
					"SB Maintenance View"), ID);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Cannot create SB Maintenance View", ex);
		}
		return null;
	}

}
