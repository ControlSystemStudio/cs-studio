/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.hibernate.secure;

import java.util.StringTokenizer;

import javax.security.jacc.EJBMethodPermission;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.PolicyContextException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;

/**
 * Adds Hibernate permissions to roles via JACC
 * 
 * @author Gavin King
 */
public class JACCConfiguration {

	private static final Logger log = LoggerFactory.getLogger( JACCConfiguration.class );

	private final PolicyConfiguration policyConfiguration;

	public JACCConfiguration(String contextId) throws HibernateException {
		try {
			policyConfiguration = PolicyConfigurationFactory
					.getPolicyConfigurationFactory()
					.getPolicyConfiguration( contextId, false );
		}
		catch (ClassNotFoundException cnfe) {
			throw new HibernateException( "JACC provider class not found", cnfe );
		}
		catch (PolicyContextException pce) {
			throw new HibernateException( "policy context exception occurred", pce );
		}
	}

	public void addPermission(String role, String entityName, String action) {

		if ( action.equals( "*" ) ) {
			action = "insert,read,update,delete";
		}

		StringTokenizer tok = new StringTokenizer( action, "," );

		while ( tok.hasMoreTokens() ) {
			String methodName = tok.nextToken().trim();
			EJBMethodPermission permission = new EJBMethodPermission( 
					entityName, 
					methodName, 
					null, // interfaces
					null // arguments
				);

			if ( log.isDebugEnabled() ) {
				log.debug( "adding permission to role " + role + ": " + permission );
			}
			try {
				policyConfiguration.addToRole( role, permission );
			}
			catch (PolicyContextException pce) {
				throw new HibernateException( "policy context exception occurred", pce );
			}
		}
	}

}
