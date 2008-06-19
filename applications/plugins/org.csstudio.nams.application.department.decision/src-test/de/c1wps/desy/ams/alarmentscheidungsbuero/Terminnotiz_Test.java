package de.c1wps.desy.ams.alarmentscheidungsbuero;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.junit.Test;

import de.c1wps.desy.ams.AbstractValue_TestCase;

public class Terminnotiz_Test extends AbstractValue_TestCase<Terminnotiz> {

	@Override
	protected Terminnotiz doGetAValueOfTypeUnderTest() throws Throwable {
		Vorgangsmappenkennung vorgangsmappenkennung = null;
		vorgangsmappenkennung = Vorgangsmappenkennung.valueOf(InetAddress
				.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42));
		Millisekunden millisekunden = Millisekunden.valueOf(42);
		String nameDesSachbearbeiters = "Horst Senkel";
		return Terminnotiz.valueOf(vorgangsmappenkennung, millisekunden,
				nameDesSachbearbeiters);
	}

	@Override
	protected Terminnotiz[] doGetDifferentInstancesOfTypeUnderTest()
			throws Throwable {
		Vorgangsmappenkennung vorgangsmappenkennung1 = null;
		Vorgangsmappenkennung vorgangsmappenkennung2 = null;
		vorgangsmappenkennung1 = Vorgangsmappenkennung.valueOf(InetAddress
				.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42));
		vorgangsmappenkennung2 = Vorgangsmappenkennung.valueOf(InetAddress
				.getByAddress(new byte[] { 127, 0, 0, 2 }), new Date(23));
		Millisekunden millisekunden = Millisekunden.valueOf(42);
		Terminnotiz terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				millisekunden, "1");
		millisekunden = Millisekunden.valueOf(23);
		Terminnotiz terminnotiz2 = Terminnotiz.valueOf(vorgangsmappenkennung2,
				millisekunden, "2");
		millisekunden = Millisekunden.valueOf(666);
		Terminnotiz terminnotiz3 = Terminnotiz.valueOf(vorgangsmappenkennung2,
				millisekunden, "3");

		return new Terminnotiz[] { terminnotiz1, terminnotiz2, terminnotiz3 };
	}

	@Test
	public void testEqualsJetztAberRichtig() {
		Vorgangsmappenkennung vorgangsmappenkennung1 = null;
		Vorgangsmappenkennung vorgangsmappenkennung2 = null;
		try {
			vorgangsmappenkennung1 = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42));
			vorgangsmappenkennung2 = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 2 }), new Date(23));
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}

		Terminnotiz vergleichsTerminnotiz = Terminnotiz.valueOf(
				vorgangsmappenkennung1, Millisekunden.valueOf(5),
				"Harry Hirsch");
		Terminnotiz terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				Millisekunden.valueOf(5), "Harry Hirsch");

		assertEquals(vergleichsTerminnotiz, terminnotiz1);

		terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				Millisekunden.valueOf(5), "Mata Pfahl");
		assertFalse(vergleichsTerminnotiz.equals(terminnotiz1));

		terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung1,
				Millisekunden.valueOf(10), "Harry Hirsch");
		assertFalse(vergleichsTerminnotiz.equals(terminnotiz1));

		terminnotiz1 = Terminnotiz.valueOf(vorgangsmappenkennung2,
				Millisekunden.valueOf(5), "Harry Hirsch");
		assertFalse(vergleichsTerminnotiz.equals(terminnotiz1));
	}

	@Test
	public void testCheckContract() throws Throwable {
		try {
			Terminnotiz.valueOf(null, Millisekunden.valueOf(100), "Horst");
			fail();
		} catch (AssertionError ae) {
		}
		try {
			Terminnotiz.valueOf(Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42)),
					null, "Horst");
			fail();
		} catch (AssertionError ae) {
		}
		try {
			Terminnotiz.valueOf(Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(42)),
					Millisekunden.valueOf(100), null);
			fail();
		} catch (AssertionError ae) {
		}
	}
}
