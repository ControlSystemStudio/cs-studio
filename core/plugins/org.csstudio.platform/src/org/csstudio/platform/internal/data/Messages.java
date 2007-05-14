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
    public final static String ColumnSeperator = "\t";
    public final static String ValueSevrStatSeparator = " [";
    public final static String SevrStatSeparator = " ";
    public final static String SevrStatEnd = "]";
    public final static String ArrayElementSeparator = ", ";
    public final static String NoValue = "#N/A";
    
    public final static String SevOK = "";
    public final static String SevMinor = "MINOR";
    public final static String SevMajor = "MAJOR";
    public final static String SevInvalid = "INVALID";
}
