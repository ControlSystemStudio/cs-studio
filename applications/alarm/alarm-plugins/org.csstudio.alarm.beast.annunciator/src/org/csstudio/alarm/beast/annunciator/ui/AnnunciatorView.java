/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.ui;

import java.util.logging.Level;

import org.csstudio.alarm.beast.annunciator.Activator;
import org.csstudio.alarm.beast.annunciator.Messages;
import org.csstudio.alarm.beast.annunciator.Preferences;
import org.csstudio.alarm.beast.annunciator.model.AnnunciationMessage;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciator;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciatorListener;
import org.csstudio.alarm.beast.annunciator.model.Severity;
import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.ui.util.MinSizeTableColumnLayout;
import org.csstudio.utility.speech.NoSoundCardAvailableException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/** Eclipse view for the Annunciator
 *  @author Kay Kasemir
 */
public class AnnunciatorView extends ViewPart implements JMSAnnunciatorListener
{
    /** View ID registered in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.annunciator.view"; //$NON-NLS-1$

    /** Annunciator that performs the actual annunciations. */
    private volatile JMSAnnunciator annunciator = null;

    /** Table of recent annunciations */
    private TableViewer message_table;

    private SilenceAction silenceAction;

    /** List of recent annunciations, shown in message_table.
     *  Synchronize on access
     */
    final private RingBuffer<AnnunciationMessage> messages =
        new RingBuffer<AnnunciationMessage>(Preferences.getRingBufferSize());

    /** Job that handles the connection.
     *  Will be set to <code>null</code> when connected
     */
    private ConnectJob connect_job;

    @Override
    public void createPartControl(final Composite parent)
    {
        // TODO Better handling of the JMSAnnunciator start/stop?
        // When restarting CSS, the Annunciator View could
        // be 'hidden' behind other tabs.
        // Such a hidden view is nothing but a title in the tab.
        // The View is not really created, hence no annunciator is
        // running
        // -> Must always keep the annunciator view visible!
        //
        // When closing the AnnunciatorView while it is still part of
        // another currently loaded perspective, the annunciator will just hide
        // to allow quick re-open in this view or when switching to one
        // of the other perspectives which had the annuniator open.
        // To the user that means: Annunciations continue even after closing
        // the view.
        // Tried IPartListener2, but no good solution at this point.
//        final IPartService service =
//            (IPartService) getSite().getService(IPartService.class);
//        service.addPartListener(new IPartListener2()
//        {
// ...
//        });

        createGUI(parent);

        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        addToolbarActions(toolbar);

        // Fake initial message that shows up in table
        messages.add(new AnnunciationMessage(Severity.forInfo(), Messages.ConnectMsg));

        // Connect table to message list
        message_table.setContentProvider(new MessageRingBufferContentProvider());
        message_table.setInput(messages);

        // Start connection in background job because it hangs when
        // there's no JMS
        connect_job = new ConnectJob(this);
        connect_job.schedule();

        // ConnectJob would set annunciator. Cleanup when view is disposed.
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                // If we were still trying to connect, give up
                if (connect_job != null)
                    connect_job.cancel();
                if (annunciator != null)
                {   // Did connect? close
                    annunciator.close();
                    annunciator = null;
                }
            }
        });
    }

    /** Create GUI Elements
     *  @param parent
     */
    private void createGUI(final Composite parent)
    {
        // Note: TableColumnLayout requires that Table is only one child widget
        final TableColumnLayout table_layout = new MinSizeTableColumnLayout(50);
        parent.setLayout(table_layout);

        // List of annunciations
        message_table = new TableViewer(parent ,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION
                | SWT.VIRTUAL);
        final Table table = message_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn col;

        // Time
        col = createColumn(message_table, table_layout, Messages.Time, 150, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AnnunciationMessage message = (AnnunciationMessage) cell.getElement();
                cell.setText(message.getTimestamp().toString());
            }
        });

        // Severity
        col = createColumn(message_table, table_layout, Messages.Severity, 80, 1);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AnnunciationMessage message = (AnnunciationMessage) cell.getElement();
                cell.setText(message.getSeverity().getName());
            }
        });

        // Message
        col = createColumn(message_table, table_layout, Messages.Message, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AnnunciationMessage message = (AnnunciationMessage) cell.getElement();
                cell.setText(message.getMessage());
            }
        });
    }

    /** Create column with layout info
     *  @param message_table
     *  @param table_layout
     *  @param title
     *  @param width
     *  @param weight
     *  @return TableViewerColumn
     */
    private TableViewerColumn createColumn(final TableViewer message_table,
            final TableColumnLayout table_layout, final String title,
            final int width, final int weight)
    {
        final TableViewerColumn view_col = new TableViewerColumn(message_table, 0);
        final TableColumn col = view_col.getColumn();
        col.setText(title);
        table_layout.setColumnData(col, new ColumnWeightData(weight, width));
        col.setMoveable(true);

        return view_col;
    }

    @Override
    public void setFocus()
    {
        // NOP
    }

    /** @param toolbar Tool bar to which to add */
    private void addToolbarActions(final IToolBarManager toolbar)
    {
        silenceAction = new SilenceAction(this);
        toolbar.add(silenceAction);
        toolbar.add(new ClearAction(this));
    }

    /** Called by ConnectJob on success */
    public void setAnnunciator(final JMSAnnunciator annunciator)
    {
        final Control control = message_table.getControl();
        if (control.isDisposed())
            return;
        control.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (messages)
                {
                    messages.clear();
                }
                // Remember that the connection is done
                connect_job = null;
                AnnunciatorView.this.annunciator = annunciator;
                annunciator.start();
            }
        });
    }

    public void setAnnunciationsEnabled(final boolean enable)
    {
        if (annunciator != null)
            annunciator.setEnabled(enable);

        // Update background color
        final Table table = message_table.getTable();
        if (enable)
            table.setBackground(null);
        else
            table.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_MAGENTA));
    }

    /** {@inheritDoc} */
    @Override
    public void performedAnnunciation(final AnnunciationMessage annunciation)
    {
        logAnnunciation(annunciation);
    }

    /** Called by ConnectJob or later Annunciator on error
     *  {@inheritDoc}
     */
    @Override
    public void annunciatorError(final Throwable ex)
    {
        if(NoSoundCardAvailableException.class.equals(ex.getClass())) {
            // Update table in UI thread
            final Control control = message_table.getControl();
            if (control.isDisposed())
                return;
            control.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (control.isDisposed())
                        return;
                    silenceAction.setEnabled(false);
                    setAnnunciationsEnabled(false);
                }
            });
            logAnnunciation(new AnnunciationMessage(Severity.forError(), ex.getMessage()));
            return;
        }
        logAnnunciation(new AnnunciationMessage(Severity.forError(), ex.getMessage()));
        Activator.getLogger().log(Level.WARNING, "Annunciator Error", ex); //$NON-NLS-1$
    }

    /** @param annunciation Annunciation to add to list of messages */
    private void logAnnunciation(final AnnunciationMessage annunciation)
    {
        final Control control = message_table.getControl();
        if (control.isDisposed())
            return;
        // Messages 'scroll', so every line in the table changes.
        // Overall table refresh is accomplished by setting the item count.
        // Sync & fetch count in this thread
        final int count;
        synchronized (messages)
        {
            messages.add(annunciation);
            count = messages.size();
        }
        // Update table in UI thread
        control.getDisplay().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                if (control.isDisposed())
                    return;
                message_table.setItemCount(count);
                message_table.refresh();
            }
        });
    }

    /** Remove all annunciations */
    public void clearAnnunciations()
    {
        synchronized (messages)
        {
            messages.clear();
        }
        message_table.setItemCount(0);
        message_table.refresh();
    }
}
