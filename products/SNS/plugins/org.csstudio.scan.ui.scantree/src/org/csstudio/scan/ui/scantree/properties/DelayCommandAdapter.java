/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import org.csstudio.scan.command.DelayCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.ui.scantree.GUI;
import org.csstudio.scan.ui.scantree.Messages;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link DelayCommand} to {@link IPropertySource}.
 *  @author Kay Kasemir
 */
public class DelayCommandAdapter extends ScanCommandAdapter<DelayCommand>
{
    final private static String DELAY = "DELAY"; //$NON-NLS-1$
    
    final private static PropertyDescriptor delay_property;

    /** Create property descriptors */
    static
    {
        delay_property = new TextPropertyDescriptor(DELAY, Messages.Lbl_Delay);
        delay_property.setCategory(Messages.Cat_Delay);
    }
    
    /** Initialize
     *  @param gui GUI to notify on change of command
     *  @param command {@link ScanCommand}
     */
    public DelayCommandAdapter(final GUI gui, final DelayCommand command)
    {
        super(gui, command);
    }
    
    /** {@inheritDoc} */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[]
        {
            delay_property
        };
        return descriptors;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            final DelayCommand command = getCommand();
            if (id == DELAY)
                command.setSeconds(Double.parseDouble(value.toString().trim()));
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
        if (id == DELAY)
            return Double.toString(getCommand().getSeconds());
        return null;
    }
}
