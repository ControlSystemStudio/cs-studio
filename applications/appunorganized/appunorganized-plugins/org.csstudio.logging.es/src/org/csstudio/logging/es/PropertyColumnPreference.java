/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es;

/**
 * Settings for a "Property" table column: Name of property to display,
 * suggested columns size, ...
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PropertyColumnPreference
{
    /**
     * Parse PropertyColumnPreference from preference string
     *
     * @param pref_string
     *            String as stored in preferences
     * @return PropertyColumnPreference
     * @throws Exception
     *             On error
     */
    static PropertyColumnPreference fromString(final String pref_string)
            throws Exception
    {
        String[] pieces = pref_string.split(",");
        if (pieces.length != 3) throw new Exception(
                "Error in property column preference '" + pref_string + "'");
        try
        {
            String name = pieces[0].trim();
            int size = Integer.parseInt(pieces[1].trim());
            int weight = Integer.parseInt(pieces[2].trim());
            return new PropertyColumnPreference(name, size, weight);
        }
        catch (NumberFormatException ex)
        {
            throw new Exception(
                    "Cannot parse size, weight from '" + pref_string + "'");
        }
    }

    final String name;
    final int size;

    final int weight;

    /**
     * Initialize
     *
     * @param name
     * @param size
     * @param weight
     */
    PropertyColumnPreference(final String name, final int size,
            final int weight)
    {
        this.name = name;
        this.size = size;
        this.weight = weight;
    }

    /** @return Property Name */
    public String getName()
    {
        return this.name;
    }

    /** @return Minimum column size */
    public int getSize()
    {
        return this.size;
    }

    /** @return Column weight for resize */
    public int getWeight()
    {
        return this.weight;
    }

    @Override
    public String toString()
    {
        return "PropertyColumnPreference '" + this.name + ", min " + this.size
                + ", weight " + this.weight;
    }
}
