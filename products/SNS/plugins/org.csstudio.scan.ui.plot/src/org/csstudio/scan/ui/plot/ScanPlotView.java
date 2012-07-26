/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.server.ScanInfo;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/** Eclipse View for the scan plot
 *  @author Kay Kasemir
 */
public class ScanPlotView extends ViewPart
{
    /** Memento tags */
    @SuppressWarnings("nls")
    final private static String TAG_SCAN = "scan",
                                TAG_XDEVICE = "xdevice",
                                TAG_YDEVICE = "ydevice"; // used with added 0, 1, 2, ...

    /** Instance of this view, used to create the secondary view ID
     *  necessary to allow multiple views
     */
    final private static AtomicInteger instance = new AtomicInteger(0);

    /** State saved from previous instance */
    private IMemento memento = null;

    /** Data model */
    private PlotDataModel model;

    // GUI elements
    private Plot plot;
    private ScanSelectorAction scan_selector;
    private DeviceSelectorAction y_selector;
    private DeviceSelectorAction x_selector;
    private Action y_removal;

    /** @return Next secondary view ID for creating multiple plot views */
    public static String getNextViewID()
    {
        return Integer.toString(instance.incrementAndGet());
    }

    /** {@inheritDoc} */
    @Override
    public void init(final IViewSite site, final IMemento memento) throws PartInitException
    {
        super.init(site, memento);
        this.memento  = memento;
    }

    /** {@inheritDoc} */
    @Override
    public void createPartControl(final Composite parent)
    {
        try
        {
            model = new PlotDataModel(parent.getDisplay());
        }
        catch (Exception ex)
        {
            final Label l = new Label(parent, 0);
            l.setText("Error getting scan info: " + ex.getMessage()); //$NON-NLS-1$
            Logger.getLogger(getClass().getName()).
                log(Level.WARNING, "Error getting scan info", ex); //$NON-NLS-1$
            return;
        }

        createComponents(parent);
        restoreState();

        // Start model after GUI has been created and updated to saved state
        model.start();
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                model.stop();
                model = null;
            }
        });
    }

    /** @param parent Parent composite under which to create GUI elements */
    private void createComponents(final Composite parent)
    {
        plot = new Plot(parent);

        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        scan_selector = new ScanSelectorAction(model, plot);
        x_selector = DeviceSelectorAction.forXAxis(model, plot);
        y_selector = DeviceSelectorAction.forYAxis(model, 0, plot);
        y_removal = DeviceSelectorAction.forYAxisRemoval(model, plot, this);
        toolbar.add(scan_selector);
        toolbar.add(x_selector);
        toolbar.add(y_selector);
        toolbar.add(DeviceSelectorAction.forNewYAxis(model, plot, this));
        toolbar.add(y_removal);
    }

    /** {@inheritDoc} */
    @Override
    public void saveState(final IMemento memento)
    {
        if (scan_selector.getSelection() != null)
            memento.putString(TAG_SCAN, scan_selector.getSelection());
        if (x_selector.getSelection() != null)
            memento.putString(TAG_XDEVICE, x_selector.getSelection());
        final String[] devices = model.getYDevices();
        for (int i=0; i<devices.length; ++i)
            memento.putString(TAG_YDEVICE + i, devices[i]);
    }

    /** Restore saved state from memento */
    private void restoreState()
    {
        if (memento == null)
            return;
        String value = memento.getString(TAG_SCAN);
        if (value != null)
        {
            scan_selector.setSelection(value);
            scan_selector.handleSelection(value);
        }

        value = memento.getString(TAG_XDEVICE);
        if (value != null)
        {
            x_selector.setSelection(value);
            model.selectXDevice(value);
        }

        final List<String> devices = new ArrayList<String>();
        String device = memento.getString(TAG_YDEVICE + devices.size());
        while (device != null)
        {
            devices.add(device);
            device = memento.getString(TAG_YDEVICE + devices.size());
        }
        model.selectYDevices(devices);

        // Update selectors in toolbar to show devices
        updateToolbar();

        // Now that model has been configured, set plot's data
        plot.setDataProviders(model.getPlotDataProviders());
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        // NOP
    }

    /** Select a scan to display in the plot
     *  @param name  Scan name  (might be replaced with the actual scan name)
     *  @param id    ID of scan to select
     */
    public void selectScan(final String name, final long id)
    {
        final String option;
        // Try to use the 'correct' scan information for the ID,
        // but if the model has not (yet) obtained that,
        // use the name and ID as given
        final ScanInfo scan = model.getScan(id);
        if (scan != null)
            option = ScanSelectorAction.encode(scan);
        else
            option = ScanSelectorAction.encode(name, id);

        scan_selector.setSelection(option);
        scan_selector.handleSelection(option);
        model.selectXDevice(option);
        plot.setDataProviders(this.model.getPlotDataProviders());
    }

    /** Select the devices to show
     *  @param devices X device, Y devices...
     */
    public void selectDevices(final String... devices)
    {
        if (devices.length > 0)
            model.selectXDevice(devices[0]);
        final List<String> y_devices = new ArrayList<String>();
        for (int i=1; i<devices.length; ++i)
            y_devices.add(devices[i]);
        model.selectYDevices(y_devices);
        plot.setDataProviders(this.model.getPlotDataProviders());
        updateToolbar();
    }

    /** Update toolbar to display selectors for the devices shown in the plot */
    void updateToolbar()
    {
        final String[] devices = model.getYDevices();
        final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        // Add/update Y axis selectors
        for (int i=0; i<devices.length; ++i)
        {
            final ActionContributionItem item = (ActionContributionItem) toolbar.find(DeviceSelectorAction.ID_Y + i);
            final DeviceSelectorAction selector;
            if (item != null)
                selector = (DeviceSelectorAction) item.getAction();
            else
            {
                selector = DeviceSelectorAction.forYAxis(model, i, plot);
                toolbar.insertBefore(DeviceSelectorAction.ID_ADD, selector);
                toolbar.update(true);
            }
            selector.setSelection(devices[i]);
        }
        // Remove extra Y selectors, if there are any
        int i = devices.length;
        ActionContributionItem item = (ActionContributionItem) toolbar.find(DeviceSelectorAction.ID_Y + i);
        while (item != null)
        {
            final DeviceSelectorAction selector = (DeviceSelectorAction) item.getAction();
            if (devices.length <= 0)
                selector.setSelection(""); //$NON-NLS-1$
            else
            {
                toolbar.remove(item);
                toolbar.update(true);
            }
            ++i;
            item = (ActionContributionItem) toolbar.find(DeviceSelectorAction.ID_Y + i);
        }

        y_removal.setEnabled(devices.length > 1);
    }
}
