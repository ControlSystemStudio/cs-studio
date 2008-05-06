package de.c1wps.desy.ams;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.c1wps.desy.ams.alarmentscheidungsbuero.Alarmentscheidungsbuero_SubSystemTestSuite;
import de.c1wps.desy.ams.allgemeines.Allgemeines_SubSystemTestSuite;
import de.c1wps.desy.ams.allgemeines.contract.AlgemeinesContract_SubSystemTestSuite;
import de.c1wps.desy.ams.allgemeines.regelwerk.AllgemeinesRegelwerk_SubSystemTestSuite;

public class AllTests extends TestCase {

	public static Test suite() throws Throwable {
		// TODO System.setErr(new PrintStream(new OutputStreamWriter(new StringWriter())));
		
		TestSuite suite = new TestSuite("Test for de.c1wps.desy.ams");
		//$JUnit-BEGIN$
		suite.addTestSuite(AllTests.class);
		suite.addTest(Allgemeines_SubSystemTestSuite.suite());
		suite.addTest(Alarmentscheidungsbuero_SubSystemTestSuite.suite());
		suite.addTest(AlgemeinesContract_SubSystemTestSuite.suite());
		suite.addTest(AllgemeinesRegelwerk_SubSystemTestSuite.suite());
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
