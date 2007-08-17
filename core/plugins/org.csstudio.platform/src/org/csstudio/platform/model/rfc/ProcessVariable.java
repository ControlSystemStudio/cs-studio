/**
 * 
 */
package org.csstudio.platform.model.rfc;

import java.io.PrintStream;

import javax.naming.NamingException;

import org.csstudio.platform.CSSPlatformPlugin;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.context.RemoteInfo;

/**
 * This data type represents a process variable pointer, which provides
 * consistent access to the information that consitute a full process variable
 * adress.
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
 * For the manual entry, we need to agree on a URI convention that represents
 * the various information combinations that are possible because of the
 * optional informations.
 * 
 * By now, the following URI-style is implemented:
 * 
 * <ul>
 * <li>//controlsystem/device/property[characteristic]</li>
 * <li>//controlsystem/device/property</li>
 * <li>//controlsystem/property[characteristic]</li>
 * <li>//controlsystem/property</li>
 * <li>/device/property[characteristic]</li>
 * <li>/device/property</li>
 * <li>property[characteristic]</li>
 * <li>property</li>
 * </ul>
 * 
 * As you can see above, the control system prefix is marked by a leading double
 * slash > //. Characteristics are surrounded by square brackets > []. All other
 * parts of the URI are separated by a single slash > /. If only the property
 * part of the URI should be entered manually, the slash can be omitted.
 * 
 * The described convention makes the following assumptions, which migth break
 * local naming conventions that are used in the different institutions:
 * 
 * <ul>
 * <li>device names contain no slashes /</li>
 * <li>property names contain no slashes /</li>
 * <li>property names contain no square brackets []</li>
 * </ul>
 * 
 * @author Sven Wende
 * 
 */
class ProcessVariable implements IProcessVariableAdress {
	public static final String PART_SEPARATOR = "/";

	private ControlSystemEnum _controlSystem;

	private String _device;

	private String _property;

	private String _characteristic;

	private String _rawName;

	/**
	 * Constructs a process variable pointer using the provided information
	 * pieces.
	 * 
	 * @param controlSystem
	 *            a control system prefix (when null is provided, a default will
	 *            be chosen)
	 * @param device
	 *            a device name (optional, provide null to leave it out)
	 * @param property
	 *            a property name
	 * @param characteristic
	 *            a DAL characteristic id (e.g. see
	 *            {@link NumericPropertyCharacteristics} (optional, provide null
	 *            to leave it out)
	 */
	public ProcessVariable(final String rawName,
			final ControlSystemEnum controlSystem, final String device,
			final String property, final String characteristic) {
		_rawName = rawName;
		// control system is mandatory, but will be set to a default, when
		// its not provided
		if (controlSystem == null) {
			String controlSystemString = CSSPlatformPlugin.getDefault()
					.getPluginPreferences().getString(
							PvAdressFactory.PROP_CONTROL_SYSTEM);
			_controlSystem = ControlSystemEnum.valueOf(controlSystemString);
			// _controlSystem = SomeWhere.DEFAULT_CONTROL_SYSTEM;
		} else {
			_controlSystem = controlSystem;
		}

		// device is optional
		_device = device;

		// property is mandatory
		if (property == null) {
			throw new IllegalArgumentException("Property is mandatory.");
		} else {
			_property = property;
		}

		// characteristics is optional
		_characteristic = characteristic;
	}

	/**
	 * Returns the "characteristic" part of the process variable pointer. May be
	 * null.
	 * 
	 * @return the characteristic part
	 */
	public String getCharacteristic() {
		return _characteristic;
	}

	/**
	 * Returns the "control system" part of the process variable.
	 * 
	 * @return the control system part
	 */
	public ControlSystemEnum getControlSystemEnum() {
		return _controlSystem;
	}

	/**
	 * Returns the "device" part of the process variable.
	 * 
	 * @return the device
	 */
	public String getDevice() {
		return _device;
	}

	/**
	 * Returns the "property" part of the process variable.
	 * 
	 * @return the property
	 */
	public String getProperty() {
		return _property;
	}

	/**
	 * Convinience methods, which returns a DAL RemoteInfo {@link RemoteInfo}
	 * object for this process variable pointer. May be null, if DAL does not
	 * support this kind of PVs.
	 * 
	 * @return a DAL RemoteInfo or null
	 */
	public RemoteInfo toDalRemoteInfo() {
		assert _controlSystem != null;
		assert _property != null;

		RemoteInfo remoteInfo = null;

		if (_controlSystem.isSupportedByDAL()) {
			try {
				remoteInfo = new RemoteInfo(_property, "DEFAULT",
						_controlSystem.getResponsibleDalPlugId());
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return remoteInfo;
	}

	/**
	 * Returns the full String representation of this process variable.
	 * 
	 * @return the full String representation
	 */
	public String getFullName() {
		assert _controlSystem != null;
		assert _property != null;
		StringBuffer sb = new StringBuffer();

		sb.append(_controlSystem.getPrefix());
		sb.append("://");
		sb.append(_property);
		
//		// device (is optional)
//		if (_device != null) {
//			if (sb.length() > 0) {
//				sb.append(PART_SEPARATOR);
//			}
//
//			sb.append(_device);
//		}
//
//		// property
//		if (sb.length() > 0) {
//			sb.append(PART_SEPARATOR);
//		}
//
//		sb.append(_property);

		// characteristic (is optional)
		if (_characteristic != null) {
			sb.append("[");
			sb.append(_characteristic);
			sb.append("]");
		}

		return sb.toString();
	}

	/**
	 * Returns the reduced String representation (property + characteristic) of
	 * this process variable.
	 * 
	 * @return the reduced String representation
	 */
	public String toReducedString() {
		assert _property != null;
		StringBuffer sb = new StringBuffer();

		sb.append(_property);

		// characteristic (is optional)
		if (_characteristic != null) {
			sb.append("[");
			sb.append(_characteristic);
			sb.append("]");
		}

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Raw Name: " + _rawName);
		sb.append("\n");
		sb.append("Control-System: " + _controlSystem);
		sb.append("\n");
		sb.append("Property-Part: " + _property);
		sb.append("\n");
		sb.append("Device-Part: " + _device);
		sb.append("\n");
		sb.append("Characteristic-Part: " + _characteristic);
		sb.append("\n");
		sb.append("RemoteInfo: " + toDalRemoteInfo());
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Prints a String representation to the provided stream.
	 * 
	 * @param out
	 *            the out stream
	 */
	public void print(final PrintStream out) {
		out.println("Full String		: " + getFullName());
		out.println("Control System		: " + getControlSystemEnum().getPrefix());
		out.println("Device			: "
				+ (getDevice() != null ? getDevice() : "<not provided>"));
		out.println("Property		: " + getProperty());
		out.println("Characteristic		: "
				+ (getCharacteristic() != null ? getCharacteristic()
						: "<not provided>"));
		out.println("DAL RemoteInfo		: " + toDalRemoteInfo());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessVariable) {
			ProcessVariable that = (ProcessVariable) obj;
			return this.getFullName().equals(that.getFullName());
		}
		return false;
	}

	public ControlSystemEnum getControlSystem() {
		return _controlSystem;
	}

	public String getRawName() {
		return _rawName;
	}

	public boolean isCharacteristic() {
		return (_characteristic!=null && _characteristic.length()>0);
	}


}