//$Id: DiscrimSubclassOneToOneTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
package org.hibernate.test.onetoone.singletable;

import java.util.List;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.stat.EntityStatistics;

/**
 * @author Gavin King
 */
public class DiscrimSubclassOneToOneTest extends FunctionalTestCase {
	
	public DiscrimSubclassOneToOneTest(String str) {
		super(str);
	}

	public String[] getMappings() {
		return new String[] { "onetoone/singletable/Person.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( DiscrimSubclassOneToOneTest.class );
	}
	
	public void testOneToOneOnSubclass() {
		Person p = new Person();
		p.name = "Gavin";
		Address a = new Address();
		a.entityName = "Gavin";
		a.zip = "3181";
		a.state = "VIC";
		a.street = "Karbarook Ave";
		p.address = a;
		
		Session s = openSession();
		Transaction t = s.beginTransaction();
		s.persist(p);
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		
		EntityStatistics addressStats = getSessions().getStatistics().getEntityStatistics( Address.class.getName() );
		EntityStatistics mailingAddressStats = getSessions().getStatistics().getEntityStatistics("MailingAddress");

		p = (Person) s.createQuery("from Person p join fetch p.address left join fetch p.mailingAddress").uniqueResult();
		assertNotNull(p.address); assertNull(p.mailingAddress);
		s.clear();

		assertEquals( addressStats.getFetchCount(), 0 );
		assertEquals( mailingAddressStats.getFetchCount(), 0 );
		
		p = (Person) s.createQuery("from Person p join fetch p.address").uniqueResult();
		assertNotNull(p.address); assertNull(p.mailingAddress);
		s.clear();
		
		assertEquals( addressStats.getFetchCount(), 0 );
		assertEquals( mailingAddressStats.getFetchCount(), 1 );

		p = (Person) s.createQuery("from Person").uniqueResult();
		assertNotNull(p.address); assertNull(p.mailingAddress);
		s.clear();
		
		assertEquals( addressStats.getFetchCount(), 1 );
		assertEquals( mailingAddressStats.getFetchCount(), 2 );

		p = (Person) s.createQuery("from Entity").uniqueResult();
		assertNotNull(p.address); assertNull(p.mailingAddress);
		s.clear();
		
		assertEquals( addressStats.getFetchCount(), 2 );
		assertEquals( mailingAddressStats.getFetchCount(), 3 );

		//note that in here join fetch is used for the nullable
		//one-to-one, due to a very special case of default
		p = (Person) s.get(Person.class, "Gavin");
		assertNotNull(p.address); assertNull(p.mailingAddress);
		s.clear();
		
		assertEquals( addressStats.getFetchCount(), 2 );
		assertEquals( mailingAddressStats.getFetchCount(), 3 );

		p = (Person) s.get(Entity.class, "Gavin");
		assertNotNull(p.address); assertNull(p.mailingAddress);
		s.clear();
		
		assertEquals( addressStats.getFetchCount(), 2 );
		assertEquals( mailingAddressStats.getFetchCount(), 3 );
		
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		Org org = new Org();
		org.name = "IFA";
		Address a2 = new Address();
		a2.entityName = "IFA";
		a2.zip = "3181";
		a2.state = "VIC";
		a2.street = "Orrong Rd";
		org.addresses.add(a2);
		s.persist(org);
		t.commit();
		s.close();
		
		s = openSession();
		t = s.beginTransaction();
		org = (Org) s.get(Entity.class, "IFA");
		s.clear();
		
		List list = s.createQuery("from Entity e order by e.name").list();
		p = (Person) list.get(0);
		assertNotNull(p.address); assertNull(p.mailingAddress);
		org = (Org) list.get(1);
		assertEquals( org.addresses.size(), 1 );
		s.clear();
		
		list = s.createQuery("from Entity e left join fetch e.address left join fetch e.mailingAddress order by e.name").list();
		p = (Person) list.get(0);
		org = (Org) list.get(1);
		assertNotNull(p.address); assertNull(p.mailingAddress);
		assertEquals( org.addresses.size(), 1 );
		
		s.delete(p);
		s.delete(org);
		
		t.commit();
		s.close();
		
	}

}

