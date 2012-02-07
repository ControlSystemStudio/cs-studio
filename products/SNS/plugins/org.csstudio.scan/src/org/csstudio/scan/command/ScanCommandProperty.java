/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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

/** Description of a {@link ScanCommand} property
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandProperty
{
    final private String name, id;
    final private Class<?> type;

    public ScanCommandProperty(final String id, final String name, final Class<?> type)
    {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    /** @return ID of the property */
    public String getID()
    {
        return id;
    }

    /** @return Name of the property, displayed in a GUI */
    public String getName()
    {
        return name;
    }

    /** @return Type of the property.
     *  @see ScanCommand ScanCommand lists supported data types
     */
    public Class<?> getType()
    {
        return type;
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Scan command property '" + id + "' (" + name + "), type " + type.getName();
    }
}
