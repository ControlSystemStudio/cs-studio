// $Id: IdClassCompositePKTest.java 18602 2010-01-21 20:48:59Z hardy.ferentschik $
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.test.annotations.idclass;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.TestCase;

/**
 * A IdClassTestCase.
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Stale W. Pedersen</a>
 * @version $Revision: 1.1 $
 */
public class IdClassCompositePKTest extends TestCase {

	public void testEntityMappningPropertiesAreNotIgnored() {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		DomainAdmin da = new DomainAdmin();
		da.setAdminUser( "admin" );
		da.setDomainName( "org" );

		s.persist( da );
		Query q = s.getNamedQuery( "DomainAdmin.testQuery" );
		assertEquals( 1, q.list().size() );

		tx.rollback();
		s.close();
	}

	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				DomainAdmin.class
		};
	}
}
