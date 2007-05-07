package org.csstudio.util.time;

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
    
    public String toString()
    {
        return time.toString();
    }
};
