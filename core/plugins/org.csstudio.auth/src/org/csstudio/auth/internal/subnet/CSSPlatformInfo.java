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

package org.csstudio.auth.internal.subnet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * 
 * @author Jan Hatje
 * 
 */

public final class CSSPlatformInfo {
	/** Holds the only one instance of this class. */
	private static CSSPlatformInfo _instance = null;
	
	/**
	 * Stores whether CSS is running onsite.
	 */
	private boolean onsite = false;
	
	private CSSPlatformInfo() {
		init();
	}

	private void init() {
		try {
			InetAddress localhost = InetAddress.getLocalHost();

			onsite = isOnsite(localhost);
		} catch (UnknownHostException uhe) {
			onsite = false;
		}
	}
	
	/**
	 * Detects whether the given address is an onsite address.
	 * @param address the address to check.
	 * @return <code>true</code> if the address is onsite, <code>false</code>
	 * otherwise.
	 */
	private boolean isOnsite(InetAddress address) {
		Collection<Subnet> onsiteSubnets =
			OnsiteSubnetPreferences.getOnsiteSubnets();
		for (Subnet subnet : onsiteSubnets) {
			if (subnet.contains(address)) {
				return true;
			}
		}
		return false;
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
		return onsite;
	}
}
