/**
 * 
 */
package org.csstudio.platform.internal.model.pvs;

import javax.naming.NamingException;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAdress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
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
final class ProcessVariableAdress implements IProcessVariableAdress {
	public static final String PART_SEPARATOR = "/";

	/**
	 * The control system.
	 */
	private ControlSystemEnum _controlSystem;

	/**
	 * The device.
	 */
	private String _device;

	/**
	 * The property.
	 */
	private String _property;

	/**
	 * The property.
	 */
	private String _characteristic;

	/**
	 * The raw name.
	 */
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
	public ProcessVariableAdress(final String rawName,
			final ControlSystemEnum controlSystem, final String device,
			final String property, final String characteristic) {
		_rawName = rawName;
		// control system is mandatory, but will be set to a default, when
		// its not provided
		if (controlSystem == null) {
			String controlSystemString = CSSPlatformPlugin.getDefault()
					.getPluginPreferences().getString(
							ProcessVariableAdressFactory.PROP_CONTROL_SYSTEM);
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
	 * {@inheritDoc}
	 */
	public String getCharacteristic() {
		return _characteristic;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDevice() {
		return _device;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProperty() {
		return _property;
	}

	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	public String getFullName() {
		assert _controlSystem != null;
		assert _property != null;
		StringBuffer sb = new StringBuffer();

		if (_controlSystem != ControlSystemEnum.UNKNOWN) {
			sb.append(_controlSystem.getPrefix());
			sb.append("://");
		}
		sb.append(_property);

		// // device (is optional)
		// if (_device != null) {
		// if (sb.length() > 0) {
		// sb.append(PART_SEPARATOR);
		// }
		//
		// sb.append(_device);
		// }
		//
		// // property
		// if (sb.length() > 0) {
		// sb.append(PART_SEPARATOR);
		// }
		//
		// sb.append(_property);

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
	public ControlSystemEnum getControlSystem() {
		return _controlSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRawName() {
		return _rawName;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCharacteristic() {
		return (_characteristic != null && _characteristic.length() > 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ProcessVariableAdress) {
			ProcessVariableAdress that = (ProcessVariableAdress) obj;
			return this.getFullName().equals(that.getFullName());
		}
		return false;
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
}