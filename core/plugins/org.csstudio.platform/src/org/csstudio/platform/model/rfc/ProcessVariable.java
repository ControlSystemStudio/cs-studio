/**
 * 
 */
package org.csstudio.platform.model.rfc;

import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ProcessVariable {
	public static final String PART_SEPARATOR = "/";

	private ControlSystemEnum _controlSystem;

	private String _device;

	private String _property;

	private String _characteristic;

	/**
	 * Constructs a process variable from a String representation.
	 * 
	 * The provided String has to follow the URI convention for CSS process
	 * variable pointers.
	 * 
	 * By now, each of the following variants is supported:
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
	 * 
	 * @param pv
	 *            the process variable String
	 */
	public ProcessVariable(final String pv) {
		// We use a regular expression to parse the various String variants
		// (A-E) that might occur.
		// 
		// To prepare the provided String as input for a regular expression, we
		// have to ensure the following preconditions:
		// - leading double-slashes must be replaced by §§§ (this is necessary
		// to differ between // and /)
		// - if there is no leading slash at all, one has to be added (this is
		// necessary when only a property is entered)

		// replace occurence of double slash with another String to prepare a
		// String that can be used as input for a regular expression
		String input = pv.replace("//", "§§§");

		// check leading slash
		if (!(input.startsWith("§§§") || input.startsWith("/"))) {
			// add a leading slash
			input = "/" + input;
		}

		// compile a regex pattern and parse the String
		Pattern p = Pattern
				.compile("^(§§§)?([^/]+)?(/([^/]+))?(/([^/\\[\\]]+))(\\[([^/\\[\\]]+)\\])?$");

		Matcher m = p.matcher(input);

		if (m.find()) {
			try {
				String s = m.group(2);
				if (s != null && s.length() > 0) {
					_controlSystem = ControlSystemEnum.valueOf(m.group(2)
							.toUpperCase());
				} else {
					String controlSystem = CSSPlatformPlugin.getDefault().getPluginPreferences().getString(ControlSystemEnum.PROP_CONTROL_SYSTEM);
					_controlSystem = ControlSystemEnum.valueOf(controlSystem);
				}
			} catch (IllegalArgumentException e) {
				String controlSystem = CSSPlatformPlugin.getDefault().getPluginPreferences().getString(ControlSystemEnum.PROP_CONTROL_SYSTEM);
				_controlSystem = ControlSystemEnum.valueOf(controlSystem);
			}

			String device = m.group(4);
			String property = m.group(6);
			String characteristic = m.group(8);

			_device = device;

			_property = property;

			_characteristic = characteristic;

		} else {
			throw new IllegalArgumentException(
					"The provided String does not match the required format: //controlsystem/device/property[characteristic]");
		}
	}

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
	public ProcessVariable(final ControlSystemEnum controlSystem, final String device,
			final String property, final String characteristic) {
		// control system is mandatory, but will be set to a default, when
		// its not provided
		if (controlSystem == null) {
			String controlSystemString = CSSPlatformPlugin.getDefault().getPluginPreferences().getString(ControlSystemEnum.PROP_CONTROL_SYSTEM);
			_controlSystem = ControlSystemEnum.valueOf(controlSystemString);
			//_controlSystem = SomeWhere.DEFAULT_CONTROL_SYSTEM;
		} else {
			_controlSystem = controlSystem;
		}

		// device is optional
		_device = device;

		// property is mandatory
		if (property == null || property.length() <= 0) {
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
	public String toFullString() {
		assert _controlSystem != null;
		assert _property != null;
		StringBuffer sb = new StringBuffer();

		sb.append(PART_SEPARATOR);
		sb.append(PART_SEPARATOR);
		sb.append(_controlSystem.getProcessVariableUriRepresentation());

		// device (is optional)
		if (_device != null) {
			if (sb.length() > 0) {
				sb.append(PART_SEPARATOR);
			}

			sb.append(_device);
		}

		// property
		if (sb.length() > 0) {
			sb.append(PART_SEPARATOR);
		}

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
		return toReducedString();
	}

	/**
	 * Prints a String representation to the provided stream.
	 * 
	 * @param out the out stream
	 */
	public void print(final PrintStream out) {
		out.println("Full String		: " + toFullString());
		out.println("Control System		: "
				+ getControlSystemEnum().getProcessVariableUriRepresentation());
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
			return this.toFullString().equals(that.toFullString());
		}
		return false;
	}

}