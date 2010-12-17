/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarm;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModel;
import org.csstudio.alarm.beast.ui.globalclientmodel.GlobalAlarmModelListener;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/** Eclipse 'View' for global alarms
 *
 *  TODO Sort by column header
 *  TODO Tooltip with detail
 *  TODO Context menu to select configuration, show guidance etc.
 *
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
        final TableColumnLayout table_layout = new TableColumnLayout();
        parent.setLayout(table_layout);

        final TableViewer table_viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        final Table table = table_viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        createColumns(table_viewer, table_layout);
        table_viewer.setContentProvider(new GlobalAlarmContentProvider());

        return table_viewer;
    }

    /** @param table_viewer {@link TableViewer} to which to add columns for GlobalAlarm display
     *  @param table_layout {@link TableColumnLayout} to use for column auto-sizing
     */
    private void createColumns(final TableViewer table_viewer, final TableColumnLayout table_layout)
    {
        // PV Path
        TableViewerColumn view_col = new TableViewerColumn(table_viewer, 0);
        TableColumn col = view_col.getColumn();
        col.setText(Messages.AlarmPV);
        table_layout.setColumnData(col, new ColumnWeightData(100, 100, true));
        col.setMoveable(true);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final GlobalAlarm alarm = (GlobalAlarm) cell.getElement();
                cell.setText(alarm.getPathName());
            }
        });

        // TODO Would be nice to display description, but that's fetched
        //      in background and not available when alarm first displayed...
        //      Send descr. with alarm server update?
        //      Delay display until RDB info available?

        // Time
        view_col = new TableViewerColumn(table_viewer, 0);
        col = view_col.getColumn();
        col.setText(Messages.AlarmTime);
        table_layout.setColumnData(col, new ColumnWeightData(50, 80, true));
        col.setMoveable(true);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final GlobalAlarm alarm = (GlobalAlarm) cell.getElement();
                cell.setText(alarm.getTimestampText());
            }
        });

        // Severity
        view_col = new TableViewerColumn(table_viewer, 0);
        col = view_col.getColumn();
        col.setText(Messages.AlarmSeverity);
        table_layout.setColumnData(col, new ColumnWeightData(30, 50, true));
        col.setMoveable(true);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final GlobalAlarm alarm = (GlobalAlarm) cell.getElement();
                cell.setText(alarm.getSeverity().getDisplayName());
            }
        });

        // Message
        view_col = new TableViewerColumn(table_viewer, 0);
        col = view_col.getColumn();
        col.setText(Messages.AlarmMessage);
        table_layout.setColumnData(col, new ColumnWeightData(30, 45, true));
        col.setMoveable(true);
        view_col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final GlobalAlarm alarm = (GlobalAlarm) cell.getElement();
                cell.setText(alarm.getMessage());
            }
        });
    }
}
