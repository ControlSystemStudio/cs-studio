/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

/** SNS Logbook: Name and internal ID
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELogbook
{
    final private String name;
    final private String id;
    
    /** Initialize
     *  @param name
     *  @param id
     */
    public ELogbook(final String name, final String id)
    {
        this.name = name;
        this.id = id;
    }

    /** @return Logbook name, e.g. "Operations" */
    public String getName()
    {
        return name;
    }

    /** @return Logbook ID, e.g. "OP" */
    public String getId()
    {
        return id;
    }
    
    @Override
    public String toString()
    {
        return "Logbook '" + name + "' (" + id + ")";
    }
}
