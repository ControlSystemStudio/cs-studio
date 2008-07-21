package org.csstudio.nams.service.messaging;

import org.csstudio.nams.common.material.AlarmNachricht_Test;
import org.csstudio.nams.service.messaging.declaration.DefaultNAMSMessage_Test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MessagingServiceAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		
		TestSuite suite = new TestSuite("MessagingServiceAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(MessagingServiceAllTestsSuite.class);

		suite.addTestSuite(AlarmNachricht_Test.class);

		suite.addTestSuite(DefaultNAMSMessage_Test.class);

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
