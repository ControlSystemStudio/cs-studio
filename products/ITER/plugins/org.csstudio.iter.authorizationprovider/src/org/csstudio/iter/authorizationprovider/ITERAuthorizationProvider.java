/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.authorizationprovider;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.security.auth.Subject;

import org.csstudio.security.authorization.AuthorizationProvider;
import org.csstudio.security.authorization.Authorizations;

/**
 * AuthorizationProvider based on LDAP group membership but settings default
 * authorization to non-LDAPusers.
 * 
 */
@SuppressWarnings("nls")
public class ITERAuthorizationProvider extends
		org.csstudio.security.authorization.LDAPGroupAuthorizationProvider
		implements AuthorizationProvider {

	@Override
	public Authorizations getAuthorizations(Subject user) throws Exception {
		Authorizations authorizations = null;
		try {
			authorizations = super.getAuthorizations(user);
		} catch (Exception e) {
			// Handle LDAP connection error.
			Activator.getLogger().log(Level.WARNING,
					"Unable to retrieve authorizations from LDAP server : " + e.getMessage());
		}
		if (authorizations == null
				|| authorizations.getAuthorizations() == null) {
			authorizations = new Authorizations(new HashSet<String>(0));
		}

		String[] defaultAuthList = Preferences.getDefaultAuthorization();
		if (defaultAuthList == null || defaultAuthList.length == 0) {
			return authorizations;
		}

		Set<String> authList = new HashSet<>(authorizations.getAuthorizations());
		for (String auth : defaultAuthList) {
			authList.add(auth);
		}

		return new Authorizations(authList);
	}
}
