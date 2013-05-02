/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import org.csstudio.trends.sscan.Messages;

/** Types of displaying a trace
 * 
 *  @author Kay Kasemir
 */
public enum TraceType
{
    /** Trace with area for min/max */ 
    AREA(Messages.TraceType_Area),
    /** Trace with error bars for min/max */ 
    ERROR_BARS(Messages.TraceType_ErrorBars),
    /** Single line, no min/max */ 
    SINGLE_LINE(Messages.TraceType_SingleLine),
    /** Square Markers */ 
    SQUARES(Messages.TraceType_Squares),
    /** Circle Markers */ 
    CIRCLES(Messages.TraceType_Circles),
    /** Diamond Markers */ 
    DIAMONDS(Messages.TraceType_Diamonds),
    /** Cross Markers */ 
    CROSSES(Messages.TraceType_Crosses),
    /** Triangle Markers */ 
    TRIANGLES(Messages.TraceType_Triangles),
    ;
    
    final private String name;
    
    private TraceType(final String name)
    {
        this.name = name;
    }
    
    /** Obtain trace type from ordinal
     *  @param ordinal
     *  @return TraceType
     *  @throws RuntimeException on invalid ordinal
     */
    public static TraceType fromOrdinal(final int ordinal)
    {
        for (TraceType type : TraceType.values())
            if (type.ordinal() == ordinal)
                return type;
        throw new RuntimeException("Invalid Trace Type " + ordinal); //$NON-NLS-1$
    }
    
    /** @return Array of display names for all trace types */
    public static String [] getDisplayNames()
    {
        final TraceType types[] = TraceType.values();
        final String names[] = new String[types.length];
        for (int i = 0; i < names.length; i++)
            names[i] = types[i].toString();
        return names;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
