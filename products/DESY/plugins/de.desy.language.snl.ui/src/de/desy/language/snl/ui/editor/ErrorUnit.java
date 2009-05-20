package de.desy.language.snl.ui.editor;

public class ErrorUnit {
	
	private final String _message;
	private final Integer _lineNumber;
	
	public ErrorUnit(String message) {
		this(message, null);
	}

	public ErrorUnit(String message, Integer lineNumber) {
		assert message != null : "message != null";
		
		_message = message;
		_lineNumber = lineNumber;
	}

	public String getMessage() {
		return _message;
	}

	public int getLineNumber() {
		assert hasLineNumber() : "hasLineNumber()";
				
		return _lineNumber;
	}
	
	public boolean hasLineNumber() {
		return _lineNumber != null;
	}

}
