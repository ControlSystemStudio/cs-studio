/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.authorizationprovider;

import java.util.HashSet;
import java.util.Set;

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
		Authorizations authorizations = super.getAuthorizations(user);
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
