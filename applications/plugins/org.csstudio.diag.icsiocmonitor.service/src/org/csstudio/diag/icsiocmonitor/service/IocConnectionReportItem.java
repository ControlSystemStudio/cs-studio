/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.icsiocmonitor.service;

import java.io.Serializable;

/**
 * An item of an {@link IocConnectionReport}. An item contains information about
 * the connection state of a single IOC.
 * 
 * @author Joerg Rathlev
 */
public final class IocConnectionReportItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String _hostname;
	private final String _iocName;
	private final IocConnectionState _connectionState;

	/**
	 * Creates a new report item.
	 * 
	 * @param hostname
	 *            the hostname of the IOC.
	 * @param iocName
	 *            the logical name of the IOC.
	 * @param connectionState
	 *            the observed connection state of the IOC.
	 * @throws NullPointerException
	 *             if one of the parameters is <code>null</code>.
	 */
	public IocConnectionReportItem(String hostname, String iocName,
			IocConnectionState connectionState) {
		if (hostname == null || iocName == null || connectionState == null) {
			throw new NullPointerException("Parameter was null.");
		}
		
		_hostname = hostname;
		_iocName = iocName;
		_connectionState = connectionState;
	}

	/**
	 * Returns the hostname of the IOC.
	 * 
	 * @return the hostname of the IOC.
	 */
	public String getIocHostname() {
		return _hostname;
	}

	/**
	 * Returns the logical name of the IOC.
	 * 
	 * @return the logical name of the IOC.
	 */
	public String getIocName() {
		return _iocName;
	}

	/**
	 * Returns the state of the connection to the IOC.
	 * 
	 * @return the state of the connection to the IOC.
	 */
	public IocConnectionState getConnectionState() {
		return _connectionState;
	}

}
