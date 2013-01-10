package org.csstudio.utility.recordproperty;


import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import com.sun.istack.internal.Nullable;

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
    static final String[] COLUMN_NAMES = new String[] {"pv", "rdb", "val", "rmi"};
    
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
        // Constructor
    }
    
    /**
     * Creates a GUI.
     */
    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        
        /**
         * Text, button, label are in this group
         */
        final Group g = new Group(parent, SWT.NONE);
        g.setText(Messages.RecordPropertyView_RECORD);
        
        g.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        cv = new ComboViewer(g, SWT.BORDER);
        cv.getCombo().setToolTipText("test");
        cv.getCombo().setText(Messages.RecordPropertyView_TYPE_HERE);
        
        final Button button = new Button(g, SWT.PUSH);
        button.setText(Messages.RecordPropertyView_GET_DATA);
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                // ignore (not called by buttons)
            }
            
            @Override
            public void widgetSelected(final SelectionEvent e) {
                // Gets text (a record name) from Combo Viewer.
                recordName = cv.getCombo().getText();
                cv.add(cv.getCombo().getText());
                fillTableWithData(recordName);
            }
            
        });
        
        Text filterText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        filterText.setMessage("Filter");
        filterText.setText("");
        
        new ViewerFilter() {
            
            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                // TODO Auto-generated method stub
                return false;
            }
        };
        RecordPropertyEntryViewerSorter sorter = new RecordPropertyEntryViewerSorter();
        createTableViewer(parent, sorter);
        tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer.setContentProvider(new RecordPropertyContentProvider());
        tableViewer.setLabelProvider(new RecordPropertyLabelProvider());
        tableViewer.setSorter(sorter);
        RecordPropertyEntryViewerFilter recordPropertyEntryViewerFilter = new RecordPropertyEntryViewerFilter(filterText);
        tableViewer.addFilter(recordPropertyEntryViewerFilter);
        
        // Creates a context menu
        initializeContextMenu();
        makeActions();
        
        getSite().setSelectionProvider(tableViewer);
        
        label = new Label(g, SWT.CENTER);
        
        // Enable 'Drop'
		new ControlSystemDropTarget(cv.getControl(), ProcessVariable[].class,
				String.class) {
			@Override
			public void handleDrop(final Object item) {
				if (item instanceof ProcessVariable[]) {
					final ProcessVariable[] pvs = (ProcessVariable[]) item;
					fillTableWithData(pvs[0].getName());
				}
			}
		};
        
    }
    
    /**
     * Creates a table.
     * @param parent
     * @param sorter 
     */
    private void createTableViewer(final Composite parent, RecordPropertyEntryViewerSorter sorter) {
        tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION);
        tableViewer.setColumnProperties(COLUMN_NAMES);
        
        final Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        addColumn(table, sorter, COL_PV, Messages.RecordPropertyView_PV_COLUMN, 180);
        addColumn(table, sorter, COL_RDB, Messages.RecordPropertyView_RDB_COLUMN, 150);
        addColumn(table, sorter, COL_VAL, Messages.RecordPropertyView_VAL_COLUMN, 150);
        addColumn(table, sorter, COL_RMI, Messages.RecordPropertyView_RMI_COLUMN, 150);
        
        final CellEditor[] editors = new CellEditor[4];
        editors[1] = new TextCellEditor(table);
        tableViewer.setCellEditors(editors);
    }
    
    /**
     * Adds a column into a table.
     * @param table a table
     * @param sorter 
     * @param index number of column
     * @param text name of column
     * @param width width of column
     */
    private void addColumn(final Table table,
                           RecordPropertyEntryViewerSorter sorter,
                           final int index,
                           final String text,
                           final int width) {
        final TableColumn column = new TableColumn(table, SWT.LEFT, index);
        column.setText(text);
        column.setWidth(width);
        column.addSelectionListener(new SortSelectionListener(sorter, index));
    }
    
    @Override
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
        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        
        // add menu items to the context menu when it is about to show
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                RecordPropertyView.this.fillContextMenu(manager);
            }
        });
        
        // add the context menu to the table viewer
        final Menu contextMenu = menuMgr.createContextMenu(tableViewer.getTable());
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
                } catch (final PartInitException e) {
                    MessageDialog.openError(getSite().getShell(),
                                            "Record Property View",
                                            e.getMessage());
                }
            }
        };
        _showPropertyViewAction.setText("Properties");
        _showPropertyViewAction.setToolTipText("Show property view");
        
        final IViewRegistry viewRegistry = getSite().getWorkbenchWindow().getWorkbench()
                .getViewRegistry();
        final IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
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
    private void fillTableWithData(final String pv_name) {
        recordName = pv_name;
        
        // Deletes all spaces before and after real text.
        recordName = recordName.trim();
        
        label.setText(Messages.RecordPropertyView_PLEASE_WAIT);
        label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        
        // New thread (Job) is created, so GUI does not freeze
        // when it is collecting data.
        final Job j = new Job("") {
            
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                
                // Variable entries gets data, but does not print it in
                // GUI yet, there would be Invalid thread access.
                final RecordPropertyGetRDB rdb = new RecordPropertyGetRDB();
                entries = rdb.getData(recordName);
                
                // asyncExec makes possible that GUI-changing can be done
                // in separate thread than GUI.
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
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
    public static boolean activateWithPV(final ProcessVariable pv_name) {
        try {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            
            final RecordPropertyView rpv = (RecordPropertyView) page
                    .showView(ID, createNewInstance(), IWorkbenchPage.VIEW_ACTIVATE);
            
            rpv.fillTableWithData(pv_name.getName());
            
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /** @return a new view instance */
    public static String createNewInstance() {
        ++instance;
        return Integer.toString(instance);
    }
    
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 26.05.2011
     */
    private final class RecordPropertyEntryViewerSorter extends ViewerSorter {
        private int _state = -1;
        private boolean _asc = true;
        private Viewer _viewer;
        
        /**
         * Constructor.
         */
        public RecordPropertyEntryViewerSorter() {
            // Constructor
        }
        
        @Override
        public int compare(@Nonnull Viewer viewer, @Nullable Object e1, @Nullable Object e2) {
            _viewer = viewer;
            if(e1 instanceof RecordPropertyEntry && e2 instanceof RecordPropertyEntry) {
                RecordPropertyEntry node1 = (RecordPropertyEntry) e1;
                RecordPropertyEntry node2 = (RecordPropertyEntry) e2;
                return compareSearchNodeDBO(node1, node2);
            }
            return 0;
        }
        
        /**
         */
        private int compareSearchNodeDBO(@Nonnull RecordPropertyEntry node1,
                                         @Nonnull RecordPropertyEntry node2) {
            int asc = 1;
            if(_asc) {
                asc = -1;
            }
            switch (_state) {
                case COL_PV:
                    return compareString(node1.getPvName(), node2.getPvName(), asc);
                case COL_RDB:
                    return compareString(node1.getRdb(), node2.getRdb(), asc);
                case COL_VAL:
                    return compareString(node1.getVal(), node2.getVal(), asc);
                case COL_RMI:
                    return compareString(node1.getRmi(), node2.getRmi(), asc);
                default:
            }
            return 0;
        }
        
        private int compareString(@CheckForNull String string1,
                                  @CheckForNull String string2,
                                  int asc) {
            
            if(string1 == null && string2 == null) {
                return 0;
            }
            if(string1 == null) {
                return asc;
            }
            if(string2 == null) {
                return -asc;
            }
            return asc * string1.compareTo(string2);
        }
        
        public void setState(int state) {
            if(_state == state) {
                _asc = !_asc;
            } else {
                _asc = true;
                _state = state;
            }
            if(_viewer != null) {
                _viewer.refresh();
            }
        }
    }
    
    /**
     * 
     * Sorter for each column of the Table.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 23.09.2010
     */
    private final class SortSelectionListener implements SelectionListener {
        private final RecordPropertyEntryViewerSorter _sorter;
        private final int _state;
        
        SortSelectionListener(@Nonnull RecordPropertyEntryViewerSorter sorter, int state) {
            _sorter = sorter;
            _state = state;
        }
        
        @Override
        public void widgetDefaultSelected(@Nullable SelectionEvent e) {
            setState();
        }
        
        @Override
        public void widgetSelected(@Nullable SelectionEvent e) {
            setState();
        }
        
        private void setState() {
            _sorter.setState(_state);
        }
    }
    
    final class RecordPropertyEntryViewerFilter extends ViewerFilter {
        
        private final Text _text;
        
        /**
         * Constructor.
         */
        public RecordPropertyEntryViewerFilter(Text text) {
            _text = text;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            boolean show = false;
            if(element instanceof RecordPropertyEntry) {
                RecordPropertyEntry entry = (RecordPropertyEntry) element;
                String text = _text.getText();
                show = entry.getPvName().contains(text) || entry.getRdb().contains(text)
                        || entry.getVal().contains(text) || entry.getRmi().contains(text);
            }
            return show;
        }
        
    }
    
}