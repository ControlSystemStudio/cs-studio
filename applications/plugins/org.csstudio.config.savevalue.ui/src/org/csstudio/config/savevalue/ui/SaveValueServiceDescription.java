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

package org.csstudio.config.savevalue.ui;


/**
 * Describes a save value service.
 * 
 * @author Joerg Rathlev
 */
final class SaveValueServiceDescription {

	/**
	 * The RMI name of the service.
	 */
	private String _rmiName;
	
	/**
	 * Whether the service is required.
	 */
	private boolean _required;

	/**
	 * A human-readable name for the service.
	 */
	private String _displayName;
	

	/**
	 * Creates a new service description.
	 * 
	 * @param rmiName
	 *            the RMI name of the service.
	 * @param required
	 *            whether the service is required.
	 * @param displayName
	 *            a name for the service that will be displayed to the user.
	 */
	SaveValueServiceDescription(final String rmiName,
			final boolean required, final String displayName) {
		_rmiName = rmiName;
		_required = required;
		_displayName = displayName;
	}

	/**
	 * Returns the RMI name of the service.
	 * 
	 * @return the RMI name of the service.
	 */
	public String getRmiName() {
		return _rmiName;
	}

	/**
	 * Returns whether the service is required.
	 * 
	 * @return whether the service is required.
	 */
	public boolean isRequired() {
		return _required;
	}
	
	/**
	 * Returns a human-readable name for the described service.
	 * 
	 * @return a human-readable name for the described service.
	 */
	public String getDisplayName() {
		return _displayName;
	}
}
