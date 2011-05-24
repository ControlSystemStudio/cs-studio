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
package org.csstudio.sns.dummyauthorization;

import org.csstudio.auth.security.IAuthorizationProvider;
import org.csstudio.auth.security.Right;
import org.csstudio.auth.security.RightSet;
import org.csstudio.auth.security.User;

/** A dummy authorization provider. It makes every user could have the rights for all actions.
 *
 *  @author Xihui Chen
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DummyAuthorizationReader implements IAuthorizationProvider
{
    /** Every user and every action will get this dummy right */
    final private Right dummy_right = new Right("DummyRole", "DummyGroup");

	/* (non-Javadoc)
	 * @see org.csstudio.platform.internal.ldapauthorization.IAuthorizationProvider#getRights(org.csstudio.platform.security.User)
	 */
	@Override
    public RightSet getRights(final User user)
	{
	    // Not using the user, but beware of this:
//		String username = user.getUsername();
//		// If the user was authenticated via Kerberos, the username may be a
//		// fully qualified name (name@EXAMPLE.COM). We only want the first
//		// part of the name.
//		if (username.contains("@")) {
//			username = username.substring(0, username.indexOf('@'));
//		}
	    final RightSet rights = new RightSet("Dummy Rights");
	    rights.addRight(dummy_right);

		return rights;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.platform.security.IAuthorizationProvider#getRights(java.lang.String)
	 */
	@Override
    public RightSet getRights(final String authId)
	{
	    final RightSet rights = new RightSet(authId);
        rights.addRight(dummy_right);

        return rights;
	}
}

