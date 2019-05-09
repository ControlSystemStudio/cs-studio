/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.csstudio.logging.es.Activator;
import org.csstudio.logging.es.Messages;
import org.csstudio.logging.es.Preferences;
import org.csstudio.logging.es.PropertyColumnPreference;
import org.csstudio.logging.es.archivedjmslog.MergedModel;
import org.csstudio.logging.es.archivedjmslog.MergedModelListener;
import org.csstudio.logging.es.model.EventLogMessage;
import org.csstudio.logging.es.model.LogArchiveModel;
import org.csstudio.logging.es.util.MessageContentProvider;
import org.csstudio.logging.es.util.PropertyLabelProvider;
import org.csstudio.logging.es.util.SeverityLabelProvider;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class GUI implements MergedModelListener<EventLogMessage>
{
    /** The model. */
    final LogArchiveModel model;

    /** Properties for the table columns. */
    private String properties[];

    /** The table_viewer. */
    TableViewer table_viewer;

    /** The end. */
    Text start;

    Text end;

    /** The auto refresh. */
    Button times;

    private Button filter;

    Button refresh;

    Button autoRefresh;

    /** The image refresh button */
    private Image imageManualRefresh = null;

    protected final ScheduledExecutorService refreshTimer = Executors
            .newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> refreshJob;

    /**
     * Construct GUI.
     *
     * @param site
     *            Workbench site or <code>null</code>.
     * @param parent
     *            Parent widget/shell
     * @param model
     *            Model to display in GUI
     */
    public GUI(IWorkbenchPartSite site, Composite parent, LogArchiveModel model)
    {
        this.model = model;
        this.imageManualRefresh = AbstractUIPlugin
                .imageDescriptorFromPlugin(Activator.ID, "icons/refresh.gif") //$NON-NLS-1$
                .createImage();
        try
        {
            createGUI(parent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            MessageDialog.openError(parent.getShell(), Messages.Error,
                    Messages.GUI_ErrorInit + ex.getMessage());
            return;
        }

        connectGUIActions();

        connectContextMenu(site);

        // Publish the current selection to the site
        // (to allow context menu extensions based on the selection)
        if (site != null)
        {
            site.setSelectionProvider(this.table_viewer);
        }

        this.model.addListener(this);
    }

    /**
     * Add context menu to table.
     *
     * @param site
     *            the site
     */
    private void connectContextMenu(IWorkbenchPartSite site)
    {
        Table table = this.table_viewer.getTable();
        MenuManager manager = new MenuManager();
        manager.add(new OpenViewAction(IPageLayout.ID_PROP_SHEET,
                Messages.GUI_ShowDetail));
        if (SingleSourcePlugin.getUIHelper().getUI().equals(UI.RCP))
        {
            manager.add(new ExportAction(table.getShell(), this.model));
        }
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        table.setMenu(manager.createContextMenu(table));

        // Allow extensions to add to the context menu
        if (site != null)
        {
            site.registerContextMenu(manager, this.table_viewer);
        }
    }

    /**
     * Connect listeners to GUI elements.
     */
    private void connectGUIActions()
    {
        this.times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                StartEndDialog dlg = new StartEndDialog(
                        GUI.this.times.getShell(), GUI.this.start.getText(),
                        GUI.this.end.getText());
                if (dlg.open() != Window.OK)
                {
                    return;
                }
                updateTimeRange(dlg.getStartSpecification(),
                        dlg.getEndSpecification());
            }
        });

        SelectionListener start_end_handler = new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                updateTimeRange(GUI.this.start.getText(),
                        GUI.this.end.getText());
            }
        };

        this.start.addSelectionListener(start_end_handler);
        this.end.addSelectionListener(start_end_handler);

        this.filter.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateFilters();
            }
        });

        // Double-click on message opens detail
        this.table_viewer.getTable().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                new OpenViewAction(IPageLayout.ID_PROP_SHEET).run();
            }
        });

        this.refresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                GUI.this.refresh.getParent().getShell().setFocus();
                updateTimeRange(GUI.this.start.getText(),
                        GUI.this.end.getText());
            }
        });
    }

    /**
     * Create GUI elements.
     *
     * @param parent
     *            Parent shell/site/window
     * @throws Exception
     *             on error
     */
    private void createGUI(Composite parent) throws Exception
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 7;
        parent.setLayout(layout);
        GridData gd;

        // Start: ___start__ End: ___end___ [Times] [Filter]
        Label l = new Label(parent, 0);
        l.setText(Messages.GUI_LabelStart);
        l.setLayoutData(new GridData());

        this.start = new Text(parent, SWT.BORDER);
        this.start.setToolTipText(Messages.GUI_ToolTipStart);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        this.start.setLayoutData(gd);

        l = new Label(parent, 0);
        l.setText(Messages.GUI_LabelEnd);
        l.setLayoutData(new GridData());

        this.end = new Text(parent, SWT.BORDER);
        this.end.setToolTipText(Messages.GUI_ToolTipEnd);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        this.end.setLayoutData(gd);

        this.times = new Button(parent, SWT.PUSH);
        this.times.setText(Messages.GUI_LabelTimes);
        this.times.setToolTipText(Messages.GUI_ToolTopTimes);
        this.times.setLayoutData(new GridData());

        this.filter = new Button(parent, SWT.PUSH);
        this.filter.setText(Messages.GUI_LabelFilter);
        this.filter.setToolTipText(Messages.GUI_ToolTipFilter);
        this.filter.setLayoutData(new GridData());

        this.refresh = new Button(parent, SWT.PUSH | SWT.NO_FOCUS);
        this.refresh.setImage(this.imageManualRefresh);
        this.refresh.setToolTipText(Messages.GUI_ToolTipRefresh);
        this.refresh.setLayoutData(new GridData());

        // New row: Table of messages
        // TableColumnLayout requires the TableViewer to be in its own Composite
        Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                layout.numColumns, 1));

        // Auto-size table columns
        TableColumnLayout table_layout = new MinSizeTableColumnLayout(10);
        table_parent.setLayout(table_layout);

        this.table_viewer = new TableViewer(table_parent, SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.VIRTUAL);
        Table table = this.table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        this.table_viewer.setContentProvider(new MessageContentProvider());
        ColumnViewerToolTipSupport.enableFor(this.table_viewer);

        // Columns display message properties
        PropertyColumnPreference[] col_pref = Preferences.getPropertyColumns();
        this.properties = new String[col_pref.length];
        for (int i = 0; i < col_pref.length; ++i)
        {
            this.properties[i] = col_pref[i].getName();
            TableViewerColumn view_col = new TableViewerColumn(
                    this.table_viewer, 0);
            TableColumn table_col = view_col.getColumn();
            table_col.setText(col_pref[i].getName());
            table_col.setMoveable(true);
            table_layout.setColumnData(table_col, new ColumnWeightData(
                    col_pref[i].getWeight(), col_pref[i].getSize()));
            // Severity columns are special
            if (this.properties[i].toLowerCase()
                    .indexOf(EventLogMessage.SEVERITY.toLowerCase()) >= 0)
            {
                view_col.setLabelProvider(
                        new SeverityLabelProvider(this.properties[i], parent));
            }
            else // other columns display & sort property as string
            {
                view_col.setLabelProvider(
                        new PropertyLabelProvider(this.properties[i]));
            }
            // Sort alphabetically
            TableColumn col = view_col.getColumn();
            col.addSelectionListener(new PropertyColumnSortingSelector(
                    this.table_viewer, col, this.properties[i]));
        }

        this.table_viewer.setInput(this.model);
    }

    /**
     * Gets the selection provider.
     *
     * @return Table which provides the currently selected message
     */
    public ISelectionProvider getSelectionProvider()
    {
        return this.table_viewer;
    }

    @Override
    public void onChange(MergedModel<EventLogMessage> m)
    {
        scheduleRefresh();
    }

    @Override
    public void onError(MergedModel<EventLogMessage> m, String error)
    {
        // TODO Auto-generated method stub
    }

    protected void refreshTable()
    {
        this.table_viewer.getTable().getDisplay().asyncExec(() -> {
            if (this.start.isDisposed())
            {
                return;
            }
            if (!this.start.isFocusControl())
            {
                this.start.setText(this.model.getStartSpec());
            }
            if (!this.end.isFocusControl())
            {
                this.end.setText(this.model.getEndSpec());
            }

            // refresh table and keep selections
            int[] tableSelectionIndices = this.table_viewer.getTable()
                    .getSelectionIndices();
            EventLogMessage[] messages = new EventLogMessage[tableSelectionIndices.length];

            for (int i = 0; i < tableSelectionIndices.length; i++)
            {
                int index = tableSelectionIndices[i];
                messages[i] = (EventLogMessage) this.table_viewer
                        .getElementAt(index);
            }
            this.table_viewer.refresh();

            if (0 != messages.length)
            {
                List<EventLogMessage> listMsgSelect = new ArrayList<>();
                // TODO
                EventLogMessage[] msgModel = this.model.getMessages();
                for (EventLogMessage element : msgModel)
                {
                    for (EventLogMessage message : messages)
                    {
                        if (element.equals(message))
                        {
                            listMsgSelect.add(element);
                        }
                    }
                }
                this.table_viewer.setSelection(
                        new StructuredSelection(listMsgSelect), true);
            }
        });
    }

    public void scheduleRefresh()
    {
        if ((null != this.refreshJob) && !this.refreshJob.isDone())
        {
            // already scheduled
            return;
        }
        this.refreshJob = this.refreshTimer.schedule(this::refreshTable, 500,
                TimeUnit.MILLISECONDS);
    }

    /**
     * Update Model's filter, display exception in dialog box. If all goes well,
     * GUI should update in response to model's update event.
     */
    void updateFilters()
    {
        FilterDialog dlg = new FilterDialog(this.filter.getShell(),
                this.properties, this.model.getFilters());
        if (dlg.open() != Window.OK)
        {
            return;
        }
        try
        {
            this.model.setFilters(dlg.getFilters());
        }
        catch (Exception ex)
        {
            MessageDialog.openError(this.times.getShell(), Messages.Error,
                    Messages.GUI_ErrorFilter + ex.getMessage());
        }
    }

    /**
     * Update Model's time range, display exception in dialog box. If all goes
     * well, GUI should update in response to model's update event.
     *
     * @param start_spec
     *            the start_spec
     * @param end_spec
     *            the end_spec
     */
    void updateTimeRange(String start_spec, String end_spec)
    {
        try
        {
            this.model.setTimerange(start_spec, end_spec);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(this.times.getShell(), Messages.Error,
                    Messages.GUI_ErrorTimes + ex.getMessage());
        }
    }
}
