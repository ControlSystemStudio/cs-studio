//$Id: ProxyTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
package org.hibernate.test.proxy;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Test;

import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.impl.SessionImpl;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.util.SerializationHelper;

/**
 * @author Gavin King
 */
public class ProxyTest extends FunctionalTestCase {
	
	public ProxyTest(String name) {
		super( name );
	}

	public String[] getMappings() {
		return new String[] { "proxy/DataPoint.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		super.configure( cfg );
		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" ); // problem on HSQLDB (go figure)
	}

	public String getCacheConcurrencyStrategy() {
		return null;
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( ProxyTest.class );
	}

	public void testFinalizeFiltered() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		DataPoint dp = new DataPoint();
		dp.setDescription("a data point");
		dp.setX( new BigDecimal(1.0) );
		dp.setY( new BigDecimal(2.0) );
		s.persist(dp);
		s.flush();
		s.clear();
		
		dp = (DataPoint) s.load(DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		
		try {
			dp.getClass().getDeclaredMethod("finalize",null);
			fail();
			
		} 
		catch (NoSuchMethodException e) {}
		
		s.delete(dp);
		t.commit();
		s.close();
		
	}
	
	public void testProxyException() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		DataPoint dp = new DataPoint();
		dp.setDescription("a data point");
		dp.setX( new BigDecimal(1.0) );
		dp.setY( new BigDecimal(2.0) );
		s.persist(dp);
		s.flush();
		s.clear();
		
		dp = (DataPoint) s.load(DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		
		try {
			dp.exception();
			fail();
		}
		catch (Exception e) {
			assertTrue( e.getClass()==Exception.class );
		}
		s.delete(dp);
		t.commit();
		s.close();
	}

	public void testProxySerializationAfterSessionClosed() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		DataPoint dp = new DataPoint();
		dp.setDescription("a data point");
		dp.setX( new BigDecimal(1.0) );
		dp.setY( new BigDecimal(2.0) );
		s.persist(dp);
		s.flush();
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		s.close();
		SerializationHelper.clone( dp );

		s = openSession();
		t = s.beginTransaction();
		s.delete( dp );
		t.commit();
		s.close();
	}

	public void testInitializedProxySerializationAfterSessionClosed() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		DataPoint dp = new DataPoint();
		dp.setDescription("a data point");
		dp.setX( new BigDecimal(1.0) );
		dp.setY( new BigDecimal(2.0) );
		s.persist(dp);
		s.flush();
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		Hibernate.initialize( dp );
		assertTrue( Hibernate.isInitialized(dp) );
		s.close();
		SerializationHelper.clone( dp );

		s = openSession();
		t = s.beginTransaction();
		s.delete( dp );
		t.commit();
		s.close();
	}

	public void testProxySerialization() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		DataPoint dp = new DataPoint();
		dp.setDescription("a data point");
		dp.setX( new BigDecimal(1.0) );
		dp.setY( new BigDecimal(2.0) );
		s.persist(dp);
		s.flush();
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		dp.getId();
		assertFalse( Hibernate.isInitialized(dp) );
		dp.getDescription();
		assertTrue( Hibernate.isInitialized(dp) );
		Object none = s.load( DataPoint.class, new Long(666));
		assertFalse( Hibernate.isInitialized(none) );
		
		t.commit();
		s.disconnect();
		
		Object[] holder = new Object[] { s, dp, none };
		
		holder = (Object[]) SerializationHelper.clone(holder);
		Session sclone = (Session) holder[0];
		dp = (DataPoint) holder[1];
		none = holder[2];
		
		//close the original:
		s.close();
		
		sclone.reconnect();
		t = sclone.beginTransaction();
		
		DataPoint sdp = (DataPoint) sclone.load( DataPoint.class, new Long( dp.getId() ) );
		assertSame(dp, sdp);
		assertFalse(sdp instanceof HibernateProxy);
		Object snone = sclone.load( DataPoint.class, new Long(666) );
		assertSame(none, snone);
		assertTrue(snone instanceof HibernateProxy);
		
		sclone.delete(dp);
		
		t.commit();
		sclone.close();
		
	}

	public void testProxy() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		DataPoint dp = new DataPoint();
		dp.setDescription("a data point");
		dp.setX( new BigDecimal(1.0) );
		dp.setY( new BigDecimal(2.0) );
		s.persist(dp);
		s.flush();
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long(dp.getId() ));
		assertFalse( Hibernate.isInitialized(dp) );
		DataPoint dp2 = (DataPoint) s.get( DataPoint.class, new Long(dp.getId()) );
		assertSame(dp, dp2);
		assertTrue( Hibernate.isInitialized(dp) );
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		dp2 = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ), LockMode.NONE );
		assertSame(dp, dp2);
		assertFalse( Hibernate.isInitialized(dp) );
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		dp2 = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ), LockMode.READ );
		assertSame(dp, dp2);
		assertTrue( Hibernate.isInitialized(dp) );
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long (dp.getId() ));
		assertFalse( Hibernate.isInitialized(dp) );
		dp2 = (DataPoint) s.get( DataPoint.class, new Long ( dp.getId() ) , LockMode.READ );
		assertSame(dp, dp2);
		assertTrue( Hibernate.isInitialized(dp) );
		s.clear();

		dp = (DataPoint) s.load( DataPoint.class, new Long  ( dp.getId() ) );
		assertFalse( Hibernate.isInitialized(dp) );
		dp2 = (DataPoint) s.createQuery("from DataPoint").uniqueResult();
		assertSame(dp, dp2);
		assertTrue( Hibernate.isInitialized(dp) );
		s.delete( dp );
		t.commit();
		s.close();
	}

	public void testSubsequentNonExistentProxyAccess() {
		Session s = openSession();
		Transaction t = s.beginTransaction();

		DataPoint proxy = ( DataPoint ) s.load( DataPoint.class, new Long(-1) );
		assertFalse( Hibernate.isInitialized( proxy ) );
		try {
			proxy.getDescription();
			fail( "proxy access did not fail on non-existent proxy" );
		}
		catch( ObjectNotFoundException onfe ) {
			// expected
		}
		catch( Throwable e ) {
			fail( "unexpected exception type on non-existent proxy access : " + e );
		}
		// try it a second (subsequent) time...
		try {
			proxy.getDescription();
			fail( "proxy access did not fail on non-existent proxy" );
		}
		catch( ObjectNotFoundException onfe ) {
			// expected
		}
		catch( Throwable e ) {
			fail( "unexpected exception type on non-existent proxy access : " + e );
		}

		t.commit();
		s.close();
	}

	public void testProxyEviction() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		Container container = new Container( "container" );
		container.setOwner( new Owner( "owner" ) );
		container.setInfo( new Info( "blah blah blah" ) );
		container.getDataPoints().add( new DataPoint( new BigDecimal( 1 ), new BigDecimal( 1 ), "first data point" ) );
		container.getDataPoints().add( new DataPoint( new BigDecimal( 2 ), new BigDecimal( 2 ), "second data point" ) );
		s.save( container );
		t.commit();
		s.close();

		s = openSession();
		t = s.beginTransaction();
		Container c = ( Container ) s.load( Container.class, container.getId() );
		assertFalse( Hibernate.isInitialized( c ) );
		s.evict( c );
		try {
			c.getName();
			fail( "expecting LazyInitializationException" );
		}
		catch( LazyInitializationException e ) {
			// expected result
		}

		c = ( Container ) s.load( Container.class, container.getId() );
		assertFalse( Hibernate.isInitialized( c ) );
		Info i = c.getInfo();
		assertTrue( Hibernate.isInitialized( c ) );
		assertFalse( Hibernate.isInitialized( i ) );
		s.evict( c );
		try {
			i.getDetails();
			fail( "expecting LazyInitializationException" );
		}
		catch( LazyInitializationException e ) {
			// expected result
		}

		s.delete( c );

		t.commit();
		s.close();
	}

	public void testFullyLoadedPCSerialization() {
		Session s = openSession();
		Transaction t = s.beginTransaction();
		Long lastContainerId = null;
		int containerCount = 10;
		int nestedDataPointCount = 5;
		for ( int c_indx = 0; c_indx < containerCount; c_indx++ ) {
			Owner owner = new Owner( "Owner #" + c_indx );
			Container container = new Container( "Container #" + c_indx );
			container.setOwner( owner );
			for ( int dp_indx = 0; dp_indx < nestedDataPointCount; dp_indx++ ) {
				DataPoint dp = new DataPoint();
				dp.setDescription( "data-point [" + c_indx + ", " + dp_indx + "]" );
// more HSQLDB fun...
//				dp.setX( new BigDecimal( c_indx ) );
				dp.setX( new BigDecimal( c_indx + dp_indx ) );
				dp.setY( new BigDecimal( dp_indx ) );
				container.getDataPoints().add( dp );
			}
			s.save( container );
			lastContainerId = container.getId();
		}
		t.commit();
		s.close();

		s = openSession();
		s.setFlushMode( FlushMode.MANUAL );
		t = s.beginTransaction();
		// load the last container as a proxy
		Container proxy = ( Container ) s.load( Container.class, lastContainerId );
		assertFalse( Hibernate.isInitialized( proxy ) );
		// load the rest back into the PC
		List all = s.createQuery( "from Container as c inner join fetch c.owner inner join fetch c.dataPoints where c.id <> :last" )
				.setLong( "last", lastContainerId.longValue() )
				.list();
		Container container = ( Container ) all.get( 0 );
		s.delete( container );
		// force a snapshot retrieval of the proxied container
		SessionImpl sImpl = ( SessionImpl ) s;
		sImpl.getPersistenceContext().getDatabaseSnapshot(
				lastContainerId,
		        sImpl.getFactory().getEntityPersister( Container.class.getName() )
		);
		assertFalse( Hibernate.isInitialized( proxy ) );
		t.commit();

//		int iterations = 50;
//		long cumulativeTime = 0;
//		long cumulativeSize = 0;
//		for ( int i = 0; i < iterations; i++ ) {
//			final long start = System.currentTimeMillis();
//			byte[] bytes = SerializationHelper.serialize( s );
//			SerializationHelper.deserialize( bytes );
//			final long end = System.currentTimeMillis();
//			cumulativeTime += ( end - start );
//			int size = bytes.length;
//			cumulativeSize += size;
////			System.out.println( "Iteration #" + i + " took " + ( end - start ) + " ms : size = " + size + " bytes" );
//		}
//		System.out.println( "Average time : " + ( cumulativeTime / iterations ) + " ms" );
//		System.out.println( "Average size : " + ( cumulativeSize / iterations ) + " bytes" );

		byte[] bytes = SerializationHelper.serialize( s );
		SerializationHelper.deserialize( bytes );

		t = s.beginTransaction();
		int count = s.createQuery( "delete DataPoint" ).executeUpdate();
		assertEquals( "unexpected DP delete count", ( containerCount * nestedDataPointCount ), count );
		count = s.createQuery( "delete Container" ).executeUpdate();
		assertEquals( "unexpected container delete count", containerCount, count );
		count = s.createQuery( "delete Owner" ).executeUpdate();
		assertEquals( "unexpected owner delete count", containerCount, count );
		t.commit();
		s.close();
	}
}

