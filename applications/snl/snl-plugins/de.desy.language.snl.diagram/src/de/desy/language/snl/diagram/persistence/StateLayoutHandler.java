package de.desy.language.snl.diagram.persistence;

import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML handler to load the stored location and size for states and state-sets of
 * the SNL diagram associated with the current file.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
public class StateLayoutHandler extends DefaultHandler {

    private final Map<String, StateLayoutData> _stateLayoutMap;

    /**
     * Constructor.
     *
     * @param stateLayoutMap
     *            the map for the stored state and state-set layout data
     * @require stateLayoutMap != null
     */
    public StateLayoutHandler(final Map<String, StateLayoutData> stateLayoutMap) {
        assert stateLayoutMap != null : "stateLayoutMap != null";
        _stateLayoutMap = stateLayoutMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String name,
            Attributes attributes) throws SAXException {
        if (XMLConstant.STATE.getIdentifier().equals(name)
                || XMLConstant.STATE_SET.getIdentifier().equals(name)) {
            createMapEntry(attributes);
        }
    }

    /**
     * Creates and inserts a {@link StateLayoutData} into the
     * <code>stateLayoutMap</code> with the name of the current element as key
     * loaded from the attributes of the element.
     *
     * @param attributes
     *            The attributes of XML state or state-set entry
     */
    private void createMapEntry(Attributes attributes) {
        String key = attributes.getValue(XMLConstant.NAME.getIdentifier());
        int x = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_X
                .getIdentifier()));
        int y = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_Y
                .getIdentifier()));
        int width = Integer.valueOf(attributes.getValue(XMLConstant.WIDTH
                .getIdentifier()));
        int height = Integer.valueOf(attributes.getValue(XMLConstant.HEIGHT
                .getIdentifier()));

        StateLayoutData layoutData = new StateLayoutData(new Point(x, y),
                new Dimension(width, height));
        _stateLayoutMap.put(key, layoutData);
    }

}
