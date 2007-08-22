package org.csstudio.platform.model.pvs;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.internal.model.pvs.AbstractProcessVariableNameParser;
import org.csstudio.platform.internal.model.pvs.DalNameParser;
import org.csstudio.platform.internal.model.pvs.SimpleNameParser;
import org.eclipse.core.runtime.Platform;

/**
 * Factory for process variable adresses.
 * 
 * @author Sven Wende
 * 
 */
public class ProcessVariableAdressFactory {
	public static final String PROP_CONTROL_SYSTEM = "PROP_CONTROL_SYSTEM"; //$NON-NLS-1$

	public static final String PROP_ASK_FOR_CONTROL_SYSTEM = "PROP_ASK_FOR_CONTROL_SYSTEM"; //$NON-NLS-1$

	private static ProcessVariableAdressFactory _instance;

	private static HashMap<ControlSystemEnum, AbstractProcessVariableNameParser> _parserMapping;

	static {
		_parserMapping = new HashMap<ControlSystemEnum, AbstractProcessVariableNameParser>();
		_parserMapping.put(ControlSystemEnum.DAL_EPICS, new DalNameParser(
				ControlSystemEnum.DAL_EPICS));
		_parserMapping.put(ControlSystemEnum.DAL_TINE, new DalNameParser(
				ControlSystemEnum.DAL_TINE));
		_parserMapping.put(ControlSystemEnum.DAL_TANGO, new DalNameParser(
				ControlSystemEnum.DAL_TANGO));
		_parserMapping.put(ControlSystemEnum.UNKNOWN, new SimpleNameParser(
				ControlSystemEnum.UNKNOWN));
		_parserMapping.put(ControlSystemEnum.EPICS, new SimpleNameParser(
				ControlSystemEnum.EPICS));
		_parserMapping.put(ControlSystemEnum.TINE, new SimpleNameParser(
				ControlSystemEnum.TINE));
		_parserMapping.put(ControlSystemEnum.TANGO, new SimpleNameParser(
				ControlSystemEnum.TANGO));
		_parserMapping.put(ControlSystemEnum.SDS_SIMULATOR,
				new SimpleNameParser(ControlSystemEnum.SDS_SIMULATOR));
		_parserMapping.put(ControlSystemEnum.DAL_SIMULATOR,
				new SimpleNameParser(ControlSystemEnum.DAL_SIMULATOR));
	}

	private ProcessVariableAdressFactory() {

	}

	public static ProcessVariableAdressFactory getInstance() {
		if (_instance == null) {
			_instance = new ProcessVariableAdressFactory();
		}
		return _instance;
	}

	public IProcessVariableAdress createProcessVariableAdress(String rawName,
			ControlSystemEnum controlSystem) {
		// determine name parser
		AbstractProcessVariableNameParser nameParser = _parserMapping
				.get(controlSystem);

		// parse raw name
		IProcessVariableAdress result = nameParser.parseRawName(rawName);

		return result;
	}

	public IProcessVariableAdress createProcessVariableAdress(String rawName) {
		// determine control system
		ControlSystemEnum controlSystem = getControlSystem(rawName);

		if (controlSystem == null) {
			controlSystem = getDefaultControlSystem();
		}

		return createProcessVariableAdress(rawName, controlSystem);
	}

	public boolean hasValidControlSystemPrefix(String rawName) {
		return getControlSystem(rawName) != null;
	}


	public ControlSystemEnum getDefaultControlSystem() {
		String defaultCs = Platform.getPreferencesService().getString(
				CSSPlatformPlugin.ID, PROP_CONTROL_SYSTEM, "", //$NON-NLS-1$
				null);

		ControlSystemEnum controlSystem = ControlSystemEnum
				.findByPrefix(defaultCs);

		if (controlSystem == null) {
			controlSystem = ControlSystemEnum.UNKNOWN;
		}
		assert controlSystem != null;
		return controlSystem;
	}

	public boolean askForControlSystem() {
		boolean result = Platform.getPreferencesService().getBoolean(
				CSSPlatformPlugin.ID, PROP_ASK_FOR_CONTROL_SYSTEM, true, //$NON-NLS-1$
				null);

		return result;
	}

	public IProcessVariableAdress createProcessVariableAdress(
			ControlSystemEnum controlSystemEnum, String device,
			String property, String characteristics) {
		throw new RuntimeException("not supported yet");
	}
	
	private ControlSystemEnum getControlSystem(String rawName) {
		ControlSystemEnum controlSystem = ControlSystemEnum.UNKNOWN;

		// compile a regex pattern and parse the String
		Pattern p = Pattern.compile("^([^:]*)://");

		Matcher m = p.matcher(rawName);

		if (m.find()) {
			String s = m.group(1);
			if (s != null && s.length() > 0) {
				controlSystem = ControlSystemEnum.findByPrefix(s.toUpperCase());
			}
		}

		return controlSystem;
	}
}
