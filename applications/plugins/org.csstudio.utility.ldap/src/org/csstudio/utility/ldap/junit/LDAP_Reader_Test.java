/**
 *
 */
package org.csstudio.utility.ldap.junit;

import static org.junit.Assert.*;

import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hrickens
 *
 */
public class LDAP_Reader_Test {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String[], org.csstudio.utility.ldap.namespacebrowser.utility.ErgebnisListe)}.
	 */
	@Test
	public void testLDAPReaderStringArrayErgebnisListe() {
		ErgebnisListe el = new ErgebnisListe();
		String nameUFilter[]= {"",""};
		LDAPReader lr = new LDAPReader(nameUFilter,el);
		lr.schedule();
		assertTrue(el.getAnswer().size()>0);
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String[], int, org.csstudio.utility.ldap.namespacebrowser.utility.ErgebnisListe)}.
	 */
	@Test
	public void testLDAPReaderStringArrayIntErgebnisListe() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String, java.lang.String, org.csstudio.utility.ldap.namespacebrowser.utility.ErgebnisListe)}.
	 */
	@Test
	public void testLDAPReaderStringStringErgebnisListe() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String, java.lang.String, int, org.csstudio.utility.ldap.namespacebrowser.utility.ErgebnisListe)}.
	 */
	@Test
	public void testLDAPReaderStringStringIntErgebnisListe() {
		fail("Not yet implemented");
	}

}
