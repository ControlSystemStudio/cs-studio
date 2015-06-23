package de.desy.language.snl.diagram.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML handler to load the stored bend-points for the connections of the SNL
 * diagram associated with the current file.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class ConnectionLayoutHandler extends DefaultHandler {

    private final Map<String, List<Point>> _connectionLayoutMap;
    private String _currentConnectionKey;

    /**
     * Constructor.
     *
     * @param connectionLayoutMap
     *            the map for the stored connection layout data
     * @require connectionLayoutMap != null
     */
    public ConnectionLayoutHandler(Map<String, List<Point>> connectionLayoutMap) {
        assert connectionLayoutMap != null : "connectionLayoutMap != null";

        _connectionLayoutMap = connectionLayoutMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (XMLConstant.CONNECTION.getIdentifier().equals(name)) {
            createConnectionEntry(attributes);
        } else if (XMLConstant.POINT.getIdentifier().equals(name)) {
            addPoint(attributes);
        }
    }

    /**
     * Creates a new Point with the data contained in the given attributes and
     * inserts the point into the list contained in the
     * <code>connectionLayoutMap</code> associated with the
     * <code>currentConnectionKey</code>
     *
     * @param attributes
     *            The attributes of the XML-point-entry
     * @require _currentConnectionKey != null
     */
    private void addPoint(Attributes attributes) {
        assert _currentConnectionKey != null : "_currentConnectionKey != null";

        int x = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_X
                .getIdentifier()));
        int y = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_Y
                .getIdentifier()));

        Point point = new Point(x, y);
        _connectionLayoutMap.get(_currentConnectionKey).add(point);
    }

    /**
     * Creates and inserts a new empty list into the
     * <code>connectionLayoutMap</code> with the
     * <code>currentConnectionKey</code> as key loaded from the attributes of the
     * connection.
     *
     * @param attributes
     *            The attributes of the XML-connection-entry
     * @require _currentConnectionKey == null
     */
    private void createConnectionEntry(Attributes attributes) {
        assert _currentConnectionKey == null : "_currentConnectionKey == null";
        _currentConnectionKey = attributes.getValue(XMLConstant.NAME
                .getIdentifier());

        _connectionLayoutMap.put(_currentConnectionKey, new ArrayList<Point>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        if (XMLConstant.CONNECTION.getIdentifier().equals(name)) {
            _currentConnectionKey = null;
        }
    }

}
