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
 * <p>Retrieves the username and password that will be used to authenticate
 * the user. Implementations of this interface are used by login modules.</p>
 * 
 * <p>Implementations of this interface will usually display a login prompt
 * to the user (for example, a graphical login dialog), but this is not a
 * strict requirement.</p>
 * 
 * <p>Note: implementations of this interface should not assume that they will
 * be called in the UI thread.</p>
 * 
 * @see ILoginModule
 * 
 * @author Anze Vodovnik, Jörg Rathlev
 */
public interface ILoginCallbackHandler {
	
	/**
	 * <p>Returns the username and password that will be used to authenticate
	 * the user. Returns <code>null</code> if username and password could not
	 * be retrieved.</p>
	 * 
	 * <p>Implementations of this method may prompt the user for username and
	 * password, for example by displaying a login dialog. Such implementations
	 * have to wait for user input before returning. Login modules calling this
	 * method therefore should not assume that it will return quickly.</p>
	 * 
	 * <p>The username and password are returned in a {@link Credentials}
	 * object. Implementations must not keep references to this object, and
	 * callers should discard all references to the returned object as early as
	 * possible to prevent passwords from being kept in memory unnecessarily.</p>
	 * 
	 * @return the username and password, or <code>null</code> if username and
	 *         password could not be retrieved.
	 */
	Credentials getCredentials();
	
	/**
	 * <p>Informs this callback handler that the previous login attempt has
	 * failed. If this callback handler is interactive, it will display an error
	 * message to the user.</p>
	 * 
	 * <p>If the login failed due to a wrong username/password, clients
	 * <em>must</em> call this method before calling {@link getCredentials}
	 * again, otherwise <code>getCredentials</code> may return the same
	 * credentials as from the previous call.</p>
	 */
	void signalFailedLoginAttempt();
}
