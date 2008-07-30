package org.csstudio.nams.common;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.common.contract.Contract_Test;
import org.csstudio.nams.common.decision.StandardAblagekorb_Test;
import org.csstudio.nams.common.decision.Vorgangsmappe_Test;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung_Test;
import org.csstudio.nams.common.fachwert.MessageKeyEnum_Test;
import org.csstudio.nams.common.fachwert.Millisekunden_Test;
import org.csstudio.nams.common.material.AlarmNachricht_Test;
import org.csstudio.nams.common.material.Regelwerkskennung_Test;
import org.csstudio.nams.common.material.regelwerk.AbstractNodeVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.NichtVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.OderVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableRegel_Test;
import org.csstudio.nams.common.material.regelwerk.Pruefliste_Test;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis_Test;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk_Test;
import org.csstudio.nams.common.material.regelwerk.StringRegel_Test;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegelAlarmBeiBestaetigung_Test;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel_Test;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel_Test;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen_Test;

public class NAMSCommonAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		final TestSuite suite = new TestSuite("NAMSCommonAllTestsSuite");
		// $JUnit-BEGIN$

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

		suite.addTestSuite(AbstractNodeVersandRegel_Test.class);
		suite.addTestSuite(NichtVersandRegel_Test.class);
		suite.addTestSuite(OderVersandRegel_Test.class);
		suite.addTestSuite(ProcessVariableRegel_Test.class);
		suite.addTestSuite(Pruefliste_Test.class);
		suite.addTestSuite(RegelErgebnis_Test.class);
		suite.addTestSuite(Regelwerkskennung_Test.class);
		suite.addTestSuite(StandardRegelwerk_Test.class);
		suite.addTestSuite(StringRegel_Test.class);
		suite.addTestSuite(TimeBasedRegel_Test.class);
		suite.addTestSuite(TimeBasedRegelAlarmBeiBestaetigung_Test.class);
		suite.addTestSuite(UndVersandRegel_Test.class);
		suite.addTestSuite(WeiteresVersandVorgehen_Test.class);

		// $JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsAktiviert() {
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			Assert.fail("Nein, Assertions sind nicht aktiviert");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Ok, Assertions sind aktiviert!", ae
					.getMessage());
		}
	}
}
