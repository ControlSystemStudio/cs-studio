package org.csstudio.platform.internal.data;

/** Localization.
 *  <p>
 *  Not really using messages.properties etc.,
 *  just hard coded so that this works as a plain
 *  library, but at least a start towards localization.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Messages
{
    final public static String ColumnSeperator = "\t";
    final public static String ValueSevrStatSeparator = " [";
    final public static String SevrStatSeparator = " ";
    final public static String SevrStatEnd = "]";
    final public static String ArrayElementSeparator = ", ";
    final public static String NoValue = "#N/A";
    final public static String Infinite = "Inf";
    final public static String NaN = "NaN";
    
    final public static String SevOK = "";
    final public static String SevMinor = "MINOR";
    final public static String SevMajor = "MAJOR";
    final public static String SevInvalid = "INVALID";

    final public static String MiniMaxiFormat = " [ {0} ... {1} ]";
}
