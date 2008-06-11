package de.c1wps.desy.ams.allgemeines;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.junit.Test;

import de.c1wps.desy.ams.AbstractValue_TestCase;

public class Vorgangsmappenkennung_Test extends
		AbstractValue_TestCase<Vorgangsmappenkennung> {

	@Override
	protected Vorgangsmappenkennung doGetAValueOfTypeUnderTest() {
		
		InetAddress hostAdress = null;
		try {
			hostAdress = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}
		assertNotNull(hostAdress);

		Date time = new Date(123456);
		return Vorgangsmappenkennung.valueOf(hostAdress, time);
	}

	@Override
	protected Vorgangsmappenkennung[] doGetDifferentInstancesOfTypeUnderTest() {
		InetAddress hostAdress = null;
		try {
			hostAdress = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}
		assertNotNull(hostAdress);

		Date time1 = new Date(123456);
		Date time2 = new Date(123457);
		Date time3 = new Date(123458);

		return new Vorgangsmappenkennung[] {
				Vorgangsmappenkennung.valueOf(hostAdress, time1),
				Vorgangsmappenkennung.valueOf(hostAdress, time2),
				Vorgangsmappenkennung.valueOf(hostAdress, time3) };
	}

	@Test
	public void testValueOf() throws UnknownHostException {
		InetAddress hostAdress = InetAddress.getByAddress(new byte[] { 127, 0,
				0, 1 });
		Date time = new Date(123456);
		assertNotNull(hostAdress);

		Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				hostAdress, time);
		assertNotNull(kennung);
		assertFalse(kennung.hatErgaenzung());

		Vorgangsmappenkennung neueKennungBasierendAufAlter = Vorgangsmappenkennung
				.valueOf(kennung, "Horst Senkel"); // "12345@127.0.0.1 / Horst
		// Senkel"
		assertNotNull(neueKennungBasierendAufAlter);
		assertTrue(neueKennungBasierendAufAlter.hatErgaenzung());

		try {
			Vorgangsmappenkennung.valueOf(neueKennungBasierendAufAlter,
					"Horst Senkel");
			fail("Vertragsbruch wurde erwartet...");
		} catch (Throwable t) {
			// Ok, Vorbedingung muss knallen...
		}
	}

	@Test
	public void testEquals() throws UnknownHostException {
		InetAddress hostAdress = InetAddress.getByAddress(new byte[] { 127, 0,
				0, 1 });
		InetAddress hostAdress2 = InetAddress.getByAddress(new byte[] { 127, 0,
				0, 2 });
		Date time1 = new Date(123456);
		Date time2 = new Date(8975);
		assertNotNull(hostAdress);
		assertNotNull(hostAdress2);

		Vorgangsmappenkennung kennung1 = Vorgangsmappenkennung.valueOf(
				hostAdress, time1);
		Vorgangsmappenkennung kennung2 = Vorgangsmappenkennung.valueOf(
				hostAdress, time1);
		assertEquals(kennung1, kennung2);
		
		Vorgangsmappenkennung kennung3 = Vorgangsmappenkennung.valueOf(
				hostAdress, time2);

		assertNotNull(kennung1);
		assertNotNull(kennung3);
		assertFalse("kennung1.equals(kennung3)", kennung1.equals(kennung3));
		
		Vorgangsmappenkennung kennung4 = Vorgangsmappenkennung.valueOf(
				hostAdress2, time1);

		assertFalse(kennung1.equals(kennung4));
		
		Vorgangsmappenkennung kennung5 = Vorgangsmappenkennung.valueOf(
				kennung1, "Horst Seidel");
		assertFalse("kennung1.equals(kennung5)", kennung1.equals(kennung5));
		Vorgangsmappenkennung kennung6 = Vorgangsmappenkennung.valueOf(
				kennung1, "Harry Hirsch");
		assertFalse("kennung1.equals(kennung6)", kennung1.equals(kennung6));
		assertFalse("kennung5.equals(kennung6)", kennung5.equals(kennung6));
		Vorgangsmappenkennung kennung7 = Vorgangsmappenkennung.valueOf(
				kennung2, "Horst Seidel");
		assertTrue("kennung5.equals(kennung7)", kennung5.equals(kennung7));
	}
	
	@Test
	public void testContractValueOf() throws Throwable {
		InetAddress hostAdress = InetAddress.getByAddress(new byte[] { 127, 0,
				0, 1 });
		Date time1 = new Date(123456);
		
		try {
			Vorgangsmappenkennung.valueOf(null, time1);
			fail();
		} catch(AssertionError ae) {
			// Ok!
		}
		
		try {
			Vorgangsmappenkennung.valueOf(hostAdress, null);
			fail();
		} catch(AssertionError ae) {
			// Ok!
		}
	}
	
	@Test
	public final void testToStringLocal() throws Throwable {
		InetAddress hostAdress = InetAddress.getByAddress(new byte[] { 127, 0,
				0, 1 });
		Date time1 = new Date(123456);
		Vorgangsmappenkennung ohneErgaenzung = Vorgangsmappenkennung.valueOf(hostAdress, time1);
		Vorgangsmappenkennung mitErgaenzung = Vorgangsmappenkennung.valueOf(ohneErgaenzung, "Horst Seidel");
		
		assertNotNull(ohneErgaenzung);
		assertEquals("123456,0@127.0.0.1", ohneErgaenzung.toString());
		
		assertNotNull(mitErgaenzung);
		assertEquals("123456,0@127.0.0.1/Horst Seidel", mitErgaenzung.toString());
	}
	
	@Test
	public final void testHashCode2() throws Throwable {
		Vorgangsmappenkennung x = getAValueOfTypeUnderTest();
		Vorgangsmappenkennung y = getAValueOfTypeUnderTest();
		Vorgangsmappenkennung z = Vorgangsmappenkennung.valueOf(x, "Horst Seidel");
		Vorgangsmappenkennung a = Vorgangsmappenkennung.valueOf(y, "Horst Seidel");

		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				x);
		assertNotNull(
				"Implementations of AbstractObject_TestCase<T>#getNewInstanceOfClassUnderTest() does not deliver null",
				y);

		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application",
				x.hashCode() == x.hashCode());
		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result",
				x.equals(y) ? x.hashCode() == y.hashCode() : true);
		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "Whenever it is invoked on the same object more than once during an execution of a Java application, the hashCode method must consistently return the same integer, provided no information used in equals comparisons on the object is modified. This integer need not remain consistent from one execution of an application to another execution of the same application",
				z.hashCode() == z.hashCode());
		assertTrue(
				"Copied from Java API documentation version JDK 1.5: "
						+ "If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result",
				z.equals(a) ? z.hashCode() == a.hashCode() : true);
		
		// Note (Copied from Java API documentation version JDK 1.5): It is not
		// required that if two objects are unequal according to the
		// equals(java.lang.Object) method, then calling the hashCode method on
		// each of the two objects must produce distinct integer results.
		// However, the programmer should be aware that producing distinct
		// integer results for unequal objects may improve the performance of
		// hashtables.
	}
}
