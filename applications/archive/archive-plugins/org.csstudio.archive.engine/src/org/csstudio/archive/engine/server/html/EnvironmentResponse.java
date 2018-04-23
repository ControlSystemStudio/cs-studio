/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine.server.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csstudio.archive.engine.Messages;
import org.csstudio.archive.engine.model.EngineModel;
import org.csstudio.archive.engine.server.AbstractResponse;

/** Provide web page with environment info.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EnvironmentResponse extends AbstractResponse
{
    /** Bytes in a MegaByte */
    final static double MB = 1024.0*1024.0;

    /** A comparable name-value pair, used to hold properties,
     *  so that we can sort them.
     */
    static class NameValue implements Comparable<NameValue>
    {
        final String name;
        final String value;

        public NameValue(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        public final String getName()
        {
            return name;
        }

        public final String getValue()
        {
            return value;
        }

        @Override
        public int compareTo(final NameValue other)
        {
            return name.compareTo(other.name);
        }

        @Override
        public int hashCode()
        {
            return name.hashCode();
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj) return true;
            if (! (obj instanceof NameValue))
                return false;
            final NameValue other = (NameValue) obj;
            return name.equals(other.name);
        }
    }

    public EnvironmentResponse(final EngineModel model)
    {
        super(model);
    }

    @Override
    public void fillResponse(final HttpServletRequest req,
                    final HttpServletResponse resp) throws Exception
    {
        final HTMLWriter html = new HTMLWriter(resp, Messages.HTTP_MainTitle);

        html.openTable(1, new String[] { "Property", "Value" });

        // This looks very inefficient, but I don't think it matters.
        // Get all properties into Array, ...
        final Properties properties = System.getProperties();
        final Enumeration<Object> keys = properties.keys();
        final List<NameValue> prop_list = new ArrayList<NameValue>();
        while (keys.hasMoreElements())
        {
            final String name = (String) keys.nextElement();
            final String value =
                splitIntoSanePieces(properties.getProperty(name));
            prop_list.add(new NameValue(name, value));
        }
        // Sort...
        Collections.sort(prop_list);
        // Dump to HTML Table
        for (NameValue prop : prop_list)
        {
            html.tableLine(new String[] { prop.getName(), prop.getValue() });
        }
        html.closeTable();

        html.close();
    }

    /** Some property values are really long, and unless they contain
     *  some spaces, the make the HTML table column explode.
     *  This adds some spaces, so the table can wrap the text around.
     *  @param property Original property
     *  @return Property, maybe with some added spaces
     */
    private String splitIntoSanePieces(String property)
    {
        final int WIDTH = 70;
        StringBuffer result = new StringBuffer();
        while (property.length() > WIDTH)
        {
            result.append(property.substring(0, WIDTH));
            result.append("...<br>\n"); //$NON-NLS-1$
            property = property.substring(WIDTH);
        }
        result.append(property);
        return result.toString();
    }
}
