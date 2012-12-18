/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import org.csstudio.logbook.Tag;

/** {@link Tag} for SNS logbook
 * 
 *  <p>Uses the 'categories'
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSTag implements Tag
{
    final private String id;
    final private String name;

    public SNSTag(final String id, final String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getID()
    {
        return id;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getState()
    {
        return null;
    }
    
    @Override
    public String toString()
    {
        return "Tag '" + name + "' (" + id + ")";
    }
}
