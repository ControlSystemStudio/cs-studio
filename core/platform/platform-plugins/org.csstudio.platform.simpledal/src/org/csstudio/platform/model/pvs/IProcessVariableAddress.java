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
package org.csstudio.platform.model.pvs;

import org.csstudio.dal.simple.RemoteInfo;

/**
 * A process variable address provides consistent and convinient access to the
 * information that constitute a full process variable name.
 * 
 * These information include:
 * 
 * <ul>
 * <li>control system prefix (mandatory)</li>
 * <li>device (optional)</li>
 * <li>property (mandatory)</li>
 * <li>characteristic (optional)</li>
 * </ul>
 * 
 * Process variable addresses should in most cases get created, using the
 * {@link ProcessVariableAdressFactory} factory.
 * 
 * @author Sven Wende
 * 
 */
public interface IProcessVariableAddress {
	/**
	 * Returns the "control system" part of the process variable.
	 * 
	 * @return the control system part
	 */
	ControlSystemEnum getControlSystem();

	/**
	 * Returns the "device" part of the process variable.
	 * 
	 * @return the device
	 */
	String getDevice();

	/**
	 * Returns the "property" part of the process variable.
	 * 
	 * @return the property
	 */
	String getProperty();

	/**
	 * Returns the "characteristic" part of the process variable pointer or
	 * null.
	 * 
	 * @return the characteristic part or null
	 */
	String getCharacteristic();

	String getRawName();

	String getFullName();

	boolean isCharacteristic();

	ValueType getValueTypeHint();

	/**
	 * Returns a DAL {@link RemoteInfo} object for this process variable
	 * address. May be null, if DAL does not support this kind of PVs.
	 * 
	 * @return a DAL RemoteInfo or null
	 */
	RemoteInfo toDalRemoteInfo();

	/**
	 * Derives an address that cuts off an existing characteristic.
	 * 
	 * @return a "normal" address (without characteristic)
	 */
	IProcessVariableAddress deriveNoCharacteristicPart();

	/**
	 * Derives an address for the specified characteristic.
	 * 
	 * @param characteristic
	 *            the characteristic
	 * 
	 * @return an address for a characteristic
	 */
	IProcessVariableAddress deriveCharacteristic(String characteristic);
}
