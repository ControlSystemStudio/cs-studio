/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.Preferences;
import org.csstudio.alarm.beast.msghist.PropertyColumnPreference;
import org.csstudio.alarm.beast.msghist.model.Message;
import org.csstudio.alarm.beast.msghist.model.Model;
import org.csstudio.alarm.beast.msghist.model.ModelListener;
import org.csstudio.apputil.ui.time.StartEndDialog;
import org.csstudio.apputil.ui.workbench.OpenViewAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/** SWT GUI for Model: Table of messages
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUI implements ModelListener
{
	final private Model model;

    /** Properties for the table columns */
    private String properties[];
    private TableViewer table_viewer;
    private Text start, end;
    private Button times, filter;

    /** Construct GUI
     *  @param site Workbench site or <code>null</code>.
     *  @param parent Parent widget/shell
     *  @param model Model to display in GUI
     */
    public GUI(IWorkbenchPartSite site, final Composite parent, final Model model)
    {
        this.model = model;

        try
        {
            createGUI(parent);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(parent.getShell(), "Error",
                "Initialization error: " + ex.getMessage());
            return;
        }

        model.addListener(this);

        connectGUIActions();

        connectContextMenu(site);

        // Publish the current selection to the site
        // (to allow context menu extensions based on the selection)
        if (site != null)
        	site.setSelectionProvider(table_viewer);
    }

    /** @return Table which provides the currently selected message */
	public ISelectionProvider getSelectionProvider()
	{
		return table_viewer;
	}

	/** Update Model's time range, display exception in dialog box.
     *  If all goes well, GUI should update in response to model's
     *  update event.
     */
    private void updateTimeRange(final String start_spec, final String end_spec)
    {
        try
        {
            model.setTimerange(start_spec, end_spec);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(times.getShell(),
                    "Error",
                    "Error in start/end times:\n" + ex.getMessage());
        }
    }

    /** Update Model's filter, display exception in dialog box.
     *  If all goes well, GUI should update in response to model's
     *  update event.
     */
    private void updateFilters()
    {
        final FilterDialog dlg = new FilterDialog(filter.getShell(),
                properties, model.getFilters());
        if (dlg.open() != Window.OK)
            return;
        try
        {
            model.setFilters(dlg.getFilters());
        }
        catch (Exception ex)
        {
            MessageDialog.openError(times.getShell(), "Error",
                    "Error in filter:\n" + ex.getMessage());
        }
    }

    /** Create GUI elements
     *  @param parent Parent shell/site/window
     *  @throws Exception on error
     */
    private void createGUI(final Composite parent) throws Exception
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 6;
        parent.setLayout(layout);
        GridData gd;

        // Start: ___start__  End: ___end___ [Times] [Filter]
        Label l = new Label(parent, 0);
        l.setText("Start:");
        l.setLayoutData(new GridData());

        start = new Text(parent, SWT.BORDER);
        start.setToolTipText("Enter start time");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        start.setLayoutData(gd);

        l = new Label(parent, 0);
        l.setText("End:");
        l.setLayoutData(new GridData());

        end = new Text(parent, SWT.BORDER);
        end.setToolTipText("Enter end time");
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        end.setLayoutData(gd);

        times = new Button(parent, SWT.PUSH);
        times.setText("Times");
        times.setToolTipText("Configure time range");
        times.setLayoutData(new GridData());

        filter = new Button(parent, SWT.PUSH);
        filter.setText("Filter");
        filter.setToolTipText("Configure filters");
        filter.setLayoutData(new GridData());

        // New row: Table of messages
        // TableColumnLayout requires the TableViewer to be in its own Composite
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));

        // Auto-size table columns
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        table_viewer = new TableViewer(table_parent,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        table_viewer.setContentProvider(new MessageContentProvider());
        ColumnViewerToolTipSupport.enableFor(table_viewer, ToolTip.NO_RECREATE);

        // Columns display message properties
        final PropertyColumnPreference[] col_pref =
        						Preferences.getPropertyColumns();
        properties = new String[col_pref.length];
        for (int i=0; i<col_pref.length; ++i)
        {
            properties[i] = col_pref[i].getName();
            final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
            final TableColumn table_col = view_col.getColumn();
            table_col.setText(col_pref[i].getName());
            table_col.setMoveable(true);
            table_layout.setColumnData(table_col,
                    new ColumnWeightData(col_pref[i].getWeight(), col_pref[i].getSize()));
            // Seq, ID columns are special
            if (properties[i].equalsIgnoreCase(Message.SEQ))
            {
                view_col.setLabelProvider(new SeqProvider());
                // Sort numerically by sequence
                final TableColumn col = view_col.getColumn();
                col.addSelectionListener(new SeqColumnSortingSelector(table_viewer, col));
            }
            else if (properties[i].equalsIgnoreCase(Message.ID))
            {
                view_col.setLabelProvider(new IDProvider());
                // Sort numerically by ID
                final TableColumn col = view_col.getColumn();
                col.addSelectionListener(new IDColumnSortingSelector(table_viewer, col));
            }
            // SEVERITY type columns have special color coding
            else if (properties[i].toLowerCase().indexOf(Message.SEVERITY.toLowerCase()) >= 0)
            {
            	view_col.setLabelProvider(
            			new SeverityLabelProvider(properties[i], parent));
            	// Sort alphabetically
                final TableColumn col = view_col.getColumn();
                col.addSelectionListener(
                        new PropertyColumnSortingSelector(table_viewer, col, properties[i]));
            }
            else // other columns display & sort property as string
            {
            	view_col.setLabelProvider(new PropertyLabelProvider(properties[i]));
            	final TableColumn col = view_col.getColumn();
                col.addSelectionListener(
                        new PropertyColumnSortingSelector(table_viewer, col, properties[i]));
            }
        }

        table_viewer.setInput(model);
    }

    /** Connect listeners to GUI elements */
    private void connectGUIActions()
    {
        times.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                final StartEndDialog dlg =
                    new StartEndDialog(times.getShell(),
                            start.getText(), end.getText());
                if (dlg.open() != Window.OK)
                    return;
                updateTimeRange(dlg.getStartSpecification(),
                            dlg.getEndSpecification());
            }
        });
        final SelectionListener start_end_handler = new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                updateTimeRange(start.getText(), end.getText());
            }
        };
        start.addSelectionListener(start_end_handler);
        end.addSelectionListener(start_end_handler);

        filter.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                updateFilters();
            }
        });

        // Double-click on message opens detail
        table_viewer.getTable().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
            	new OpenViewAction(IPageLayout.ID_PROP_SHEET).run();
            }
        });
    }

    /** Add context menu to table
     *  @param site
     */
    private void connectContextMenu(final IWorkbenchPartSite site)
    {
        final Table table = table_viewer.getTable();
        final MenuManager manager = new MenuManager();
        manager.add(new OpenViewAction(IPageLayout.ID_PROP_SHEET, "Show Detail"));
        manager.add(new ExportAction(table.getShell(), model));
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        table.setMenu(manager.createContextMenu(table));

        // Allow extensions to add to the context menu
        if (site != null)
        	site.registerContextMenu(manager, table_viewer);
    }

    /** Update GUI when model changed
     *  @see ModelListener
     */
    @Override
    public void modelChanged(final Model model)
    {   // Can be called from background thread...
        Display.getDefault().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (start.isDisposed())
                    return;
                start.setText(model.getStartSpec());
                end.setText(model.getEndSpec());
                table_viewer.refresh();
            }
        });
    }
}
