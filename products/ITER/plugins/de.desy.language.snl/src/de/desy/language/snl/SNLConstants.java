package de.desy.language.snl;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import de.desy.language.snl.internal.SNLCoreActivator;

public enum SNLConstants {
	
	SOURCE_FOLDER("source"),
	GENERATED_FOLDER("generated"),
	BIN_FOLDER("bin"),
	C_FILE_EXTENSION(".c"),
	O_FILE_EXTENSION(".o"),
	ST_FILE_EXTENSION(".st"),
	I_FILE_EXTENSION(".i"),
	APPLICATION_FILE_EXTENSION("");
	
	private final String _value;
    private final IPreferencesService prefs = Platform.getPreferencesService();

	private SNLConstants(String value) {
		_value = value;
	}

	public String getValue() {
		String val = prefs.getString(SNLCoreActivator.PLUGIN_ID, name(), _value, null);
		return "<null>".equals(val) ? null : val;
	}

}
