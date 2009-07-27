package de.desy.language.snl.ui;

public enum Identifier {
	
	SNL_EDITOR_ID("de.desy.language.snl.ui.SNLEditor");
	
	private final String _id;

	private Identifier(String id) {
		assert id != null : "id != null";
		assert id.trim().length() > 0 : "${param}.trim().length() > 0";
		
		_id = id;
	}
	
	public String getId() {
		return _id;
	}

}
