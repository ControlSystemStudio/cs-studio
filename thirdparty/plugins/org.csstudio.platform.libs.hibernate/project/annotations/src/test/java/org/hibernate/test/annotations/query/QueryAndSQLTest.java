//$Id: QueryAndSQLTest.java 18602 2010-01-21 20:48:59Z hardy.ferentschik $
/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2009, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
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
package org.hibernate.test.annotations.query;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;
import org.hibernate.test.annotations.A320;
import org.hibernate.test.annotations.A320b;
import org.hibernate.test.annotations.Plane;
import org.hibernate.test.annotations.TestCase;

/**
 * Test named queries
 *
 * @author Emmanuel Bernard
 */
public class QueryAndSQLTest extends TestCase {
	public QueryAndSQLTest(String x) {
		super( x );
	}

	public void testPackageQueries() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Plane p = new Plane();
		s.persist( p );
		Query q = s.getNamedQuery( "plane.getAll" );
		assertEquals( 1, q.list().size() );
		tx.commit();
		s.close();
	}

	public void testClassQueries() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Night n = new Night();
		Calendar c = new GregorianCalendar();
		c.set( 2000, 2, 2 );
		Date now = c.getTime();
		c.add( Calendar.MONTH, -1 );
		Date aMonthAgo = c.getTime();
		c.add( Calendar.MONTH, 2 );
		Date inAMonth = c.getTime();
		n.setDate( now );
		n.setDuration( 14 );
		s.persist( n );
		tx.commit();
		s.close();
		s = openSession();
		tx = s.beginTransaction();
		Query q = s.getNamedQuery( "night.moreRecentThan" );
		q.setDate( "date", aMonthAgo );
		assertEquals( 1, q.list().size() );
		q = s.getNamedQuery( "night.moreRecentThan" );
		q.setDate( "date", inAMonth );
		assertEquals( 0, q.list().size() );
		Statistics stats = getSessions().getStatistics();
		stats.setStatisticsEnabled( true );
		stats.clear();
		q = s.getNamedQuery( "night.duration" );
		q.setParameter( "duration", 14l );
		assertEquals( 1, q.list().size() );
		assertEquals( 1, stats.getQueryCachePutCount() );
		q = s.getNamedQuery( "night.duration" );
		q.setParameter( "duration", 14l );
		s.delete( q.list().get( 0 ) );
		assertEquals( 1, stats.getQueryCacheHitCount() );
		tx.commit();
		s.close();
	}

	public void testSQLQuery() {
		Night n = new Night();
		Calendar c = new GregorianCalendar();
		c.set( 2000, 2, 2 );
		Date now = c.getTime();
		c.add( Calendar.MONTH, -1 );
		Date aMonthAgo = c.getTime();
		c.add( Calendar.MONTH, 2 );
		Date inAMonth = c.getTime();
		n.setDate( now );
		n.setDuration( 9999 );
		Area area = new Area();
		area.setName( "Monceau" );

		Session s = openSession();
		Transaction tx = s.beginTransaction();
		s.persist( n );
		s.persist( area );
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		Query q = s.getNamedQuery( "night.getAll.bySQL" );
		q.setParameter( 0, 9990 );
		List result = q.list();
		assertEquals( 1, result.size() );
		Night n2 = ( Night ) result.get( 0 );
		assertEquals( n2.getDuration(), n.getDuration() );
		List areas = s.getNamedQuery( "getAreaByNative" ).list();
		assertTrue( 1 == areas.size() );
		assertEquals( area.getName(), ( ( Area ) areas.get( 0 ) ).getName() );
		tx.commit();
		s.close();
	}

	
	/**
	 * We are testing 2 things here:
	 * 1. The query 'night.olderThan' is defined in a MappedSuperClass - Darkness.
	 *    We are verifying that queries defined in a MappedSuperClass are processed.  
	 * 2. There are 2 Entity classes that extend from Darkness - Night and Twilight. 
	 *    We are verifying that this does not cause any issues.eg. Double processing of the 
	 *    MappedSuperClass
	 */
	
	public void testImportQueryFromMappedSuperclass() {
		Session s = openSession();
		try {
			s.getNamedQuery( "night.olderThan" );
		}
		catch(MappingException ex) {
			fail("Query imported from MappedSuperclass");
		}
		s.close();
	}
	
	public void testSQLQueryWithManyToOne() {
		Night n = new Night();
		Calendar c = new GregorianCalendar();
		c.set( 2000, 2, 2 );
		Date now = c.getTime();
		c.add( Calendar.MONTH, -1 );
		Date aMonthAgo = c.getTime();
		c.add( Calendar.MONTH, 2 );
		Date inAMonth = c.getTime();
		n.setDate( now );
		n.setDuration( 9999 );
		Area a = new Area();
		a.setName( "Paris" );
		n.setArea( a );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		s.persist( a );
		s.persist( n );
		tx.commit();
		s.close();
		s = openSession();
		tx = s.beginTransaction();
		Statistics stats = getSessions().getStatistics();
		stats.setStatisticsEnabled( true );
		Query q = s.getNamedQuery( "night&areaCached" );
		List result = q.list();
		assertEquals( 1, result.size() );
		assertEquals( 1, stats.getQueryCachePutCount() );
		q.list();
		assertEquals( 1, stats.getQueryCacheHitCount() );
		Night n2 = ( Night ) ( ( Object[] ) result.get( 0 ) )[0];
		assertEquals( n2.getDuration(), n.getDuration() );
		tx.commit();
		s.close();
	}

	public void testImplicitNativeQuery() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		SpaceShip ship = new SpaceShip();
		ship.setModel( "X-Wing" );
		ship.setName( "YuBlue" );
		ship.setSpeed( 2000 );
		ship.setDimensions( new Dimensions() );
		s.persist( ship );
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		Query q = s.getNamedQuery( "implicitSample" );
		List result = q.list();
		assertEquals( 1, result.size() );
		assertEquals( ship.getModel(), ( ( SpaceShip ) result.get( 0 ) ).getModel() );
		s.delete( result.get( 0 ) );
		tx.commit();
		s.close();
	}

	public void testNativeQueryAndCompositePKAndComponents() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		SpaceShip ship = new SpaceShip();
		ship.setModel( "X-Wing" );
		ship.setName( "YuBlue" );
		ship.setSpeed( 2000 );
		ship.setDimensions( new Dimensions() );
		ship.getDimensions().setLength( 10 );
		ship.getDimensions().setWidth( 5 );
		Captain captain = new Captain();
		captain.setFirstname( "Luke" );
		captain.setLastname( "Skywalker" );
		ship.setCaptain( captain );
		s.persist( captain );
		s.persist( ship );
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		Query q = s.getNamedQuery( "compositekey" );
		List result = q.list();
		assertEquals( 1, result.size() );
		Object[] row = ( Object[] ) result.get( 0 );
		SpaceShip spaceShip = ( SpaceShip ) row[0];
		assertEquals( ship.getModel(), spaceShip.getModel() );
		assertNotNull( spaceShip.getDimensions() );
		assertEquals( ship.getDimensions().getWidth(), spaceShip.getDimensions().getWidth() );
		assertEquals( ship.getDimensions().getLength(), spaceShip.getDimensions().getLength() );
		assertEquals( ship.getCaptain().getFirstname(), ship.getCaptain().getFirstname() );
		assertEquals( ship.getCaptain().getLastname(), ship.getCaptain().getLastname() );
		//FIXME vary depending on databases
		assertTrue( row[1].toString().startsWith( "50" ) );
		assertTrue( row[2].toString().startsWith( "500" ) );
		s.delete( spaceShip.getCaptain() );
		s.delete( spaceShip );
		tx.commit();
		s.close();
	}

	public void testDiscriminator() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Dictionary dic = new Dictionary();
		dic.setName( "Anglais-Francais" );
		dic.setEditor( "Harrap's" );
		SynonymousDictionary syn = new SynonymousDictionary();
		syn.setName( "Synonymes de tous les temps" );
		syn.setEditor( "Imagination edition" );
		s.persist( dic );
		s.persist( syn );
		tx.commit();
		s.clear();
		tx = s.beginTransaction();
		List results = s.getNamedQuery( "all.dictionaries" ).list();
		assertEquals( 2, results.size() );
		assertTrue(
				results.get( 0 ) instanceof SynonymousDictionary
						|| results.get( 1 ) instanceof SynonymousDictionary
		);
		tx.commit();
		s.close();
	}

//	public void testScalarQuery() throws Exception {
//        Session s = openSession();
//		Transaction tx;
//		tx = s.beginTransaction();
//		Mark bad = new Mark();
//		bad.value = 5;
//		Mark good = new Mark();
//		good.value = 15;
//		s.persist(bad);
//		s.persist(good);
//		tx.commit();
//		s.clear();
//		tx = s.beginTransaction();
//		List result = s.getNamedQuery("average").list();
//		assertEquals( 1, result.size() );
//		tx.commit();
//		s.close();
//
//	}

	public void testCache() throws Exception {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Plane plane = new Plane();
		plane.setNbrOfSeats( 5 );
		s.persist( plane );
		tx.commit();
		s.close();
		getSessions().getStatistics().clear();
		getSessions().getStatistics().setStatisticsEnabled( true );
		s = openSession();
		tx = s.beginTransaction();
		Query query = s.getNamedQuery( "plane.byId" ).setParameter( "id", plane.getId() );
		plane = ( Plane ) query.uniqueResult();
		assertEquals( 1, getSessions().getStatistics().getQueryCachePutCount() );
		plane = ( Plane ) s.getNamedQuery( "plane.byId" ).setParameter( "id", plane.getId() ).uniqueResult();
		assertEquals( 1, getSessions().getStatistics().getQueryCacheHitCount() );
		tx.commit();
		s.close();

		s = openSession();
		tx = s.beginTransaction();
		s.delete( s.get( Plane.class, plane.getId() ) );
		tx.commit();
		s.close();
	}

	public void testEntitySQLOverriding() {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Chaos chaos = new Chaos();
		chaos.setSize( 123l );
		chaos.setId( 1l );

		String lowerName = "hello";
		String upperName = lowerName.toUpperCase();
		assertFalse( lowerName.equals( upperName ) );

		chaos.setName( "hello" );
		chaos.setNickname( "NickName" );
		s.persist( chaos );
		s.flush();
		s.clear();
		s.getSessionFactory().evict( Chaos.class );

		Chaos resultChaos = ( Chaos ) s.load( Chaos.class, chaos.getId() );
		assertEquals( upperName, resultChaos.getName() );
		assertEquals( "nickname", resultChaos.getNickname() );

		tx.rollback();
		s.close();
	}

	public void testCollectionSQLOverriding() {
		Session s;
		Transaction tx;
		s = openSession();
		tx = s.beginTransaction();
		Chaos chaos = new Chaos();
		chaos.setSize( 123l );
		chaos.setId( 1l );

		chaos.setName( "hello" );
		s.persist( chaos );
		CasimirParticle p = new CasimirParticle();
		p.setId( 1l );
		s.persist( p );
		chaos.getParticles().add( p );
		p = new CasimirParticle();
		p.setId( 2l );
		s.persist( p );
		chaos.getParticles().add( p );
		s.flush();
		s.clear();
		s.getSessionFactory().evict( Chaos.class );

		Chaos resultChaos = ( Chaos ) s.load( Chaos.class, chaos.getId() );
		assertEquals( 2, resultChaos.getParticles().size() );
		resultChaos.getParticles().remove( resultChaos.getParticles().iterator().next() );
		resultChaos.getParticles().remove( resultChaos.getParticles().iterator().next() );
		s.flush();

		s.clear();
		resultChaos = ( Chaos ) s.load( Chaos.class, chaos.getId() );
		assertEquals( 0, resultChaos.getParticles().size() );

		tx.rollback();
		s.close();
	}

	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Darkness.class,
				Plane.class,
				A320.class,
				A320b.class,
				Night.class,
				Twilight.class,
				Area.class,
				SpaceShip.class,
				Dictionary.class,
				SynonymousDictionary.class,
				Captain.class,
				Chaos.class,
				CasimirParticle.class
		};
	}

	protected String[] getAnnotatedPackages() {
		return new String[] {
				"org.hibernate.test.annotations.query"
		};
	}

	@Override
	protected String[] getXmlFiles() {
		return new String[] {
				"org/hibernate/test/annotations/query/orm.xml"
		};
	}

	protected void configure(Configuration cfg) {
		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
	}
}
