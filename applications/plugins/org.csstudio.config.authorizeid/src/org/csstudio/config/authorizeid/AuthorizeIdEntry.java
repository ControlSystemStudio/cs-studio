package org.csstudio.config.authorizeid;

import java.io.Serializable;

import org.eclipse.core.runtime.PlatformObject;

/**
 * Entry for {@code AuthorizeIdLabelProvider}.
 * @author Rok Povsic
 */
public class AuthorizeIdEntry extends PlatformObject implements Serializable {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 7330376130143617241L;
	
	private String eaig;
	private String eair;

	/**
	 * The constructor.
	 * @param _eaig eaig (group)
	 * @param _eair eair (role)
	 */
	public AuthorizeIdEntry(String _eaig, String _eair) {
		eaig = _eaig;
		eair = _eair;
	}
	
	/**
	 * Getter for eaig.
	 * @return eaig
	 */
	public String getEaig() {
		return eaig;
	}
	
	/**
	 * Getter for eair.
	 * @return eair
	 */
	public String getEair() {
		return eair;
	}
	
}
