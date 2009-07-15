package de.desy.language.snl.diagram.persistence;

public enum XMLConstant {
	
	DIAGRAM("diagram"),
	NAME("name"),
	STATE("state"),
	STATE_SET("stateSet"),
	CONNECTION("connection"),
	POINT("point"),
	LOCATION_X("location_x"),
	LOCATION_Y("location_y"),
	WIDTH("width"),
	HEIGHT("height"),
	STATE_DATA("stateData"),
	CONNECTION_DATA("connectionData");

	
	private final String _identifier;

	private XMLConstant(String identifier) {
		_identifier = identifier;
	}
	
	public String getIdentifier() {
		return _identifier;
	}
	
}
