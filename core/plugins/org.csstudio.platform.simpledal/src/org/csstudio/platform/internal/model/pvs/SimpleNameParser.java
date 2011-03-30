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
