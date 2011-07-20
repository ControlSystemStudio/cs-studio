/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.alarm.beast.ui.actions.AcknowledgeAction;
import org.csstudio.alarm.beast.ui.actions.MaintenanceModeAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the alarm table.
 *  @author Kay Kasemir
 */
public class AlarmTableView extends ViewPart
{
    /** ID of view, defined in plugin.xml */
    public static final String ID = "org.csstudio.alarm.beast.ui.alarmtable.view"; //$NON-NLS-1$

    private AlarmClientModel model;

    @Override
    public void createPartControl(final Composite parent)
    {
        try
        {
            model = AlarmClientModel.getInstance();
        }
        catch (final Throwable ex)
        {   // Instead of actual GUI, create error message
            final String error = ex.getCause() != null
                ? ex.getCause().getMessage()
                : ex.getMessage();
            final String message = NLS.bind(Messages.ServerErrorFmt, error);
            // Add to log, also display in text widget
            Logger.getLogger(Activator.ID).log(Level.SEVERE, "Cannot load alarm model", ex); //$NON-NLS-1$
            parent.setLayout(new FillLayout());
            new Text(parent, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI)
                .setText(message);
            return;
        }

        // Arrange for model to be released
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.release();
                model = null;
            }
        });

        // Add GUI to model
        final GUI gui = new GUI(parent, model, getSite());
        if (model.isWriteAllowed())
        {
            // Add Toolbar buttons
            final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
            toolbar.add(new MaintenanceModeAction(model));
            toolbar.add(new Separator());
            AcknowledgeAction action = new AcknowledgeAction(true, gui.getActiveAlarmTable());
            action.clearSelectionOnAcknowledgement(gui.getActiveAlarmTable());
			toolbar.add(action);
			action = new AcknowledgeAction(false, gui.getAcknowledgedAlarmTable());
            action.clearSelectionOnAcknowledgement(gui.getAcknowledgedAlarmTable());
			toolbar.add(action);
        }
    }

    @Override
    public void setFocus()
    {
        // NOP
    }
}
