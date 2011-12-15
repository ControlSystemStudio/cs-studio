/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.ui.scantree.Messages;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link SetCommand} to {@link IPropertySource}.
 *  @author Kay Kasemir
 */
public class SetCommandAdapter extends ScanCommandAdapter<SetCommand>
{
    final private static String DEVICE = "DEVICE"; //$NON-NLS-1$

    final private static String VALUE = "VALUE"; //$NON-NLS-1$
    
    final private static PropertyDescriptor device_property;
    
    final private static PropertyDescriptor value_property;

    /** Create property descriptors */
    static
    {
        device_property = new TextPropertyDescriptor(DEVICE, Messages.Lbl_Device);
        value_property = new TextPropertyDescriptor(VALUE, Messages.Lbl_SetValue);
        final String set = Messages.Cat_Set;
        device_property.setCategory(set);
        value_property.setCategory(set);
    }
    
    /** Initialize
     *  @param editor GUI to notify on change of command
     *  @param command {@link ScanCommand}
     */
    public SetCommandAdapter(final ScanEditor editor, final SetCommand command)
    {
        super(editor, command);
    }
    
    /** {@inheritDoc} */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[]
        {
            device_property,
            value_property
        };
        return descriptors;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            final SetCommand command = getCommand();
            if (id == DEVICE)
                command.setDeviceName(value.toString());
            else if (id == VALUE)
                command.setValue(Double.parseDouble(value.toString().trim()));
            refreshCommand(command);
        }
        catch (NumberFormatException ex)
        {
            // Ignore, keeping original value
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object getPropertyValue(final Object id)
    {
        if (id == DEVICE)
            return getCommand().getDeviceName();
        else if (id == VALUE)
            return getCommand().getValue().toString();
        return null;
    }
}
