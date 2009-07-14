package de.desy.language.snl.diagram.persistence;

import java.util.Map;

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
//		attributes.getValue(qName)
	}
	
}
