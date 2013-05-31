/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor;

import org.csstudio.scan.client.ScanInfoModel;
import org.csstudio.scan.ui.scanmonitor.actions.AbortAction;
import org.csstudio.scan.ui.scanmonitor.actions.InfoAction;
import org.csstudio.scan.ui.scanmonitor.actions.PauseAction;
import org.csstudio.scan.ui.scanmonitor.actions.ResumeAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View that displays the {@link GUI}
 *  @author Kay Kasemir
 */
public class ScanMonitorView extends ViewPart
{
    private ScanInfoModel model;

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        // Create and start Model
        try
        {
            model = ScanInfoModel.getInstance();
        }
        catch (Exception ex)
        {
            Label l = new Label(parent, 0);
            l.setText(NLS.bind(Messages.ErrorMsgFmt, ex.getClass().getName(), ex.getMessage()));
            model = null;
            return;
        }

        // Connect to view
        new GUI(parent, model, getSite());

        // Stop model when view is closed
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.release();
            }
        });

        // Toolbar actions (duplicating context menu actions)
        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        final Shell shell = parent.getShell();
		toolbar.add(new InfoAction(shell, model));
        toolbar.add(new ResumeAction(shell, model, null));
        toolbar.add(new PauseAction(shell, model, null));
        toolbar.add(new AbortAction(shell, model, null));
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }
}
