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

import org.csstudio.scan.server.ScanInfo;
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
    /** View ID defined in plugin.xml */
    final public static String ID = "org.csstudio.scan.ui.plot.view";
    
    private PlotDataModel model;

    private Plot plot;

    private ScanSelectorAction scan_selector;
    
    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        try
        {
            model = new PlotDataModel(parent.getDisplay());
            model.start();
        }
        catch (Exception ex)
        {
            final Label l = new Label(parent, 0);
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
                model.stop();
                model = null;
            }
        });
        
        createComponents(parent);
    }

    /** @param parent Parent composite under which to create GUI elements */
    private void createComponents(final Composite parent)
    {
        plot = new Plot(parent);
        plot.addTrace(model.getPlotData());
        
        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        scan_selector = new ScanSelectorAction(model, plot);
        toolbar.add(scan_selector);
        toolbar.add(DeviceSelectorAction.forXAxis(model, plot));
        toolbar.add(DeviceSelectorAction.forYAxis(model, plot));
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }

    /** @param scan_id ID of scan to select */
    public void selectScan(final long scan_id)
    {
        final ScanInfo scan = model.getScan(scan_id);
        if (scan != null)
        {
            final String option = ScanSelectorAction.encode(scan);
            scan_selector.setSelection(option);
            scan_selector.handleSelection(option);
        }
    }
}
