/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.device.DeviceInfo;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/** Descriptor for a property that holds a String for a <code>DeviceInfo</code>
 *  @author Kay Kasemir
 */
public class DeviceInfoPropertyDescriptor extends PropertyDescriptor
{
    final private DeviceInfo[] devices;

    /** Initialize
     *  @param id ID to edit
     *  @param label Label to show
     *  @param devices Available devices
     */
    public DeviceInfoPropertyDescriptor(final String id, final String label, final DeviceInfo[] devices)
    {
        super(id, label);
        this.devices = devices;
    }

    /** {@inheritDoc} */
    @Override
    public CellEditor createPropertyEditor(final Composite parent)
    {
        final CellEditor editor = new DeviceInfoCellEditor(parent, devices);
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
}
