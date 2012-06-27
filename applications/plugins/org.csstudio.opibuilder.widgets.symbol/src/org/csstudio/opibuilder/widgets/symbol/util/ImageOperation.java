package org.csstudio.opibuilder.widgets.symbol.util;

public enum ImageOperation {

	DEFAULT("Default", 0),				
	FH("Flip horizontal", 1),	
	FV("Flip vertical", 2),
	RR90("Rotate right 90", 3),
	RL90("Rotate left 90", 4),
	R180("Rotate 180", 5);
	
	public static String[] stringValues() {
		String[] result = new String[values().length];
		int i = 0;
		for (ImageOperation io : values()) {
			result[i++] = io.toString();
		}
		return result;
	}

	int value;
	String descripion;

	ImageOperation(String description, int value) {
		this.value = value;
		this.descripion = description;
	}
	
	@Override
	public String toString() {
		return descripion;
	}
}
