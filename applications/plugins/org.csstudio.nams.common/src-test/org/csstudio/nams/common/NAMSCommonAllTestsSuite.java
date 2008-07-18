package org.csstudio.nams.common;

import org.csstudio.nams.common.contract.Contract_Test;
import org.csstudio.nams.common.decision.StandardAblagekorb_Test;
import org.csstudio.nams.common.decision.Vorgangsmappe_Test;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung_Test;
import org.csstudio.nams.common.fachwert.MessageKeyEnum_Test;
import org.csstudio.nams.common.fachwert.Millisekunden_Test;
import org.csstudio.nams.common.material.Regelwerkskennung_Test;

import de.c1wps.desy.ams.allgemeines.regelwerk.AlarmNachricht_Test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NAMSCommonAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		// TODO System.setErr(new PrintStream(new OutputStreamWriter(new StringWriter())));
		
		TestSuite suite = new TestSuite("NAMSCommonAllTestsSuite");
		//$JUnit-BEGIN$
//		suite.addTest(Alarmentscheidungsbuero_SubSystemTestSuite.suite());

		suite.addTestSuite(NAMSCommonAllTestsSuite.class);
		suite.addTestSuite(CommonActivator_Test.class);
		
		suite.addTestSuite(Contract_Test.class);
		
		suite.addTestSuite(StandardAblagekorb_Test.class);
		suite.addTestSuite(Vorgangsmappe_Test.class);
		suite.addTestSuite(Vorgangsmappenkennung_Test.class);
		
		suite.addTestSuite(Millisekunden_Test.class);
		suite.addTestSuite(MessageKeyEnum_Test.class);

		suite.addTestSuite(AlarmNachricht_Test.class);
		suite.addTestSuite(Regelwerkskennung_Test.class);
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
