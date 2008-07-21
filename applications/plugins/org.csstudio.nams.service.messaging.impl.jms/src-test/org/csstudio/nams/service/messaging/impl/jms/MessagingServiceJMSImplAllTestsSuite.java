package org.csstudio.nams.service.messaging.impl.jms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MessagingServiceJMSImplAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		
		TestSuite suite = new TestSuite("MessagingServiceJMSImplAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(MessagingServiceJMSImplAllTestsSuite.class);

		suite.addTestSuite(MessageKeyKonverter_Test.class);

		//$JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsAktiviert()
	{
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			fail("Nein, Assertions sind nicht aktiviert");
		} catch(AssertionError ae) {
			assertEquals("Ok, Assertions sind aktiviert!", ae.getMessage());
		}
	}
}
