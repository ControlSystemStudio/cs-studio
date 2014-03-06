package org.csstudio.utility.caSnooperUi.ui.ChangeView;

import java.util.ArrayList;

import org.csstudio.utility.caSnooperUi.parser.ChannelCollector;
import org.csstudio.utility.caSnooperUi.parser.ChannelStructure;
import org.csstudio.utility.caSnooperUi.parser.SnooperStringParser;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * view part of the caSnooper
 * 
 * @author rkosir
 */
public class SnooperView extends ViewPart{

    private static final Logger LOG = LoggerFactory.getLogger(SnooperView.class);
    
	/**
	 * The table viewer
	 */
	private TableViewer tableViewer;
	
	/**
	 * Column index for the id column.
	 */
	static final int COL_ID = 0;
	
	/**
	 * Column index for the host column.
	 */
	static final int COL_HOST = 1;
	
	/**
	 * Column index for the PV column.
	 */
	static final int COL_PV = 2;
	
	/**
	 * Column index for the PV frequency column.
	 */
	static final int COL_FREQUENCY = 3;
	
	/**
	 * The column properties which are used to identify the columns in cell
	 * modifiers.
	 */
	static final String[] COLUMN_PROPERTIES =
		new String[] { "id", "host", "pv", "frequency"};
	
	/**
	 * Text field for channel statistics
	 */
	private Text text; 
	
	/**
	 * Instance of MessageCollector
	 */
	private final ChannelCollector msg = new ChannelCollector();
	
	/**
	 * The Show Property View action.
	 */
	private Action _showPropertyViewAction;
	
	/**
	 * The ID of the property view.
	 */
	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	/**
	 * The ID of the snooper view
	 */
	public static final String ID = "org.csstudio.utility.caSnooper.ui.ChangeView.SnooperView";
	
	/**
	 * The snooper status field
	 */
	private boolean snooperActive = false;
	
	/**
	 * ArrayList of Snooping intervals
	 */
	ArrayList<String> snoopIntervals = new ArrayList<String>();

	/**
	 * Sorting direction is remembered in lastSort
	 */
	protected String lastSort;

	@Override
    public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		final Composite iocBar = new Composite(parent, SWT.NONE);
		iocBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		iocBar.setLayout(new GridLayout(3, false));
		
	    Label label = new Label(iocBar, SWT.NULL);
	    label.setText("Interval: ");

	    snoopIntervals.add("15s");
	    snoopIntervals.add("30s");
	    snoopIntervals.add("45s");
	    snoopIntervals.add("60s");
	    snoopIntervals.add("10min");
	    
	    final Combo combo = new Combo(iocBar,SWT.DROP_DOWN|SWT.READ_ONLY);
	    for(int i=0;i<snoopIntervals.size();i++)
	    	combo.add((String)snoopIntervals.get(i));
	    combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	    
		final Canvas canvas = new Canvas(iocBar, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.widthHint = 50;
		gridData.heightHint = 20;
		gridData.verticalAlignment = SWT.TOP;
		canvas.addPaintListener(new PaintListener() {
		      @Override
            public void paintControl(PaintEvent e) {
		    	  if(snooperActive)
		    		  e.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		    	  else
		    		  e.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));  
		    	  e.gc.fillOval(20, 0, 17, 17);
		      }
		});
		canvas.setLayoutData(gridData);
		
		/**
		 * Stop CA snooper on window closing
		 */
		canvas.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				dispose();
				
			}
		});
		
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	    gridData.horizontalSpan = 2;
	    
		Button startSnooping = new Button(iocBar, SWT.PUSH);
		startSnooping.setText("Start snooping");
		
		Button stopSnooping = new Button(iocBar, SWT.PUSH);
		stopSnooping.setText("Stop snooping");
	    
		text = new Text(iocBar, SWT.MULTI | SWT.BORDER | SWT.WRAP |SWT.READ_ONLY );
		
		GridData gridData2 = new GridData(GridData.FILL_BOTH);
		gridData2.horizontalSpan = 3;
		gridData2.verticalSpan = 3;
		gridData2.heightHint = 60;
		text.setLayoutData(gridData2);
		
		startSnooping.addSelectionListener(new SelectionListener() {

			@Override
            public void widgetDefaultSelected(final SelectionEvent e) {
				// ignore (not called by buttons)
			}

			@Override
            public void widgetSelected(final SelectionEvent e) {
				//start the snooping with selected time period
				snooperActive = true;
				switch (combo.getSelectionIndex()){
				case 0:
					msg.start(15);
					break;
				case 1:
					msg.start(30);
					break;
				case 2: 
					msg.start(45);
					break;
				case 3:
					msg.start(60);
					break;
				case 4:
					msg.start(600);
					break;
				default:
					text.setText("Invalid combo box selection");
					snooperActive = false;
				}
			    canvas.redraw();
			    Thread thread = new Thread(new DataTimer());
				thread.start();
			}
		});
		
		stopSnooping.addSelectionListener(new SelectionListener() {
			
			@Override
            public void widgetDefaultSelected(final SelectionEvent e) {
				// ignore (not called by buttons)
			}

			@Override
            public void widgetSelected(final SelectionEvent e) {
				//stop the snooping and remove listener
				if(snooperActive){
					msg.stop();
					snooperActive = false;
					canvas.redraw();
				}
			}
		});
		
		// create the table viewer
		createTableViewer(parent);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		
		//initialize menus and create actions
		initializeContextMenu();
		makeActions();
		
		// the table viewer is the selection provider for the site
		getSite().setSelectionProvider(tableViewer);
	}

	/**
	 * Creates the table viewer.
	 * 
	 * @param parent the parent composite.
	 */
	private void createTableViewer(final Composite parent) {
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setColumnProperties(COLUMN_PROPERTIES);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		addColumn(table, COL_ID, "Id", 50);
		addColumn(table, COL_HOST, "Hostname", 250);
		addColumn(table, COL_PV, "PV", 250);
		addColumn(table, COL_FREQUENCY, "Frequency", 100);
		
		CellEditor[] editors = new CellEditor[4];
		editors[1] = new TextCellEditor(table);
		tableViewer.setCellEditors(editors);
	}
	
	
	/**
	 * Adds a column to the given table.
	 * 
	 * @param table the table.
	 * @param index the column index.
	 * @param text the column text.
	 * @param width the width of the column.
	 */
	private void addColumn(final Table table, final int index, final String text, final int width) {
		TableColumn column = new TableColumn(table, SWT.LEFT, index);
		column.setText(text);
		column.setWidth(width);
		column.addSelectionListener(new SelectionAdapter() {
			//so the sort order is remembered this is local variable
			private boolean sort;
	        @Override
            public void widgetSelected(SelectionEvent e) {
	        	sort = !sort;
	        	tableViewer.setSorter(
	        			new SnooperSorter(index,sort));
	        	}
			});
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
			@Override
            public void menuAboutToShow(final IMenuManager manager) {
				SnooperView.this.fillContextMenu(manager);
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
					MessageDialog.openError(getSite().getShell(), "Snooper view",
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
	
	public void setMessage(final Object param){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
            public void run() {
				if(!snooperActive){
					if(param.equals("")){
						text.setText("No snoops received");
					}
					else{
						Object[] tmp;
						SnooperStringParser p = new SnooperStringParser();
						if(param instanceof String){
							tmp = p.unparse((String)param);
							tableViewer.setInput((ArrayList<ChannelStructure>)tmp[1]);
							text.setText((String)tmp[0]);
						}
						else
							LOG.error("Incorrect data format received!"); 
					}
				}
				else
					text.setText("Snooper must be stopped in order to load external data!");					
			}
		});
	}
	
	/**
	 * Retrieves the data from snooper and prints it in the table
	 * 
	 */
	public void processData(){
		Display.getDefault().asyncExec(new Runnable() {
			@Override
            public void run() {
				final ArrayList<ChannelStructure> entries = msg.getSnoops();
				if(entries!=null){
					tableViewer.setInput(entries);
					text.setText(msg.getStatistics(entries));
				}else
					text.setText("No broadcasts were received");
		}
	});
		Thread t = new Thread(new DataTimer());
		t.start();
	}
	
	@Override
    public void setFocus() {
		tableViewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		if(snooperActive){
			msg.stop();
			snooperActive = false;
		}
		super.dispose();
	}
	
	/**
	 * Thread for checking if data is ready for processing
	 * 
	 */
	class DataTimer implements Runnable{

		@Override
        public void run() {
			while(!msg.isReady() && snooperActive){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(snooperActive){
				processData();
			}
		}
	}
}
