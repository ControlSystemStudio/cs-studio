package org.csstudio.platform.model.rfc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractProcessVariableNameParser {
	public IProcessVariableAdress parseRawName(String rawName) {
		String input = removeProtocol(rawName);
		return doParse(input, rawName);
	}
	
	protected abstract IProcessVariableAdress doParse(String input, String rawName);
	
	
	private String removeProtocol(String rawName) {
		String rawNameWithoutControlSystem = new String(rawName);
		
		// compile a regex pattern and parse the String
		Pattern p = Pattern.compile("^.*://(.*)");

		Matcher m = p.matcher(rawName);

		if (m.find()) {
			rawNameWithoutControlSystem = m.group(1);
		}

		assert rawNameWithoutControlSystem != null;
		return rawNameWithoutControlSystem;
	}

	
	protected IProcessVariableAdress createFallbackProcessVariableAdress(String rawName) {
		return new ProcessVariable(rawName, ControlSystemEnum.UNKNOWN, null, removeProtocol(rawName), null);
	}
}
