package org.csstudio.apputil.time;

/** Parse seconds from 'HH:MM:SS' text and vice versa.
 *  @author Kay Kasemir
 */
public class SecondsParser
{
	final static int SECS_PER_MINUTE=60;
	
	final static int SECS_PER_HOUR=60*SECS_PER_MINUTE;
	
    /** Parse time string, return seconds.
     *  <p>
     *  Allowed formats:
     *  <ul>
     *  <li>"1234" - results in 1234 seconds
     *  <li>"1:30" - results in 1 minute, 30 seconds = 90 seconds
     *  <li>"1:20:30" - results in 1 hour, 20 minutes, 30 seconds = ...
     *  </ul>
     *  Special cases:
     *  One overall '-' is allowed to specify negative seconds.
     *  
     *  @return The seconds parsed from the string.
     *  @throws Exception on parse error.
     */
    @SuppressWarnings("nls")
    public static double parseSeconds(String text) throws Exception
    {
        final char sep = ':';
        text = text.trim();
        if (text.length() <= 1)
            throw new Exception("Empty String");

        double sign;
        if (text.charAt(0) == '-')
        {
            text = text.substring(1);
            sign = -1.0;
        }
        else
            sign = +1.0;
        // Get the first number out of the string
        int i = text.indexOf(sep);
        if (i < 0) // That's it.
            return sign * Double.parseDouble(text);
        
        // There's more; these are the hours or minutes
        double secs = 60 * Double.parseDouble(text.substring(0, i));

        int j = text.indexOf(sep, i+1);
        if (j < 0) // That's it: Minutes and hours
            return sign * (secs + Double.parseDouble(text.substring(i+1)));
        // Hours, minutes, and seconds
        secs = 60 * (secs + Double.parseDouble(text.substring(i+1, j)));
        
        // More than two ':'?
        if (text.indexOf(sep, j+1) >= 0)
            throw new Exception("Invalid format");
        // Get the seconds
        return sign * (secs + Double.parseDouble(text.substring(j+1)));
    }
    
    /** Convert seconds into "HH:MM:SS" string.
     *  @param seconds Seconds
     *  @return String with "HH:MM:SS"
     */
    public static String formatSeconds(double seconds)
    {
    	final int hours = (int) (seconds / SECS_PER_HOUR);
    	seconds -= hours * SECS_PER_HOUR;
    	final int minutes = (int) (seconds / SECS_PER_MINUTE);
    	seconds -= minutes * SECS_PER_MINUTE;
    	return String.format("%02d:%02d:%02d",
    			             hours, minutes, (int) seconds);
    }
}
