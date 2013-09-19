/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/** SNS 'ELog' support
 * 
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public enum ELogPriority
{
    // Implemented as enum, using enum's name and ordinal,
    // but code should access getName(), getID() to later
    // allow different implementation.
    //
    // Values are ordered according to
    // SELECT * FROM LOGBOOK.LOG_ENTRY_PRIOR
    // Perfect implementation would fetch them
    // from RDB, but this is unlikely to change.
    // If new priorities are ever added,
    // then this needs to be re-implemented.
    None,
    Urgent,
    High,
    Normal;
    
    public static List<String> getNames()
    {
        final List<String> names = new ArrayList<>();
        for (ELogPriority p : ELogPriority.values())
            names.add(p.getName());
        return names;
    }
    
    public static ELogPriority forName(final String name)
    {
        try
        {
            return valueOf(name);
        }
        catch (Throwable ex)
        {
            Logger.getLogger(ELogPriority.class.getName()).log(Level.WARNING, "Unknown SNS ELog priority {0}", name);
            return None;
        }
    }
    
    public String getName()
    {
        return name();
    }
    
    public int getID()
    {
        return ordinal();
    }
}
