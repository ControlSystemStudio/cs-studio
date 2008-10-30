package org.csstudio.trends.databrowser.model;

import org.csstudio.swt.chart.axes.Marker;

/** Information about an axis marker
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MarkerInfo
{
    /** The position and value, i.e. x/y in value space */
    final private double position, value;
    
    /** The (multiline) text to display */
    final private String text;

    /** Initialize
     *  @param position Position on X axis
     *  @param value Value on Y axis
     *  @param text Multiline text content of marker
     */
    public MarkerInfo(final double position, final double value, final String text)
    {
        super();
        this.position = position;
        this.value = value;
        this.text = text;
    }

    /** @return XML encoded marker info */
    public String getXML()
    {
        return "<marker><position>" + position + "</position>" +
               "<value>" + value + "</value>" +
               "<text>" + text + "</text>" + 
               "</marker>"; 
    }

    
    public Marker createMarker()
    {
        return new Marker(position, value, text);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return getXML(); 
    }
}
