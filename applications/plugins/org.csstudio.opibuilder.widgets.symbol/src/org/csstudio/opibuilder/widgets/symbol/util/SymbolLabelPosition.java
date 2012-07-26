package org.csstudio.opibuilder.widgets.symbol.util;

public enum SymbolLabelPosition {

	DEFAULT("Default"),				
	TOP("Top"),	
	LEFT("Left"),
	CENTER("Center"),
	RIGHT("Right"),
	BOTTOM("Bottom"),
	TOP_LEFT("Top Left"),
	TOP_RIGHT("Top Right"),	
	BOTTOM_LEFT("Bottom Left"),
	BOTTOM_RIGHT("Bottom Right");
	
	public static String[] stringValues() {
		String[] result = new String[values().length];
		int i = 0;
		for (SymbolLabelPosition h : values()) {
			result[i++] = h.toString();
		}
		return result;
	}

	String descripion;

	SymbolLabelPosition(String description) {
		this.descripion = description;
	}

	@Override
	public String toString() {
		return descripion;
	}
}
