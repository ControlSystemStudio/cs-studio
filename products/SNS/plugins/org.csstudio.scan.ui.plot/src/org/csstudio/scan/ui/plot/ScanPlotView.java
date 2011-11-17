/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the scan plot
 *  @author Kay Kasemir
 */
public class ScanPlotView extends ViewPart
{
    private PlotDataModel model;

    public ScanPlotView()
    {
        // TODO Auto-generated constructor stub
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        try
        {
            model = new PlotDataModel();
        }
        catch (Exception ex)
        {
            Label l = new Label(parent, 0);
            l.setText("Error getting scan info: " + ex.getMessage());
            Logger.getLogger(getClass().getName()).
                log(Level.WARNING, "Error getting scan info", ex);
            return;
        }
        
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.dispose();
                model = null;
            }
        });
        
        createComponents(parent);
    }

    private void createComponents(final Composite parent)
    {
        final Plot plot = new Plot(parent);
        plot.addTrace();
        
        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add(new ScanSelectorAction(model));
        toolbar.add(DeviceSelectorAction.forXAxis(model));
        toolbar.add(DeviceSelectorAction.forYAxis(model));
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }
}
