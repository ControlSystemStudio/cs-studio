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
 *  based on introspection, assuming that the actual command has
 *  suitable 'getter' and 'setter' methods.
 *  A property with ID "some_property" must have associated "getSomeProperty"
 *  and "setSomeProperty" methods, i.e. using a CamelCase version
 *  of the property ID.
 *  The setter must accept an Object like Double, not a plain type like double.
 *
 *  <p>Supported property types:
 *  <ul>
 *  <li><code>Double</code> - Edited as number "5", "1e-20" etc.
 *  <li><code>String</code> - Edited as text
 *  <li><code>DeviceInfo</code> - Edited as String, but using device names
 *  <li><code>String[]</code> - Edited as comma-separated list of strings
 *  <li><code>Enum</code> - Allows selection among the <code>toString()</code> values of the Enum
 *  <li><code>Object</code> - Edited as String, and if possible converted to Double
 *  </ul>
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
abstract public class ScanCommand
{
    /** @return Descriptions of Properties for this command */
    abstract public ScanCommandProperty[] getProperties();

    /** Set a command's property
     *  @param property_id ID of the property to set
     *  @param value New value
     *  @throws Exception on error, for example unknown property ID
     */
    public void setProperty(final String property_id, final Object value) throws Exception
    {
        for (ScanCommandProperty property : getProperties())
            if (property.getID().equals(property_id))
            {
                setProperty(property, value);
                return;
            }
        throw new Exception("Unkown property ID " + property_id + " for " + getClass().getName());
    }

    /** Set a command's property
     *  @param property_id ID of the property to set
     *  @param value New value
     *  @throws Exception on error, for example no suitable "setter"
     */
    public void setProperty(final ScanCommandProperty property, final Object value) throws Exception
    {
        final String meth_name = getMethodName("set", property.getID());

        // Type patching: DeviceInfo is read/set as String
        Class<?> type = property.getType();
        if (type == DeviceInfo.class)
            type = String.class;

        final Method method = getClass().getMethod(meth_name, type);
        method.invoke(this, value);
    }

    /** Get a command's property
     *  @param property_id ID of the property to set
     *  @return Value
     *  @throws Exception on error, for example unknown property ID
     */
    public Object getProperty(final String property_id) throws Exception
    {
        for (ScanCommandProperty property : getProperties())
            if (property.getID().equals(property_id))
                return getProperty(property);
        throw new Exception("Unkown property ID " + property_id + " for " + getClass().getName());
    }

    /** Set a command's property
     *  @param property_id ID of the property to set
     *  @param value New value
     *  @throws Exception on error, for example no suitable "getter"
     */
    public Object getProperty(final ScanCommandProperty property) throws Exception
    {
        final String meth_name = getMethodName("get", property.getID());
        final Method method = getClass().getMethod(meth_name);
        return method.invoke(this);
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
    protected void writeIndent(final PrintStream out, final int level)
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
