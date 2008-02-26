/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
