//$Id: EntityManagerTest.java 18585 2010-01-19 22:16:05Z hardy.ferentschik $
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
package org.hibernate.ejb.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.ejb.AvailableSettings;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.stat.Statistics;

/**
 * @author Gavin King
 */
public class EntityManagerTest extends TestCase {

	public Class[] getAnnotatedClasses() {
		return new Class[] {
				Item.class,
				Distributor.class,
				Wallet.class
		};
	}

	public Map<Class, String> getCachedClasses() {
		Map<Class, String> result = new HashMap<Class, String>();
		result.put( Item.class, "read-write" );
		return result;
	}

	public Map<String, String> getCachedCollections() {
		Map<String, String> result = new HashMap<String, String>();
		result.put( Item.class.getName() + ".distributors", "read-write, RegionName" );
		return result;
	}

	public void testEntityManager() {

		Item item = new Item( "Mouse", "Micro$oft mouse" );

		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.persist( item );
		assertTrue( em.contains( item ) );
		em.getTransaction().commit();

		assertTrue( em.contains( item ) );

		em.getTransaction().begin();
		Item item1 = ( Item ) em.createQuery( "select i from Item i where descr like 'M%'" ).getSingleResult();
		assertNotNull( item1 );
		assertSame( item, item1 );
		item.setDescr( "Micro$oft wireless mouse" );
		assertTrue( em.contains( item ) );
		em.getTransaction().commit();

		assertTrue( em.contains( item ) );

		em.getTransaction().begin();
		item1 = em.find( Item.class, "Mouse" );
		assertSame( item, item1 );
		em.getTransaction().commit();
		assertTrue( em.contains( item ) );

		item1 = em.find( Item.class, "Mouse" );
		assertSame( item, item1 );
		assertTrue( em.contains( item ) );

		item1 = ( Item ) em.createQuery( "select i from Item i where descr like 'M%'" ).getSingleResult();
		assertNotNull( item1 );
		assertSame( item, item1 );
		assertTrue( em.contains( item ) );

		em.getTransaction().begin();
		assertTrue( em.contains( item ) );
		em.remove( item );
		em.remove( item ); //Second should be no-op
		em.getTransaction().commit();

		em.close();

	}

	public void testConfiguration() throws Exception {
		Item item = new Item( "Mouse", "Micro$oft mouse" );
		Distributor res = new Distributor();
		res.setName( "Bruce" );
		item.setDistributors( new HashSet<Distributor>() );
		item.getDistributors().add( res );
		Statistics stats = ( ( HibernateEntityManagerFactory ) factory ).getSessionFactory().getStatistics();
		stats.clear();
		stats.setStatisticsEnabled( true );

		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();

		em.persist( res );
		em.persist( item );
		assertTrue( em.contains( item ) );

		em.getTransaction().commit();
		em.close();

		assertEquals( 1, stats.getSecondLevelCachePutCount() );
		assertEquals( 0, stats.getSecondLevelCacheHitCount() );

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Item second = em.find( Item.class, item.getName() );
		assertEquals( 1, second.getDistributors().size() );
		assertEquals( 1, stats.getSecondLevelCacheHitCount() );
		em.getTransaction().commit();
		em.close();

		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		second = em.find( Item.class, item.getName() );
		assertEquals( 1, second.getDistributors().size() );
		assertEquals( 3, stats.getSecondLevelCacheHitCount() );
		em.remove( second );
		em.remove( second.getDistributors().iterator().next() );
		em.getTransaction().commit();
		em.close();

		stats.clear();
		stats.setStatisticsEnabled( false );
	}

	public void testContains() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Integer nonManagedObject = new Integer( 4 );
		try {
			em.contains( nonManagedObject );
			fail( "Should have raised an exception" );
		}
		catch ( IllegalArgumentException iae ) {
			//success
			if ( em.getTransaction() != null ) {
				em.getTransaction().rollback();
			}
		}
		finally {
			em.close();
		}
		em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Item item = new Item();
		item.setDescr( "Mine" );
		item.setName( "Juggy" );
		em.persist( item );
		em.getTransaction().commit();
		em.getTransaction().begin();
		item = em.getReference( Item.class, item.getName() );
		assertTrue( em.contains( item ) );
		em.remove( item );
		em.getTransaction().commit();
		em.close();
	}

	public void testClear() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Wallet w = new Wallet();
		w.setBrand( "Lacoste" );
		w.setModel( "Minimic" );
		w.setSerial( "0100202002" );
		em.persist( w );
		em.flush();
		em.clear();
		assertFalse( em.contains( w ) );
		em.getTransaction().rollback();
		em.close();
	}

	public void testFlushMode() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.setFlushMode( FlushModeType.COMMIT );
		assertEquals( FlushModeType.COMMIT, em.getFlushMode() );
		( ( HibernateEntityManager ) em ).getSession().setFlushMode( FlushMode.ALWAYS );
		assertNull( em.getFlushMode() );
		em.close();
	}

	public void testPersistNoneGenerator() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Wallet w = new Wallet();
		w.setBrand( "Lacoste" );
		w.setModel( "Minimic" );
		w.setSerial( "0100202002" );
		em.persist( w );
		em.getTransaction().commit();
		em.getTransaction().begin();
		Wallet wallet = em.find( Wallet.class, w.getSerial() );
		assertEquals( w.getBrand(), wallet.getBrand() );
		em.remove( wallet );
		em.getTransaction().commit();
		em.close();
	}

	public void testSerializableException() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		try {
			Query query = em.createQuery( "SELECT p FETCH JOIN p.distributors FROM Item p" );
			query.getSingleResult();
		}
		catch ( IllegalArgumentException e ) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream( stream );
			out.writeObject( e );
			out.close();
			byte[] serialized = stream.toByteArray();
			stream.close();
			ByteArrayInputStream byteIn = new ByteArrayInputStream( serialized );
			ObjectInputStream in = new ObjectInputStream( byteIn );
			IllegalArgumentException deserializedException = ( IllegalArgumentException ) in.readObject();
			in.close();
			byteIn.close();
			assertNull( deserializedException.getCause().getCause() );
			assertNull( e.getCause().getCause() );
		}
		em.getTransaction().rollback();
		em.close();


		Exception e = new HibernateException( "Exception", new NullPointerException( "NPE" ) );
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream( stream );
		out.writeObject( e );
		out.close();
		byte[] serialized = stream.toByteArray();
		stream.close();
		ByteArrayInputStream byteIn = new ByteArrayInputStream( serialized );
		ObjectInputStream in = new ObjectInputStream( byteIn );
		HibernateException deserializedException = ( HibernateException ) in.readObject();
		in.close();
		byteIn.close();
		assertNotNull( "Arbitrary exceptions nullified", deserializedException.getCause() );
		assertNotNull( e.getCause() );
	}

	public void testIsOpen() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		assertTrue( em.isOpen() );
		em.getTransaction().begin();
		assertTrue( em.isOpen() );
		em.getTransaction().rollback();
		em.close();
		assertFalse( em.isOpen() );
	}

	//EJB-9
	public void testGet() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Item item = ( Item ) em.getReference( Item.class, "nonexistentone" );
		try {
			item.getDescr();
			em.getTransaction().commit();
			fail( "Object with wrong id should have failed" );
		}
		catch ( EntityNotFoundException e ) {
			//success
			if ( em.getTransaction() != null ) {
				em.getTransaction().rollback();
			}
		}
		finally {
			em.close();
		}
	}

	public void testGetProperties() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		Map<String, Object> properties = em.getProperties();
		assertNotNull( properties );
		try {
			properties.put( "foo", "bar" );
			fail();
		}
		catch ( UnsupportedOperationException e ) {
			// success
		}

		assertTrue( properties.containsKey( AvailableSettings.FLUSH_MODE ) );
	}

	public void testSetProperty() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Wallet wallet = new Wallet();
		wallet.setSerial( "000" );
		em.persist( wallet );
		em.getTransaction().commit();

		em.clear();
		assertEquals( em.getProperties().get( AvailableSettings.FLUSH_MODE ), "AUTO" );
		assertNotNull(
				"With default settings the entity should be persisted on commit.",
				em.find( Wallet.class, wallet.getSerial() )
		);

		em.getTransaction().begin();
		wallet = em.merge( wallet );
		em.remove( wallet );
		em.getTransaction().commit();

		em.clear();
		assertNull( "The entity should have been removed.", em.find( Wallet.class, wallet.getSerial() ) );

		em.setProperty( "org.hibernate.flushMode", "MANUAL" +
				"" );
		em.getTransaction().begin();
		wallet = new Wallet();
		wallet.setSerial( "000" );
		em.persist( wallet );
		em.getTransaction().commit();

		em.clear();
		assertNull(
				"With a flush mode of manual the entity should not have been persisted.",
				em.find( Wallet.class, wallet.getSerial() )
		);
		assertEquals( "MANUAL", em.getProperties().get( AvailableSettings.FLUSH_MODE ) );
		em.close();
	}
}
