package de.desy.language.snl.ui.editor;

public class ErrorUnit {
	
	private final String _message;
	private final Integer _lineNumber;
	private final String _details;
	
	public ErrorUnit(String message, String details) {
		this(message, null, details);
	}

	public ErrorUnit(String message, Integer lineNumber, String details) {
		assert message != null : "message != null";
		assert details != null : "details != null";
		
		_message = message;
		_lineNumber = lineNumber;
		_details = details;
	}

	public String getMessage() {
		return _message;
	}
	
	public String getDetails() {
		return _details;
	}

	public int getLineNumber() {
		assert hasLineNumber() : "hasLineNumber()";
				
		return _lineNumber;
	}
	
	public boolean hasLineNumber() {
		return _lineNumber != null;
	}

}
