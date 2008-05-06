package de.c1wps.desy.ams.allgemeines;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;
import de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste;
import de.c1wps.desy.ams.allgemeines.regelwerk.Regelwerkskennung;
import de.c1wps.desy.ams.allgemeines.regelwerk.StandardRegelwerk;

public class Vorgangsmappe_Test extends AbstractObject_TestCase<Vorgangsmappe> {

	@Test
	public void testGibAusloesendeAlarmNachrichtDiesesVorganges()
			throws UnknownHostException {
		AlarmNachricht alarmNachricht = new AlarmNachricht("Test-Nachricht");
		Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.valueOf(InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(123456));
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(vorgangsmappenkennung,
				alarmNachricht);
		assertSame("Hineingereichte Nachricht ist auch die, die herauskommt",
				alarmNachricht, vorgangsmappe
						.gibAusloesendeAlarmNachrichtDiesesVorganges());
	}

	@Test
	public void testKopieren() throws UnknownHostException {
		Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		AlarmNachricht alarmNachricht = new AlarmNachricht("Test-Nachricht");
		Pruefliste pruefliste = new StandardRegelwerk(Regelwerkskennung.valueOf()).gibNeueLeerePruefliste();

		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(kennung, alarmNachricht);
		vorgangsmappe.setzePruefliste(pruefliste);

		Vorgangsmappe neueVorgangsmappe = vorgangsmappe
				.erstelleKopieFuer("Horst Senkel");

		assertNotNull(neueVorgangsmappe);
		assertFalse(neueVorgangsmappe == vorgangsmappe);
		assertNotNull(neueVorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		assertFalse(neueVorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges() == vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		assertEquals(vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges(),
				neueVorgangsmappe.gibAusloesendeAlarmNachrichtDiesesVorganges());
		assertFalse("Kennung bleibt nicht gleich!",
				kennung == neueVorgangsmappe.gibMappenkennung());
		assertFalse("Kennung bleibt nicht gleich!", kennung
				.equals(neueVorgangsmappe.gibMappenkennung()));
		assertTrue(neueVorgangsmappe.gibMappenkennung().hatErgaenzung());
		assertEquals(Vorgangsmappenkennung.valueOf(kennung, "Horst Senkel"),
				neueVorgangsmappe.gibMappenkennung());
		assertFalse(neueVorgangsmappe.gibPruefliste() == vorgangsmappe
				.gibPruefliste());
		assertEquals(vorgangsmappe.gibPruefliste(), neueVorgangsmappe
				.gibPruefliste());
	}
	
	@Test
	public void testAbgeschlossenDurch() throws UnknownHostException {
		Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		AlarmNachricht alarmNachricht = new AlarmNachricht("Test-Nachricht");
		Vorgangsmappe mappe = new Vorgangsmappe(kennung, alarmNachricht);
		assertFalse("mappe.istAbgeschlossen()", mappe.istAbgeschlossen());
		Vorgangsmappenkennung abschliesserKennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 3 }),
				new Date(123457));
		mappe.pruefungAbgeschlossenDurch(abschliesserKennung);
		assertTrue("mappe.istAbgeschlossen()", mappe.istAbgeschlossen());
		assertTrue("abschliesserKennung.equals(mappe.gibAbschliessendeMappenkennung())", abschliesserKennung.equals(mappe.gibAbschliessendeMappenkennung()));
	}

	@Test
	public void testMappenkennung() throws UnknownHostException {
		Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		AlarmNachricht alarmNachricht = new AlarmNachricht("Test-Nachricht");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(kennung, alarmNachricht);

		Vorgangsmappenkennung kennungAusDerMappe = vorgangsmappe
				.gibMappenkennung();
		assertNotNull(kennungAusDerMappe);
		assertEquals(kennung, kennungAusDerMappe);
	}

	@Override
	protected Vorgangsmappe getNewInstanceOfClassUnderTest() {
		Vorgangsmappenkennung kennung = null;
		try {
			kennung = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}

		AlarmNachricht alarmNachricht = new AlarmNachricht("Test-Nachricht");
		return new Vorgangsmappe(kennung, alarmNachricht);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected Vorgangsmappe[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		Vorgangsmappenkennung kennung1 = null;
		Vorgangsmappenkennung kennung2 = null;
		Vorgangsmappenkennung kennung3 = null;
		try {
			kennung1 = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
			kennung2 = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
			kennung3 = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}

		AlarmNachricht alarmNachricht1 = new AlarmNachricht("Test-Nachricht 1");
		AlarmNachricht alarmNachricht2 = new AlarmNachricht("Test-Nachricht 2");
		AlarmNachricht alarmNachricht3 = new AlarmNachricht("Test-Nachricht 3");
		return new Vorgangsmappe[] {
				new Vorgangsmappe(kennung1, alarmNachricht1),
				new Vorgangsmappe(kennung2, alarmNachricht2),
				new Vorgangsmappe(kennung3, alarmNachricht3) };
	}
	
	public void testLocalToString() {
		Vorgangsmappenkennung kennung = null;
		try {
			kennung = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (UnknownHostException e) {
			fail(e.getMessage());
		}

		AlarmNachricht alarmNachricht = new AlarmNachricht("Test-Nachricht");
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(kennung, alarmNachricht);
		 
		 assertEquals(kennung.toString(), vorgangsmappe.gibMappenkennung().toString());
	}
}
