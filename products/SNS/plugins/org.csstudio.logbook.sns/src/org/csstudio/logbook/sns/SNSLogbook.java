/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import org.csstudio.logbook.Logbook;

/** SNS logbook
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbook implements Logbook
{
    final private String name;
    
    public SNSLogbook(final String name)
    {
        this.name = name;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getOwner()
    {
        return "";
    }

    @Override
    public String toString()
    {
        return "Logbook '" + name + "'";
    }
}
