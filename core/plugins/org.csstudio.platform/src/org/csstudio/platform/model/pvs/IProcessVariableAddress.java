package org.csstudio.platform.model.pvs;

import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.context.RemoteInfo;

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
}
