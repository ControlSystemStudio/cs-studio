package org.csstudio.trends.databrowser.configview;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDropTarget;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableOrArchiveDataSourceDropTarget;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.configview.PVTableHelper.Column;
import org.csstudio.trends.databrowser.model.FormulaModelItem;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.model.formula_gui.FormulaDialog;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
import org.csstudio.util.swt.RGBCellEditor;
import org.csstudio.util.swt.ScrolledContainerHelper;
import org.csstudio.util.time.swt.StartEndDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

/** An Eclipse ViewPart for configuring the DataBrowser.
 *  @author Kay Kasemir
 */
public class ConfigView extends PlotAwareView
{
    private static final String FORM_WEIGHT_TAG = "pv_form_weights"; //$NON-NLS-1$
    public static final String ID = ConfigView.class.getName();
    private static final String colon = ":"; //$NON-NLS-1$

    /** Use a combo box for the axis cell?
     *  Seems natural, but:
     *  - unclear how to best populate/update the available items
     *  - looks really poor under Mac OSX, doesn't fit,
     *    and requires to click + select item + click outside to send value..
     *  -> not for now.
     */
    public static final boolean use_axis_combobox = false;
    
    /** Sash that holds the GUI Elements for the "PV" Tab */
    private SashForm pv_form;
    
    /** Initial sizes, possibly updated in init() from memento */
    private int pv_form_weights[] = new int[] { 60, 40 };
    
    // Sash Section for PV Table
    private TableViewer pv_table_viewer;
    private PVTableLazyContentProvider table_content;
    private PVTableLabelProvider label_provider;
    private Action add_pv_action, add_formula_action, delete_pv_action;
    private Action archive_up_action, archive_down_action;
    private Action delete_archive_action;

    // Sash Section for ArchiveDataSources
    private TableViewer archive_table_viewer;

    // GUI Elements for the "Live Data" Tab
    private Text scan_period_text;
    private Text update_period_text;
    private Text ring_size_text;
    private Label help;

    // GUI Elements for the "Time Config" Tab
    private Text start_specification;
    private Text end_specification;
    private Label start_end_info;
    
    private ModelListener model_listener = new ModelListener()
    {
        // Almost all the same:
        // Whatever changed, we need to display the current model info.
        public void timeSpecificationsChanged()
        {   entriesChanged(); }
        
        public void timeRangeChanged()
        {   // Ignore changes to 'current' time range from scroll
        }

        public void periodsChanged()
        {   entriesChanged(); }

        public void entriesChanged()
        {
            Model model = getModel();
            updateModel(model, model);
        }

        public void entryAdded(IModelItem new_item)
        {   entriesChanged(); }

        public void entryConfigChanged(IModelItem item)
        {   entriesChanged(); }

        public void entryLookChanged(IModelItem item)
        {   /* not configurable */ }
        
        public void entryArchivesChanged(IModelItem item)
        {   entriesChanged(); }

        public void entryRemoved(IModelItem removed_item)
        {   entriesChanged(); }

    };
    /** The 'archive' list of the PV detail */
    private Composite archive_box;
    /** The 'formula' info of the PV detail */
    private Composite formula_box;
    /** Formula display in <code>formula_box</code> */
    private Text formula_txt;
    
    /** Try to restore some things from memento */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        
        if (memento != null)
            for (int i=0; i<pv_form_weights.length; ++i)
            {
                final Integer val = memento.getInteger(FORM_WEIGHT_TAG + i);
                if (val != null)
                    pv_form_weights[i] = val.intValue();
            }
    }

    /** Save the display state. */
    @Override
    public void saveState(IMemento memento)
    {
        pv_form_weights = pv_form.getWeights();
        for (int i=0; i<pv_form_weights.length; ++i)
            memento.putInteger(FORM_WEIGHT_TAG + i, pv_form_weights[i]);
    }
        
    /** @return Returns the table viewer used to display the PV entries. */
    public TableViewer getPVTableViewer()
    {   return pv_table_viewer; }

    
    /** @return Returns the table viewer used to display the Archive entries. */
    public TableViewer getArchiveTableViewer()
    {   return archive_table_viewer; }
    
    /** @return Returns the currently selected chart entries or <code>null</code>. */
    public IModelItem[] getSelectedModelEntries()
    {
        IStructuredSelection sel = (IStructuredSelection)pv_table_viewer.getSelection();
        if (sel.isEmpty())
            return null;
        Object o[] = sel.toArray();
        IModelItem items[] = new IModelItem[o.length];
        for (int i=0; i<o.length; ++i)
            items[i] = (IModelItem)o[i];
        return items;
    }
        
    /** @return Returns the currently selected chart entries or <code>null</code>. */
    public IArchiveDataSource[] getSelectedArchiveEntries()
    {
        IStructuredSelection sel =
            (IStructuredSelection)archive_table_viewer.getSelection();
        if (sel.isEmpty())
            return null;
        Object o[] = sel.toArray();
        IArchiveDataSource archives[] = new IArchiveDataSource[o.length];
        for (int i=0; i<o.length; ++i)
            archives[i] = (IArchiveDataSource)o[i];
        return archives;
    }
    
    /** Create the GUI elements. */
    @Override
    public void createPartControl(Composite parent)
    {
        // Create the GUI Elements
        Composite scroll = ScrolledContainerHelper.create(parent, 200, 200);
        scroll.setLayout(new FillLayout());
        // Quirk: Don't set a layout on the TabFolder nor the TabItem.
        // Do set a layout on the Composite that's inside the TabItem,
        // which has the TabFolder as a parent.
        TabFolder tabs = new TabFolder(scroll, SWT.BORDER);
        createPVTab(tabs);
        createLiveTab(tabs);
        createTimeTab(tabs);
        
        // Update the archive table for the selected PVs
        pv_table_viewer.addPostSelectionChangedListener(
                        new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {   updateLowerSash();  }
        });
             
        // Create actions, hook them to menues
        add_pv_action = new AddPVAction(this);
        add_formula_action = new AddFormulaAction(this);
        delete_pv_action = new DeletePVAction(this);
        archive_up_action = new ArchiveUpAction(this);
        archive_down_action = new ArchiveDownAction(this);
        delete_archive_action = new DeleteArchiveAction(this);
        makePVContextMenu(getSite());
        makeArchiveContextMenu(getSite());

        // Allow dragging PVs from the PV table
        new ProcessVariableDragSource(pv_table_viewer.getTable(),
                        pv_table_viewer);
        // Allow dragging Archive info from the archive table
        new ArchiveDataSourceDragSource(archive_table_viewer.getTable(),
                        archive_table_viewer);
        // One can drop PVs with or w/o archive info into the PV table
        new ProcessVariableOrArchiveDataSourceDropTarget(
                        pv_table_viewer.getTable())
        {
            @Override
            public void handleDrop(IProcessVariable name,
                            IArchiveDataSource archive, DropTargetEvent event)
            {   // Add PV with archive info
                IModelItem item = addPV(name.getName());
                if (item != null)
                    addArchive(item, archive);
            }

            @Override
            public void handleDrop(IProcessVariable name,
                                   DropTargetEvent event)
            {   // Add a new PV, no archive info
                addPV(name.getName());
            }

            @Override
            public void handleDrop(IArchiveDataSource archive,
                            DropTargetEvent event)
            {   // If archive info was dropped on PV, add to only that PV
                if (event.item != null && event.item instanceof TableItem)
                {
                    Object data = ((TableItem) event.item).getData();
                    if (data != null && data instanceof IModelItem)
                    {
                        addArchive((IModelItem) data, archive);
                        return;
                    }
                }
                // else: Add to all PVs
                addArchive(null, archive);
            }
        };
        // .. or drop archive info to the archive sub-table
        new ArchiveDataSourceDropTarget(archive_table_viewer.getTable())
        {
            @Override
            public void handleDrop(IArchiveDataSource archive, 
                            DropTargetEvent event)
            {
                // Add to selected entries, if there are any
                IModelItem items[] = getSelectedModelEntries();
                if (items.length < 1)
                    addArchive(null, archive); // add to all items
                else
                    for (IModelItem item : items)
                        addArchive(item, archive);
            }
        };

        super.createPartControl(parent);
     }
    
    /** Create one tab of the TabFolder GUI. */
    private void createPVTab(TabFolder tabs)
    {
        TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.PVs);
        tab.setToolTipText(Messages.ConfigPV);

        // Tab contains single SashForm with these children:
        // Upper sash, "PVs:"
        // Table with list of PVs
        //
        // Lower Sashm "Archives:" or formula info
        // Table with list of archive servers for selected PV,
        // or stuff about the formula, depending on the type
        // of selected item in the PVs list.
        pv_form = new SashForm(tabs, SWT.VERTICAL | SWT.BORDER);
        pv_form.setLayout(new FillLayout());
        tab.setControl(pv_form);

        createPVTabListItem(pv_form);
        createPVTabInfoItem(pv_form);
        
        // Initial sizes of PV list vs. archive detail
        pv_form.setWeights(pv_form_weights);

    }
    
    /** SashForm item for PVs */
    private void createPVTabListItem(SashForm pv_form)
    {
        Composite box = new Composite(pv_form, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        box.setLayout(layout);

        Label l = new Label(box, 0);
        l.setText(Messages.PVs + colon);
        GridData gd = new GridData();
        l.setLayoutData(gd);
        
        Table table = new Table(box,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION
                | SWT.VIRTUAL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        
        final boolean show_request_types = Preferences.getShowRequestTypes();
        final Column[] all_columns = PVTableHelper.Column.values();
        final Column[] columns = new Column[show_request_types
                                            ? all_columns.length
                                            : all_columns.length - 1];
        int i = 0;
        for (PVTableHelper.Column col : all_columns)
        {
            if (col == PVTableHelper.Column.REQUEST_TYPE
                &&   show_request_types == false)
                continue;
            columns[i++] = col;
            AutoSizeColumn.make(table, col.getTitle(), col.getMinSize(),
                                col.getWeight(), col.isCentered());
        }
        // Configure table to auto-size the columns
        new AutoSizeControlListener(box, table);
        
        pv_table_viewer = new TableViewer(table);
        // Enable hashmap for resolving 'PVListEntry' to associated SWT widget.
        pv_table_viewer.setUseHashlookup(true);
        label_provider = new PVTableLabelProvider();
        pv_table_viewer.setLabelProvider(label_provider);
        table_content = new PVTableLazyContentProvider();
        pv_table_viewer.setContentProvider(table_content);
        
        // Allow editing
        CellEditor editors[] = new CellEditor[columns.length];
        editors[PVTableHelper.Column.VISIBLE.ordinal()] = new CheckboxCellEditor(table);
        editors[PVTableHelper.Column.NAME.ordinal()] = new TextCellEditor(table);
        editors[PVTableHelper.Column.MIN.ordinal()] = new TextCellEditor(table);
        editors[PVTableHelper.Column.MAX.ordinal()] = new TextCellEditor(table);
        if (use_axis_combobox)
        {   // Allow axes 0...3 ?
            String axis_items[] = new String[4];
            for (i=0; i<axis_items.length; ++i)
                axis_items[i] = Integer.toString(i);
            editors[PVTableHelper.Column.AXIS.ordinal()] = new ComboBoxCellEditor(table, axis_items);
        }
        else
            editors[PVTableHelper.Column.AXIS.ordinal()] = new TextCellEditor(table);
        editors[PVTableHelper.Column.COLOR.ordinal()] = new RGBCellEditor(table);
        editors[PVTableHelper.Column.LINE_WIDTH.ordinal()] = new TextCellEditor(table);
        editors[PVTableHelper.Column.LOG_SCALE.ordinal()] = new CheckboxCellEditor(table);
        editors[PVTableHelper.Column.TRACE_TYPE.ordinal()] =
            new ComboBoxCellEditor(table, TraceType.getTypeStrings(), SWT.READ_ONLY);
        editors[PVTableHelper.Column.AUTO_SCALE.ordinal()] = new CheckboxCellEditor(table);
        if (show_request_types)
        {
            editors[PVTableHelper.Column.REQUEST_TYPE.ordinal()] =
                new ComboBoxCellEditor(table, IPVModelItem.RequestType.getTypeStrings(), SWT.READ_ONLY);
        }
        String titles[] = new String[columns.length];
        for (i=0; i<columns.length; ++i)
            titles[i] = columns[i].getTitle();
        pv_table_viewer.setColumnProperties(titles);
        pv_table_viewer.setCellEditors(editors);
        pv_table_viewer.setCellModifier(new PVTableCellModifier(this));
    }
    
    /** SashForm item for archives or formulas of current PV */
    private void createPVTabInfoItem(SashForm pv_form)
    {
        final Composite box = new Composite(pv_form, 0);
        // Both the archive_box and the formula_box
        // are configured to occupy the full space,
        // with the idea that only one of them is visible at the same time.
        box.setLayout(new FormLayout());
    
        // Archive Box
        archive_box = new Composite(box, 0);
        FormData fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        archive_box.setLayoutData(fd);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        archive_box.setLayout(layout);
        
        Label l = new Label(archive_box, 0);
        l.setText(Messages.ArchsForPVs + colon);
        GridData gd = new GridData();
        l.setLayoutData(gd);
        
        Table table = new Table(archive_box,
                        SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        AutoSizeColumn.make(table, Messages.Archive, 80, 60);
        AutoSizeColumn.make(table, Messages.Key,   50,  10);
        AutoSizeColumn.make(table, Messages.URL,  200, 100);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(box, table);
        // Attach TableViewer
        archive_table_viewer = new TableViewer(table);
        archive_table_viewer.setLabelProvider(new ArchiveDataSourceLabelProvider());
        archive_table_viewer.setContentProvider(new ArrayContentProvider());

        // Formula Box
        formula_box = new Composite(box, 0);
        fd = new FormData();
        fd.left = new FormAttachment(0);
        fd.top = new FormAttachment(0);
        fd.right = new FormAttachment(100);
        fd.bottom = new FormAttachment(100);
        formula_box.setLayoutData(fd);
        
        layout = new GridLayout();
        layout.numColumns = 2;
        formula_box.setLayout(layout);

        // Row:   "Formula: " ___________
        l = new Label(formula_box, 0);
        l.setText(Messages.Formula_Label);
        l.setLayoutData(new GridData());
        
        formula_txt = new Text(formula_box, SWT.READ_ONLY);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        formula_txt.setLayoutData(gd);
        
        Button config_formula = new Button(formula_box, 0);
        config_formula.setText(Messages.Formula_ConfigButton);
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        config_formula.setLayoutData(gd);
        config_formula.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                configureSelectedFormula();
            }
        });
        
        formula_box.setVisible(false);
    }     

    /** Create one tab of the TabFolder GUI. */
    private void createLiveTab(TabFolder tabs)
    {
        TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.LiveConfig);
        tab.setToolTipText(Messages.ConfigPVUpdates);
        Composite parent = new Composite(tabs, 0);
        
        GridLayout gl = new GridLayout();
        gl.numColumns = 2;
        parent.setLayout(gl);
        GridData gd;

        // Row 1
        Label l = new Label(parent, 0);
        l.setText(Messages.LivePVConfig);
        gd = new GridData();
        gd.horizontalSpan = gl.numColumns;
        l.setLayoutData(gd);
 
        // Row 2
        l = new Label(parent, 0);
        l.setText(Messages.ScanPeriodSecs + ":"); //$NON-NLS-1$
        gd = new GridData();
        l.setLayoutData(gd);
        scan_period_text = new Text(parent, SWT.LEFT);
        scan_period_text.setToolTipText(Messages.EnterSecs);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        scan_period_text.setLayoutData(gd);

        // Row 3
        l = new Label(parent, 0);
        l.setText(Messages.UpdatePeriodSecs + colon);
        gd = new GridData();
        l.setLayoutData(gd);
        update_period_text = new Text(parent, SWT.LEFT);
        update_period_text.setToolTipText(Messages.EnterSecs);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        update_period_text.setLayoutData(gd);

        // Row 4
        l = new Label(parent, 0);
        l.setText(Messages.BufferSize + colon);
        gd = new GridData();
        l.setLayoutData(gd);
        ring_size_text = new Text(parent, SWT.LEFT);
        ring_size_text.setToolTipText(Messages.EnterCount);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        ring_size_text.setLayoutData(gd);

        SelectionListener validator = new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {   checkLivePVInputs(); }
        };
        scan_period_text.addSelectionListener(validator);
        update_period_text.addSelectionListener(validator);
        ring_size_text.addSelectionListener(validator);
        
        // Row 5
        help = new Label(parent, SWT.LEFT);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        help.setLayoutData(gd);
        
        tab.setControl(parent);
    }
    
    /** Create one tab of the TabFolder GUI. */
    private void createTimeTab(TabFolder tabs)
    {
        TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.TimeAxisConfig);
        tab.setToolTipText(Messages.TimeAxisConfig_TT);
        Composite parent = new Composite(tabs, 0);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        parent.setLayout(layout);
        GridData gd;
        
        // Start: __________________ [...]
        // End:   __________________ [...]
        // Info
        // [Update Graph]    [Read from Graph]

        // Row 1
        Label l = new Label(parent, 0);
        l.setText(Messages.StartTime);
        gd = new GridData();
        l.setLayoutData(gd);

        start_specification = new Text(parent, SWT.LEFT);
        start_specification.setToolTipText(Messages.StartTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        start_specification.setLayoutData(gd);
        
        Button dlg1 = new Button(parent, SWT.PUSH);
        dlg1.setText(Messages.StartEndDlg);
        gd = new GridData();
        dlg1.setLayoutData(gd);
        
        // Row 2
        l = new Label(parent, 0);
        l.setText(Messages.EndTime);
        gd = new GridData();
        l.setLayoutData(gd);

        end_specification = new Text(parent, SWT.LEFT);
        end_specification.setToolTipText(Messages.EndTime_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end_specification.setLayoutData(gd);
        
        Button dlg2 = new Button(parent, SWT.PUSH);
        dlg2.setText(Messages.StartEndDlg);
        gd = new GridData();
        dlg2.setLayoutData(gd);

        // Row 3
        start_end_info = new Label(parent, 0);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.horizontalSpan =  layout.numColumns;
        start_end_info.setLayoutData(gd);
        
        // Row 4
        Button set_graph_times = new Button(parent, SWT.PUSH);
        set_graph_times.setText(Messages.SetGraphTimes);
        gd = new GridData();
        set_graph_times.setLayoutData(gd);

        Button read_graph_times = new Button(parent, SWT.PUSH);
        read_graph_times.setText(Messages.ReadGraphTimes);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        read_graph_times.setLayoutData(gd);
        
        // Update the model in response to newly entered start/end times
        SelectionListener validator = new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {   checkTimeConfigInputs(); }
        };
        start_specification.addSelectionListener(validator);
        end_specification.addSelectionListener(validator);
                
        // Connect the "..." buttons to a start/end dialog.
        SelectionListener start_stop_dlg = new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                StartEndDialog dlg =
                    new StartEndDialog(ConfigView.this.getSite().getShell(),
                                       start_specification.getText(),
                                       end_specification.getText());
                if (dlg.open() != StartEndDialog.OK)
                    return;
                // Update the text fields
                start_specification.setText(dlg.getStartSpecification());
                end_specification.setText(dlg.getEndSpecification());
                // .. and proceed as if new values were entered there
                checkTimeConfigInputs();
            }
        };
        dlg1.addSelectionListener(start_stop_dlg);
        dlg2.addSelectionListener(start_stop_dlg);
        
        // Update graph to config's start/end time
        set_graph_times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Model model = getModel();
                if (model == null)
                    return;
                try
                {
                    model.setTimeSpecifications(start_specification.getText(),
                                                end_specification.getText());
                }
                catch (Exception ex)
                {
                    start_end_info.setText(Messages.Error + ex.getMessage());
                }
            }
        });
        
        // Read start/end time from graph
        read_graph_times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Model model = getModel();
                if (model == null)
                    return;
                start_specification.setText(model.getStartTime().toString());
                end_specification.setText(model.getEndTime().toString());
            }
        });
        
        tab.setControl(parent);
    }

    /** Set the initial focus. */
    @Override
    public void setFocus()
    {
        pv_table_viewer.getTable().setFocus();
    }
    
    private void makePVContextMenu(IWorkbenchPartSite site)
    {
        // See Plug-ins book p. 285
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(add_pv_action);
        manager.add(add_formula_action);
        manager.add(delete_pv_action);
        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = manager.createContextMenu(pv_table_viewer.getControl());
        pv_table_viewer.getControl().setMenu(menu);
        site.registerContextMenu(manager, pv_table_viewer);
    }

    private void makeArchiveContextMenu(IWorkbenchPartSite site)
    {
        // See Plug-ins book p. 285
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(archive_up_action);
        manager.add(archive_down_action);
        manager.add(delete_archive_action);
        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = manager.createContextMenu(pv_table_viewer.getControl());
        archive_table_viewer.getControl().setMenu(menu);
        site.registerContextMenu(manager, pv_table_viewer);
    }
    
    /** Check the current input to the various 'live PV' tab text fields. */
    private void checkLivePVInputs()
    {
        String help_text = null;
        Model model = getModel();
        if (model == null)
            return;
        double scan_period = model.getScanPeriod();
        double update_period = model.getUpdatePeriod();
        int ring_size = model.getRingSize();
        // no loop, just a construct from which to 'break' on first error.
        while (true)
        {
            try
            {
                scan_period = Double.parseDouble(scan_period_text.getText());
                if (scan_period < Model.MIN_SCAN_RATE)
                {
                    help_text = Messages.ScanPeriodMustBeGt + Model.MIN_SCAN_RATE;
                    break;
                }

                update_period = Double.parseDouble(update_period_text.getText());
                if (update_period < Model.MIN_UPDATE_RATE)
                {
                    help_text = Messages.UpdatePeriodMustBeGt + Model.MIN_UPDATE_RATE;
                    break;
                }

                if (update_period < scan_period)
                {
                    help_text = Messages.UpdateVsScanPeriod;
                    update_period = scan_period;
                }

                ring_size = Integer.parseInt(ring_size_text.getText());
                if (ring_size <= 10)
                {
                    help_text = Messages.RingBufferMinSize;
                    ring_size = 10;
                    break;
                }
            }
            catch (Exception e)
            {
                help_text = e.getMessage();
            }
            break;
        }
        if (help_text != null)
        {
            help.setText(help_text);
            return;
        }
        double seconds = ring_size * scan_period;
        help.setText(Messages.RingBuffer + seconds + Messages.Secs);
        // Update model with anything that changed.
        if (scan_period != model.getScanPeriod()  ||
            update_period != model.getUpdatePeriod())
            model.setPeriods(scan_period, update_period);
        if (ring_size != model.getRingSize())
            model.setRingSize(ring_size);
    }

    /** Validate inputs into the 'Time' tab. */
    private void checkTimeConfigInputs()
    {
        Model model = getModel();
        if (model == null)
            return;
        try
        {
            model.setTimeSpecifications(start_specification.getText(), end_specification.getText());
            start_end_info.setText(model.getStartTime()
                                   + " ... " + //$NON-NLS-1$
                                   model.getEndTime());
        }
        catch (Exception ex)
        {
            start_end_info.setText(Messages.Error + ex.getMessage());
        }
    }
    
    /** We have a new model because the editor changed.
     *  <p>
     *  Note that we also call this ourself with old == new model
     *  to trigger a GUI update.
     */
    @Override
    protected void updateModel(Model old_model, Model model)
    {
        // Conditionally enable the 'add' action
        add_pv_action.setEnabled(model != null);
        add_formula_action.setEnabled(model != null);
        delete_pv_action.setEnabled(model != null);
        // If the model switched, change our subscription
        if (old_model != model)
        {
            if (old_model != null)
                old_model.removeListener(model_listener);
            if (model != null)
                model.addListener(model_listener);
            table_content.inputChanged(pv_table_viewer, old_model, model);
        }
        // Update all GUI elements with info from current editor
        if (model == null)
        {
            scan_period_text.setText(""); //$NON-NLS-1$
            update_period_text.setText(""); //$NON-NLS-1$
            ring_size_text.setText(""); //$NON-NLS-1$
            pv_table_viewer.setItemCount(0);
            start_specification.setText(""); //$NON-NLS-1$
            end_specification.setText(""); //$NON-NLS-1$
        }
        else
        {
            scan_period_text.setText(Double.toString(model.getScanPeriod()));
            update_period_text.setText(Double.toString(model.getUpdatePeriod()));
            ring_size_text.setText(Integer.toString(model.getRingSize()));
            // The '+1' is the line that allows entry of new PVs!!
            pv_table_viewer.setItemCount(model.getNumItems() + 1);
            start_specification.setText(model.getStartSpecification());
            end_specification.setText(model.getEndSpecification());
        }
        help.setText(""); //$NON-NLS-1$
        pv_table_viewer.refresh();
        updateLowerSash();
    }
   
    /** Add a new PV to the model. */
    public IModelItem addPV(String name)
    {
        name = name.trim();
        if (name.length() < 1)
            return null;
        Model model = getModel();
        if (model == null)
            return null;
        IPVModelItem pv_item = model.addPV(name);
        model.addDefaultArchiveSources(pv_item);
        return pv_item;
    }
    
    /** Add a new formula to the model. */
    public IModelItem addFormula(String name)
    {
        name = name.trim();
        if (name.length() < 1)
            return null;
        Model model = getModel();
        if (model == null)
            return null;
        IModelItem item = model.add(Model.ItemType.Formula, name);
        configureFormula((FormulaModelItem) item);
        return item;
    }
    
    /** Add an archive source to a specific or all items.
     *  Helper for dropped data. */
    private void addArchive(IModelItem item, IArchiveDataSource archive)
    {
        Model model = getModel();
        if (model == null)
            return;
        if (item != null)
        {
            if (item instanceof IPVModelItem)
                ((IPVModelItem)item).addArchiveDataSource(archive);
        }
        else
            model.addArchiveDataSource(archive);
    }
    
    /** Re-populate the archive table or show the formula stuff
     *  after model or selection changes.
     */
    private void updateLowerSash()
    {
        IStructuredSelection sel = 
            (IStructuredSelection) pv_table_viewer.getSelection();
        if (sel.size() == 1)
        {
            Object item = sel.getFirstElement();
            if (item == PVTableHelper.empty_row)
            {   // hide all
                archive_box.setVisible(false);
                formula_box.setVisible(false);
            }
            else if (item instanceof IPVModelItem)
            {   // Display the archive sources for the PV
                formula_box.setVisible(false);
                archive_box.setVisible(true);
                archive_table_viewer.setInput(
                                ((IPVModelItem)item).getArchiveDataSources());
            }
            else
            {   // Display the formula
                archive_box.setVisible(false);
                formula_box.setVisible(true);
                formula_txt.setText(((FormulaModelItem)item).getFormula());
            }
        }
        else
        {   // empty archive info
            formula_box.setVisible(false);
            archive_box.setVisible(true);
            archive_table_viewer.setInput(null);
        }        
        // Ask shell to re-evaluate the changed layout
        archive_box.getShell().layout(true, true);
    }  

    /** Run dialog for selected formula.
     *  NOP if none selected.
     */
    private void configureSelectedFormula()
    {
        IStructuredSelection sel = 
            (IStructuredSelection) pv_table_viewer.getSelection();
        if (sel.size() == 1)
        {
            Object item = sel.getFirstElement();
            if (item instanceof FormulaModelItem)
                configureFormula((FormulaModelItem)item);
        }
    }

    /** Run dialog for given formula. */
    private void configureFormula(FormulaModelItem item)
    {
        FormulaDialog dlg =
            new FormulaDialog(pv_table_viewer.getControl().getShell(), item);
        dlg.open();
    }
}
