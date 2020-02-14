//$Id: AssociationOverrideTest.java 18602 2010-01-21 20:48:59Z hardy.ferentschik $
package org.hibernate.test.annotations.override;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.TestCase;
import org.hibernate.test.util.SchemaUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Emmanuel Bernard
 */
public class AssociationOverrideTest extends TestCase {

	public void testOverriding() throws Exception {
		Location paris = new Location();
		paris.setName( "Paris" );
		Location atlanta = new Location();
		atlanta.setName( "Atlanta" );
		Trip trip = new Trip();
		trip.setFrom( paris );
		//trip.setTo( atlanta );
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		s.persist( paris );
		s.persist( atlanta );
		try {
			s.persist( trip );
			s.flush();
			fail( "Should be non nullable" );
		}
		catch (HibernateException e) {
			//success
		}
		finally {
			tx.rollback();
			s.close();
		}
	}

	public void testDottedNotation() throws Exception {
		assertTrue( SchemaUtil.isTablePresent( "Employee", getCfg() ) );
		assertTrue( "Overridden @JoinColumn fails",
				SchemaUtil.isColumnPresent( "Employee", "fld_address_fk", getCfg() ) );

		assertTrue( "Overridden @JoinTable name fails", SchemaUtil.isTablePresent( "tbl_empl_sites", getCfg() ) );
		assertTrue( "Overridden @JoinTable with default @JoinColumn fails",
				SchemaUtil.isColumnPresent( "tbl_empl_sites", "employee_id", getCfg() ) );
		assertTrue( "Overridden @JoinTable.inverseJoinColumn fails",
				SchemaUtil.isColumnPresent( "tbl_empl_sites", "to_website_fk", getCfg() ) );

		Session s = openSession();
		Transaction tx = s.beginTransaction();
		ContactInfo ci = new ContactInfo();
		Addr address = new Addr();
		address.setCity("Boston");
		address.setCountry("USA");
		address.setState("MA");
		address.setStreet("27 School Street");
		address.setZipcode("02108");
		ci.setAddr(address);
		List<PhoneNumber> phoneNumbers = new ArrayList();
		PhoneNumber num = new PhoneNumber();
		num.setNumber(5577188);
		Employee e = new Employee();
		Collection employeeList = new ArrayList();
		employeeList.add(e);
		e.setContactInfo(ci);
		num.setEmployees(employeeList);
		phoneNumbers.add(num);
		ci.setPhoneNumbers(phoneNumbers);
		SocialTouchPoints socialPoints = new SocialTouchPoints();
		List<SocialSite> sites = new ArrayList<SocialSite>();
		SocialSite site = new SocialSite();
		site.setEmployee(employeeList);
		site.setWebsite("www.jboss.org");
		sites.add(site);
		socialPoints.setWebsite(sites);
		ci.setSocial(socialPoints);
		s.persist(e);
		tx.commit();

		tx = s.beginTransaction();
		s.clear();
		e = (Employee) s.get(Employee.class,e.getId());
		tx.commit();
		s.close();
	}

	protected Class[] getAnnotatedClasses() {
		return new Class[]{
				Employee.class,
				Location.class,
				Move.class,
				Trip.class,
				PhoneNumber.class,
				Addr.class,
				SocialSite.class,
				SocialTouchPoints.class
		};
	}
}
