/**
 * 
 */
package org.csstudio.platform.model.pvs;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;


/**
 * An enumeration for all available control system prefixes.
 * 
 * Maybe this indirection is not needed, but it can help to match different
 * namespaces.
 * 
 * @author Sven Wende
 * 
 */
public enum ControlSystemEnum implements IAdaptable {
	SDS_SIMULATOR("local", null, false),

	DAL_SIMULATOR("simulator", "Simulator", true),

	DAL_EPICS("dal-epics", "EPICS", true),

	DAL_TINE("dal-tine", "TINE", true),

	DAL_TANGO("dal-tango", "TANGO", true),

	TINE("tine", "TINE", true),

	EPICS("epics", "EPICS", true),

	TANGO("tango", "TANGO", false),
	
	UNKNOWN("", null, false);


	private String _prefix;

	private String _dalName;

	private boolean _supportedByDAL;

	ControlSystemEnum(String prefix, String dalName, boolean supportedByDAL) {
		assert prefix != null;
		_prefix = prefix;
		_dalName = dalName;
		_supportedByDAL = supportedByDAL;
	}

	public String getPrefix() {
		return _prefix;
	}

	public String getResponsibleDalPlugId() {
		return _dalName;
	}

	public boolean isSupportedByDAL() {
		return _supportedByDAL;
	}

	@Override
	public String toString() {
		return _prefix;
	}
	
	public static ControlSystemEnum findByPrefix(String prefix) {
		ControlSystemEnum result=UNKNOWN;
		for(ControlSystemEnum e : values()) {
			if(e.getPrefix().equalsIgnoreCase(prefix)) {
				result = e;
			}
		}
		
		return result;
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}