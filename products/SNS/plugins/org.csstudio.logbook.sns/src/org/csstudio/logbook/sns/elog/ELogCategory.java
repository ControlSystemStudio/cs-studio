/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

/** ELog categories
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogCategory
{
    final private String id;
    final private String name;

    public ELogCategory(final String id, final String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getID()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return "Tag '" + name + "' (" + id + ")";
    }
}
