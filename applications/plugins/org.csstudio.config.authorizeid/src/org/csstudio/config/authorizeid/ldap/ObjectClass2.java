package org.csstudio.config.authorizeid.ldap;

/**
 * objectClass for accessing eaig and eair from LDAP
 * @author rpovsic
 */
public enum ObjectClass2 {
	
	AUTHORIZEID("epicsAuthId", "epicsAuthIdGR", "eair", "eain", "eaig");
	
	private final String _objectClass;
	
	private final String _objectClassGR;
	
	private final String _eair;
	
	private final String _eain;
	
	private final String _eaig;
	
	private ObjectClass2(String objectClass, String objectClassGR, String eair, String eain, String eaig) {
		_objectClass = objectClass;
		_objectClassGR = objectClassGR;
		_eair = eair;
		_eain = eain;
		_eaig = eaig;
	}
	
	/**
	 * Returns object class name.
	 * @return object class name
	 */
	public String getObjectClassName() {
		return _objectClass;
	}
	
	/**
	 * Returns object class group, role name.
	 * @return
	 */
	public String getObjectClassGrName() {
		return _objectClassGR;
	}

	/**
	 * Returns eair attribute.
	 * @return eair (role)
	 */
	public String getEair() {
		return _eair;
	}
	
	/**
	 * Returns eain.
	 * @return eain (name)
	 */
	public String getEain() {
		return _eain;
	}
	
	/**
	 * Returns eaig attribute.
	 * @return eaig (group)
	 */
	public String getEaig() {
		return _eaig;
	}
}
