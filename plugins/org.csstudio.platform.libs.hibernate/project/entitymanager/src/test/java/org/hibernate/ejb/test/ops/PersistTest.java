// $Id: PersistTest.java 19255 2010-04-21 01:57:44Z steve.ebersole@jboss.com $
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
package org.hibernate.ejb.test.ops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.hibernate.cfg.Environment;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.ejb.test.TestCase;

/**
 * @author Gavin King
 * @author Hardy Ferentschik
 */
public class PersistTest extends TestCase {

	public void testCreateTree() {

		clearCounts();
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Node root = new Node( "root" );
		Node child = new Node( "child" );
		root.addChild( child );
		em.persist( root );
		em.getTransaction().commit();
		em.close();

		assertInsertCount( 2 );
		assertUpdateCount( 0 );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		root = ( Node ) em.find( Node.class, "root" );
		Node child2 = new Node( "child2" );
		root.addChild( child2 );
		em.getTransaction().commit();
		em.close();

		assertInsertCount( 3 );
		assertUpdateCount( 0 );
	}

	public void testCreateTreeWithGeneratedId() {
		clearCounts();

		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		NumberedNode root = new NumberedNode( "root" );
		NumberedNode child = new NumberedNode( "child" );
		root.addChild( child );
		em.persist( root );
		em.getTransaction().commit();
		em.close();

		assertInsertCount( 2 );
		assertUpdateCount( 0 );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		root = ( NumberedNode ) em.find( NumberedNode.class, root.getId() );
		NumberedNode child2 = new NumberedNode( "child2" );
		root.addChild( child2 );
		em.getTransaction().commit();
		em.close();

		assertInsertCount( 3 );
		assertUpdateCount( 0 );
	}

	public void testCreateException() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Node dupe = new Node( "dupe" );
		em.persist( dupe );
		em.persist( dupe );
		em.getTransaction().commit();
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.persist( dupe );
		try {
			em.getTransaction().commit();
			fail( "Cannot persist() twice the same entity" );
		}
		catch ( Exception cve ) {
			//verify that an exception is thrown!
		}
		em.close();

		Node nondupe = new Node( "nondupe" );
		nondupe.addChild( dupe );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.persist( nondupe );
		try {
			em.getTransaction().commit();
			assertFalse( true );
		}
		catch ( RollbackException e ) {
			//verify that an exception is thrown!
		}
		em.close();
	}

	public void testCreateExceptionWithGeneratedId() {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		NumberedNode dupe = new NumberedNode( "dupe" );
		em.persist( dupe );
		em.persist( dupe );
		em.getTransaction().commit();
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		try {
			em.persist( dupe );
			fail();
		}
		catch ( PersistenceException poe ) {
			//verify that an exception is thrown!
		}
		em.getTransaction().rollback();
		em.close();

		NumberedNode nondupe = new NumberedNode( "nondupe" );
		nondupe.addChild( dupe );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		try {
			em.persist( nondupe );
			fail();
		}
		catch ( PersistenceException poe ) {
			//verify that an exception is thrown!
		}
		em.getTransaction().rollback();
		em.close();
	}

	public void testBasic() throws Exception {

		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Employer er = new Employer();
		Employee ee = new Employee();
		em.persist( ee );
		Collection<Employee> erColl = new ArrayList<Employee>();
		Collection<Employer> eeColl = new ArrayList<Employer>();
		erColl.add( ee );
		eeColl.add( er );
		er.setEmployees( erColl );
		ee.setEmployers( eeColl );
		em.getTransaction().commit();
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		er = ( Employer ) em.find( Employer.class, er.getId() );
		assertNotNull( er );
		assertNotNull( er.getEmployees() );
		assertEquals( 1, er.getEmployees().size() );
		Employee eeFromDb = ( Employee ) er.getEmployees().iterator().next();
		assertEquals( ee.getId(), eeFromDb.getId() );
		em.getTransaction().commit();
		em.close();
	}

	private void clearCounts() {
		( ( EntityManagerFactoryImpl ) factory ).getSessionFactory().getStatistics().clear();
	}

	private void assertInsertCount(int count) {
		int inserts = ( int ) ( ( EntityManagerFactoryImpl ) factory ).getSessionFactory()
				.getStatistics()
				.getEntityInsertCount();
		assertEquals( count, inserts );
	}

	private void assertUpdateCount(int count) {
		int updates = ( int ) ( ( EntityManagerFactoryImpl ) factory ).getSessionFactory()
				.getStatistics()
				.getEntityUpdateCount();
		assertEquals( count, updates );
	}

	protected void addConfigOptions(Map options) {
		options.put( Environment.GENERATE_STATISTICS, "true" );
		options.put( Environment.STATEMENT_BATCH_SIZE, "0" );
	}

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Node.class };
	}

	protected String[] getMappings() {
		return new String[] {
				"org/hibernate/ejb/test/ops/Node.hbm.xml",
				"org/hibernate/ejb/test/ops/Employer.hbm.xml"
		};
	}
}

