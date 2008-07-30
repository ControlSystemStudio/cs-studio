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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/** Parse an absolute date/time string.
 *  
 *  @see #parse(Calendar, String)
 *  
 *  @author Sergei Chevtsov developed the original code for the
 *          Java Archive Viewer, from which this code heavily borrows.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AbsoluteTimeParser {
    /** The accepted date formats for absolute times. */
    @SuppressWarnings("nls")
    private static final DateFormat[] parsers = new SimpleDateFormat[]
    {   // Most complete version first
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS"),
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
        new SimpleDateFormat("yyyy/MM/dd HH:mm"),
        new SimpleDateFormat("yyyy/MM/dd HH"),
        new SimpleDateFormat("yyyy/MM/dd")
    };

    /** Like parse(), using a base calendar of 'now', 'current time zone'.
     *  @see #parse(Calendar, String)
     *  @return Calendar initialized from parsed text.
     */
    public static Calendar parse(String text) throws Exception {
        Calendar cal = Calendar.getInstance();
        return parse(cal, text);
    }

    /** Adjust given calendar to the date and time parsed from the text.
     *  <p>
     *  The date/time text should follow the format
     *  <pre>
     *  YYYY/MM/DD hh:mm:ss.sss
     *  </pre>
     *  The milliseconds (sss), seconds(ss), minutes(mm), hours(hh)
     *  might be left off, and will then default to zero.
     *  <p>
     *  When omitting the year, the year from the passed-in calendar is used.
     *  When omitting the whole date (YYYY/MM/DD), the values from the passed-in
     *  calendar are used.
     *  It is not possible to provide <i>only</i> the month <i>without</i>
     *  the day or vice vesa.
     *  <p>
     *  An empty text leaves the provided calendar unchanged.
     *  <p>
     *  All other cases result in an exception.
     *  
     *  @param cal Base calendar, defines the time zone as well as
     *             the year, in case the text doesn't include a year.
     *  @param text The text to parse.
     *  @return Adjusted Calendar.
     *  @exceptionx On error.
     */
    public static Calendar parse(Calendar cal, String text) throws Exception
    {
        String cooked = text.trim().toLowerCase();
        // Empty string? Pass cal as is back, since we didn't change it?
        if (cooked.length() < 1)
            return cal;
        final Calendar result = Calendar.getInstance();
        // Provide missing year from given cal
        int datesep = cooked.indexOf('/');
        if (datesep < 0) // No date at all provided? Use the one from cal.
            cooked = String.format("%04d/%02d/%02d %s",
                                   cal.get(Calendar.YEAR),
                                   cal.get(Calendar.MONTH) + 1,
                                   cal.get(Calendar.DAY_OF_MONTH),
                                   cooked);
        else
        {   // Are there two date separators?
            datesep = cooked.indexOf('/', datesep + 1);
            // If not, assume that we have MM/DD, and add the YYYY.
            if (datesep < 0)
                cooked = String.format("%04d/%s",
                                       cal.get(Calendar.YEAR), cooked);
        }
        // reduce time to milisec (from 2008/07/29 14:41:14.123456789 to 2008/07/29 14:41:14.123) 
        int index = cooked.indexOf(".");
        if(cooked.length()-index>3){
            cooked=cooked.substring(0,index+4);
        }
        
        // Try the parsers
        for (DateFormat parser : parsers)
        {
            try
            {   // DateFormat returns Date, but pretty much all of Date
                // is deprecated, which is why we use Calendar.
                long millis = parser.parse(cooked).getTime();
                result.setTimeInMillis(millis);
                return result;
            }
            catch (Exception e)
            {   // Ignore, try the next one
            }
        }
        // No parser parsed the string?
        throw new Exception("Cannot parse date and time from '" + text + "'");
    }
    
    /** Format given calendar value into something that this parser would handle.
     *  @return Date and time string.
     */
    public static String format(Calendar cal)
    {
        return parsers[1].format(cal.getTime());
    }
}
