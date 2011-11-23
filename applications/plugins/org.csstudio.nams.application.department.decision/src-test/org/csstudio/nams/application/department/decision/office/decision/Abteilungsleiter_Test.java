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
package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.util.Date;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Eingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

public class Abteilungsleiter_Test extends
		AbstractTestObject<Abteilungsleiter> {

	protected volatile int anzahlDerSachbearbeiterDieEineMappeErhaltenHaben;
	protected Throwable testFailed;

	@SuppressWarnings("unchecked")
	@Test(timeout = 4000)
	public void testAbteilungsleiter() throws Throwable {
		final Vorgangsmappe vorgangsmappe = new Vorgangsmappe(
				Vorgangsmappenkennung.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }), new Date(
						123)), new AlarmNachricht("Test-Nachricht"));
		this.anzahlDerSachbearbeiterDieEineMappeErhaltenHaben = 0;

		final Eingangskorb<Vorgangsmappe> eingangskorb = EasyMock
				.createMock(Eingangskorb.class);
		EasyMock.expect(eingangskorb.entnehmeAeltestenEingang()).andReturn(
				vorgangsmappe).times(1).andStubAnswer(
				new IAnswer<Vorgangsmappe>() {
					public Vorgangsmappe answer() throws Throwable {
						Thread.sleep(Integer.MAX_VALUE);
						Assert.fail();
						return null;
					}
				});

		final Eingangskorb<Vorgangsmappe> sachbearbeiter1 = new Eingangskorb<Vorgangsmappe>() {
			public void ablegen(Vorgangsmappe dokument) {
				try {
					Assert.assertNotSame("Vorgangsmappen nicht identisch",
							vorgangsmappe, dokument);
					Assert.assertFalse("Vorgangsmappen nicht gleich",
							vorgangsmappe.equals(dokument));

					Assert
							.assertNotSame(
									"Vorgangsmappen.Alarmnachricht nicht identisch",
									vorgangsmappe
											.gibAusloesendeAlarmNachrichtDiesesVorganges(),
									dokument
											.gibAusloesendeAlarmNachrichtDiesesVorganges());
					Assert
							.assertEquals(
									"Vorgangsmappen.Alarmnachrichten bleiben in diesem Büro gleich",
									vorgangsmappe
											.gibAusloesendeAlarmNachrichtDiesesVorganges(),
									dokument
											.gibAusloesendeAlarmNachrichtDiesesVorganges());

					Abteilungsleiter_Test.this.anzahlDerSachbearbeiterDieEineMappeErhaltenHaben++;
				} catch (Throwable t) {
					Abteilungsleiter_Test.this.testFailed = t;
				}
			}

			public Vorgangsmappe entnehmeAeltestenEingang()
					throws InterruptedException {
				Assert.fail("not to be called!");
				return null;
			}
		};
		final Eingangskorb<Vorgangsmappe> sachbearbeiter2 = sachbearbeiter1; // Da
		// die
		// Exemplare
		// nicht
		// unterschieden werden, ist dieses
		// derzeit möglich!

		EasyMock.replay(eingangskorb);
		this.testFailed = null;

		final Eingangskorb<Ablagefaehig>[] sachbearbeiterkoerbe = new Eingangskorb[] {
				sachbearbeiter1, sachbearbeiter2 };
		final Abteilungsleiter abteilungsleiter = new Abteilungsleiter(
				new DefaultExecutionService(), eingangskorb,
				sachbearbeiterkoerbe);

		abteilungsleiter.beginneArbeit();

		// Der Mock simuliert jetzt folgendes:
		// eingangskorb.ablegen(vorgangsmappe);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int wartezeit = 0; wartezeit < 3000; wartezeit += 10) {
			if (this.anzahlDerSachbearbeiterDieEineMappeErhaltenHaben > 1) {
				break;
			}
			Thread.sleep(10);
		}

		abteilungsleiter.beendeArbeit();

		if (this.testFailed != null) {
			throw this.testFailed;
		}
		EasyMock.verify(eingangskorb);
	}

	@Test
	public void testArbeit() throws InterruptedException {
		final Abteilungsleiter abteilungsleiter = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("abteilungsleiter.istAmArbeiten()", abteilungsleiter
				.istAmArbeiten());
		abteilungsleiter.beginneArbeit();
		Assert.assertTrue("abteilungsleiter.istAmArbeiten()", abteilungsleiter
				.istAmArbeiten());
		abteilungsleiter.beendeArbeit();
		Thread.sleep(100);
		Assert.assertFalse("abteilungsleiter.istAmArbeiten()", abteilungsleiter
				.istAmArbeiten());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Abteilungsleiter getNewInstanceOfClassUnderTest() {
		return new Abteilungsleiter(new DefaultExecutionService(),
				new StandardAblagekorb<Vorgangsmappe>(),
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
				new Abteilungsleiter(new DefaultExecutionService(),
						new StandardAblagekorb<Vorgangsmappe>(),
						new StandardAblagekorb[] {}),
				new Abteilungsleiter(new DefaultExecutionService(),
						new StandardAblagekorb<Vorgangsmappe>(),
						new StandardAblagekorb[] {}),
				new Abteilungsleiter(new DefaultExecutionService(),
						new StandardAblagekorb<Vorgangsmappe>(),
						new StandardAblagekorb[] {}) };
	}

}
