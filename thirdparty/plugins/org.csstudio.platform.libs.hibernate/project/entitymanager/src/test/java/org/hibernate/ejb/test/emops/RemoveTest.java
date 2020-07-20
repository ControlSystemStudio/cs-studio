//$Id: RemoveTest.java 16594 2009-05-19 09:55:46Z hardy.ferentschik $
package org.hibernate.ejb.test.emops;

import org.hibernate.StaleObjectStateException;
import org.hibernate.ejb.test.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import java.util.Map;

/**
 * @author Emmanuel Bernard
 */
public class RemoveTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(RemoveTest.class);

	public void testRemove() {
		Race race = new Race();
		race.competitors.add( new Competitor() );
		race.competitors.add( new Competitor() );
		race.competitors.add( new Competitor() );
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.persist( race );
		em.flush();
		em.remove( race );
		em.flush();
		em.getTransaction().rollback();
		em.close();
	}

	public void testRemoveAndFind() {
		Race race = new Race();
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.persist( race );
		em.remove( race );
		assertNull( em.find( Race.class, race.id ) );
		em.getTransaction().rollback();
		em.close();
	}

	public void testUpdatedAndRemove() throws Exception {
		Music music = new Music();
		music.setName( "Classical" );
		EntityManager em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.persist( music );
		em.getTransaction().commit();
		em.clear();


		EntityManager em2 = factory.createEntityManager();
		try {
			em2.getTransaction().begin();
			//read music from 2nd EM
			music = em2.find( Music.class, music.getId() );
		} catch (Exception e) {
			em2.getTransaction().rollback();
			em2.close();
			throw e;
		}

		//change music
        em = getOrCreateEntityManager();
		em.getTransaction().begin();
		em.find( Music.class, music.getId() ).setName( "Rap" );
		em.getTransaction().commit();

		try {
			em2.remove( music ); //remove changed music
			em2.flush();
			fail("should have an optimistic lock exception");
		}
         
        catch( OptimisticLockException e ) {
			log.debug("success");
		}
		finally {
			em2.getTransaction().rollback();
			em2.close();
		}

		//clean
        em.getTransaction().begin();
		em.remove( em.find( Music.class, music.getId() ) );
	    em.getTransaction().commit();
		em.close();
	}

	public Class[] getAnnotatedClasses() {
		return new Class[] {
				Race.class,
				Competitor.class,
				Music.class
		};
	}


	public Map getConfig() {
		Map cfg =  super.getConfig();
		cfg.put( "hibernate.jdbc.batch_size", "0");
		return cfg;
	}
}
