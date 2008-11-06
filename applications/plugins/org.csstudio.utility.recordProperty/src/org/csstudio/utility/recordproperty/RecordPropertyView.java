package org.csstudio.utility.recordproperty;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDropTarget;
import org.csstudio.utility.recordproperty.rdb.data.RecordPropertyGetRDB;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * RecordPropertyView creates view for the plugin.
 * 
 * @author Rok Povsic
 */
public class RecordPropertyView extends ViewPart {
	
	public static final String ID = "org.csstudio.utility.recordproperty";
	
	static final int COL_PV = 0;
	static final int COL_RDB = 1;
	static final int COL_VAL = 2;
	static final int COL_RMI = 3;
	
	private TableViewer tableViewer;
		
	/**
	 * Column names.
	 */
	static final String[] COLUMN_NAMES =
		new String[] { "pv", "rdb", "val", "rmi" };
	
	/**
	 * Data, that is filled in table.
	 */
	public RecordPropertyEntry[] entries;
	
	private ComboViewer cv;
	
	public static String recordName;
	
	public String ltext;
	public boolean isRunning = false;
	private Label label;
	
	/**
	 * The Show Property View action.
	 */
	private Action _showPropertyViewAction;
	
	/**
	 * The ID of the property view.
	 */
	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";
	
	/** Instance number, used to create a unique ID
     *  @see #createNewInstance()
     */
    private static int instance = 0;
	
	public RecordPropertyView() {
	}
	
	/**
	 * Creates a GUI.
	 */
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		/**
		 * Text, button, label are in this group
		 */
		Group g = new Group(parent, SWT.NONE);
		g.setText(Messages.RecordPropertyView_RECORD);
		
		g.setLayout(new FillLayout(SWT.HORIZONTAL));
		
        cv = new ComboViewer(g, SWT.BORDER);
        cv.getCombo().setToolTipText("test");
        cv.getCombo().setText(Messages.RecordPropertyView_TYPE_HERE);
        
		Button button = new Button(g, SWT.PUSH);
		button.setText(Messages.RecordPropertyView_GET_DATA);
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
				// ignore (not called by buttons)
			}
			
			public void widgetSelected(final SelectionEvent e) {
				// Gets text (a record name) from Combo Viewer.
				recordName = cv.getCombo().getText();
				cv.add(cv.getCombo().getText());
				fillTableWithData(recordName);
			}
			
		});
				
		createTableViewer(parent);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new RecordPropertyContentProvider());
		tableViewer.setLabelProvider(new RecordPropertyLabelProvider());
		
		// Creates a context menu
		initializeContextMenu();
		makeActions();
		
		getSite().setSelectionProvider(tableViewer);
		
		label = new Label(g, SWT.CENTER);
		
        // Enable 'Drop'
        new ProcessVariableDropTarget(cv.getControl())
        {
            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {
                fillTableWithData(name.getName());
            }
        };
		
		///////////////////// - copied from Probe
        // In principle, this could allow 'dragging' of PV names.
        // In practice, however, any mouse click & drag only selects
        // portions of the text and moves the cursor. It won't
        // initiate a 'drag'.
        // Maybe it works on some OS? Maybe there's another magic
        // modifier key to force a 'drag'?
        new ProcessVariableDragSource(cv.getControl(), cv);
	}
	
	/**
	 * Creates a table.
	 * @param parent
	 */
	private void createTableViewer(final Composite parent) {
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setColumnProperties(COLUMN_NAMES);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		addColumn(table, COL_PV,  Messages.RecordPropertyView_PV_COLUMN, 180);
		addColumn(table, COL_RDB, Messages.RecordPropertyView_RDB_COLUMN, 150);
		addColumn(table, COL_VAL, Messages.RecordPropertyView_VAL_COLUMN, 150);
		addColumn(table, COL_RMI, Messages.RecordPropertyView_RMI_COLUMN, 150);
		
		CellEditor[] editors = new CellEditor[4];
		editors[1] = new TextCellEditor(table);
		tableViewer.setCellEditors(editors);
	}
	
	/**
	 * Adds a column into a table.
	 * @param table a table
	 * @param index number of column
	 * @param text name of column
	 * @param width width of column
	 */
	private void addColumn(final Table table, final int index, final String text, final int width) {
		final TableColumn column = new TableColumn(table, SWT.LEFT, index);
		column.setText(text);
		column.setWidth(width);
	}
		
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
		
	private void fillContextMenu(final IMenuManager menu) {
		// adds a separator after which contributed actions from other plug-ins
		// will be displayed
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Adds a context menu to the tree view.
	 */
	private void initializeContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		
		// add menu items to the context menu when it is about to show
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				RecordPropertyView.this.fillContextMenu(manager);
			}
		});
		
		// add the context menu to the table viewer
		Menu contextMenu = menuMgr.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(contextMenu);
		
		// register the context menu for extension by other plug-ins
		getSite().registerContextMenu(menuMgr, tableViewer);
	}

	private void makeActions() {
		_showPropertyViewAction = new Action() {
			@Override
			public void run() {
				try {
					getSite().getPage().showView(PROPERTY_VIEW_ID);
				} catch (PartInitException e) {
					MessageDialog.openError(getSite().getShell(), "Record Property View",
							e.getMessage());
				}
			}
		};
		_showPropertyViewAction.setText("Properties");
		_showPropertyViewAction.setToolTipText("Show property view");
		
		IViewRegistry viewRegistry = getSite().getWorkbenchWindow().getWorkbench().getViewRegistry();
		IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
		_showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());
	}
	
	/**
	 * Getter for a record name.
	 * @return the record name
	 */
	public static String getRecordName() {
		return recordName;
	}
	
	/**
	 * Fills table with data, when user types record name into Combo Viewer
	 * OR when user opens it from another plugin.
	 * @param pv_name the name of pv (record)
	 */
	private void fillTableWithData(String pv_name) {
		recordName = pv_name;
		
		// Deletes all spaces before and after real text.
		recordName = recordName.trim();
		
		label.setText(Messages.RecordPropertyView_PLEASE_WAIT);
		label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		
		// New thread (Job) is created, so GUI does not freeze
		// when it is collecting data.
		Job j = new Job("") {
			
			protected IStatus run(IProgressMonitor monitor) {

				// Variable entries gets data, but does not print it in
				// GUI yet, there would be Invalid thread access.
				RecordPropertyGetRDB rdb = new RecordPropertyGetRDB();
				entries = rdb.getData(recordName);

				// asyncExec makes possible that GUI-changing can be done
				// in separate thread than GUI.
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						// Here data is printed in GUI.
						tableViewer.setInput(entries);
						
						label.setText(Messages.RecordPropertyView_DONE);
						label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
						
					}		
				});
										
				return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
			}
		};
		
		j.setPriority(Job.SHORT);
		j.schedule();
		
		// sets ComboViewer text to record name
		cv.getCombo().setText(recordName);
	}
	
	/**
	 * Used when user opens a Record Property in context menu of some other plugin.
	 * @param pv_name the name of pv (record)
	 * @return
	 */
	public static boolean activateWithPV(IProcessVariable pv_name) {
		try
        {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            
            RecordPropertyView rpv = (RecordPropertyView) page.showView(ID, createNewInstance(),
                                              IWorkbenchPage.VIEW_ACTIVATE);
            
            rpv.fillTableWithData(pv_name.getName());
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
	}
	
    /** @return a new view instance */
    public static String createNewInstance()
    {
        ++instance;
        return Integer.toString(instance);
    }
}