/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.ui;

import org.csstudio.alarm.beast.annunciator.Messages;
import org.csstudio.alarm.beast.annunciator.Preferences;
import org.csstudio.alarm.beast.annunciator.model.AnnunciationMessage;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciator;
import org.csstudio.alarm.beast.annunciator.model.JMSAnnunciatorListener;
import org.csstudio.alarm.beast.annunciator.model.Severity;
import org.csstudio.apputil.ringbuffer.RingBuffer;
import org.csstudio.platform.ui.swt.AutoSizeColumn;
import org.csstudio.platform.ui.swt.AutoSizeControlListener;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
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
    
    /** List of recent annunciations, shown in message_table.
     *  Synchronize on access
     */
    final private RingBuffer<AnnunciationMessage> messages =
        new RingBuffer<AnnunciationMessage>(Preferences.getRingBufferSize());
    
    @Override
    public void createPartControl(final Composite parent)
    {
        // TODO Better handling of the JMSAnnunciator start/stop?
        // On OS X, closing the AnnunciatorView will just hide & deactivate
        // the view, just as if it's 'hidden' behind another view.
        // When the opening the view again, that just re-activates the
        // existing view.
        // To the user that means: Annunciations continue even after closing
        // the view.
        // On Linux, the view seems to really close when the visible view
        // is closed.
        // Tried IPartListener2, but no good solution at this point. 
//        final IPartService service =
//            (IPartService) getSite().getService(IPartService.class);
//        service.addPartListener(new IPartListener2()
//        {
// ...
//        });
        
        createGUI(parent);
        
        // Fake initial message that shows up in table
        messages.add(new AnnunciationMessage(Severity.forInfo(), Messages.ConnectMsg));
        
        // Connect table to message list
        message_table.setContentProvider(new MessageRingBufferContentProvider());
        message_table.setInput(messages);
        
        // Start connection in background job because it hangs when
        // there's no JMS
        final ConnectJob connect_job = new ConnectJob(this);
        connect_job.schedule();
        
        // ConnectJob would set annunciator. Cleanup when view is disposed.
        parent.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                if (annunciator != null)
                {
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
        parent.setLayout(new FillLayout());
        
        // List of annunciations
        message_table = new TableViewer(parent ,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION
                | SWT.VIRTUAL);
        final Table table = message_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        TableViewerColumn col;

        // Time
        col = AutoSizeColumn.make(message_table, Messages.Time, 150, 10);
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
        col = AutoSizeColumn.make(message_table, Messages.Severity, 80, 1);
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
        col = AutoSizeColumn.make(message_table, Messages.Message, 100, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final AnnunciationMessage message = (AnnunciationMessage) cell.getElement();
                cell.setText(message.getMessage());
            }
        });

        new AutoSizeControlListener(table);
    }

    @Override
    public void setFocus()
    {
        // NOP
    }

    /** Called by ConnectJob on success */
    public void setAnnunciator(final JMSAnnunciator annunciator)
    {
        final Control control = message_table.getControl();
        if (control.isDisposed())
            return;
        control.getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                synchronized (messages)
                {
                    messages.clear();
                }
                AnnunciatorView.this.annunciator = annunciator;
                annunciator.start();
            }
        });
    }

    /** {@inheritDoc} */
    public void performedAnnunciation(final AnnunciationMessage annunciation)
    {
        final Control control = message_table.getControl();
        if (control.isDisposed())
            return;
        control.getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                if (control.isDisposed())
                    return;
                final int size;
                synchronized (messages)
                {
                    messages.add(annunciation);
                    size = messages.size();
                }
                message_table.setItemCount(size);
                message_table.refresh();
            }
        });
    }

    /** Called by ConnectJob or later Annunciator on error
     *  {@inheritDoc}
     */
    public void annunciatorError(final Exception ex)
    {
        ex.printStackTrace();
    }
}
