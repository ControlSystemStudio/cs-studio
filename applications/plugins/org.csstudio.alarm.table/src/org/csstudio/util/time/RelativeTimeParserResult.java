/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
    
    @Override
    public String toString()
    {
        return time.toString();
    }
};
