/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import java.util.List;

import org.csstudio.apputil.ui.swt.DropdownToolbarAction;

/** Toolbar actions to select a device for the 'X' or 'Y' axis
 *  @author Kay Kasemir
 */
abstract public class DeviceSelectorAction extends DropdownToolbarAction
{
    /** Scan model */
    final protected PlotDataModel model;

    /** Plot */
    final protected Plot plot;

    /** @param model
     *  @return X axis device selector
     */
    public static DeviceSelectorAction forXAxis(final PlotDataModel model, final Plot plot)
    {
        return new DeviceSelectorAction(model, plot, "X Axis", "Select device for horizontal axis")
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                this.model.selectXDevice(item);
                this.plot.setXAxisTitle(item);
            }
        };
    }

    /** @param model
     *  @return Y axis device selector
     */
    public static DeviceSelectorAction forYAxis(final PlotDataModel model, final Plot plot)
    {
        return new DeviceSelectorAction(model, plot, "Y Axis", "Select device for vertical axis")
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                this.model.selectYDevice(item);
                this.plot.setYAxisTitle(item);
            }
        };
    }
    
    /** Initialize 
     *  @param model
     *  @param label
     *  @param tooltip
     */
    private DeviceSelectorAction(final PlotDataModel model, final Plot plot,
            final String label,
            final String tooltip)
    {
        super(label, tooltip);
        this.model = model;
        this.plot = plot;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        final List<String> devices = model.getDevices();
        if (devices == null)
            return new String[0];
        return devices.toArray(new String[devices.size()]);
    }
}
