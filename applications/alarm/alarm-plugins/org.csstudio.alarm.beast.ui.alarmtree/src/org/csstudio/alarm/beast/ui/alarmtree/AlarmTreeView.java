/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtree;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.alarm.beast.Preferences;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.ui.actions.AcknowledgeAction;
import org.csstudio.alarm.beast.ui.actions.ConfigureItemAction;
import org.csstudio.alarm.beast.ui.actions.MaintenanceModeAction;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmClientModel;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/** Eclipse view that displays the alarm tree.
 *  @author Kay Kasemir
 */
public class AlarmTreeView extends ViewPart
{
    /** ID of the view a defined in plugin.xml */
    final public static String ID = "org.csstudio.alarm.beast.ui.alarmtree.View"; //$NON-NLS-1$

    private AlarmClientModel model;

    private GUI gui = null;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        try
        {
            model = AlarmClientModel.getInstance();
        }
        catch (final Throwable ex)
        {   // Instead of actual GUI, create error message
            final String message =
                NLS.bind(Messages.CannotGetAlarmInfoFmt,
                        ex.getCause() != null
                        ? ex.getCause().getMessage()
                        : ex.getMessage());

            // Add to log, also display in text
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

        // Have model, create GUI
        gui = new GUI(parent, model, getViewSite());

        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        if (Preferences.isConfigSelectionAllowed())
        {
            toolbar.add(new SelectConfigurationAction(parent, model));
            toolbar.add(new Separator());
        }
        if (model.isWriteAllowed())
        {
            toolbar.add(new MaintenanceModeAction(model));
            toolbar.add(new Separator());
        }

        final Shell shell = parent.getShell();
        toolbar.add(new InfoAction(shell, model));

        if (model.isWriteAllowed())
        {
            // TODO Toolbar layout problems on some OS/WS.
            // On OS X/cocoa, Toolbar buttons 'wrap' around to the next
            // line when the view is too small.
            // On Linux/GTK, however, buttons vanish at the right edge of the view.
            // Tried SWT.Resize listener with toolbar.update(true), no improvement.
            toolbar.add(new DebugAction(shell, model));
            toolbar.add(new ConfigureItemAction(shell, model, gui.getTreeViewer()));
            toolbar.add(new AcknowledgeAction(true, gui.getTreeViewer()));
            toolbar.add(new AcknowledgeAction(false, gui.getTreeViewer()));
            toolbar.add(new Separator());
        }
        toolbar.add(new CollapseAlarmTreeAction(gui));
        toolbar.add(new OnlyAlarmsAction(gui));

        // Inform workbench about currently selected alarm in tree viewer
        getSite().setSelectionProvider(gui.getTreeViewer());
    }




    /** {@inheritDoc} */

    @Override

    public void setFocus()
    {
        if (gui != null)
            gui.setFocus();
    }

    /** @param item Alarm tree item to focus, i.e. to select and show */
    public void setFocus(final AlarmTreeItem item)
    {
        if (gui != null)
            gui.getTreeViewer().setSelection(new StructuredSelection(item));
    }
}
