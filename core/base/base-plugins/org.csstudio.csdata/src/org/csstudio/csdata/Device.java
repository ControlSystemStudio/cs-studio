/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.csdata;

import java.io.Serializable;

/** Control System Device Name
 *
 *  Allows Drag-and-Drop to transfer device names,
 *  can be used for context menu object contributions.
 *
 *  All control system model items must serialize for Drag-and-Drop.
 *  They should be immutable. They should implement proper <code>equals()</code>
 *  and <code>hashCode()</code> to support collections.
 *
 *  @author Gabriele Carcassi
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Device implements Serializable
{
    /** @see Serializable */
    final private static long serialVersionUID = 1L;

    /** Device name */
    protected String name;

    /** Initialize
     *  @param name Device name
     */
    public Device(final String name)
    {
        if (name == null)
            throw new IllegalArgumentException("Empty name");
        this.name = name;
    }

    /** @return Device Name */
    public String getName()
    {
        return name;
    }

    /** Determine hash code from name
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /** Check equality by name
     *  {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (! (obj instanceof Device))
            return false;
        final Device other = (Device) obj;
        return name.equals(other.getName());
    }

    @Override
    public String toString()
    {
        return "Device '" + name + "'";
    }
}
