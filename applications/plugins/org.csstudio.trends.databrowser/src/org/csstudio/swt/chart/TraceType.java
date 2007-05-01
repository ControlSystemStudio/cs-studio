package org.csstudio.swt.chart;

/** The various types for drawing a Trace.
 *  @author Blaz Lipuscek 
 *  @author Kay Kasemir
 */
public enum TraceType
{
    /** Connect samples with lines. */
    Lines,
    Markers,
    Candlestick,
    MinMaxAverage,
    Bars;
    
    private static String[] type_strings;
    
    /** @return An array of Strings that describes the available TraceTypes. */ 
    public final static String[] getTypeStrings()
    {
        if (type_strings == null) 
        {
            TraceType[] types = TraceType.values();
            type_strings = new String[types.length]; 
            for (int i = 0; i < types.length; i++)
                type_strings[i] = types[i].name();
        }
        return type_strings;
    }
    
    /** @return TraceType for the given ordinal. */
    public static TraceType fromOrdinal(int ordinal)
    {   // This is expensive, but java.lang.Enum offers no easy way...
        for (TraceType id : TraceType.values())
            if (id.ordinal() == ordinal)
                return id;
        throw new Error("Invalid ordinal " + ordinal); //$NON-NLS-1$
    }
}
