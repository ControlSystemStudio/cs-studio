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
package org.hibernate.test.filter.hql;

import java.util.Date;

import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.Session;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JoinedFilteredBulkManipulationTest extends FunctionalTestCase {
	public JoinedFilteredBulkManipulationTest(String string) {
		super( string );
	}

	public String[] getMappings() {
		return new String[]{
			"filter/hql/filter-defs.hbm.xml",
			"filter/hql/Joined.hbm.xml"
		};
	}

	public void testFilteredJoinedSubclassHqlDeleteRoot() {
		Session s = openSession();
		s.beginTransaction();
		s.save( new Employee( "John", 'M', "john", new Date() ) );
		s.save( new Employee( "Jane", 'F', "jane", new Date() ) );
		s.save( new Customer( "Charlie", 'M', "charlie", "Acme" ) );
		s.save( new Customer( "Wanda", 'F', "wanda", "ABC" ) );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.enableFilter( "sex" ).setParameter( "sexCode", new Character('M' ) );
		int count = s.createQuery( "delete Person" ).executeUpdate();
		assertEquals( 2, count );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete Person" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	public void testFilteredJoinedSubclassHqlDeleteNonLeaf() {
		Session s = openSession();
		s.beginTransaction();
		s.save( new Employee( "John", 'M', "john", new Date() ) );
		s.save( new Employee( "Jane", 'F', "jane", new Date() ) );
		s.save( new Customer( "Charlie", 'M', "charlie", "Acme" ) );
		s.save( new Customer( "Wanda", 'F', "wanda", "ABC" ) );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.enableFilter( "sex" ).setParameter( "sexCode", new Character('M' ) );
		int count = s.createQuery( "delete User" ).executeUpdate();
		assertEquals( 2, count );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete Person" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	public void testFilteredJoinedSubclassHqlDeleteLeaf() {
		Session s = openSession();
		s.beginTransaction();
		s.save( new Employee( "John", 'M', "john", new Date() ) );
		s.save( new Employee( "Jane", 'F', "jane", new Date() ) );
		s.save( new Customer( "Charlie", 'M', "charlie", "Acme" ) );
		s.save( new Customer( "Wanda", 'F', "wanda", "ABC" ) );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.enableFilter( "sex" ).setParameter( "sexCode", new Character('M' ) );
		int count = s.createQuery( "delete Employee" ).executeUpdate();
		assertEquals( 1, count );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete Person" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	public void testFilteredJoinedSubclassHqlUpdateRoot() {
		Session s = openSession();
		s.beginTransaction();
		s.save( new Employee( "John", 'M', "john", new Date() ) );
		s.save( new Employee( "Jane", 'F', "jane", new Date() ) );
		s.save( new Customer( "Charlie", 'M', "charlie", "Acme" ) );
		s.save( new Customer( "Wanda", 'F', "wanda", "ABC" ) );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.enableFilter( "sex" ).setParameter( "sexCode", new Character('M' ) );
		int count = s.createQuery( "update Person p set p.name = '<male>'" ).executeUpdate();
		assertEquals( 2, count );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete Person" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	public void testFilteredJoinedSubclassHqlUpdateNonLeaf() {
		Session s = openSession();
		s.beginTransaction();
		s.save( new Employee( "John", 'M', "john", new Date() ) );
		s.save( new Employee( "Jane", 'F', "jane", new Date() ) );
		s.save( new Customer( "Charlie", 'M', "charlie", "Acme" ) );
		s.save( new Customer( "Wanda", 'F', "wanda", "ABC" ) );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.enableFilter( "sex" ).setParameter( "sexCode", new Character('M' ) );
		int count = s.createQuery( "update User u set u.username = :un where u.name = :n" )
				.setString( "un", "charlie" )
				.setString( "n", "Wanda" )
				.executeUpdate();
		assertEquals( 0, count );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete Person" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}

	public void testFilteredJoinedSubclassHqlUpdateLeaf() {
		Session s = openSession();
		s.beginTransaction();
		s.save( new Employee( "John", 'M', "john", new Date() ) );
		s.save( new Employee( "Jane", 'F', "jane", new Date() ) );
		s.save( new Customer( "Charlie", 'M', "charlie", "Acme" ) );
		s.save( new Customer( "Wanda", 'F', "wanda", "ABC" ) );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.enableFilter( "sex" ).setParameter( "sexCode", new Character('M' ) );
		int count = s.createQuery( "update Customer c set c.company = 'XYZ'" ).executeUpdate();
		assertEquals( 1, count );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		s.createQuery( "delete Person" ).executeUpdate();
		s.getTransaction().commit();
		s.close();
	}
}
