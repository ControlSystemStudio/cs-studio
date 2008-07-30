package org.csstudio.nams.common.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.StandardRegelwerk;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Test;

public class Vorgangsmappe_Test extends AbstractObject_TestCase<Vorgangsmappe> {

	@Test
	public void testAbgeschlossenDurch() throws UnknownHostException {
		final Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
		final Vorgangsmappe mappe = new Vorgangsmappe(kennung, alarmNachricht);
		Assert
				.assertFalse("mappe.istAbgeschlossen()", mappe
						.istAbgeschlossen());
		final Vorgangsmappenkennung abschliesserKennung = Vorgangsmappenkennung
				.valueOf(InetAddress.getByAddress(new byte[] { 127, 0, 0, 3 }),
						new Date(123457));
		mappe.pruefungAbgeschlossenDurch(abschliesserKennung);
		Assert.assertTrue("mappe.istAbgeschlossen()", mappe.istAbgeschlossen());
		Assert
				.assertTrue(
						"abschliesserKennung.equals(mappe.gibAbschliessendeMappenkennung())",
						abschliesserKennung.equals(mappe
								.gibAbschliessendeMappenkennung()));
	}

	@Test
	public void testGibAusloesendeAlarmNachrichtDiesesVorganges()
			throws UnknownHostException {
		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
		final Vorgangsmappenkennung vorgangsmappenkennung = Vorgangsmappenkennung
				.valueOf(InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(123456));
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				vorgangsmappenkennung, alarmNachricht);
		Assert.assertSame(
				"Hineingereichte Nachricht ist auch die, die herauskommt",
				alarmNachricht, vorgangsmappe
						.gibAusloesendeAlarmNachrichtDiesesVorganges());
	}

	@Test
	public void testKopieren() throws UnknownHostException {
		final Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
		final Pruefliste pruefliste = new StandardRegelwerk(Regelwerkskennung
				.valueOf()).gibNeueLeerePruefliste();

		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(kennung,
				alarmNachricht);
		vorgangsmappe.setzePruefliste(pruefliste);

		final Vorgangsmappe neueVorgangsmappe = vorgangsmappe
				.erstelleKopieFuer("Horst Senkel");

		Assert.assertNotNull(neueVorgangsmappe);
		Assert.assertFalse(neueVorgangsmappe == vorgangsmappe);
		Assert.assertNotNull(neueVorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		Assert.assertFalse(neueVorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges() == vorgangsmappe
				.gibAusloesendeAlarmNachrichtDiesesVorganges());
		Assert
				.assertEquals(vorgangsmappe
						.gibAusloesendeAlarmNachrichtDiesesVorganges(),
						neueVorgangsmappe
								.gibAusloesendeAlarmNachrichtDiesesVorganges());
		Assert.assertFalse("Kennung bleibt nicht gleich!",
				kennung == neueVorgangsmappe.gibMappenkennung());
		Assert.assertFalse("Kennung bleibt nicht gleich!", kennung
				.equals(neueVorgangsmappe.gibMappenkennung()));
		Assert.assertTrue(neueVorgangsmappe.gibMappenkennung().hatErgaenzung());
		Assert.assertEquals(Vorgangsmappenkennung.valueOf(kennung,
				"Horst Senkel"), neueVorgangsmappe.gibMappenkennung());
		Assert.assertFalse(neueVorgangsmappe.gibPruefliste() == vorgangsmappe
				.gibPruefliste());
		Assert.assertEquals(vorgangsmappe.gibPruefliste(), neueVorgangsmappe
				.gibPruefliste());
	}

	public void testLocalToString() {
		Vorgangsmappenkennung kennung = null;
		try {
			kennung = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(kennung,
				alarmNachricht);

		Assert.assertEquals(kennung.toString(), vorgangsmappe
				.gibMappenkennung().toString());
	}

	@Test
	public void testMappenkennung() throws UnknownHostException {
		final Vorgangsmappenkennung kennung = Vorgangsmappenkennung.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(kennung,
				alarmNachricht);

		final Vorgangsmappenkennung kennungAusDerMappe = vorgangsmappe
				.gibMappenkennung();
		Assert.assertNotNull(kennungAusDerMappe);
		Assert.assertEquals(kennung, kennungAusDerMappe);
	}

	@Override
	protected Vorgangsmappe getNewInstanceOfClassUnderTest() {
		Vorgangsmappenkennung kennung = null;
		try {
			kennung = Vorgangsmappenkennung.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

		final AlarmNachricht alarmNachricht = new AlarmNachricht(
				"Test-Nachricht");
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
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

		final AlarmNachricht alarmNachricht1 = new AlarmNachricht(
				"Test-Nachricht 1");
		final AlarmNachricht alarmNachricht2 = new AlarmNachricht(
				"Test-Nachricht 2");
		final AlarmNachricht alarmNachricht3 = new AlarmNachricht(
				"Test-Nachricht 3");
		return new Vorgangsmappe[] {
				new Vorgangsmappe(kennung1, alarmNachricht1),
				new Vorgangsmappe(kennung2, alarmNachricht2),
				new Vorgangsmappe(kennung3, alarmNachricht3) };
	}
}
