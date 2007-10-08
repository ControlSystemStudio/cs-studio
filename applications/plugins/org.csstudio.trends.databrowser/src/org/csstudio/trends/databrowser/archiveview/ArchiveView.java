package org.csstudio.trends.databrowser.archiveview;

import java.util.ArrayList;

import org.csstudio.apputil.ui.swt.AutoSizeColumn;
import org.csstudio.apputil.ui.swt.AutoSizeControlListener;
import org.csstudio.apputil.ui.swt.ComboHistoryHelper;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDropTarget;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableWithArchiveDragSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** An Eclipse ViewPart for browsing archives.
 *  @author Kay Kasemir
 */
public class ArchiveView extends ViewPart
{

    public static final String ID = ArchiveView.class.getName();

    private static final String URL_LIST_TAG = "url_list"; //$NON-NLS-1$
    private static final String PATTERN_LIST_TAG = "pattern_list"; //$NON-NLS-1$

    private ArchiveServer server;

    // Sash for the two GUI sub-sections
    private SashForm form;
    private int form_weights[] = new int[] {50,50};
    
    private IMemento memento = null;
    
    /** Memento tag for form weights */
    private static final String FORM_WEIGHT_TAG = "form_weights"; //$NON-NLS-1$
    /** Memento tag for regex checkbox */
    private static final String REGEX_TAG = "regex"; //$NON-NLS-1$

    // Archive info GUI Elements
    private Combo url;
    private Button info;
    private TableViewer archive_table_viewer;
    private ArrayList<ArchiveTableItem> archive_table_items =
        new ArrayList<ArchiveTableItem>();

    // Name search GUI Elements
    private Combo pattern;
    private Button search;
    private Button replace_results;
    private Button reg_ex;
    private TableViewer name_table_viewer;
    private ArrayList<NameTableItem> name_table_items =
        new ArrayList<NameTableItem>();


    /** Remove selected items from the name_table_viewer. */
    class RemoveAction extends Action
    {
        public RemoveAction()
        {
            setText(Messages.Remove);
            setToolTipText(Messages.Remove_TT);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
            setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
            setEnabled(false);
            // Conditionally enable this action
            name_table_viewer.addSelectionChangedListener(
            new ISelectionChangedListener()
            {
                public void selectionChanged(SelectionChangedEvent event)
                {
                    setEnabled(! event.getSelection().isEmpty());
                }
            });
        }
        
        @Override
        public void run()
        {
            IStructuredSelection sel =
                (IStructuredSelection) name_table_viewer.getSelection();
            if (sel.isEmpty())
                return;
            Object items[] = sel.toArray();
            for (int i = 0; i < items.length; i++)
            {
                name_table_items.remove(items[i]);
            }
            name_table_viewer.setItemCount(name_table_items.size());
        }
    };
    
    /** Try to restore some things from memento */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento = memento;
    }

    /** Save the display state.
     *  @see #restoreState()
     */
    @Override
    public void saveState(IMemento memento)
    {
        form_weights = form.getWeights();
        for (int i=0; i<form_weights.length; ++i)
            memento.putInteger(FORM_WEIGHT_TAG + i, form_weights[i]);
        memento.putInteger(REGEX_TAG, reg_ex.getSelection() ? 1 : 0);
    }

    /** Restore state from memento.
     *  @see #saveState(IMemento)
     */
    private void restoreState()
    {
        if (memento == null)
            return;
        Integer val;
        for (int i=0; i<form_weights.length; ++i)
        {
            val = memento.getInteger(FORM_WEIGHT_TAG + i);
            if (val != null)
                form_weights[i] = val.intValue();
        }
        val = memento.getInteger(REGEX_TAG);
        if (val != null)
            reg_ex.setSelection(val.intValue() > 0);
    }

    @Override
    public void createPartControl(Composite parent)
    {
        createGUI(parent);
        // Allow dragging PV names & Archive Info out of the name table.
        new ProcessVariableWithArchiveDragSource(name_table_viewer.getTable(),
                        name_table_viewer);
        // ...or just Archive Info out of the archive table.
        new ArchiveDataSourceDragSource(archive_table_viewer.getTable(),
                        archive_table_viewer);
        // Accept archive URLs:
        new ArchiveDataSourceDropTarget(url)
        {
            @Override
            public void handleDrop(IArchiveDataSource archive,
                                   DropTargetEvent event)
            {
                connectToURL(archive.getUrl());
            }
        };
    }

    private void createGUI(Composite parent)
    {
        parent.setLayout(new FillLayout());
        // SashForm with these children:
        //
        // URL: ________(url)_________________________ [Info?]
        // Table with list of archives
        //
        // Search: ____ (pattern) ____  [Search !]
        // Table with list of names

        form = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
        form.setLayout(new FillLayout());

        // First box under sash form. --------------------------------
        Composite box = new Composite(form, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        box.setLayout(layout);
        GridData gd;
        
        Label label;
        label = new Label(box, SWT.NULL);
        label.setText(Messages.URL);
        gd = new GridData();
        label.setLayoutData(gd);
        
        url = new Combo(box, SWT.DROP_DOWN);
        url.setToolTipText(Messages.URL_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        url.setLayoutData(gd);
        url.setEnabled(false);
        
        ComboHistoryHelper url_helper =
            new ComboHistoryHelper(Plugin.getDefault().getDialogSettings(),
                               URL_LIST_TAG, url)
        {
            @Override
            public void newSelection(String new_pv_name)
            {   connectToURL(new_pv_name); }
        };
        
        info = new Button(box, SWT.PUSH);
        info.setText(Messages.Info);
        info.setToolTipText(Messages.Info_TT);
        gd = new GridData();
        info.setLayoutData(gd);
        info.setEnabled(false);
        info.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (server == null)
                    throw new Error("Info button should be disabled"); //$NON-NLS-1$
                InfoDialog dlg = new InfoDialog(info.getShell(), server);
                dlg.open();
            }
        });
                
        // Table with list of archives
        Table table;
        table = new Table(box,
               SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        // ...
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        AutoSizeColumn.make(table, Messages.ArchiveCol, 100, 50);
        AutoSizeColumn.make(table, Messages.KeyCol, 50, 20);
        AutoSizeColumn.make(table, Messages.DescriptionCol, 100, 100);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(box, table);
        archive_table_viewer = new TableViewer(table);
        archive_table_viewer.setLabelProvider(new ArchiveTableLabelProvider());
        archive_table_viewer.setContentProvider(new ArrayContentProvider());
        
        // Second box under sash form. --------------------------------
        //
        // Pattern: ______________________ [ Search ]
        // ( ) Add  (*) Replace            [X] RegEx    
        // name_table_viewer table .....
        // .............................
        // .............................
        // .............................
        box = new Composite(form, SWT.NULL);
        layout = new GridLayout();
        layout.numColumns = 3;
        box.setLayout(layout);
        
        // Pattern: ____ (pattern) ____  [Search !]
        label = new Label(box, SWT.NULL);
        label.setText(Messages.Pattern);
        label.setLayoutData(new GridData());
        
        pattern = new Combo(box, SWT.DROP_DOWN);
        pattern.setToolTipText(Messages.Pattern_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pattern.setLayoutData(gd);
        pattern.setEnabled(false);
        ComboHistoryHelper pattern_helper =
            new ComboHistoryHelper(Plugin.getDefault().getDialogSettings(),
                               PATTERN_LIST_TAG, pattern)
        {
            @Override
            public void newSelection(String new_pattern)
            {   search(new_pattern); }
        };

        search = new Button(box, SWT.PUSH);
        search.setText(Messages.Seach);
        search.setToolTipText(Messages.Seach_TT);
        search.setLayoutData(new GridData());
        search.setEnabled(false);
        search.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {   search(pattern.getText()); }
        });

        // ( ) Add  (*) Replace            [X] RegEx    
        Button add_results = new Button(box, SWT.RADIO);
        add_results.setText(Messages.AddResults);
        add_results.setToolTipText(Messages.AddResults_TT);
        add_results.setLayoutData(new GridData());
        
        replace_results = new Button(box, SWT.RADIO);
        replace_results.setText(Messages.ReplaceResults);
        replace_results.setToolTipText(Messages.ReplaceResults_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.LEFT;
        replace_results.setLayoutData(gd);

        reg_ex = new Button(box, SWT.CHECK);
        reg_ex.setText(Messages.RegEx);
        reg_ex.setToolTipText(Messages.RegEx_TT);
        reg_ex.setLayoutData(new GridData());
        
        // Table with list of names
        table = new Table(box,
               SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION
               | SWT.VIRTUAL);
        // ...
        gd = new GridData();
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        AutoSizeColumn.make(table, Messages.NameCol, 200, 100);
        AutoSizeColumn.make(table, Messages.ArchiveCol, 50, 40);
        AutoSizeColumn.make(table, Messages.StartCol, 85, 20);
        AutoSizeColumn.make(table, Messages.EndCol, 85, 20);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(box, table);
        name_table_viewer = new TableViewer(table);
        name_table_viewer.setUseHashlookup(true);
        name_table_viewer.setLabelProvider(new NameTableItemLabelProvider());
        name_table_viewer.setContentProvider(
            new LazyNameTableContentProvider(name_table_viewer,
                                             name_table_items));
        name_table_viewer.setItemCount(name_table_items.size());

        // Initialize stuff from memento
        restoreState();
        
        // Set initial (relative) sizes
        form.setWeights(form_weights);

        // Add context menu to the name table.
        // One reason: Get object contribs for the NameTableItems.
        IWorkbenchPartSite site = getSite();
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        manager.add(new RemoveAction());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        Menu menu = manager.createContextMenu(name_table_viewer.getControl());
        name_table_viewer.getControl().setMenu(menu);
        site.registerContextMenu(manager, name_table_viewer);
        
        // Get the URLs from preferences.
        String urls[] = Preferences.getArchiveServerURLs();
        for (int i=0; i<urls.length; ++i)
        {
            if (urls[i].length() > 0)
                url.add(urls[i]);
        }
        url.setEnabled(true);
        replace_results.setSelection(true);
        
        // Then load the user's last values, which might cause values
        // from prefs to drop off the list
        url_helper.loadSettings();
        
        // Load previously entered patterns
        pattern_helper.loadSettings();
    }

    @Override
    public void setFocus()
    {
        url.setFocus();
    }
    
    /** Connect to archive server with given URL. */
    public void connectToURL(String url)
    {
        // If this is a new url, add it to the combo box's list.
        boolean new_url = true;
        for (int i=0; i<this.url.getItemCount(); ++i)
            if (this.url.getItem(i).equals(url))
            {
                new_url = false;
                break;
            }
        if (new_url)
            this.url.add(url);
        // Reflect 'current' url in bombo box
        if (! this.url.getText().equals(url))
            this.url.setText(url);
        // Forget what we know
        setArchiveServer(null, null);
        // Start background job
        Job job = new ConnectJob(this, url);
        job.schedule();
    }
 
    /** Set the archive server instance.
     *  <p>
     *  Invoked by the ConnectJob after(!) it successfully connected
     *  to the URL.
     *  Can also be invoked with null to 'clear'.
     *  @param server
     */
    public void setArchiveServer(ArchiveServer server,
                    ArchiveInfo archive_infos[])
    {
        boolean have_server = server != null;
        this.server = server;
        // Reset GUI
        info.setEnabled(have_server);
        archive_table_items.clear();
        if (have_server)
        {
            for (ArchiveInfo info : archive_infos)
                archive_table_items.add(new ArchiveTableItem(server.getURL(),
                                                        info.getKey(),
                                                        info.getName(),
                                                        info.getDescription()));
        }
        archive_table_viewer.setInput(archive_table_items);
        pattern.setEnabled(have_server);
        search.setEnabled(have_server);
    }

    /** Search archive server for names with given pattern. */
    public void search(String pattern)
    {
        if (server == null)
            throw new Error("Search invoked without current server"); //$NON-NLS-1$
        // Update displayed pattern
        // (in case we're not in the callback for the pattern textbox)
        if (! this.pattern.getText().equals(pattern))
            this.pattern.setText(pattern);
        
        if (reg_ex.getSelection() == false)
            // Convert 'glob' pattern into regular expression
            pattern = RegExHelper.regexFromGlob(pattern);
        
        // Really search all?
        if (pattern.length() == 0)
        {
            if (! MessageDialog.openConfirm(getSite().getShell(),
                Messages.SearchConfirmTitle,
                Messages.SearchConfirmMessage))
                return;
        }
        // Obtain the keys to seach from the list of archives.
        // Either the full list, or the selected items.
        int keys[];
        IStructuredSelection sel = 
            (IStructuredSelection)archive_table_viewer.getSelection();
        if (sel.isEmpty())
        {
            keys = new int[archive_table_items.size()];
            for (int i=0; i<archive_table_items.size(); ++i)
                keys[i] = archive_table_items.get(i).getKey();
        }
        else
        {   // sel.toArray only gives Object[], so we can't be type safe
            Object infos[] = sel.toArray();
            keys = new int[infos.length];
            for (int i=0; i<infos.length; ++i)
                keys[i] = ((ArchiveTableItem)infos[i]).getKey();
        }
        // Forget what we know?
        if (replace_results.getSelection())
            clearNames();
        // Start a background search job
        Job job = new SearchJob(this, server, keys, pattern);
        job.schedule();
    }

    /** Clear the list of names. */
    public void clearNames()
    {
        name_table_items.clear();
        name_table_viewer.setItemCount(name_table_items.size());
    }
    
    /** Add given name infos to the list of names. */
    public void addNameInfos(NameInfo name_infos[], int key)
    {
        try
        {
            for (int i = 0; i < name_infos.length; i++)
            {
                name_table_items.add(new NameTableItem(server, key,
                                name_infos[i].getName(),
                                name_infos[i].getStart(),
                                name_infos[i].getEnd()));
            }
        }
        catch (Exception e)
        {
            Plugin.logException("Cannot add name", e); //$NON-NLS-1$
        }
        name_table_viewer.setItemCount(name_table_items.size());
    }
}
