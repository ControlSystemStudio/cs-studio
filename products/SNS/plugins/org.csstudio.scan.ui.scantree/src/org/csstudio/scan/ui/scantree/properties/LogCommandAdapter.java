/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.GUI;
import org.csstudio.scan.ui.scantree.Messages;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link LogCommand} to {@link IPropertySource}.
 *  @author Kay Kasemir
 */
public class LogCommandAdapter extends ScanCommandAdapter<LogCommand>
{
    final public static String DEVICES = "DEVICES"; //$NON-NLS-1$
    
    final private static PropertyDescriptor devices_property;

    /** Create property descriptors */
    static
    {
        devices_property = new TextPropertyDescriptor(DEVICES, Messages.Lbl_LogDevices);
        devices_property.setCategory(Messages.Cat_Log);
    }
    
    /** Initialize
     *  @param gui GUI to notify on change of command
     *  @param command {@link ScanCommand}
     */
    public LogCommandAdapter(final GUI gui, final LogCommand command)
    {
        super(gui, command);
    }
    
    /** {@inheritDoc} */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[]
        {
            devices_property
        };
        return descriptors;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            final LogCommand command = getCommand();
            if (id == DEVICES)
                command.setDeviceNames(decode(value.toString().trim()));
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
        if (id == DEVICES)
        {
            final String[] names = getCommand().getDeviceNames();
            final StringBuilder result = new StringBuilder();
            for (String name : names)
            {
                if (result.length() > 0)
                    result.append(", "); //$NON-NLS-1$
                result.append(name);
            }
            return result.toString();
        }
        return null;
    }

    /** @param device_list String that lists device names, separated by space and comma
     *  @return Array of device names
     */
    public String[] decode(final String device_list)
    {
        return device_list.split("[ ,]+"); //$NON-NLS-1$
    }
}
