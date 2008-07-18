package org.csstudio.nams.common.decision;

import java.util.Iterator;

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Ablagekorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.junit.Test;

public class StandardAblagekorb_Test
		extends
		AbstractAblagekorb_TestCase<AblagefaehigesObject, StandardAblagekorb<AblagefaehigesObject>> {

	volatile int fertigeConsumer = 0;
	volatile private StandardAblagekorb<Ablagefaehig> korb;

	@Test(timeout = 4000)
	public void testMassigAblegenUndEntnehmen() {
		korb = new StandardAblagekorb<Ablagefaehig>();
		final StandardAblagekorb<Ablagefaehig> korb2 = new StandardAblagekorb<Ablagefaehig>();

		class Producer implements Runnable {
			public void run() {
				int i = 0;
				while (i < 100) {
					try {
						korb.ablegen(new AblagefaehigesObject());
						// System.out.println("Producer.run()");
					} catch (InterruptedException ex) {
						fail();
					}
					i++;
					Thread.yield();
				}
			}
		}
		class Consumer1 implements Runnable {
			// private final String name;

			public Consumer1(String name) {
				// this.name = name;
			}

			public void run() {
				int i = 0;
				while (i < 100) {
					Ablagefaehig eingang = null;
					try {
						eingang = korb.entnehmeAeltestenEingang();
						// System.out.println("Consumer1.run()" + name);
					} catch (InterruptedException ex) {
					}

					assertNotNull(eingang);

					try {
						korb2.ablegen(eingang);
					} catch (InterruptedException ex) {
					}

					i++;
					Thread.yield();
				}
				fertigeConsumer++;
			}
		}
		class Consumer2 implements Runnable {
			// private final String name;

			public Consumer2(String name) {
				// this.name = name;
			}

			public void run() {
				try {
					int i = 0;
					while (i < 100) {
						Ablagefaehig eingang = null;
						eingang = korb2.entnehmeAeltestenEingang();
						assertNotNull(eingang);
						// System.out.println("Consumer2.run(): " + name);

						i++;
						Thread.yield();
					}
					fertigeConsumer++;
				} catch (InterruptedException ex) {
				}
			}
		}

		Producer p = new Producer();
		Consumer2 c2 = new Consumer2("B");
		Consumer1 c1 = new Consumer1("A");
		Thread ct2 = new Thread(c2);
		Thread ct1 = new Thread(c1);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ct2.start();
		ct1.start();

		new Thread(p).start();
		while (fertigeConsumer < 2) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected AblagefaehigesObject gibNeuesAblagefaehigesExemplar() {
		return new AblagefaehigesObject();
	}

	@Override
	protected Ablagekorb<AblagefaehigesObject> gibNeuesExemplar() {
		return new StandardAblagekorb<AblagefaehigesObject>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean pruefeObEnthalten(Ablagekorb<AblagefaehigesObject> korb,
			AblagefaehigesObject element) {
		return ((StandardAblagekorb) korb).istEnthalten(element);
	}

	@Override
	protected StandardAblagekorb<AblagefaehigesObject> getNewInstanceOfClassUnderTest() {
		return new StandardAblagekorb<AblagefaehigesObject>();
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected StandardAblagekorb<AblagefaehigesObject>[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		// TODO Auto-generated method stub
		return new StandardAblagekorb[] {
				new StandardAblagekorb<AblagefaehigesObject>(),
				new StandardAblagekorb<AblagefaehigesObject>(),
				new StandardAblagekorb<AblagefaehigesObject>() };
	}

	@Test
	public void testIterator() throws InterruptedException {
		StandardAblagekorb<Ablagefaehig> korb = new StandardAblagekorb<Ablagefaehig>();

		korb.ablegen(new AblagefaehigesObject());
		korb.ablegen(new AblagefaehigesObject());
		korb.ablegen(new AblagefaehigesObject());

		Iterator<Ablagefaehig> iterator = korb.iterator();
		int anzahl = 0;
		while (iterator.hasNext()) {
			Ablagefaehig ablagefaehig = iterator.next();
			assertNotNull(ablagefaehig);
			iterator.remove();
			anzahl++;
		}
		assertEquals(3, anzahl);
		anzahl = 0;
		while (iterator.hasNext()) {
			Ablagefaehig ablagefaehig = iterator.next();
			assertNotNull(ablagefaehig);
			anzahl++;
		}
		assertEquals(0, anzahl);
	}

	@Test
	public void testIteratorNebenlaeufig() throws InterruptedException {
		final StandardAblagekorb<Ablagefaehig> korb = new StandardAblagekorb<Ablagefaehig>();

		class Producer implements Runnable {
			public void run() {
				int i = 0;
				while (i < 1000) {
					try {
						korb.ablegen(new AblagefaehigesObject());
					} catch (InterruptedException ex) {
						fail();
					}
					i++;
					Thread.yield();
				}
			}
		}

		class IteratorConsumer implements Runnable {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
				Iterator<Ablagefaehig> iterator = korb.iterator();
				int anzahl = 0;
				while (iterator.hasNext()) {
					Ablagefaehig ablagefaehig = iterator.next();
					assertNotNull(ablagefaehig);
					iterator.remove();
					anzahl++;
				}
				assertTrue(anzahl > 0);
			}
		}
		;

		new Thread(new IteratorConsumer()).start();
		new Thread(new Producer()).start();
	}

}
