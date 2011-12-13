/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.WaitCommand;
import org.csstudio.scan.ui.scantree.GUI;
import org.csstudio.scan.ui.scantree.Messages;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link WaitCommand} to {@link IPropertySource}.
 *  @author Kay Kasemir
 */
public class WaitForValueCommandAdapter extends ScanCommandAdapter<WaitCommand>
{
    final private static String DEVICE = "DEVICE"; //$NON-NLS-1$
    final private static String DESIRED = "DESIRED"; //$NON-NLS-1$
    final private static String TOLERANCE = "TOLERANCE"; //$NON-NLS-1$

    final private static PropertyDescriptor device_property;
    final private static PropertyDescriptor desired_property;
    final private static PropertyDescriptor tolerance_property;

    /** Create property descriptors */
    static
    {
        device_property = new TextPropertyDescriptor(DEVICE, Messages.Lbl_Device);
        desired_property = new TextPropertyDescriptor(DESIRED, Messages.Lbl_WaitValue);
        tolerance_property = new TextPropertyDescriptor(TOLERANCE, Messages.Lbl_WaitTolerance);
        
        final String wait = Messages.Cat_Wait;
        device_property.setCategory(wait);
        desired_property.setCategory(wait);
        tolerance_property.setCategory(wait);
    }
    
    /** Initialize
     *  @param gui GUI to notify on change of command
     *  @param command {@link ScanCommand}
     */
    public WaitForValueCommandAdapter(final GUI gui, final WaitCommand command)
    {
        super(gui, command);
    }
    
    /** {@inheritDoc} */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[]
        {
            // type_property,
            device_property,
            desired_property,
            tolerance_property
        };
        return descriptors;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            final WaitCommand command = getCommand();
            if (id == DEVICE)
                command.setDeviceName(value.toString());
            else if (id == DESIRED)
                command.setDesiredValue(Double.parseDouble(value.toString().trim()));
            else if (id == TOLERANCE)
                command.setTolerance(Double.parseDouble(value.toString().trim()));
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
        else if (id == DESIRED)
            return Double.toString(getCommand().getDesiredValue());
        else if (id == TOLERANCE)
            return Double.toString(getCommand().getTolerance());
        return null;
    }
}
