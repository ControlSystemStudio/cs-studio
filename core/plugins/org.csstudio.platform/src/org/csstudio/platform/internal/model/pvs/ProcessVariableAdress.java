/**
 * 
 */
package org.csstudio.platform.internal.model.pvs;

import javax.naming.NamingException;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.epics.css.dal.context.RemoteInfo;

/**
 * {@link IProcessVariableAddress} implementation.
 * 
 * @author Sven Wende
 * 
 */
final class ProcessVariableAdress implements IProcessVariableAddress {
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
	 * 
	 */
	private DalPropertyTypes _typeHint;
	
	
	/**
	 * Constructs a process variable address using the provided information
	 * pieces.
	 * 
	 * @param rawName
	 *            the raw address (mandatory)
	 * @param controlSystem
	 *            the control system (mandatory)
	 * @param device
	 *            a device name (optional, provide null to leave it out)
	 * @param property
	 *            the property part of the address (mandatory)
	 * @param characteristic
	 *            the characteristics part of the address (optional, provide
	 *            null to leave it out)
	 */
	public ProcessVariableAdress(final String rawName,
			final ControlSystemEnum controlSystem, final String device,
			final String property, final String characteristic) {
		assert controlSystem != null;
		assert rawName != null;
		assert property != null;
		_rawName = rawName;
		_controlSystem = controlSystem;
		_device = device;
		_property = property;
		_characteristic = characteristic;
	}

	/**
	 * {@inheritDoc}
	 */
	public DalPropertyTypes getTypeHint() {
		return _typeHint;
	}

	public void setTypeHint(DalPropertyTypes typeHint) {
		_typeHint = typeHint;
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

		if (_characteristic != null) {
			sb.append("[");
			sb.append(_characteristic);
			sb.append("]");
		}

		if(_typeHint!=null) {
			sb.append(", ");
			sb.append(_typeHint.toPortableString());
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
	public int hashCode() {
		return getFullName().hashCode();
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