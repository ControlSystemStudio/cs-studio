/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.GUI;
import org.csstudio.scan.ui.scantree.Messages;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link LoopCommand} to {@link IPropertySource}.
 *  @author Kay Kasemir
 */
public class LoopCommandAdapter extends ScanCommandAdapter<LoopCommand>
{
    final private static String DEVICE = "DEVICE"; //$NON-NLS-1$
    final private static String START = "START"; //$NON-NLS-1$
    final private static String END = "END"; //$NON-NLS-1$
    final private static String STEP = "STEP"; //$NON-NLS-1$

    final private static PropertyDescriptor device_property;
    final private static PropertyDescriptor start_property;
    final private static PropertyDescriptor end_property;
    final private static PropertyDescriptor step_property;

    /** Create property descriptors */
    static
    {
        device_property = new TextPropertyDescriptor(DEVICE, Messages.Lbl_Device);
        start_property = new TextPropertyDescriptor(START, Messages.Lbl_LoopStart);
        end_property = new TextPropertyDescriptor(END, Messages.Lbl_LoopEnd);
        step_property = new TextPropertyDescriptor(STEP, Messages.Lbl_LoopStep);
        final String loop = Messages.Cat_Loop;
        device_property.setCategory(loop);
        start_property.setCategory(loop);
        end_property.setCategory(loop);
        step_property.setCategory(loop);
    }
    
    /** Initialize
     *  @param gui GUI to notify on change of command
     *  @param command {@link ScanCommand}
     */
    public LoopCommandAdapter(final GUI gui, final LoopCommand command)
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
            start_property,
            end_property,
            step_property
        };
        return descriptors;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            final LoopCommand loop = getCommand();
            if (id == DEVICE)
                loop.setDeviceName(value.toString());
            else if (id == START)
                loop.setStart(Double.parseDouble(value.toString().trim()));
            else if (id == END)
                loop.setEnd(Double.parseDouble(value.toString().trim()));
            else if (id == STEP)
                loop.setStepsize(Double.parseDouble(value.toString().trim()));
            refreshCommand(loop);
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
        else if (id == START)
            return Double.toString(getCommand().getStart());
        else if (id == END)
            return Double.toString(getCommand().getEnd());
        else if (id == STEP)
            return Double.toString(getCommand().getStepSize());
        return null;
    }
}
