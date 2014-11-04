package org.csstudio.askap.sb;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.askap.sb.ui.SourceSelectionDialog;
import org.csstudio.askap.sb.util.CalibrationSource;
import org.csstudio.askap.sb.util.ParamDataModel;
import org.csstudio.askap.sb.util.SBParameter;
import org.csstudio.askap.sb.util.SBTemplate;
import org.csstudio.askap.sb.util.SBTemplateDataModel;
import org.csstudio.askap.sb.util.SchedulingBlock;
import org.csstudio.askap.utility.AskapEditorInput;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	private static final int EXPAND_ITEM_HEIGHT = 200;

	private SBTemplateDataModel sbTemplateDataModel = null;
	private ParamDataModel userConfigModel = new ParamDataModel();
	private ParamDataModel fixedConfigModel = new ParamDataModel();

	private TableViewer obsParameterTable;
	private TableViewer obsVarTable;
	
	private ExpandItem obsVarExpandItem;
	private ExpandItem scriptExpandItem;

	private Text scriptText;
	
	private Label sbInfoLabel;

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
		
		sbInfoLabel = new Label(parent, 0);
		GridData g = new GridData();
		g.horizontalAlignment = GridData.FILL;	
		g.grabExcessHorizontalSpace = true;
		g.horizontalSpan = NUM_OF_COLUMNS;
		sbInfoLabel.setLayoutData(g);
		
		FontData[] fontData = sbInfoLabel.getFont().getFontData();
		for (FontData f : fontData) {
//			f.setHeight(f.getHeight()*2);
			f.setStyle(SWT.ITALIC|SWT.BOLD);
		}
		
		sbInfoLabel.setFont(new Font(parent.getDisplay(), fontData));
		sbInfoLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
//		sbInfoLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
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
		g1.horizontalSpan = NUM_OF_COLUMNS;
		scriptText.setLayoutData(g1);
				
		scriptExpandItem = new ExpandItem (bar, SWT.V_SCROLL, 0);
		scriptExpandItem.setText("Observation Procedure");
		scriptExpandItem.setHeight(EXPAND_ITEM_HEIGHT);
		scriptExpandItem.setControl(script);
		
		scriptExpandItem.setExpanded(false);
		
		// 2nd item
		Composite obsVariables = new Composite(bar, SWT.NONE);
		GridLayout gridLayout3 = new GridLayout(1, false);
		obsVariables.setLayout(gridLayout3);
		
		obsVarTable = createObsVarTable(obsVariables);
		GridData tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;	
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		tableGridData.horizontalSpan = NUM_OF_COLUMNS;
		obsVarTable.getControl().setLayoutData(tableGridData);
		
		obsVarExpandItem = new ExpandItem (bar, SWT.V_SCROLL, 1);
		obsVarExpandItem.setText("Observation Variables");
		obsVarExpandItem.setHeight(EXPAND_ITEM_HEIGHT);
		obsVarExpandItem.setControl(obsVariables);
		
		obsVarExpandItem.setExpanded(true);
		
		obsVariables.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize(obsVarTable.getTable());
			}
		});
		
		
		bar.addExpandListener(new ExpandListener() {

			public void itemExpanded(ExpandEvent e) {
				ExpandItem item = (ExpandItem) e.item;					
				int h=item.getHeaderHeight();
				item.setHeight(EXPAND_ITEM_HEIGHT);
				
				if (item == scriptExpandItem) {
					// if item0 is being expanded, then we have to look at if item1 is expanded
					if (obsVarExpandItem.getExpanded())
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
					if (obsVarExpandItem.getExpanded())
						gridData.heightHint = EXPAND_ITEM_HEIGHT+h*2+BAR_SPACING*3;
					else
						gridData.heightHint = h*2+BAR_SPACING*3;
				} else {
					if (obsVarExpandItem.getExpanded())
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
		userParamLabel.setText("Observation Parameters");
		GridData g3 = new GridData();
		g3.horizontalAlignment = GridData.FILL;	
		g3.grabExcessHorizontalSpace = true;
		g3.horizontalSpan = NUM_OF_COLUMNS;
		userParamLabel.setLayoutData(g3);
		
		obsParameterTable = createParamTable(parent, true);
		tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;	
		tableGridData.verticalAlignment = GridData.FILL;	
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.grabExcessVerticalSpace = true;
		tableGridData.horizontalSpan = NUM_OF_COLUMNS;

		obsParameterTable.getControl().setLayoutData(tableGridData);
				
		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				setTableSize(obsParameterTable.getTable());
			}
		});
	}


	private TableViewer createParamTable(Composite parent, boolean isEditable) {
		TableViewer table = new TableViewer (parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		table.getTable().setLinesVisible (true);
		table.getTable().setHeaderVisible (true);
		
		TableColumn column = new TableColumn (table.getTable(), SWT.NONE);
		column.setText ("Name");
		
		column = new TableColumn (table.getTable(), SWT.NONE);
		column.setText ("Value");

		return table;
	}

	private TableViewer createObsVarTable(Composite parent) {
		TableViewer table = new TableViewer (parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		table.getTable().setLinesVisible (true);
		table.getTable().setHeaderVisible (true);
		
		TableColumn column = new TableColumn (table.getTable(), SWT.NONE);
		column.setText ("Name");
		
		column = new TableColumn (table.getTable(), SWT.NONE);
		column.setText ("Value");
		
		return table;
	}


	private void setSBInfo(SchedulingBlock sb) {
		sbInfoLabel.setText("ID: " + sb.getId()
					+ "         Alias: " + sb.getAliasName()
					+ "         Template Name: " + sb.getTemplateName()
					+ "         Version: " + sb.getMajorVersion());
	}
	
	public void display(long id) {
		
		try {
			SchedulingBlock sb = sbTemplateDataModel.getSB(id);
			
			setSBInfo(sb);
			
			loadSB(sb, false); 
			
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not load SchedulingBlock " + id, e);
            ExceptionDetailsErrorDialog.openError(getShell(),
                    "ERROR",
                    "Could not load SchedulingBlock " + id,
                    e);
		}
	}


	public void display(SchedulingBlock sb, SBTemplateDataModel dataModel) {
		sbTemplateDataModel = dataModel;
		SBTemplate latestTemplate = sbTemplateDataModel.getLatestVersion(sb.getTemplateName());
		boolean isLatest = (sb.getMajorVersion()==latestTemplate.getMajorVersion());

		setSBInfo(sb);

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
		
		sbInfoLabel.setText("SB Template: " + template.getDisplayName());

		sbTemplateDataModel = dataModel;
		SBTemplate latestTemplate = sbTemplateDataModel.getLatestVersion(template.getName());
		boolean isLatest = (template.getVersion().equals(latestTemplate.getVersion()));
		
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
		populateTable(obsParameterTable, sb.getParameterMap());
		populateTable(obsVarTable, sb.getObsVariable());
		
		SBTemplate template = sbTemplateDataModel.getLatestVersion(sb.getTemplateName(), sb.getMajorVersion());
		scriptText.setText(template.getPythonScript());
	}

	private void populateTable(TableViewer tableViewer,
			ParamDataModel dataModel, boolean isEditable) {
		tableViewer.setInput(dataModel.getParamters());
	}

	private void populateTable(TableViewer tableViewer, Map<String, String> valueMap) {
		
		if (valueMap==null) {
			tableViewer.getTable().clearAll();
		} else {
			tableViewer.setItemCount(valueMap.size());
			
			int index = 0;
			
			Set<String> keys = valueMap.keySet();
			TreeSet<String> sortedKeys = new TreeSet<String>(keys);
			
			for (String key : sortedKeys) {
				TableItem item = tableViewer.getTable().getItem(index);
				item.setText(new String[]{key, valueMap.get(key)});
				index++;
			}
		}
	}

	
	private void loadTemplate(SBTemplate template, boolean canCreateSB) throws Exception {
		userConfigModel.clear();
		fixedConfigModel.clear();
				
		scriptText.setText(template.getPythonScript());
		ParamDataModel.loadParamModel(template.getParameterMap(), userConfigModel, fixedConfigModel);

		populateTable(obsParameterTable, userConfigModel, true);
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
			table.getColumn(0).setWidth(width * 30 / 100);
			table.getColumn(1).setWidth(width * 70 / 100);
			table.setSize(area.width, area.height);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
			table.setSize(area.width, area.height);
			table.getColumn(0).setWidth(width * 30 / 100);
			table.getColumn(1).setWidth(width * 70 / 100);
		}
		table.pack();
	}

	
	protected String selectSource(String param) {
		String sourceName = "";
		SourceSelectionDialog sourceSelectionDialog = new SourceSelectionDialog(
				getShell());
		CalibrationSource source = sourceSelectionDialog.open();
		if (source != null) {
			for (TableItem tableItem : obsParameterTable.getTable().getItems()) {
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
		try {
			sbTemplateDataModel = new SBTemplateDataModel();
		} catch (Exception e) {
            ExceptionDetailsErrorDialog.openError(getShell(),
                    "ERROR",
                    "Could not create data model",
                    e);
			logger.log(Level.WARNING, "Could retrieve templates", e);
		}
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
