package org.csstudio.util.time;

import java.util.Calendar;

/** Container for the read-only result of RelativeTimeParser.parse().
 *  @author Kay Kasemir
 */
class RelativeTimeParserResult
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
    
    /** @see RelativeTime#get(int)
     *  @return One of the RelativeTime pieces.
     */
    public int get(int piece)
    {
        return time.get(piece);
    }
    
    /** @see RelativeTime#adjust(Calendar) */
    public void adjust(Calendar calendar)
    {
        time.adjust(calendar);
    }
    
    /** @return Offset of the next character in the parsed string
     *          after the last recognized relative time specification.
     */
    public int getOffsetOfNextChar()
    {
        return offset_of_next_char;
    }
    
    public String toString()
    {
        return time.toString();
    }
};
