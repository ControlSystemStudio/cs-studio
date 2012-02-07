/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandProperty;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.ui.scantree.CommandsInfo;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link ScanCommand} to {@link IPropertySource}
 *  to allow display and editing of commands in Properties View.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GenericCommandAdapter implements IPropertySource
{
    final private ScanEditor editor;
    final private ScanCommand command;
    final PropertyDescriptor[] descriptors;

    /** Initialize
     *  @param editor ScanEditor that displays the command and needs to be updated
     *  @param command {@link ScanCommand}
     *  @throws Exception if command cannot be adapted
     */
    @SuppressWarnings("unchecked")
    public GenericCommandAdapter(final ScanEditor editor, final ScanCommand command) throws Exception
    {
        this.editor = editor;
        this.command = command;

        final ScanCommandProperty[] properties = command.getProperties();
        descriptors = new PropertyDescriptor[properties.length];
        for (int i=0; i<properties.length; ++i)
        {
            final ScanCommandProperty property = properties[i];
            final String id = property.getID();
            // Properties View sorts all properties alphabetically.
            // By prefixing the display names with a number,
            // the order of properties defined by ScanCommand.getProperties()
            // will be reflected in the view.
            final String name = Integer.toString(i+1) + ". " + property.getName();
            if (property.getType() == String.class)
                descriptors[i] = new TextPropertyDescriptor(id, name);
            else if (property.getType() == DeviceInfo.class)
            {   // Try to offer list of devices
                final DeviceInfo[] devices = editor.getDevices();
                if (devices != null)
                    descriptors[i] = new DeviceInfoPropertyDescriptor(id, name, devices);
                else // Fall back to editing device names as string
                    descriptors[i] = new TextPropertyDescriptor(id, name);
            }
            else if (property.getType() == Double.class)
                descriptors[i] = new DoublePropertyDescriptor(id, name);
            else if (property.getType() == Object.class)
                descriptors[i] = new StringOrDoublePropertyDescriptor(id, name);
            else if (property.getType() == String[].class)
                descriptors[i] = new StringArrayPropertyDescriptor(id, name);
            else if (property.getType().isEnum())
                descriptors[i] = new EnumPropertyDescriptor(id, name, (Class<? extends Enum<?>>) property.getType());
            else
                throw new Exception(command.getClass().getName()
                        + ", Property '" + id
                        + "': Cannot handle properties of type " + property.getType().getName());
            descriptors[i].setCategory(CommandsInfo.getInstance().getName(command));
        }
    }

    /** GUI (editor) refresh */
    protected void refreshCommand()
    {
        if (editor != null)
            editor.refreshCommand(command);
    }

    /** {@inheritDoc} */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        return descriptors;
    }

    /** {@inheritDoc} */
    @Override
    public Object getEditableValue()
    {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPropertySet(final Object id)
    {
        return getPropertyValue(id) != null;
    }

    /** {@inheritDoc} */
    @Override
    public Object getPropertyValue(final Object id)
    {
        try
        {
            return command.getProperty((String) id);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // Ignore
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            command.setProperty((String) id, value);
            refreshCommand();
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot update '" + id + "' of " + command, ex);
            // Ignore, keeping original value
        }
    }

    /** {@inheritDoc} */
    @Override
    public void resetPropertyValue(final Object id)
    {
        // NOP
    }
}
