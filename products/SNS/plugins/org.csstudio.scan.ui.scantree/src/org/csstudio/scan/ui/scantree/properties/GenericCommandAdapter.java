/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandProperty;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.ui.scantree.ScanEditor;
import org.csstudio.scan.ui.scantree.gui.CommandsInfo;
import org.csstudio.scan.ui.scantree.operations.PropertyChangeOperation;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/** Adapter from {@link ScanCommand} to {@link IPropertySource}
 *  to allow display and editing of commands in Properties View.
 *
 *  <p>Creates a property view {@link PropertyDescriptor} for each scalar {@link ScanCommandProperty}.
 *
 *  <p>For array properties, it creates one {@link PropertyDescriptor} per array element,
 *  plus an extra for entering a new, additional array element.
 *  When entering nothing for an array element, it is removed.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GenericCommandAdapter implements IPropertySource
{
    final private ScanEditor editor;
    final private ScanCommand command;

    /** Initialize
     *  @param editor ScanEditor that displays the command and needs to be updated
     *  @param command {@link ScanCommand}
     *  @throws Exception if command cannot be adapted
     */
    public GenericCommandAdapter(final ScanEditor editor, final ScanCommand command) throws Exception
    {
        this.editor = editor;
        this.command = command;
    }

    /** {@inheritDoc} */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        final List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        final ScanCommandProperty[] properties = command.getProperties();
        try
        {
            for (int i=0; i<properties.length; ++i)
                createPropertyDescriptors(descriptors, properties[i], i);

            final String category = CommandsInfo.getInstance().getName(command);
            for (PropertyDescriptor descriptor : descriptors)
                descriptor.setCategory(category);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "Property error for " + command.getClass().getName(), ex);
        }

        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    /** Create {@link PropertyDescriptor}s for a scan command property
     *  @param descriptors List where new descriptors are added
     *  @param property Scan property for which to create descriptors
     *  @param index Index of property within the command's properties
     *  @throws Exception on error
     */
    @SuppressWarnings("unchecked")
    private void createPropertyDescriptors(final List<PropertyDescriptor> descriptors,
            final ScanCommandProperty property, final int index) throws Exception
    {
        final String id = property.getID();

        // Properties View sorts all properties alphabetically by their label.
        // By prefixing the display names with a number,
        // the order of properties defined by ScanCommand.getProperties()
        // will be reflected in the view.
        final String label = Integer.toString(index+1) + ". " + property.getName();

        if (property.getType() == String.class)
            descriptors.add(new TextPropertyDescriptor(id, label));
        else if (property.getType() == Boolean.class)
            descriptors.add(new BooleanPropertyDescriptor(id, label));
        else if (property.getType() == DeviceInfo.class)
            descriptors.add(createDevicePropertyDescriptor(id, label));
        else if (property.getType() == Double.class)
            descriptors.add(new DoublePropertyDescriptor(id, label));
        else if (property.getType() == Object.class)
            descriptors.add(new StringOrDoublePropertyDescriptor(id, label));
        else if (property.getType() == String[].class)
        {   // String arrays use "@s" + array index + "-" + original property descriptor,
            // for example "@s0-devices"
            final String[] value = (String[]) command.getProperty(property);
            for (int e=0; e<=value.length; ++e)
            {
                if (e==0)
                    descriptors.add(new TextPropertyDescriptor("@s" + e + "-" + id, label));
                else
                    descriptors.add(new TextPropertyDescriptor("@s" + e + "-" + id, label + " ..."));
            }
        }
        else if (property.getType() == DeviceInfo[].class)
        {   // DeviceInfo arrays are basically also string arrays,
            // but if possible they are edited by selecting available devices,
            // not free-form strings
            final String[] value = (String[]) command.getProperty(property);
            for (int e=0; e<=value.length; ++e)
            {
                if (e==0)
                    descriptors.add(createDevicePropertyDescriptor("@s" + e + "-" + id, label));
                else
                    descriptors.add(createDevicePropertyDescriptor("@s" + e + "-" + id, label + " ..."));
            }
        }
        else if (property.getType().isEnum())
            descriptors.add(new EnumPropertyDescriptor(id, label, (Class<? extends Enum<?>>) property.getType()));
        else
            throw new Exception(command.getClass().getName()
                    + ", Property '" + id
                    + "': Cannot handle properties of type " + property.getType().getName());
    }

    /** Create property descriptor for editing a device name.
     *
     *  <p>If possible, the {@link DeviceInfoPropertyDescriptor} is used.
     *
     *  @param id Property ID
     *  @param label Label for property view
     *  @return Device or text property descriptor
     */
    private PropertyDescriptor createDevicePropertyDescriptor(final String id, final String label)
    {
        final DeviceInfo[] devices = editor.getDevices();
        if (devices != null)
            return new DeviceInfoPropertyDescriptor(id, label, devices);
        // Fall back to editing device names as string
        return new TextPropertyDescriptor(id, label);
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
            final String prop_id = (String) id;
            // Element of an array property?
            if (prop_id.startsWith("@s"))
            {
                final int sep = prop_id.indexOf('-');
                final int index = Integer.valueOf(prop_id.substring(2, sep));
                final String actual_prop = prop_id.substring(sep+1);
                final String[] strings = (String[]) command.getProperty(actual_prop);

                if (index <= strings.length-1)
                    return strings[index];
                else
                    return "";
            }
            else
                return command.getProperty(prop_id);
        }
        catch (Exception ex)
        {
            Logger.getLogger(getClass().getName())
                .log(Level.WARNING, "Cannot get property '" + id + "' of " + command, ex);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setPropertyValue(final Object id, final Object value)
    {
        try
        {
            final String prop_id = (String) id;
            // Element of an array property?
            if (prop_id.startsWith("@s"))
            {
                final int sep = prop_id.indexOf('-');
                final int index = Integer.valueOf(prop_id.substring(2, sep));
                final String actual_prop = prop_id.substring(sep+1);
                final String[] strings = (String[]) command.getProperty(actual_prop);
                final String new_value = ((String) value).trim();

                if (new_value.length() <= 0  &&  index <= strings.length-1)
                {   // Delete array element
                    final String[] smaller = Arrays.copyOf(strings, strings.length-1);
                    System.arraycopy(strings, index+1, smaller, index, smaller.length - index);
                    editor.executeForUndo(new PropertyChangeOperation(editor, command,
                            actual_prop, smaller));
                }
                else if (index >= strings.length)
                {   // Add array element
                    final String[] bigger = Arrays.copyOf(strings, strings.length+1);
                    bigger[strings.length] = new_value;
                    editor.executeForUndo(new PropertyChangeOperation(editor, command,
                            actual_prop, bigger));
                }
                else
                {
                    strings[index] = new_value;
                    editor.executeForUndo(new PropertyChangeOperation(editor, command,
                            actual_prop, strings));
                }
            }
            else // Scalar property
                editor.executeForUndo(new PropertyChangeOperation(editor, command,
                        prop_id, value));
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
