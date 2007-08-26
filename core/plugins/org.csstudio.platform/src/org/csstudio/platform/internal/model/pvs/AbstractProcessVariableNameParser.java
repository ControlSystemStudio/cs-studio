package org.csstudio.platform.internal.model.pvs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * Base class for name parsers which parse process variable addresses from
 * Strings.
 * 
 * @author Sven Wende
 * 
 */
public abstract class AbstractProcessVariableNameParser {
	/**
	 * Parses the specified raw name and returns a pv adress.
	 * 
	 * @param rawName
	 *            the raw name
	 * @return the pv adress or null
	 */
	public final IProcessVariableAddress parseRawName(final String rawName) {
		String nameWithoutPrefix = removeProtocol(rawName);
		IProcessVariableAddress result = doParse(nameWithoutPrefix, rawName);

		if (result == null) {
			result = createFallbackProcessVariableAdress(nameWithoutPrefix);
		}

		return result;
	}

	/**
	 * Template method which has to be implemented by subclasses.
	 * 
	 * @param input
	 *            the raw name without control system prefix
	 * 
	 * @param rawName
	 *            the raw name
	 * 
	 * @return
	 */
	protected abstract IProcessVariableAddress doParse(String input,
			String rawName);

	/**
	 * Helper method, which removes the control system prefix from the specified
	 * raw name.
	 * 
	 * @param rawName
	 *            the raw name
	 * 
	 * @return a name without control system prefix
	 */
	private String removeProtocol(final String rawName) {
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

	/**
	 * Crates a default pv adress which can be used as fallback.
	 * 
	 * @param rawName
	 *            the raw name
	 * 
	 * @return a fallback pv adress
	 */
	private IProcessVariableAddress createFallbackProcessVariableAdress(
			final String rawName) {
		return new ProcessVariableAdress(rawName, ControlSystemEnum.UNKNOWN,
				null, removeProtocol(rawName), null);
	}
}
