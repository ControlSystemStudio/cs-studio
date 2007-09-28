package org.csstudio.platform.internal.model.pvs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * Name parser, which can be parameterized using a {@link ControlSystemEnum}.
 * 
 * The parser will detect so called "characteristics", which are appended to the
 * normal process variable String using square bracket, e.g.
 * <b>prefix://anyproperty[characteristic]</b>.
 * 
 * 
 * @author Sven Wende
 * 
 */
public class DalNameParser extends AbstractProcessVariableNameParser {

	/**
	 * The control system.
	 */
	private ControlSystemEnum _controlSystem;

	/**
	 * Constructor.
	 * 
	 * @param controlSystem
	 *            the control system
	 */
	public DalNameParser(final ControlSystemEnum controlSystem) {
		_controlSystem = controlSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProcessVariableAddress doParse(final String nameWithoutPrefix,
			final String rawName) {
		ProcessVariableAdress result = null;
		// compile a regex pattern and parse the String
		// the used regular expression checks for the following uri components:
		// 1) line start
		// 2) 1-n arbitrary chars, except [ and ] (mandatory)
		// 3) [ followed by 1-n arbitrary chars, except [ and ], followed by ]
		// (optional)
		// 4) line end
		Pattern p = Pattern.compile("^([^,\\[\\]]+)(\\[([^\\[\\]]+)\\])?(, ([a-z;A-Z]+))?$");

		Matcher m = p.matcher(nameWithoutPrefix);

		if (m.find()) {
			String property = m.group(1);
			String characteristic = m.group(3);
			String typeHint = m.group(5);

			result = new ProcessVariableAdress(rawName, _controlSystem, null,
					property, characteristic);
			
			if(typeHint!=null) {
				DalPropertyTypes type = DalPropertyTypes.createFromPortable(typeHint);
				result.setTypeHint(type);
			}

		}

		return result;
	}

}
