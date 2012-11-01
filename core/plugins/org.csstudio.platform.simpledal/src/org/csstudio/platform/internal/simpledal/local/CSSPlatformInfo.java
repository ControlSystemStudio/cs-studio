/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.platform.internal.simpledal.local;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @author Markus Moeller
 * 
 */

public final class CSSPlatformInfo {
	/** Holds the host name. */
	private String hostID = null;
	
	/** Holds the qualified host name. */
	private String qualifiedHostName = null;

	/** Holds the user name. */
	private String userID = null;

	/** Holds the css application id */
	private String applicationID = null;

	/** Holds the only one instance of this class. */
	private static CSSPlatformInfo _instance = null;
	
	private CSSPlatformInfo() {
		init();
	}

	private void init() {
		userID = System.getProperty("user.name");

		try {
			InetAddress localhost = InetAddress.getLocalHost();

			hostID = localhost.getHostName();
			qualifiedHostName = localhost.getCanonicalHostName();
		} catch (UnknownHostException uhe) {
			hostID = "NA";
			qualifiedHostName = "";
		}

		applicationID = "CSS";
	}
	
	/**
	 * Return the only one instance of this class.
	 * 
	 * @return The only one instance of this class.
	 */
	public static synchronized CSSPlatformInfo getInstance() {
		if (_instance == null) {
			_instance = new CSSPlatformInfo();
		}

		return _instance;
	}
	
	/**
	 * Returns the qualified hostname of the host this CSS instance runs on.
	 * If the hostname is unknown, returns the empty string.
	 * @return the qualified hostname of the host this CSS instance runs on.
	 */
	public String getQualifiedHostname() {
		return qualifiedHostName;
	}

	public String getHostId() {
		return hostID;
	}

	public String getUserId() {
		return userID;
	}

	public String getApplicationId() {
		return applicationID;
	}
}
