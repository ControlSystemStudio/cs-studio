package org.csstudio.platform.model.pvs;

import org.epics.css.dal.context.RemoteInfo;

/**
 * A process variable pointer, which provides consistent access to the
 * information that consitute a full process variable adress.
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
 * At runtime, process variables might be created by tools (e.g. the data
 * browser) or can be entered manually by users.
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

	/**
	 * Convinience methods, which returns a DAL RemoteInfo {@link RemoteInfo}
	 * object for this process variable pointer. May be null, if DAL does not
	 * support this kind of PVs.
	 * 
	 * @return a DAL RemoteInfo or null
	 */
	RemoteInfo toDalRemoteInfo();
}
