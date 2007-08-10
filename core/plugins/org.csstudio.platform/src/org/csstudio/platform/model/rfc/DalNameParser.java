package org.csstudio.platform.model.rfc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.CSSPlatformPlugin;

public class DalNameParser extends
		AbstractProcessVariableNameParser {

	private ControlSystemEnum _controlSystem;
	
	
	public DalNameParser(ControlSystemEnum controlSystem) {
		_controlSystem = controlSystem;
	}


	@Override
	protected IProcessVariableAdress doParse(String nameWithoutPrefix, String rawName) {
		IProcessVariableAdress result = null;
		// compile a regex pattern and parse the String
		// the used regular expression checks for the following uri components:
		// 1) line start
		// 2) 1-n arbitrary chars, except [ and ] (mandatory)
		// 3) [ followed by 1-n arbitrary chars, except [ and ], followed by ] (optional)
		// 4) line end
		Pattern p = Pattern
				.compile("^([^\\[\\]]+)(\\[([^\\[\\]]+)\\])?$");

		Matcher m = p.matcher(nameWithoutPrefix);

		if (m.find()) {
			String property = m.group(1);
			String characteristic = m.group(3);

			result = new ProcessVariable(rawName, _controlSystem,
					null, property, characteristic);

		} else {
			result = createFallbackProcessVariableAdress(rawName);
		}

		return result;
	}

}
