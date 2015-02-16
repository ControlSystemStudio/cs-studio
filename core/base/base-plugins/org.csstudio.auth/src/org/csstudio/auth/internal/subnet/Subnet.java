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
 package org.csstudio.auth.internal.subnet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Represents an internet subnet.
 * 
 * @author Joerg Rathlev
 */
public final class Subnet {

	/**
	 * The IP address of this subnet.
	 */
	private final InetAddress _subnetAddress;
	
	/**
	 * The netmask for this subnet's address.
	 */
	private final InetAddress _netmask;
	
	/**
	 * Creates a new subnet.
	 * @param address the address of the subnet.
	 * @param netmask the netmask for the address.
	 */
	public Subnet(InetAddress address, InetAddress netmask) {
		if (address == null || netmask == null)
			throw new NullPointerException();
		if (address.getAddress().length != netmask.getAddress().length)
			throw new IllegalArgumentException("address and netmask must " +
					"have the same length.");
		if (!isValidNetmask(netmask))
			throw new IllegalArgumentException(netmask
					+ " is not a valid netmask.");
		
		this._subnetAddress = address;
		this._netmask = netmask;
	}
	
	/**
	 * Checks if the given address is a valid netmask.
	 * @param address the address to check.
	 * @return <code>true</code> if the address is a netmask, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isValidNetmask(InetAddress address) {
		byte[] addr = address.getAddress();
		
		// A netmask is valid if it has (in binary) only ones, followed by
		// only zeros. The check iterates from most siginificant to least
		// significant bit and checks that there aren't any non-zero bits after
		// the first zero bit.
		boolean zeroFound = false;
		// mask is in network byte order (most sigificant first), so the loop
		// starts with mask[0].
		for (int i = 0; i < addr.length; i++) {
			for (int bit = 7; bit >= 0; bit--) {
				boolean isZero = ((addr[i] >>> bit) & 1) == 0;
				
				// If the current bit is a non-zero bit following a zero-bit,
				// the address is not a valid netmask.
				if (zeroFound && !isZero) {
					return false;
				}
				
				zeroFound |= isZero;
			}
		}
		return true;
	}

	/**
	 * Returns whether this subnet contains the given address.
	 * @param address the address.
	 * @return <code>true</code> if this subnet contains the address,
	 *         <code>false</code> otherwise.
	 */
	public boolean contains(InetAddress address) {
		byte[] subnet = _subnetAddress.getAddress();
		byte[] mask = _netmask.getAddress();
		byte[] addr = address.getAddress();
		
		// If the address does not have the same length (in bytes) as the
		// subnet address and netmask, it cannot be compared. (Note: subnet
		// address and netmask are guaranteed to have equal length at this
		// point, this is checked in the constructor.)
		if (subnet.length != addr.length) {
			return false;
		}
		
		// Compare the given address with this subnet's address
		for (int i = 0; i < subnet.length; i++) {
			if ((subnet[i] & mask[i]) != (addr[i] & mask[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a string representation of this subnet. The returned string will
	 * be in the format address/netmask.
	 */
	@Override
	public String toString() {
		return _subnetAddress.getHostAddress() + "/" + _netmask.getHostAddress();
	}
	
	/**
	 * Parses the given string into a <code>Subnet</code>. The string must be
	 * in the form address/netmask.
	 * @param s the string.
	 * @return the subnet.
	 * @throws IllegalArgumentException if the string is not a valid subnet
	 *         address.
	 */
	public static Subnet parseSubnet(String s) {
		String[] parts = s.split("/");
		if (parts.length != 2)
			throw new IllegalArgumentException("String must have one address " +
					"and one netmask, separated by a forward slash.");
		try {
			InetAddress address = InetAddress.getByName(parts[0]);
			InetAddress netmask = InetAddress.getByName(parts[1]);
			return new Subnet(address, netmask);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Unknown or invalid address", e);
		}
	}
}
