package org.csstudio.config.savevalue.service;

import java.io.Serializable;

/**
 * Represents an entry in a changelog file.
 * 
 * @author Joerg Rathlev
 */
public final class ChangelogEntry implements Serializable {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -8452811844114209923L;

	/**
	 * The name of the process variable.
	 */
	private String _pvName;
	
	/**
	 * The value.
	 */
	private String _value;
	
	/**
	 * The name of the user who last modified the entry.
	 */
	private String _username;
	
	/**
	 * The hostname from which the entry was created.
	 */
	private String _hostname;
	
	/**
	 * The date and time of the last modification.
	 */
	private String _lastModified;
	
	/**
	 * @param pv the process variable.
	 * @param value the value.
	 * @param user the username.
	 * @param host the hostname.
	 * @param lastModified the last modified date.
	 */
	public ChangelogEntry(final String pv, final String value,
			final String user, final String host, final String lastModified) {
		_pvName = pv;
		_value = value;
		_username = user;
		_hostname = host;
		_lastModified = lastModified;
	}

	/**
	 * @return the pvName
	 */
	public String getPvName() {
		return _pvName;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return _username;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return _hostname;
	}

	/**
	 * @return the lastModified
	 */
	public String getLastModified() {
		return _lastModified;
	}
}
