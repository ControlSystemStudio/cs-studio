/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
	private static final Pattern CONTROL_SYSTEM_PREFIX_PATTERN = Pattern.compile("^.*://(.*)");
	
	/**
	 * Parses the specified raw name and returns a pv adress.
	 * 
	 * @param rawName
	 *            the raw name
	 * @return the pv adress or null
	 */
	public final IProcessVariableAddress parseRawName(final String rawName) {
		IProcessVariableAddress result = null;
		assert rawName != null;
		
		if (rawName.length() > 0) {
			String nameWithoutPrefix = removeProtocol(rawName);
			result = doParse(nameWithoutPrefix, rawName);
		}

		if (result == null) {
			result = createFallbackProcessVariableAdress(rawName);
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
	protected abstract IProcessVariableAddress doParse(
			String nameWithoutPrefix, String rawName);

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
		String rawNameWithoutControlSystem = rawName;


		Matcher m = CONTROL_SYSTEM_PREFIX_PATTERN.matcher(rawName);

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
