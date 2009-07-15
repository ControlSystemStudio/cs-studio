package de.desy.language.snl.diagram.persistence;

import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StateLayoutHandler extends DefaultHandler {

	private final Map<String, StateLayoutData> _stateLayoutMap;

	public StateLayoutHandler(final Map<String, StateLayoutData> stateLayoutMap) {
		assert stateLayoutMap != null : "stateLayoutMap != null";
		_stateLayoutMap = stateLayoutMap;
	}
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (XMLConstant.STATE.getIdentifier().equals(name) || XMLConstant.STATE_SET.getIdentifier().equals(name)) {
			createMapEntry(attributes);
		} 
	}

	private void createMapEntry(Attributes attributes) {
		String key = attributes.getValue(XMLConstant.NAME.getIdentifier());
		int x = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_X.getIdentifier()));
		int y = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_Y.getIdentifier()));
		int width = Integer.valueOf(attributes.getValue(XMLConstant.WIDTH.getIdentifier()));
		int height = Integer.valueOf(attributes.getValue(XMLConstant.HEIGHT.getIdentifier()));
		
		StateLayoutData layoutData = new StateLayoutData(new Point(x, y), new Dimension(width, height));
		_stateLayoutMap.put(key, layoutData);
	}
	
}
