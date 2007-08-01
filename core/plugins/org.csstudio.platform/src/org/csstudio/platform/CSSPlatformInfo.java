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

package org.csstudio.platform;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.csstudio.platform.logging.CentralLogger;

/**
 * 
 * @author Markus Moeller
 * 
 */

public final class CSSPlatformInfo {
	/** Holds the host name. */
	private String hostID = null;

	/** Holds the user name. */
	private String userID = null;

	/** Holds the css application id */
	private String applicationID = null;

	/** Holds the only one instance of this class. */
	private static CSSPlatformInfo _instance = null;
	
	/**
	 * Stores whether CSS is running onsite.
	 */
	private boolean onsite = false;
	
	/**
	 * Logger for this class. 
	 */
	private static final CentralLogger log = CentralLogger.getInstance();
	
	private CSSPlatformInfo() {
		init();
	}

	private void init() {
		userID = System.getProperty("user.name");

		try {
			hostID = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException uhe) {
			hostID = "NA";
		}

		applicationID = "CSS";
		
		detectOnsiteOffsite();
	}
	
	/**
	 * Detects if CSS is running onsite or offsite.
	 */
	private void detectOnsiteOffsite() {
		try {
			// Currently hardcoded for DESY. TODO: retrieve from preferences
			InetAddress subnet = InetAddress.getByName("131.169.0.0");
			InetAddress netmask = InetAddress.getByName("255.255.0.0");
			
			InetAddress thishost = InetAddress.getLocalHost();
			onsite = isInSubnet(thishost, subnet, netmask);
		} catch (UnknownHostException e) {
			throw new AssertionError("this cannot happen");
		}
	}
	
	/**
	 * Returns whether the given IP address is located in the given subnet.
	 * @param address the IP address.
	 * @param subnet the subnet.
	 * @param netmask the netmask for the subnet.
	 * @return <code>true</code> if the address is in the subnet,
	 *         <code>false</code> otherwise.
	 */
	private boolean isInSubnet(InetAddress address, InetAddress subnet, InetAddress netmask) {
		byte[] addr = address.getAddress();
		byte[] sub = subnet.getAddress();
		byte[] mask = netmask.getAddress();
		
		// Address, subnet and mask should all have the same length (in bytes),
		// otherwise they cannot be compared.
		if (addr.length != sub.length || sub.length != mask.length) {
			log.info(this, "Running in offsite mode");
			return false;
		}
		
		// Compare all bytes of the addresses, masked with mask
		for (int i = 0; i < addr.length; i++) {
			if ((addr[i] & mask[i]) != (sub[i] & mask[i])) {
				log.info(this, "Running in offsite mode");
				return false;
			}
		}
		log.info(this, "Running in onsite mode");
		return true;
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
	 * Returns whether CSS is running onsite.
	 * @return <code>true</code> if CSS is running onsite, <code>false</code>
	 *         otherwise.
	 */
	public boolean isOnsite() {
		return true;  // for compatibility until subnet etc. can be configured
//		return onsite;
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
