package org.csstudio.platform.internal.model.pvs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAdress;

public class GenericNameParser extends
		AbstractProcessVariableNameParser {

	@Override
	protected IProcessVariableAdress doParse(String nameWithoutPrefix, String rawName) {
		IProcessVariableAdress result = null;
		// We use a regular expression to parse the various String variants
		// (A-E) that might occur.
		// 
		// To prepare the provided String as input for a regular expression, we
		// have to ensure the following preconditions:
		// - leading double-slashes must be replaced by §§§ (this is necessary
		// to differ between // and /)
		// - if there is no leading slash at all, one has to be added (this is
		// necessary when only a property is entered)

		// replace occurence of double slash with another String to prepare a
		// String that can be used as input for a regular expression
		String input = nameWithoutPrefix.replace("//", "§§§");

		// check leading slash
		if (!(input.startsWith("§§§") || input.startsWith("/"))) {
			// add a leading slash
			input = "/" + input;
		}

		// compile a regex pattern and parse the String
		Pattern p = Pattern
				.compile("^([^/]+)?(/([^/]+))?(/([^/\\[\\]]+))(\\[([^/\\[\\]]+)\\])?$");

		Matcher m = p.matcher(input);

		if (m.find()) {
			String device = m.group(3);
			String property = m.group(5);
			String characteristic = m.group(7);

			result = new ProcessVariableAdress(rawName, ControlSystemEnum.EPICS,
					device, property, characteristic);

		} else {
			throw new IllegalArgumentException(
					"The provided String does not match the required format: //controlsystem/device/property[characteristic]");
		}

		return result;
	}

}
