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
 package org.csstudio.auth.security;


/**
 * Objects of this class are used to pass a user's username and password
 * from an {@link ILoginCallbackHandler} to a {@link ILoginModule}.
 * References to {@code Credentials} objects should be discarded as early
 * as possible to prevent passwords from being kept in memory
 * unnecessarily.
 *
 * @author Anze Vodovnik, Jörg Rathlev, Xihui Chen
 */
public final class Credentials {

	/**
	 * The username.
	 */
	private String _username;
	
	/**
	 * The password.
	 */
	private String _password;
	
	/**
	 * Anonymous user's credentials
	 */
	final public static Credentials ANONYMOUS = 
		new Credentials(null, null);
	
	/**
	 * Creates a new {@code Credentials} object.
	 * 
	 * @param username the username.
	 * @param password the password.
	 */
	public Credentials(String username, String password) {
		this._username = username;
		this._password = password;
	}
	
	/**
	 * Returns the username.
	 * @return the username.
	 */
	public String getUsername() {
		return _username;
	}
	
	/**
	 * Returns the password.
	 * @return the password.
	 */
	public String getPassword() {
		return _password;
	}
}
