/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

/** Container for the read-only result of RelativeTimeParser.parse().
 *  @author Kay Kasemir
 */
public class RelativeTimeParserResult
{
    /** The relative time specification. */
    private final RelativeTime time;
    
    /** Offset of the next character in the parsed string
     *  after the last recognized relative time specification.
     */
    private final int offset_of_next_char;
    
    /** Constructor, called from within package by RelativeTime.parse(). */
    RelativeTimeParserResult(RelativeTime time, int offset_of_next_char)
    {
        this.time = time;
        this.offset_of_next_char = offset_of_next_char;
    }
    
    /** @return The RelativeTime. */
    public RelativeTime getRelativeTime()
    {
        return time;
    }
    
    /** @return <code>true</code> if all parsed text was absolute,
     *          i.e. no relative time pieces were found.
     */
    public boolean isAbsolute()
    {
        return offset_of_next_char <= 0;
    }
    
    /** @return Offset of the next character in the parsed string
     *          after the last recognized relative time specification.
     */
    public int getOffsetOfNextChar()
    {
        return offset_of_next_char;
    }
    
    @Override
    public String toString()
    {
        return time.toString();
    }
}
