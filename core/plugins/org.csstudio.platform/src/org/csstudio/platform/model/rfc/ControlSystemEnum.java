/**
 * 
 */
package org.csstudio.platform.model.rfc;

/**
 * An enumeration for all available control system prefixes.
 * 
 * Maybe this indirection is not needed, but it can help to match different namespaces.
 * 
 * @author Sven Wende
 * 
 */
public enum ControlSystemEnum {
	LOCAL("local", null, false),
	
	SIMULATOR("epics", "EPICS", true),
	
	EPICS("epics", "EPICS", true),

	TINE("tine", "TINE", true),
	
	TANGO("tango", "TANGO", false);

	private String _name;
	
	private String _dalName;
	
	private boolean _supportedByDAL;
	
	public static final String PROP_CONTROL_SYSTEM = "PROP_CONTROL_SYSTEM";

	ControlSystemEnum(String name, String dalName, boolean supportedByDAL) {
		assert name != null;
		_name = name;
		_dalName = dalName;
		_supportedByDAL = supportedByDAL;
	}

	public String getProcessVariableUriRepresentation() {
		return _name;
	}

	public String getResponsibleDalPlugId() {
		return _dalName;
	}
	
	public boolean isSupportedByDAL() {
		return _supportedByDAL;
	}

	@Override
	public String toString() {
		return _name;
	}

}