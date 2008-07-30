package org.csstudio.nams.common.decision;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

public class StandardAblagekorb_Test
		extends
		AbstractAblagekorb_TestCase<AblagefaehigesObject, StandardAblagekorb<AblagefaehigesObject>> {

	volatile int fertigeConsumer = 0;
	volatile private StandardAblagekorb<Ablagefaehig> korb;

	@Test
	public void testIterator() throws InterruptedException {
		final StandardAblagekorb<Ablagefaehig> korb = new StandardAblagekorb<Ablagefaehig>();

		korb.ablegen(new AblagefaehigesObject());
		korb.ablegen(new AblagefaehigesObject());
		korb.ablegen(new AblagefaehigesObject());

		final Iterator<Ablagefaehig> iterator = korb.iterator();
		int anzahl = 0;
		while (iterator.hasNext()) {
			final Ablagefaehig ablagefaehig = iterator.next();
			Assert.assertNotNull(ablagefaehig);
			iterator.remove();
			anzahl++;
		}
		Assert.assertEquals(3, anzahl);
		anzahl = 0;
		while (iterator.hasNext()) {
			final Ablagefaehig ablagefaehig = iterator.next();
			Assert.assertNotNull(ablagefaehig);
			anzahl++;
		}
		Assert.assertEquals(0, anzahl);
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
					} catch (final InterruptedException ex) {
						Assert.fail();
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
				} catch (final InterruptedException e) {
					Assert.fail(e.getMessage());
				}
				final Iterator<Ablagefaehig> iterator = korb.iterator();
				int anzahl = 0;
				while (iterator.hasNext()) {
					final Ablagefaehig ablagefaehig = iterator.next();
					Assert.assertNotNull(ablagefaehig);
					iterator.remove();
					anzahl++;
				}
				Assert.assertTrue(anzahl > 0);
			}
		}
		;

		new Thread(new IteratorConsumer()).start();
		new Thread(new Producer()).start();
	}

	@Test(timeout = 4000)
	public void testMassigAblegenUndEntnehmen() {
		this.korb = new StandardAblagekorb<Ablagefaehig>();
		final StandardAblagekorb<Ablagefaehig> korb2 = new StandardAblagekorb<Ablagefaehig>();

		class Producer implements Runnable {
			public void run() {
				int i = 0;
				while (i < 100) {
					try {
						StandardAblagekorb_Test.this.korb
								.ablegen(new AblagefaehigesObject());
						// System.out.println("Producer.run()");
					} catch (final InterruptedException ex) {
						Assert.fail();
					}
					i++;
					Thread.yield();
				}
			}
		}
		class Consumer1 implements Runnable {
			// private final String name;

			public Consumer1(final String name) {
				// this.name = name;
			}

			public void run() {
				int i = 0;
				while (i < 100) {
					Ablagefaehig eingang = null;
					try {
						eingang = StandardAblagekorb_Test.this.korb
								.entnehmeAeltestenEingang();
						// System.out.println("Consumer1.run()" + name);
					} catch (final InterruptedException ex) {
					}

					Assert.assertNotNull(eingang);

					try {
						korb2.ablegen(eingang);
					} catch (final InterruptedException ex) {
					}

					i++;
					Thread.yield();
				}
				StandardAblagekorb_Test.this.fertigeConsumer++;
			}
		}
		class Consumer2 implements Runnable {
			// private final String name;

			public Consumer2(final String name) {
				// this.name = name;
			}

			public void run() {
				try {
					int i = 0;
					while (i < 100) {
						Ablagefaehig eingang = null;
						eingang = korb2.entnehmeAeltestenEingang();
						Assert.assertNotNull(eingang);
						// System.out.println("Consumer2.run(): " + name);

						i++;
						Thread.yield();
					}
					StandardAblagekorb_Test.this.fertigeConsumer++;
				} catch (final InterruptedException ex) {
				}
			}
		}

		final Producer p = new Producer();
		final Consumer2 c2 = new Consumer2("B");
		final Consumer1 c1 = new Consumer1("A");
		final Thread ct2 = new Thread(c2);
		final Thread ct1 = new Thread(c1);

		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		Thread.yield();

		ct2.start();
		ct1.start();

		new Thread(p).start();
		while (this.fertigeConsumer < 2) {
			Thread.yield();
		}
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
	protected boolean pruefeObEnthalten(
			final Ablagekorb<AblagefaehigesObject> korb,
			final AblagefaehigesObject element) {
		return ((StandardAblagekorb) korb).istEnthalten(element);
	}

}
