/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

/** Select how a trace should be displayed.
 *  @author Blaz Lipuscek 
 *  @author Kay Kasemir
 */
public enum TraceType
{
    /** Using line for the average and area for
     *  the min/max envelope.
     */
    Area(Messages.TraceType_Area),
    
    /** Connect samples with lines.
     *  Uses additional min/max lines for samples
     *  that carry min/max info.
     */
    Lines(Messages.TraceType_Lines),
    
    /** Connect samples with lines.
     *  Uses additional min/max lines for samples
     *  that carry min/max info.
     */
    SingleLine(Messages.TraceType_SingleLine),

    /** Place a marker at each sample.
     *  <p>
     *  Uses candlesticks for samples that carry min/max info.
     */
    Markers(Messages.TraceType_Markers),
    
    /** Bar from the x axis up to the y value.
     *  <p>
     *  Doesn't show min/max info.
     */
    Bars(Messages.TraceType_Bars);

    /** User-readable name, localized. */
    final private String localized_name;
    
    /** List of all localized names. */
    private static String[] localized_names;
    
    /** Constructor. */
    private TraceType(final String localized_name)
    {
        this.localized_name = localized_name;
    }
    
    /** @return Localized name.
     *  @see #name()
     */
    final public String getLocalizedName()
    {
        return localized_name;
    }
    
    /** Get the available trace types as localized names.
     *  @return An array of Strings that describes the available TraceTypes.
     *  @see #fromOrdinal(int)
     */ 
    public final static String[] getLocalizedNames()
    {
        if (localized_names == null) 
        {
            final TraceType[] types = TraceType.values();
            localized_names = new String[types.length]; 
            for (int i = 0; i < types.length; i++)
                localized_names[i] = types[i].localized_name;
        }
        return localized_names;
    }
    
    /** Obtain a trace type from its ordinal
     *  @return TraceType for the given ordinal. 
     */
    public static TraceType fromOrdinal(int ordinal)
    {   // This is expensive, but java.lang.Enum offers no easy way...
        for (TraceType id : TraceType.values())
            if (id.ordinal() == ordinal)
                return id;
        throw new Error("Invalid ordinal " + ordinal); //$NON-NLS-1$
    }

    /** Obtain a trace type from its name.
     *  That's the fixed name, not a localized title that the application
     *  might use elsewhere!
     *  @return TraceType for the given ordinal.
     */
    public static TraceType fromName(String name)
    {   // This is expensive, but java.lang.Enum offers no easy way...
        for (TraceType id : TraceType.values())
            if (id.name().equals(name))
                return id;
        throw new Error("Invalid name " + name); //$NON-NLS-1$
    }
}
