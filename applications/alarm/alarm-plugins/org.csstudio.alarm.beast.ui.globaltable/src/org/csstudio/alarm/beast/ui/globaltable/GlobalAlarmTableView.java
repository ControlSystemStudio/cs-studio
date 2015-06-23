/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import java.util.List;

import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.ContextMenuHelper;
import org.csstudio.alarm.beast.ui.actions.AlarmPerspectiveAction;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModel;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModelListener;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.csstudio.utility.singlesource.UIHelper.UI;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;

/** Eclipse 'View' for global alarms
 *  @author Kay Kasemir
 */
public class GlobalAlarmTableView extends ViewPart
{
    /** View ID defined in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.ui.globaltable.view"; //$NON-NLS-1$

    /** Table viewer for GlobalAlarm rows */
    private TableViewer table_viewer;

    // ViewPart
    @Override
    public void createPartControl(final Composite parent)
    {
        table_viewer = createTable(parent);
        // createTable already handles the layout of the parent and the only widget in the view
        //  GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(parent);

        // Connect to model
        final GlobalAlarmModel model = GlobalAlarmModel.reference();
        table_viewer.setInput(model);
        final GlobalAlarmModelListener listener = new GlobalAlarmModelListener()
        {
            @Override
            public void globalAlarmsChanged(final GlobalAlarmModel model)
            {
                final Table table = table_viewer.getTable();
                if (table.isDisposed())
                    return;
                table.getDisplay().asyncExec(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (! table.isDisposed())
                            table_viewer.refresh();
                    }
                });
            }
        };
        model.addListener(listener);

        // Arrange to be disconnected from model
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.removeListener(listener);
                model.release();
            }
        });

        addContextMenu(table_viewer, getSite());
    }

    /** Add context menu
     *  @param table_viewer
     *  @param site Workbench site or <code>null</code>
     */
    private void addContextMenu(final TableViewer table_viewer,
            final IWorkbenchPartSite site)
    {
        final Table table = table_viewer.getTable();
        final boolean isRcp = UI.RCP.equals(SingleSourcePlugin.getUIHelper()
                .getUI());

        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(new IMenuListener()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void menuAboutToShow(final IMenuManager manager)
            {
                // TODO 'Select configuration' action
                final List<AlarmTreeItem> items =
                    ((IStructuredSelection)table_viewer.getSelection()).toList();
                new ContextMenuHelper(null, manager, table.getShell(), items, false);
                manager.add(new Separator());
                if(isRcp) {
                    manager.add(new AlarmPerspectiveAction());
                    manager.add(new Separator());
                }
                manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        table.setMenu(manager.createContextMenu(table));

        // Allow extensions to add to the context menu
        if (site != null)
            site.registerContextMenu(manager, table_viewer);
    }

    // ViewPart
    @Override
    public void setFocus()
    {
        table_viewer.getTable().setFocus();
    }

    /** @param parent Parent widget
     *  @return Table viewer for GlobalAlarmModel
     */
    private TableViewer createTable(final Composite parent)
    {
        // TableColumnLayout requires the TableViewer to be in its
        // own Composite! For now, the 'parent' is used because there is
        // no other widget in the view.
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final TableColumnLayout table_layout = new MinSizeTableColumnLayout(10);
        parent.setLayout(table_layout);

        final TableViewer table_viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        createColumns(table_viewer, table_layout);
        table_viewer.setContentProvider(new GlobalAlarmContentProvider());

        ColumnViewerToolTipSupport.enableFor(table_viewer);

        return table_viewer;
    }

    /** @param table_viewer {@link TableViewer} to which to add columns for GlobalAlarm display
     *  @param table_layout {@link TableColumnLayout} to use for column auto-sizing
     */
    private void createColumns(final TableViewer table_viewer, final TableColumnLayout table_layout)
    {
        for (GlobalAlarmColumnInfo info : GlobalAlarmColumnInfo.values())
        {
            final TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
            final TableColumn col = view_col.getColumn();
            col.setText(info.getTitle());
            table_layout.setColumnData(col, info.getLayoutData());
            col.setMoveable(true);
            view_col.setLabelProvider(info.getLabelProvider());
            col.addSelectionListener(new GobalAlarmColumnSortingSelector(table_viewer, col, info));
        }
    }
}
