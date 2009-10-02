package org.epics.css.dal.tango;

/**
 * 
 * <code>PropertyName</code> is a name holder for tango property
 * in the property model. It provides the device and property name
 * of a remote connection point.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PropertyName {
	
	private final String deviceName;
	private final String propertyName;
	
	/**
	 * Constructs a new PropertyName from the given complete name.
	 * The complete name is a combination of device and property names
	 * split with '/'.
	 * 
	 * @param completeName the complete name
	 */
	PropertyName(String completeName) {
		int idx = completeName.lastIndexOf("/");
		deviceName = completeName.substring(0,idx);
		if (idx + 1 < completeName.length()) {
			propertyName = completeName.substring(idx+1);
		} else {
			propertyName = "";
		}
	}
	
	/**
	 * Returns the device name.
	 * 
	 * @return the device name
	 */
	public String getDeviceName() {
		return deviceName;
	}
	
	/**
	 * Returns the property name.
	 * 
	 * @return the property name
	 */
	public String getPropertyName() {
		return propertyName;
	}
	
	
}
