// $Id: ExceptionTest.java 18850 2010-02-22 21:17:12Z hardy.ferentschik $
package org.hibernate.ejb.test.exception;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.cfg.Environment;
import org.hibernate.ejb.test.TestCase;
import org.hibernate.exception.ConstraintViolationException;

/**
 * @author Emmanuel Bernard
 */
@SuppressWarnings("unchecked")
public class ExceptionTest extends TestCase {

	private final Logger log = LoggerFactory.getLogger( ExceptionTest.class );

	public void testOptimisticLockingException() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		EntityManager em2 = factory.createEntityManager();
		em.getTransaction().begin();
		Music music = new Music();
		music.setName( "Old Country" );
		em.persist( music );
		em.getTransaction().commit();

		try {
			em2.getTransaction().begin();
			Music music2 = em2.find( Music.class, music.getId() );
			music2.setName( "HouseMusic" );
			em2.getTransaction().commit();
		}
		catch ( Exception e ) {
			em2.getTransaction().rollback();
			throw e;
		}
		finally {
			em2.close();
		}

		em.getTransaction().begin();
		music.setName( "Rock" );
		try {

			em.flush();
			fail( "Should raise an optimistic lock exception" );
		}
		catch ( OptimisticLockException e ) {
			//success
			assertEquals( music, e.getEntity() );
		}
		catch ( Exception e ) {
			fail( "Should raise an optimistic lock exception" );
		}
		finally {
			em.getTransaction().rollback();
			em.close();
		}
	}

	public void testEntityNotFoundException() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		Music music = em.getReference( Music.class, -1 );
		try {
			music.getName();
			fail( "Non existent entity should raise an exception when state is accessed" );
		}
		catch ( EntityNotFoundException e ) {
			log.debug( "success" );
		}
		finally {
			em.close();
		}
	}

	public void testConstraintViolationException() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Music music = new Music();
		music.setName( "Jazz" );
		em.persist( music );
		Musician lui = new Musician();
		lui.setName( "Lui Armstrong" );
		lui.setFavouriteMusic( music );
		em.persist( lui );
		em.getTransaction().commit();
		try {
			em.getTransaction().begin();
			String hqlDelete = "delete Music where name = :name";
			em.createQuery( hqlDelete ).setParameter( "name", "Jazz" ).executeUpdate();
			em.getTransaction().commit();
			fail();
		}
		catch ( PersistenceException e ) {
			Throwable t = e.getCause();
			assertTrue( "Should be a constraint violation", t instanceof ConstraintViolationException );
			em.getTransaction().rollback();
		}
		finally {
			em.close();
		}
	}

	// HHH-4676
	public void testInterceptor() throws Exception {
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		Instrument instrument = new Instrument();
		instrument.setName( "Guitar" );
		try {
			em.persist( instrument );
			fail( "Commit should have failed." );
		}
		catch ( RuntimeException e ) {
			assertTrue( em.getTransaction().getRollbackOnly() );
		}
		em.close();
	}

	@Override
	public Map getConfig() {
		Map config = super.getConfig();
		config.put( Environment.BATCH_VERSIONED_DATA, "false" );
		return config;
	}

	public Class[] getAnnotatedClasses() {
		return new Class[] {
				Music.class, Musician.class, Instrument.class
		};
	}
}
