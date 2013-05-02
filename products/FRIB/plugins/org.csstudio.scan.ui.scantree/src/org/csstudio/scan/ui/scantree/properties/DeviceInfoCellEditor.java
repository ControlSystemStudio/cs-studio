/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.device.DeviceInfo;
import org.eclipse.swt.widgets.Composite;

/** CellEditor for String that refers to DeviceInfo
 *
 *  <p>Value is string, but available options
 *  are names of devices
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceInfoCellEditor extends OptionListCellEditor
{
    /** Initialize
     *  @param parent Parent widget
     *  @param devices Available devices
     */
    public DeviceInfoCellEditor(final Composite parent, final DeviceInfo[] devices)
    {
        super(parent, getLabels(devices));
    }

    @Override
    protected boolean isReadOnly()
    {
        return false;
    }

    /** @param devices Available devices
     *  @return Labels to use in editor
     */
    private static String[] getLabels(final DeviceInfo[] devices)
    {
        final String[] labels = new String[1+devices.length];
        labels[0] = ""; // Allow selecting "no" device
        for (int i=0; i<devices.length; ++i)
            labels[i+1] = devices[i].getAlias();
        return labels;
    }

    /** Turn label into device name */
    @Override
    protected Object optionForLabel(final String label)
    {
        return label;
    }

    /** Turn device name into label */
    @Override
    protected String labelForOption(final Object value)
    {
        if (! (value instanceof String))
            throw new Error("DeviceInfoCellEditor called with " + value.getClass().getName());
        return (String) value;
    }
}
