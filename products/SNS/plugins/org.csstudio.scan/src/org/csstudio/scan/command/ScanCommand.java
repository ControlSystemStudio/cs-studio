/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.command;

import java.io.PrintStream;
import java.lang.reflect.Method;

import org.csstudio.scan.device.DeviceInfo;
import org.w3c.dom.Element;

/** Description of a scan server command
 *
 *  <p>Used by the client to describe commands to the server,
 *  and returned by the server to describe elements of a Scan.
 *
 *  <p>This class offers generic property access
 *  based on introspection, assuming that the command has
 *  suitable 'getter' and 'setter' methods.
 *  A property with ID "some_property" must have associated "getSomeProperty"
 *  and "setSomeProperty" methods, i.e. using a CamelCase version
 *  of the property ID.
 *
 *  <p>The setter must accept an {@link Object} like {@link Double} or {@link Boolean},
 *  not a plain type like double or boolean.
 *
 *  <p>The command must allow concurrent access to its
 *  properties. The getter and setter should <code>synchronize</code>,
 *  or the property needs to be <code>volatile</code>.
 *
 *  <p>Supported property types:
 *  <ul>
 *  <li><code>Double</code> - Edited as number "5", "1e-20" etc.
 *  <li><code>String</code> - Edited as text
 *  <li><code>Boolean</code> - Edited as yes/no
 *  <li><code>String[]</code> - Edited as strings
 *  <li><code>DeviceInfo</code> - Edited as string, but editor suggests available device names
 *  <li><code>DeviceInfo[]</code> - Edited as strings, but editor suggests available device names
 *  <li><code>Enum</code> - Allows selection among the <code>toString()</code> values of the Enum
 *  <li><code>Object</code> - Edited as String, and if possible converted to Double
 *  </ul>
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class ScanCommand
{
    /** Address of this command within command sequence.
     *  <p>The {@link CommandSequence} assigns addresses 0, 1, 2, ...
     *  to all commands in the sequence to allow identification
     *  of each command while the sequence is executed.
     */
    private long address = -1;

    /** @return Name of the command, which is the base of the class name */
    final public String getCommandName()
    {
        final String name = getClass().getName();
        final int sep = name.lastIndexOf('.');
        return name.substring(sep + 1);
    }

    /** @return Address of this command within command sequence */
    final public long getAddress()
    {
        return address;
    }

    /** Set the address of this command.
     *
     *  <p>To be called by scan system, not end user code.
     *  Derived commands, i.e. custom commands that wrap a
     *  "body" of commands need to override and forward
     *  the address update to their embedded commands.
     *
     *  @param address Address of this command within command sequence
     *  @return Address of next command
     */
    public long setAddress(final long address)
    {
        this.address = address;
        return address+1;
    }

    /** @return Descriptions of Properties for this command */
    abstract public ScanCommandProperty[] getProperties();

    /** @param property_id ID of a property
     *  @return Property description or <code>null</code> if property ID is not supported
     */
    final public ScanCommandProperty getPropertyDescription(final String property_id)
    {
        for (ScanCommandProperty property : getProperties())
            if (property.getID().equals(property_id))
                return property;
        return null;
    }

    /** Set a command's property
     *  @param property_id ID of the property to set
     *  @param value New value
     *  @throws UnknownScanCommandPropertyException when there is no property with that ID and value type
     */
    public void setProperty(final String property_id, final Object value) throws UnknownScanCommandPropertyException
    {
        for (ScanCommandProperty property : getProperties())
            if (property.getID().equals(property_id))
            {
                setProperty(property, value);
                return;
            }
        throw new UnknownScanCommandPropertyException("Unkown property ID " + property_id + " for " + getClass().getName());
    }

    /** Set a command's property
     *  @param property Property to set
     *  @param value New value
     *  @throws UnknownScanCommandPropertyException when there is no property with that ID and value type
     */
    public void setProperty(final ScanCommandProperty property, final Object value) throws UnknownScanCommandPropertyException
    {
        final String meth_name = getMethodName("set", property.getID());

        // Type patching: DeviceInfo is read/set as String
        Class<?> type = property.getType();
        if (type == DeviceInfo.class)
            type = String.class;
        else if (type == DeviceInfo[].class)
            type = String[].class;

        try
        {
            final Method method = getClass().getMethod(meth_name, type);
            method.invoke(this, value);
        }
        catch (Throwable ex)
        {   // Expect Exception or RuntimeException, but FindBugs complained about using Exception,
            // so using Throwable
            throw new UnknownScanCommandPropertyException("Unkown property ID " + property.getID() +
                    " with value type " + value.getClass().getName() + " for " + getClass().getName());
        }
    }

    /** Get a command's property
     *  @param property_id ID of the property to set
     *  @return Value
     *  @throws UnknownScanCommandPropertyException when there is no property with that ID and value type
     */
    public Object getProperty(final String property_id) throws UnknownScanCommandPropertyException
    {
        for (ScanCommandProperty property : getProperties())
            if (property.getID().equals(property_id))
                return getProperty(property);
        throw new UnknownScanCommandPropertyException("Unkown property ID " + property_id + " for " + getClass().getName());
    }

    /** Get a command's property
     *  @return Value
     *  @throws UnknownScanCommandPropertyException when there is no property with that ID and value type
     */
    public Object getProperty(final ScanCommandProperty property) throws UnknownScanCommandPropertyException
    {
        final String meth_name = getMethodName("get", property.getID());
        try
        {
            final Method method = getClass().getMethod(meth_name);
            return method.invoke(this);
        }
        catch (Exception ex)
        {
            throw new UnknownScanCommandPropertyException("Unkown property ID " + property.getID() + " for " + getClass().getName());
        }
    }

    /** Construct method name
     *  @param get_set Method prefix "get" or "set"
     *  @param property_id Property ID like "device_name"
     *  @return Method name like "setDeviceName"
     */
    private String getMethodName(final String get_set, final String property_id)
    {
        final String[] sections = property_id.split("_");
        final StringBuilder result = new StringBuilder(get_set);
        for (String sec : sections)
        {
            result.append(sec.substring(0, 1).toUpperCase());
            result.append(sec.substring(1));
        }
        return result.toString();
    }

    /** Write the command (and its sub-commands) in an XML format.
     *
     *  <p>A command called AbcCommand should write itself as a tag "abc"
     *  so that the {@link XMLCommandReader} can later determine
     *  which class to use for reading the command back from XML.
     *
     *  @param out {@link PrintStream}
     *  @param level Indentation level
     */
    abstract public void writeXML(PrintStream out, final int level);

    /** Read command parameters from XML element
     *  @param factory ScanCommandFactory to use in case inner scan commands,
     *                 for example a loop body, need to be created
     *  @param element
     *  @throws Exception on error, for example missing essential data
     */
    abstract public void readXML(final SimpleScanCommandFactory factory, final Element element) throws Exception;

    /** Write indentation
     *  @param out Where to print
     *  @param level Indentation level
     */
    final protected void writeIndent(final PrintStream out, final int level)
    {
        for (int i=0; i<level; ++i)
            out.print("  ");
    }

    /** @return Debug representation.
     *          Derived classes should provide their command name with properties
     */
    @Override
    public String toString()
    {
        return getClass().getName();
    }
}
