package de.desy.language.snl.diagram.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ConnectionLayoutHandler extends DefaultHandler {

	private final Map<String, List<Point>> _connectionLayoutMap;
	private String _currentConnectionKey;

	public ConnectionLayoutHandler(Map<String, List<Point>> connectionLayoutmap) {
		_connectionLayoutMap = connectionLayoutmap;
	}
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (XMLConstant.CONNECTION.getIdentifier().equals(name)) {
			createConnectionEntry(attributes);
		} else if (XMLConstant.POINT.getIdentifier().equals(name)) {
			addPoint(attributes);
		}
	}

	private void addPoint(Attributes attributes) {
		assert _currentConnectionKey != null : "_currentConnectionKey != null";
		
		int x = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_X.getIdentifier()));
		int y = Integer.valueOf(attributes.getValue(XMLConstant.LOCATION_Y.getIdentifier()));
		
		Point point = new Point(x, y);
		_connectionLayoutMap.get(_currentConnectionKey).add(point);
	}

	private void createConnectionEntry(Attributes attributes) {
		assert _currentConnectionKey == null : "_currentConnectionKey == null";
		_currentConnectionKey = attributes.getValue(XMLConstant.NAME.getIdentifier());
		
		_connectionLayoutMap.put(_currentConnectionKey, new ArrayList<Point>());
	}
	
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (XMLConstant.CONNECTION.getIdentifier().equals(name)) {
			_currentConnectionKey = null;
		}
	}

}
