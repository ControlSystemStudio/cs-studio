package de.desy.language.snl.ui;

public enum SNLEditorConstants {
	
	SOURCE_FOLDER("source"),
	GENERATED_C_FOLDER("generated-c"),
	BIN_FOLDER("bin"),
	C_FILE_EXTENSION(".c"),
	O_FILE_EXTENSION(".o");
	
	private final String _value;

	private SNLEditorConstants(String value) {
		_value = value;
	}

	public String getValue() {
		return _value;
	}

}
