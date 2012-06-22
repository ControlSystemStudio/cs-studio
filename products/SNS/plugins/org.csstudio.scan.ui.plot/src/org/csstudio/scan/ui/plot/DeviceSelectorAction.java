/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.plot;

import org.csstudio.apputil.ui.swt.DropdownToolbarAction;
import org.eclipse.osgi.util.NLS;

/** Toolbar actions to select a device for the 'X' or 'Y' axis
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class DeviceSelectorAction extends DropdownToolbarAction
{
    /** Action ID for X selector */
    final public static String ID_X = "x";

    /** Action ID for Y selector (plus 0, 1, 2, ...) */
    final public static String ID_Y = "y";

    /** Action ID for adding Y device */
    final public static String ID_ADD = "add";

    /** Scan model */
    final protected PlotDataModel model;

    /** Plot */
    final protected Plot plot;

    /** @param model
     *  @return X axis device selector
     */
    public static DeviceSelectorAction forXAxis(final PlotDataModel model, final Plot plot)
    {
        return new DeviceSelectorAction(ID_X, model, plot, Messages.Device_X, Messages.Device_X_TT)
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                this.model.selectXDevice(item);
                this.plot.setDataProviders(this.model.getPlotDataProviders());
            }
        };
    }

    /** @param model
     *  @return Y axis device selector
     */
    public static DeviceSelectorAction forYAxis(final PlotDataModel model, final int index, final Plot plot)
    {
        final String label;
        if (index == 0)
            label = Messages.Device_Y;
        else
            label = NLS.bind(Messages.Device_Y_Fmt, index + 1);
        return new DeviceSelectorAction(ID_Y + index, model, plot, label, Messages.Device_Y_TT)
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                if (this.model.getYDevices().length < 1)
                    this.model.addYDevice(item);
                else
                    this.model.selectYDevice(index, item);
                this.plot.setDataProviders(this.model.getPlotDataProviders());
            }
        };
    }

    /** @param model
     *  @return Y axis device selector
     */
    public static DeviceSelectorAction forNewYAxis(final PlotDataModel model, final Plot plot,
            final ScanPlotView view)
    {
        return new DeviceSelectorAction(ID_ADD, model, plot, "Add...", "Add trace for another signal to the plot")
        {
            /** {@inheritDoc} */
            @Override
            public void handleSelection(final String item)
            {
                this.model.addYDevice(item);
                this.plot.setDataProviders(this.model.getPlotDataProviders());
                view.updateToolbar();
            }
        };
    }

    /** Initialize
     *  @param id Action ID
     *  @param model Model that provides data
     *  @param plot Plot that displays the data
     *  @param label Label for selector
     *  @param tooltip Tooltip
     */
    private DeviceSelectorAction(final String id, final PlotDataModel model, final Plot plot,
            final String label,
            final String tooltip)
    {
        super(label, tooltip);
        setId(id);
        this.model = model;
        this.plot = plot;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getOptions()
    {
        final String[] devices = model.getDevices();
        if (devices == null)
            return new String[0];
        return devices;
    }
}
