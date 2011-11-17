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
import org.csstudio.scan.data.ScanData;

/** Toolbar actions to select a device for the 'X' or 'Y' axis
 *  @author Kay Kasemir
 */
abstract public class DeviceSelectorAction extends DropdownToolbarAction
{
    final private PlotDataModel model;

    /** @param model
     *  @return X axis device selector
     */
    public static DeviceSelectorAction forXAxis(final PlotDataModel model)
    {
        return new DeviceSelectorAction(model, "X Axis", "Select device for horizontal axis")
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                model.selectXDevice(item);
            }
        };
    }

    /** @param model
     *  @return Y axis device selector
     */
    public static DeviceSelectorAction forYAxis(final PlotDataModel model)
    {
        return new DeviceSelectorAction(model, "Y Axis", "Select device for vertical axis")
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                model.selectYDevice(item);
            }
        };
    }
    
    /** Initialize 
     *  @param model
     *  @param label
     *  @param tooltip
     */
    private DeviceSelectorAction(final PlotDataModel model, final String label,
            final String tooltip)
    {
        super(label, tooltip);
        this.model = model;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        final ScanData data = model.getScanData();
        if (data == null)
            return new String[0];
        final List<String> devices = data.getDevices();
        return devices.toArray(new String[devices.size()]);
    }
}
