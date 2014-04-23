/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

import java.util.Arrays;
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
    __NotUsed__,
    Urgent,
    High,
    Normal;
    
    public static List<String> getNames()
    {
        // Return order that differs from numeric IDs, and skip __NotUsed__
        return Arrays.asList(Normal.getName(), High.getName(), Urgent.getName());
    }
    
    public static ELogPriority forName(final String name)
    {
        if (name == null)
            return Normal;
        try
        {
            return valueOf(name);
        }
        catch (Throwable ex)
        {
            Logger.getLogger(ELogPriority.class.getName()).log(Level.WARNING, "Unknown SNS ELog priority {0}", name);
            return Normal;
        }
    }
    
    /** @return Name of the priority, for GUI */
    public String getName()
    {
        return name();
    }
    
    /** @return Priority ID as used within ELog RDB */
    public int getID()
    {
        return ordinal();
    }
    
    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return name() + " (" + ordinal() + ")";
    }
}
