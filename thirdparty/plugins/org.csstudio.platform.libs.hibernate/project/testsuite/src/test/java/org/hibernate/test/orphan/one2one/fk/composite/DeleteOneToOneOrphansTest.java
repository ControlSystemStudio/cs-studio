/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
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
 */
package org.hibernate.test.orphan.one2one.fk.composite;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.junit.functional.FunctionalTestCase;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class DeleteOneToOneOrphansTest extends FunctionalTestCase {
	public DeleteOneToOneOrphansTest(String string) {
		super( string );
	}

	public String[] getMappings() {
		return new String[] { "orphan/one2one/fk/composite/Mapping.hbm.xml" };
	}

	private void createData() {
		Session session = openSession();
		session.beginTransaction();
		Employee emp = new Employee();
		emp.setInfo( new EmployeeInfo( 1L, 1L) );
		session.save( emp );
		session.getTransaction().commit();
		session.close();
	}

	private void cleanupData() {
		Session session = openSession();
		session.beginTransaction();
		session.createQuery( "delete EmployeeInfo" ).executeUpdate();
		session.createQuery( "delete Employee" ).executeUpdate();
		session.getTransaction().commit();
		session.close();
	}

	public void testOrphanedWhileManaged() {
		createData();

		Session session = openSession();
		session.beginTransaction();
		List results = session.createQuery( "from EmployeeInfo" ).list();
		assertEquals( 1, results.size() );
		results = session.createQuery( "from Employee" ).list();
		assertEquals( 1, results.size() );
		Employee emp = ( Employee ) results.get( 0 );
		assertNotNull( emp.getInfo() );
		emp.setInfo( null );
		session.getTransaction().commit();
		session.close();

		session = openSession();
		session.beginTransaction();
		emp = ( Employee ) session.get( Employee.class, emp.getId() );
		assertNull( emp.getInfo() );
		results = session.createQuery( "from EmployeeInfo" ).list();
		assertEquals( 0, results.size() );
		results = session.createQuery( "from Employee" ).list();
		assertEquals( 1, results.size() );
		session.getTransaction().commit();
		session.close();

		cleanupData();
	}
}