/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.jms2rdb;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.jms.MapMessage;

import org.csstudio.logging.JMSLogMessage;

/** Filter to suppress for example messages of type ALARM with TEXT=IDLE.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Filter
{
    final private String type;
    final private String property;
    final private Pattern pattern;

    /** Parse filters
     *  @param filter_spec
     *         String "&lt;Type&gt;;&lt;Property&gt;=&lt;Pattern&gt;, &lt;Type&gt;;&lt;Property&gt;=&lt;Pattern&gt;"
     *         with message 'TYPE' property, additional property name,
     *         regex pattern for value.
     *  @return Array of Filter
     */
    public static Filter[] parse(final String filter_spec) throws Exception
    {
        final ArrayList<Filter> filters = new ArrayList<Filter>();
        // Iterate over comma-separated filters
        final String[] specs = filter_spec.split(", *");
        for (String spec : specs)
        {
            // Separate  spec  :=   <type> ';' <property> '=' <pattern>
            final int semi = spec.indexOf(';');
            if (semi < 0)
                throw new Exception("Missing TYPE delimited ';' in filter " +
                                    spec);
            final int equal = spec.indexOf('=', semi+1);
            if (equal < 0)
                throw new Exception("Missing value delimited '=' in filter " +
                                    spec);
            final String type = spec.substring(0, semi);
            final String property = spec.substring(semi+1, equal);
            final String pattern = spec.substring(equal+1);
            filters.add(new Filter(type, property, pattern));
        }
        // Convert to plain array
        return filters.toArray(new Filter[filters.size()]);
    }

    /** Initialize
     *  @param type Message type (ALARM, LOG, TALK, ...)
     *  @param property Additional property to check (TEXT)
     *  @param pattern Regular expression pattern for the property's value.
     *                 Will be checked within the value, i.e. the pattern needn't
     *                 match the whole value, just a substring of the value.
     */
    public Filter(final String type, final String property, final String pattern)
    {
        this.type = type;
        this.property = property;
        this.pattern = Pattern.compile(pattern, Pattern.MULTILINE);
    }

    /** Check if message matches this filter
     *  @param map MapMessage to check
     *  @return <code>true</code> if message matches this filter
     */
    public boolean matches(final MapMessage map)
    {
        try
        {
            if (!type.equalsIgnoreCase(map.getString(JMSLogMessage.TYPE)))
                return false;
            final String value = map.getString(property);
            if (pattern.matcher(value).find())
                return true;
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "Filter exception", ex);
        }
        return false;
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return String.format("Filter for type '%s', '%s' ~= '%s'",
                type, property, pattern.toString());
    }
}
