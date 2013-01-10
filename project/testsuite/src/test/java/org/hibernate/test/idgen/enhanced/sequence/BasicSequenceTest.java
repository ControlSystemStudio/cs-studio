package org.hibernate.test.idgen.enhanced.sequence;

import junit.framework.Test;

import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.Session;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.persister.entity.EntityPersister;

import static org.hibernate.id.IdentifierGeneratorHelper.BasicHolder;

/**
 * {@inheritDoc}
 *
 * @author Steve Ebersole
 */
public class BasicSequenceTest extends FunctionalTestCase {
	public BasicSequenceTest(String string) {
		super( string );
	}

	public String[] getMappings() {
		return new String[] { "idgen/enhanced/sequence/Basic.hbm.xml" };
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( BasicSequenceTest.class );
	}

	public void testNormalBoundary() {
		EntityPersister persister = sfi().getEntityPersister( Entity.class.getName() );
		assertClassAssignability( SequenceStyleGenerator.class, persister.getIdentifierGenerator().getClass() );
		SequenceStyleGenerator generator = ( SequenceStyleGenerator ) persister.getIdentifierGenerator();

		int count = 5;
		Entity[] entities = new Entity[count];
		Session s = openSession();
		s.beginTransaction();
		for ( int i = 0; i < count; i++ ) {
			entities[i] = new Entity( "" + ( i + 1 ) );
			s.save( entities[i] );
			long expectedId = i + 1;
			assertEquals( expectedId, entities[i].getId().longValue() );
			assertEquals( expectedId, generator.getDatabaseStructure().getTimesAccessed() );
			assertEquals( expectedId, ( (BasicHolder) generator.getOptimizer().getLastSourceValue() ).getActualLongValue() );
		}
		s.getTransaction().commit();

		s.beginTransaction();
		for ( int i = 0; i < count; i++ ) {
			assertEquals( i + 1, entities[i].getId().intValue() );
			s.delete( entities[i] );
		}
		s.getTransaction().commit();
		s.close();

	}
}
