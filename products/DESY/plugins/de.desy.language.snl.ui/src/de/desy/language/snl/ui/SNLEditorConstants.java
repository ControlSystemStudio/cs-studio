package de.desy.language.snl.ui;

public enum SNLEditorConstants {
	
	SOURCE_FOLDER("source"),
	GENERATED_FOLDER("generated"),
	BIN_FOLDER("bin"),
	C_FILE_EXTENSION(".c"),
	O_FILE_EXTENSION(".o"),
	ST_FILE_EXTENSION(".st"),
	I_FILE_EXTENSION(".i"),
	APPLICATION_FILE_EXTENSION("");
	
	private final String _value;

	private SNLEditorConstants(String value) {
		_value = value;
	}

	public String getValue() {
		return _value;
	}

}
