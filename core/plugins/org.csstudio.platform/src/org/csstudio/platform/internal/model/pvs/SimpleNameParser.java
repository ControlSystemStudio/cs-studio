package org.csstudio.platform.internal.model.pvs;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;

/**
 * Simple name parser, which can be parameterized using a
 * {@link ControlSystemEnum}.
 * 
 * The parser does NOT further process the raw input. Instead it returns process
 * variable addresses which use the raw input (without control system prefix) as
 * their property. This is sufficient for many control systems.
 * 
 * @author Sven Wende
 * 
 */
public class SimpleNameParser extends AbstractProcessVariableNameParser {
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
	public SimpleNameParser(final ControlSystemEnum controlSystem) {
		assert controlSystem != null;
		_controlSystem = controlSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProcessVariableAddress doParse(final String nameWithoutPrefix,
			final String rawName) {
		IProcessVariableAddress result = new ProcessVariableAdress(rawName,
				_controlSystem, null, nameWithoutPrefix, null);
		return result;
	}

}
