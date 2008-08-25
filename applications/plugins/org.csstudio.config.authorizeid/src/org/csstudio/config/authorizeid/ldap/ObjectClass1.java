package org.csstudio.config.authorizeid.ldap;

/**
 * objectClass for accessing eain from LDAP.
 * @author rpovsic
 */
public enum ObjectClass1 {

	AUTHORIZEID("epicsAuthId", "eain");
	
	private final String _objectClass;
	
	private final String _rdn;
	
	private ObjectClass1(final String objectClass, final String rdn) {
		_objectClass = objectClass;
		_rdn = rdn;
	}
	
	/**
	 * Returns object class name.
	 * @return object class name
	 */
	public String getObjectClassName() {
		return _objectClass;
	}
	
	/**
	 * Returns rdn attribute.
	 * @return rdn attribute
	 */
	public String getRdnAttribute() {
		return _rdn;
	}
}
