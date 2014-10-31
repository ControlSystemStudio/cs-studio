package de.desy.language.snl.ui.editor;

public class HighlightingRegionMarker {
	
	private final String _beginMarker;
	private final String _endMarker;
	private final boolean _singleLine;

	public HighlightingRegionMarker(String beginSingleLineMarker) {
		assert beginSingleLineMarker != null : "beginSingleLineMarker != null";
		
		_beginMarker = beginSingleLineMarker;
		_endMarker = null;
		_singleLine = true;
	}
	
	public HighlightingRegionMarker(String beginMarker, String endMarker) {
		assert beginMarker != null : "beginMarker != null";
		assert endMarker != null : "endMarker != null";
		
		_beginMarker = beginMarker;
		_endMarker = endMarker;
		_singleLine = false;
	}
	
	public boolean isSingleLine() {
		return _singleLine;
	}

	public String getBeginMarker() {
		return _beginMarker;
	}

	public String getEndMarker() {
		assert !isSingleLine() : "!isSingleLine()";
		return _endMarker;
	}

}
