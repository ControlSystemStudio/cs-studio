package org.csstudio.trends.databrowser.model;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.swt.chart.axes.Marker;
import org.w3c.dom.Element;

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

    /** Parse MarkerInfo from XML DOM
     *  @param marker_node Node that contains the marker
     *                     as created by <code>getXML</code>
     *  @return MarkerInfo
     *  @throws Exception on error
     */
    public static MarkerInfo fromDOM(final Element marker_node) throws Exception
    {
        final double position =
            DOMHelper.getSubelementDouble(marker_node, "position");
        final double value =DOMHelper.getSubelementDouble(marker_node, "value");
        final String text = DOMHelper.getSubelementString(marker_node, "text");
        return new MarkerInfo(position, value, text);
    }

    /** @return XML encoded marker info */
    public String getXML()
    {
        return "<marker><position>" + position + "</position>" +
               "<value>" + value + "</value>" +
               "<text>" + text + "</text>" + 
               "</marker>"; 
    }

    /** @return Chart Marker for this MarkerInfo */
    public Marker toMarker()
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
