/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import java.util.Collection;

import org.csstudio.autocomplete.ui.AutoCompleteUIHelper;
import org.csstudio.autocomplete.ui.AutoCompleteTypes;
import org.csstudio.scan.device.DeviceInfo;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/** Descriptor for a property that holds a String for a <code>DeviceInfo</code>
 *  @author Kay Kasemir
 */
public class DeviceInfoPropertyDescriptor extends PropertyDescriptor
{
    final private Collection<DeviceInfo> devices;

    /** Initialize
     *  @param id ID to edit
     *  @param label Label to show
     *  @param devices Available devices
     */
    public DeviceInfoPropertyDescriptor(final String id, final String label, final Collection<DeviceInfo> devices)
    {
        super(id, label);
        this.devices = devices;
    }

    /** {@inheritDoc} */
    @Override
    public CellEditor createPropertyEditor(final Composite parent)
    {
        CellEditor editor = null;
        if (devices == null || devices.isEmpty()) {
            // If no device are defined in configuration,
            // provides a PV autocomplete cell editor
            editor = AutoCompleteUIHelper.createAutoCompleteTextCellEditor(
                    parent, AutoCompleteTypes.PV);
        } else {
            // Otherwise, provides the devices list
            editor = new DeviceInfoCellEditor(parent, devices);
        }
        if (getValidator() != null)
            editor.setValidator(getValidator());
        return editor;
    }
}
