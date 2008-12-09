package org.csstudio.trends.databrowser.configview;

import org.csstudio.apputil.time.RelativeTime;
import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.csstudio.apputil.ui.swt.ScrolledContainerHelper;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDropTarget;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableOrArchiveDataSourceDropTarget;
import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.configview.PVTableHelper.Column;
import org.csstudio.trends.databrowser.fileimport.ImportFileAction;
import org.csstudio.trends.databrowser.model.ArchiveRescale;
import org.csstudio.trends.databrowser.model.FormulaModelItem;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.trends.databrowser.model.IPVModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelListener;
import org.csstudio.trends.databrowser.model.formula_gui.FormulaDialog;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.trends.databrowser.plotpart.AddFormulaAction;
import org.csstudio.trends.databrowser.plotpart.AddPVAction;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.csstudio.util.swt.RGBCellEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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

    /** On Windows, the background of the tab items seems to be white,
     *  on which the borders of text entries etc. vanish.
     *  So we force this background color onto the container inside
     *  the tab item...
     */
	private Color tab_bg;
    
    /** Initial sizes, possibly updated in init() from memento */
    private int pv_form_weights[] = new int[] { 60, 40 };
    
    // Sash Section for PV Table
    private TableViewer pv_table_viewer;
    private PVTableLazyContentProvider table_content;
    private PVTableLabelProvider label_provider;
    private AddPVAction add_pv_action;
    private AddFormulaAction add_formula_action;
    private Action delete_pv_action;
    private Action archive_up_action, archive_down_action;
    private Action delete_archive_action;
    private Action import_from_file_action;

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
        public void plotColorsChangedChanged()
        {   entriesChanged(); }
        
        public void markersChanged()
        { /* Ignore */ }

		public void timeSpecificationsChanged()
        {   entriesChanged(); }
        
        public void timeRangeChanged()
        {   // Ignore changes to 'current' time range from scroll
        }

        public void samplingChanged()
        {   entriesChanged(); }

        public void entriesChanged()
        {
            final Model model = getModel();
            updateModel(model, model);
        }

        public void entryAdded(IModelItem new_item)
        {   entriesChanged(); }

        public void entryConfigChanged(final IModelItem item)
        {
            // Used to call plain refresh(), but this call seems to
            // avoid "Ignored reentrant call while viewer is busy" under Eclipse 3.4
            pv_table_viewer.refresh(item);
            updateLowerSash();
        }

        public void entryMetaDataChanged(IModelItem item)
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
    /** How to handle new data */
    private Button rescale_none, rescale_zoom, rescale_stagger;
    /** Overall colors */
	private ColorBlob background, foreground, grid_color;
    
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
    
    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(Composite parent)
    {
        // Create the GUI Elements
        final Composite scroll = ScrolledContainerHelper.create(parent, 200, 300);
        scroll.setLayout(new FillLayout());
        // Quirk: Don't set a layout on the TabFolder nor the TabItem.
        // Do set a layout on the Composite that's inside the TabItem,
        // which has the TabFolder as a parent.
        TabFolder tabs = new TabFolder(scroll, SWT.BORDER);
        tab_bg = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        createPVTab(tabs);
        createLiveTab(tabs);
        createPlotTab(tabs);
        createTimeTab(tabs);
        
        // Update the archive table for the selected PVs
        pv_table_viewer.addPostSelectionChangedListener(
                        new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {   updateLowerSash();  }
        });
             
        // Create actions, hook them to menus
        add_pv_action = new AddPVAction(parent.getShell(), getModel());
        add_formula_action = new AddFormulaAction(parent.getShell(), getModel());
        delete_pv_action = new DeletePVAction(this);
        archive_up_action = new ArchiveUpAction(this);
        archive_down_action = new ArchiveDownAction(this);
        delete_archive_action = new DeleteArchiveAction(this);
        import_from_file_action = new ImportFileAction(this);
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
    private void createPVTabListItem(final SashForm pv_form)
    {
        final Composite box = new Composite(pv_form, SWT.NULL);
		box.setBackground(tab_bg);

		final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        box.setLayout(layout);

        Label l = new Label(box, 0);
        l.setText(Messages.PVs + colon);
        GridData gd = new GridData();
        l.setLayoutData(gd);
        
        final Table table = new Table(box,
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
        
        final Column[] all_columns = PVTableHelper.Column.values();
        final Column[] columns = new Column[all_columns.length];
        int i = 0;
        for (PVTableHelper.Column col : all_columns)
        {
            columns[i++] = col;
            AutoSizeColumn.make(table, col.getTitle(), col.getMinSize(),
                                col.getWeight(), col.isCentered());
        }
        // Configure table to auto-size the columns
        new AutoSizeControlListener(table);
        
        pv_table_viewer = new TableViewer(table);
        // Enable hashmap for resolving 'PVListEntry' to associated SWT widget.
        pv_table_viewer.setUseHashlookup(true);
        label_provider = new PVTableLabelProvider(table);
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
            new ComboBoxCellEditor(table, TraceType.getLocalizedNames(), SWT.READ_ONLY);
        editors[PVTableHelper.Column.AUTO_SCALE.ordinal()] = new CheckboxCellEditor(table);
        editors[PVTableHelper.Column.REQUEST_TYPE.ordinal()] =
            new ComboBoxCellEditor(table, IPVModelItem.RequestType.getTypeStrings(), SWT.READ_ONLY);
        String titles[] = new String[columns.length];
        for (i=0; i<columns.length; ++i)
            titles[i] = columns[i].getTitle();
        pv_table_viewer.setColumnProperties(titles);
        pv_table_viewer.setCellEditors(editors);
        pv_table_viewer.setCellModifier(new PVTableCellModifier(this));
    }
    
    /** SashForm item for archives or formulas of current PV */
    private void createPVTabInfoItem(final SashForm pv_form)
    {
        final Composite box = new Composite(pv_form, 0);
		box.setBackground(tab_bg);

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
        
        final Table table = new Table(archive_box,
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
        new AutoSizeControlListener(table);
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
    private void createLiveTab(final TabFolder tabs)
    {
        final TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.LiveConfig);
        tab.setToolTipText(Messages.ConfigPVUpdates);
        final Composite parent = new Composite(tabs, 0);
		parent.setBackground(tab_bg);
        
		final GridLayout gl = new GridLayout();
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

        final SelectionListener validator = new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {   checkLivePVInputs(); }
        };
        scan_period_text.addSelectionListener(validator);
        update_period_text.addSelectionListener(validator);
        ring_size_text.addSelectionListener(validator);

        // Row 5
        l = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        l.setLayoutData(gd);

        // Row 6
        help = new Label(parent, SWT.LEFT);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        help.setLayoutData(gd);
        
        tab.setControl(parent);
    }
    
    /** Create one tab of the TabFolder GUI. */
	private void createPlotTab(final TabFolder tabs)
	{
	    final TabItem tab = new TabItem(tabs, 0);
	    tab.setText(Messages.PlotTab);
	    tab.setToolTipText(Messages.PlotTab_TT);
	    final Composite parent = new Composite(tabs, SWT.SHADOW_ETCHED_IN);
		parent.setBackground(tab_bg);
	    
		final GridLayout layout = new GridLayout();
	    layout.numColumns = 2;
	    parent.setLayout(layout);
	    
        // ArchiveRescale: (*) NONE ( ) ...
        final Composite rescale_box = new Composite(parent, 0);
        rescale_box.setLayoutData(new GridData(SWT.FILL, 0, true, false, layout.numColumns, 1));
        rescale_box.setLayout(new RowLayout());
        final Label l = new Label(rescale_box, 0);
        l.setText(Messages.Rescale_Label);
        rescale_none = new Button(rescale_box, SWT.RADIO);
        rescale_none.setText(Messages.Rescale_None);
        rescale_zoom = new Button(rescale_box, SWT.RADIO);
        rescale_zoom.setText(Messages.Rescale_Autozoom);
        rescale_stagger = new Button(rescale_box, SWT.RADIO);
        rescale_stagger.setText(Messages.Rescale_Stagger);
        rescale_none.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final Model model = getModel();
                if (model == null)
                    return;
                model.setArchiveRescale(ArchiveRescale.NONE);
            }
        });
        rescale_zoom.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final Model model = getModel();
                if (model == null)
                    return;
                model.setArchiveRescale(ArchiveRescale.AUTOZOOM);
            }
        });
        rescale_stagger.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final Model model = getModel();
                if (model == null)
                    return;
                model.setArchiveRescale(ArchiveRescale.STAGGER);
            }
        });

        // Color configurations
	    background = addColorConfigurator(parent,
	    		Messages.BackColor, Messages.BackColor_TT,
	    		new SelectionAdapter()
	    {
	        @Override
	        public void widgetSelected(SelectionEvent e)
	        {
	            final Model model = getModel();
	            if (model == null)
	                return;
	            final ColorDialog dialog = new ColorDialog(parent.getShell());
                dialog.setRGB(model.getPlotBackground());
                final RGB value = dialog.open();
	            if (value != null)
	            	model.setPlotBackground(value);
	        }
	    });
	    
	    foreground = addColorConfigurator(parent,
	    		Messages.ForeColor, Messages.ForeColor_TT,
	    		new SelectionAdapter()
	    {
	        @Override
	        public void widgetSelected(SelectionEvent e)
	        {
	            final Model model = getModel();
	            if (model == null)
	                return;
	            final ColorDialog dialog = new ColorDialog(parent.getShell());
                dialog.setRGB(model.getPlotForeground());
                final RGB value = dialog.open();
	            if (value != null)
	            	model.setPlotForeground(value);
	        }
	    });
	    
	    grid_color = addColorConfigurator(parent,
	    		Messages.GridColor, Messages.GridColor_TT, 
	    		new SelectionAdapter()
	    {
	        @Override
	        public void widgetSelected(SelectionEvent e)
	        {
	            final Model model = getModel();
	            if (model == null)
	                return;
	            final ColorDialog dialog = new ColorDialog(parent.getShell());
                dialog.setRGB(model.getPlotGrid());
                final RGB value = dialog.open();
	            if (value != null)
	            	model.setPlotGrid(value);
	        }
	    });
	    
	    tab.setControl(parent);
	}

	/** Add GUI elements for configuring a color
	 *  @param parent Parent widget
	 *  @param label Label
	 *  @param tooltip Tooltip
	 *  @param configure Button SelectionAdapter invoked to configure color
	 *  @return Label that's supposed to show the color
	 */
	private ColorBlob addColorConfigurator(final Composite parent, final String label,
			final String tooltip, final SelectionAdapter configure)
	{
	    final Label l = new Label(parent, 0);
	    l.setText(label);
	    l.setLayoutData(new GridData());
	
	    final ColorBlob color_blob = new ColorBlob(parent);
	    color_blob.setToolTipText(tooltip);
	    color_blob.addSelectionListener(configure);
	    final GridData gd = new GridData();
	    gd.minimumWidth = 80;
	    gd.widthHint = 80;
	    gd.heightHint = 15;
	    color_blob.setLayoutData(gd);
	    
	    return color_blob;
	}

	/** Create one tab of the TabFolder GUI. */
    private void createTimeTab(final TabFolder tabs)
    {
        final TabItem tab = new TabItem(tabs, 0);
        tab.setText(Messages.TimeAxisConfig);
        tab.setToolTipText(Messages.TimeAxisConfig_TT);
        Composite parent = new Composite(tabs, SWT.SHADOW_ETCHED_IN);
		parent.setBackground(tab_bg);
        
		final GridLayout layout = new GridLayout();
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
        dlg1.setToolTipText(Messages.StartEndDlg_TT);
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
        dlg2.setToolTipText(Messages.StartEndDlg_TT);
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
        final Button set_graph_times = new Button(parent, SWT.PUSH);
        set_graph_times.setText(Messages.SetGraphTimes);
        gd = new GridData();
        set_graph_times.setLayoutData(gd);

        final Button read_graph_times = new Button(parent, SWT.PUSH);
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
                final Model model = getModel();
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
                final Model model = getModel();
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
        //show the import file action only if preference 'show sample file import action'
        //is set to true.
        if (Preferences.getShowSampleFileImportAction())
        	manager.add(import_from_file_action);
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
        final Model model = getModel();
        if (model == null)
        {
            help.setText(""); //$NON-NLS-1$
            return;
        }
        double scan_period = model.getScanPeriod();
        double update_period = model.getUpdatePeriod();
        final int old_ring_size = model.getLiveBufferSize();
        int ring_size = old_ring_size;
        try
        {
            scan_period = Double.parseDouble(scan_period_text.getText());
            if (scan_period < Preferences.MIN_SCAN_PERIOD)
            {
                help.setText(Messages.ScanPeriodMustBeGt + Preferences.MIN_SCAN_PERIOD);
                return;
            }

            update_period = Double.parseDouble(update_period_text.getText());
            if (update_period < Preferences.MIN_UPDATE_PERIOD)
            {
                help.setText(Messages.UpdatePeriodMustBeGt + Preferences.MIN_UPDATE_PERIOD);
                return;
            }

            if (update_period < scan_period)
            {
                help.setText(Messages.UpdateVsScanPeriod);
                return;
            }

            ring_size = Integer.parseInt(ring_size_text.getText());
            if (ring_size <= Preferences.MIN_LIVE_BUFFER_SIZE)
            {
                help.setText(Messages.RingBufferMinSize);
                return;
            }
        }
        catch (Exception e)
        {
            help.setText(e.getMessage());
            return;
        }
        // Update model with anything that changed.
        if (scan_period != model.getScanPeriod()  ||
            update_period != model.getUpdatePeriod())
            model.setPeriods(scan_period, update_period);
        if (ring_size != old_ring_size)
        {
            try
            {
                model.setLiveBufferSize(ring_size);
            }
            catch (Exception ex)
            {
                help.setText(ex.getMessage());
                try // revert back to previous setting
                {
                    model.setLiveBufferSize(old_ring_size);
                }
                catch (Exception another_ex)
                {
                    Plugin.getLogger().error(another_ex);
                }
            }
        }
        // Display resulting buffer size in seconds
        final double seconds = ring_size * scan_period;
        final RelativeTime span = new RelativeTime(seconds);
        help.setText(NLS.bind(Messages.RingBufferInfoFmt,
                span.toString(),
                String.format("%.1f", getUsedMemoryPercentage()))); //$NON-NLS-1$
    }

    /** @return Percentage [0..100] of used memory */
    private double getUsedMemoryPercentage()
    {
        final Runtime runtime = Runtime.getRuntime();
        // Max bytes that the JVM will ever try to get 
        final long max = runtime.maxMemory();
        // Quite unclear how to interpret this...
        // Amount of memory used, including what the JVM reserved for
        // new objects, so it's really 'free' at this time...
        final long total = runtime.totalMemory();
        // Additional 'free' memory?!
        final long free = runtime.freeMemory();
        
        // 'total' = allocated mem; used + free lists etc.
        // real free mem = max - total + free
        // real used = total - free
        return (total-free)*100.0/max;
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
    	if (scan_period_text.isDisposed())
    	{   // We should have removed the model listener on disposal...
    	    Plugin.getLogger().error("ConfigView.updateModel called after disposal"); //$NON-NLS-1$
    		return;
    	}
        // Conditionally enable the 'add' action
        add_pv_action.setModel(model);
        add_formula_action.setModel(model);
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
        	background.setColor(new RGB(100, 100, 100));
        	foreground.setColor(new RGB(100, 100, 100));
        	grid_color.setColor(new RGB(100, 100, 100));
        }
        else
        {
            scan_period_text.setText(Double.toString(model.getScanPeriod()));
            update_period_text.setText(Double.toString(model.getUpdatePeriod()));
            ring_size_text.setText(Integer.toString(model.getLiveBufferSize()));
            // The '+1' is the line that allows entry of new PVs!!
            pv_table_viewer.setItemCount(model.getNumItems() + 1);
            start_specification.setText(model.getStartSpecification());
            end_specification.setText(model.getEndSpecification());
            final ArchiveRescale rescale = model.getArchiveRescale();
            rescale_none.setSelection(rescale == ArchiveRescale.NONE); 
            rescale_zoom.setSelection(rescale == ArchiveRescale.AUTOZOOM); 
            rescale_stagger.setSelection(rescale == ArchiveRescale.STAGGER); 
        	background.setColor(model.getPlotBackground());
        	foreground.setColor(model.getPlotForeground());
        	grid_color.setColor(model.getPlotGrid());
        }
        pv_table_viewer.refresh();
        updateLowerSash();
        // Initialize the ring buffer info
        checkLivePVInputs();
    }
   
    /** Add a new PV to the model. */
    public IModelItem addPV(String name)
    {
        name = name.trim();
        if (name.length() < 1)
            return null;
        final Model model = getModel();
        if (model == null)
            return null;
        try
        {
            final IPVModelItem pv_item = model.addPV(name);
            model.addDefaultArchiveSources(pv_item);
            return pv_item;
        }
        catch (Throwable ex)
        {
            Plugin.getLogger().error(ex);
            MessageDialog.openError(pv_form.getShell(),
                    Messages.Error,
                    ex.getMessage());
        }
        return null;
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
