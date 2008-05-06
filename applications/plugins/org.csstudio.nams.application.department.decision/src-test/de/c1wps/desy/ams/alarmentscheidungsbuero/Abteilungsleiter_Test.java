/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */
package de.c1wps.desy.ams.alarmentscheidungsbuero;

import java.net.InetAddress;
import java.util.Date;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;
import de.c1wps.desy.ams.allgemeines.AlarmNachricht;
import de.c1wps.desy.ams.allgemeines.Eingangskorb;
import de.c1wps.desy.ams.allgemeines.StandardAblagekorb;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappe;
import de.c1wps.desy.ams.allgemeines.Vorgangsmappenkennung;

public class Abteilungsleiter_Test extends
		AbstractObject_TestCase<Abteilungsleiter> {

	protected volatile int anzahlDerSachbearbeiterDieEineMappeErhaltenHaben;
	protected Throwable testFailed;

	@SuppressWarnings("unchecked")
	@Test(timeout = 4000)
	public void testAbteilungsleiter() throws Throwable {
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				Vorgangsmappenkennung.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(
						123)), new AlarmNachricht("Test-Nachricht"));
		anzahlDerSachbearbeiterDieEineMappeErhaltenHaben = 0;

		Eingangskorb<Vorgangsmappe> eingangskorb = EasyMock
				.createMock(Eingangskorb.class);
		EasyMock.expect(eingangskorb.entnehmeAeltestenEingang()).andReturn(
				vorgangsmappe).times(1).andStubAnswer(
				new IAnswer<Vorgangsmappe>() {
					public Vorgangsmappe answer() throws Throwable {
						Thread.sleep(Integer.MAX_VALUE);
						fail();
						return null;
					}
				});

		Eingangskorb<Vorgangsmappe> sachbearbeiter1 = new Eingangskorb<Vorgangsmappe>() {
			public Vorgangsmappe entnehmeAeltestenEingang()
					throws InterruptedException {
				fail("not to be called!");
				return null;
			}

			public void ablegen(Vorgangsmappe dokument) {
				try {
					assertNotSame("Vorgangsmappen nicht identisch",
							vorgangsmappe, dokument);
					assertFalse("Vorgangsmappen nicht gleich", vorgangsmappe
							.equals(dokument));

					assertNotSame(
							"Vorgangsmappen.Alarmnachricht nicht identisch",
							vorgangsmappe
									.gibAusloesendeAlarmNachrichtDiesesVorganges(),
							dokument
									.gibAusloesendeAlarmNachrichtDiesesVorganges());
					assertEquals(
							"Vorgangsmappen.Alarmnachrichten bleiben in diesem Büro gleich",
							vorgangsmappe
									.gibAusloesendeAlarmNachrichtDiesesVorganges(),
							dokument
									.gibAusloesendeAlarmNachrichtDiesesVorganges());

					anzahlDerSachbearbeiterDieEineMappeErhaltenHaben++;
				} catch (Throwable t) {
					testFailed = t;
				}
			}
		};
		Eingangskorb<Vorgangsmappe> sachbearbeiter2 = sachbearbeiter1; // Da
		// die
		// Exemplare
		// nicht
		// unterschieden werden, ist dieses
		// derzeit möglich!

		EasyMock.replay(eingangskorb);
		testFailed = null;

		Eingangskorb<Vorgangsmappe>[] sachbearbeiterkoerbe = new Eingangskorb[] {
				sachbearbeiter1, sachbearbeiter2 };
		Abteilungsleiter abteilungsleiter = new Abteilungsleiter(eingangskorb,
				sachbearbeiterkoerbe);

		abteilungsleiter.beginneArbeit();

		// Der Mock simuliert jetzt folgendes:
		// eingangskorb.ablegen(vorgangsmappe);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int wartezeit = 0; wartezeit < 3000; wartezeit += 10) {
			if (anzahlDerSachbearbeiterDieEineMappeErhaltenHaben > 1)
				break;
			Thread.sleep(10);
		}

		abteilungsleiter.beendeArbeit();

		if (testFailed != null) {
			throw testFailed;
		}
		EasyMock.verify(eingangskorb);
	}
	
	@Test
	public void testArbeit() throws InterruptedException {
		Abteilungsleiter abteilungsleiter = this.getNewInstanceOfClassUnderTest();
		assertFalse("abteilungsleiter.istAmArbeiten()", abteilungsleiter.istAmArbeiten());
		abteilungsleiter.beginneArbeit();
		assertTrue("abteilungsleiter.istAmArbeiten()", abteilungsleiter.istAmArbeiten());
		abteilungsleiter.beendeArbeit();
		Thread.sleep(100);
		assertFalse("abteilungsleiter.istAmArbeiten()", abteilungsleiter.istAmArbeiten());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Abteilungsleiter getNewInstanceOfClassUnderTest() {
		return new Abteilungsleiter(new StandardAblagekorb<Vorgangsmappe>(),
				new StandardAblagekorb[] {});
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Abteilungsleiter[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new Abteilungsleiter[] {
				new Abteilungsleiter(new StandardAblagekorb<Vorgangsmappe>(),
						new StandardAblagekorb[] {}),
				new Abteilungsleiter(new StandardAblagekorb<Vorgangsmappe>(),
						new StandardAblagekorb[] {}),
				new Abteilungsleiter(new StandardAblagekorb<Vorgangsmappe>(),
						new StandardAblagekorb[] {}) };
	}

}
