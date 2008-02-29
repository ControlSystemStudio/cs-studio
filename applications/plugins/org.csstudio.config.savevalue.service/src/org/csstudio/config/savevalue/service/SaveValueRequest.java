/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.config.savevalue.service;

import java.io.Serializable;

/**
 * A save value request.
 * 
 * @author Joerg Rathlev
 */
public final class SaveValueRequest implements Serializable {
	
	/**
	 * The serial version UID. 
	 */
	private static final long serialVersionUID = -1974165845739574264L;

	/**
	 * The name of the process variable.
	 */
	private String _pvName;

	/**
	 * The name of the IOC.
	 */
	private String _iocName;

	/**
	 * The value.
	 */
	private String _value;

	/**
	 * The username of the user who created this request.
	 */
	private String _username;

	/**
	 * The hostname of the host from which this request originated.
	 */
	private String _hostname;

	/**
	 * Creates a new save value request.
	 */
	public SaveValueRequest() {
	}

	/**
	 * Returns whether this request is valid. A request is valid if all required
	 * fields are set.
	 * 
	 * @return <code>true</code> if this request is valid, <code>false</code>
	 *         otherwise.
	 */
	public boolean isValid() {
		return _pvName != null && _iocName != null && _value != null
				&& _username != null && _hostname != null;
	}

	/**
	 * Returns the name of the process variable.
	 * 
	 * @return the name of the process variable.
	 */
	public String getPvName() {
		return _pvName;
	}

	/**
	 * Sets the process variable name.
	 * 
	 * @param pvName
	 *            the process variable name.
	 */
	public void setPvName(final String pvName) {
		this._pvName = pvName;
	}

	/**
	 * Returns the name of the IOC.
	 * 
	 * @return the name of the IOC.
	 */
	public String getIocName() {
		return _iocName;
	}

	/**
	 * Sets the name of the IOC.
	 * 
	 * @param iocName
	 *            the name of the IOC.
	 */
	public void setIocName(final String iocName) {
		this._iocName = iocName;
	}

	/**
	 * Returns the value to be saved.
	 * 
	 * @return the value to be saved.
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * Sets the value to be saved.
	 * 
	 * @param value
	 *            the value to be saved.
	 */
	public void setValue(final String value) {
		this._value = value;
	}

	/**
	 * Returns the username of the user sending this request.
	 * 
	 * @return the username of the user sending this request.
	 */
	public String getUsername() {
		return _username;
	}

	/**
	 * Sets the username of the user sending this request.
	 * 
	 * @param username
	 *            the username of the user sending this request.
	 */
	public void setUsername(final String username) {
		this._username = username;
	}

	/**
	 * Returns the hostname of the host sending this request.
	 * 
	 * @return the hostname of the host sending this request.
	 */
	public String getHostname() {
		return _hostname;
	}

	/**
	 * Sets the name of the host sending this request.
	 * 
	 * @param hostname
	 *            the name of the host sending this request.
	 */
	public void setHostname(final String hostname) {
		this._hostname = hostname;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "SaveValueRequest(pv=" + _pvName + ", ioc=" + _iocName
				+ ", value=" + _value + ", user=" + _username + ", host="
				+ _hostname + ")";
	}
}
