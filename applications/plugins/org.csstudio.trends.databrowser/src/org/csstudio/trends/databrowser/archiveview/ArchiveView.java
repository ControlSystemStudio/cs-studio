package org.csstudio.trends.databrowser.archiveview;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveServer;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDragSource;
import org.csstudio.platform.ui.internal.dataexchange.ArchiveDataSourceDropTarget;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableWithArchiveDragSource;
import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.PlotAwareView;
import org.csstudio.trends.databrowser.preferences.Preferences;
import org.csstudio.util.swt.AutoSizeColumn;
import org.csstudio.util.swt.AutoSizeControlListener;
import org.csstudio.util.swt.ComboHistoryHelper;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

/** An Eclipse ViewPart for handling the DataBrowser archives.
 *  @author Kay Kasemir
 */
public class ArchiveView extends PlotAwareView
{
    public static final String ID = ArchiveView.class.getName();

    private static final String URL_LIST_TAG = "url_list"; //$NON-NLS-1$

    private ArchiveServer server;

    // Sash for the two GUI sub-sections
    // TODO: on exit, persist the form's weight, and init from saved settings?
    private SashForm form;

    // Archive info GUI Elements
    private Combo url;
    private Button info;
    private Button connect;
    private TableViewer archive_table_viewer;
    private ArrayList<ArchiveTableItem> archive_table_items =
        new ArrayList<ArchiveTableItem>();

    // Name search GUI Elements
    private Text pattern;
    private Button search;
    private Button replace_results;
    private TableViewer name_table_viewer;
    private ArrayList<NameTableItem> name_table_items =
        new ArrayList<NameTableItem>();

    private ComboHistoryHelper url_helper;
    
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
            Object o[] = sel.toArray();
            name_table_viewer.remove(o);
        }
    };

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
        // Hook to model updates
        super.createPartControl(parent);
    }

    private void createGUI(Composite parent)
    {
        parent.setLayout(new FillLayout());
        // SashForm with these children:
        //
        // URL: ________(url)_________________________ [Info?] [Connect!]
        // Table with list of archives
        //
        // Search: ____ (pattern) ____  [Search !]
        // Table with list of names

        form = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
        form.setLayout(new FillLayout());

        // First box under sash form. --------------------------------
        Composite box = new Composite(form, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
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
        url_helper = new ComboHistoryHelper(
                                Plugin.getDefault().getDialogSettings(),
                                URL_LIST_TAG, url)
                {
                    public void newSelection(String new_pv_name)
                    {   connectToURL(new_pv_name); }
                };
        info = new Button(box, SWT.PUSH);
        info.setText(Messages.Info);
        info.setToolTipText(Messages.Info_TT);
        gd = new GridData();
        info.setLayoutData(gd);
        info.setEnabled(false);
        info.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {}

            public void widgetSelected(SelectionEvent e)
            {
                if (server == null)
                    throw new Error("Info button should be disabled"); //$NON-NLS-1$
                MessageBox box =
                    new MessageBox(info.getShell(), SWT.OK);
                box.setText(Messages.Info_Title);
                box.setMessage(Messages.URL
                                + server.getURL() + "\n" //$NON-NLS-1$
                                + server.getDescription());
                box.open();
            }
        });
        
        connect = new Button(box, SWT.PUSH);
        connect.setText(Messages.Connect);
        connect.setToolTipText(Messages.Connect_TT);
        gd = new GridData();
        connect.setLayoutData(gd);
        connect.setEnabled(false);
        connect.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {}

            public void widgetSelected(SelectionEvent e)
            {
                connectToURL(url.getText());
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
        // Add ( )  Replace (*)
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
        gd = new GridData();
        label.setLayoutData(gd);
        
        // TODO: Add a regular expression helper dialog, xlate 'glob' patterns?
        
        pattern = new Text(box, SWT.BORDER | SWT.LEFT);
        pattern.setToolTipText(Messages.Pattern_TT);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        pattern.setLayoutData(gd);
        pattern.setEnabled(false);
        pattern.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {   search(pattern.getText());  }
            public void widgetSelected(SelectionEvent e)
            {}
        });

        search = new Button(box, SWT.PUSH);
        search.setText(Messages.Seach);
        search.setToolTipText(Messages.Seach_TT);
        gd = new GridData();
        search.setLayoutData(gd);
        search.setEnabled(false);
        search.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {}
            public void widgetSelected(SelectionEvent e)
            {   search(pattern.getText()); }
        });

        Button add_results = new Button(box, SWT.RADIO);
        add_results.setText(Messages.AddResults);
        add_results.setToolTipText(Messages.AddResults_TT);
        gd = new GridData();
        add_results.setLayoutData(gd);
        
        replace_results = new Button(box, SWT.RADIO);
        replace_results.setText(Messages.ReplaceResults);
        replace_results.setToolTipText(Messages.ReplaceResults_TT);
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.LEFT;
        replace_results.setLayoutData(gd);
        
        // Table with list of names
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
        AutoSizeColumn.make(table, Messages.NameCol, 200, 100);
        AutoSizeColumn.make(table, Messages.ArchiveCol, 50, 40);
        AutoSizeColumn.make(table, Messages.StartCol, 85, 20);
        AutoSizeColumn.make(table, Messages.EndCol, 85, 20);
        // Configure table to auto-size the columns
        new AutoSizeControlListener(box, table);
        name_table_viewer = new TableViewer(table);
        name_table_viewer.setLabelProvider(new NameTableItemLabelProvider());
        name_table_viewer.setContentProvider(new ArrayContentProvider());
        name_table_viewer.setInput(name_table_items);

        // Set initial (relative) sizes
        form.setWeights(new int[] {50,50});

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
        for (int i=0; i<Preferences.getNumURLs(); ++i)
        {
            String s = Preferences.getURL(i);
            if (s.length() > 0)
                url.add(s);
        }
        connect.setEnabled(true);
        url.setEnabled(true);
        replace_results.setSelection(true);
        
        // Load? Or use values from prefs?
        // url_helper.loadSettings();
    }

    @Override
    public void setFocus()
    {
        url.setFocus();
    }
    
    @Override
    public void dispose()
    {
        url_helper.saveSettings();
        super.dispose();
    }

    /** We have a new model because the editor changed. */
    @Override
    protected void updateModel(Model old_model, Model model)
    {
        // TODO Get URL(s) from model?
        /*
        if (model == null)
        {
            connect.setEnabled(false);
            url.setEnabled(false);
            url.setText(Messages.NoCurrentPlot);
        }
        else
        {
            connect.setEnabled(true);
            url.setEnabled(true);
            //   "http://ics-srv-web2.sns.ornl.gov/archive/cgi/ArchiveDataServer.cgi";
            url.setText(Messages.DefaultURL);
        }
        */
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
        name_table_viewer.refresh();
    }
    
    /** Add given name info to the list of names. */
    public void addNameInfo(NameInfo name_info, int key)
    {
        try
        {
            name_table_items.add(new NameTableItem(server, key,
                                                   name_info.getName(),
                                                   name_info.getStart(),
                                                   name_info.getEnd()));
            name_table_viewer.refresh();
        }
        catch (Exception e)
        {
            Plugin.logException("Cannot add name", e); //$NON-NLS-1$
        }
    }
}
