/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import java.util.HashMap;
import java.util.Map;

/** Severity consists of a Severity name and a severity level.
 *  The level of severity is used for prioritizing SpeechPriorityQueue entries.
 *
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 *
 *     reviewed by Delphy 1/29/09
 */
public class Severity implements Comparable<Severity>
{
    /** Map name of Severity to Severity */
    final private static Map<String, Severity> severityMap =
        new HashMap<String, Severity>();

    /** Name of severity */
    final private String name;

    /** Level or priority of the severity. Higher number for higher priority */
    final private int level;

    /** @return Severity used for informational messages */
    final public static Severity forInfo()
    {
        return Severity.fromString("INFO"); //$NON-NLS-1$
    }

    /** @return Severity used for error messages */
    final public static Severity forError()
    {
        return Severity.fromString("ERROR"); //$NON-NLS-1$
    }

    /** Initialize understood severities.
     *  @param severities List of comma-separated severities. Most severe first.
     */
    public static void initialize(final String severities)
    {
        // Split on comma, maybe followed by space
        final String names[] = severities.split(",\\s*"); //$NON-NLS-1$

        // First severity has the highest priority, last has numeric level 1.
        // Store the severities by name in a HashMap.
        for (int i=0;  i<names.length;  i++)
        {
            final Severity severity = new Severity(names[i], names.length - i);
            // System.out.println(severity);
            severityMap.put(severity.getName(), severity);
        }
    }

    /** Return a Severity for the given name.
     *  For unknown severity names, a low-priority severity (level 0) is created.
     *  @param name Severity name to find
     *  @return Severity class pertaining to the input severity name.
     *  @see #initialize(String)
     */
    public static Severity fromString(final String name)
    {
        // If the severity name isn't in the map, create it and give it the
        // lowest priority.
        Severity severity = severityMap.get(name);
        if (severity == null)
        {   // Create unknown severity
            severity = new Severity(name, 0);
            severityMap.put(name, severity);
        }
        return severity;

    }

    /** Initialize
     *  @param name Name of the severity
     *  @param level Numeric level
     */
    public Severity(final String name, final int level)
    {
        this.name = name;
        this.level = level;
    }

    /** @return Name of severity */
    public String getName()
    {
        return name;
    }

    /** Compare Severities by level
     *  @see Comparable<Severity>
     */
    @Override
    public int compareTo(final Severity other)
    {
        return level - other.level;
    }

    /** Compare Severities by level
     *  {@inheritDoc}
     */
	@Override
    public boolean equals(final Object obj)
    {
		if (! (obj instanceof Severity))
			return false;
	    Severity other = (Severity) obj;
	    return level == other.level;
    }

    /** Compare Severities by level
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
    	return 31 * level;
    }

	/** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "Severity '" + name + "' (" + level + ")";
    }
}


