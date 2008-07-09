package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SynchronizeServiceImpl_Test extends
		AbstractObject_TestCase<SynchronizeService> {

	protected boolean synAbgebrochenGerufen;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAbgebrocheneSynchronizationWegenUngesicherterAenderungen() {
		synAbgebrochenGerufen = false;

		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		SynchronizeService.Callback callback = new SynchronizeService.Callback() {
			@Override
			public boolean pruefeObUngesicherteAenderungenDasSynchronisierenVerhindern() {
				return false; // In diesem Test hier abrechen!
			}

			@Override
			public void synchronisationAbgebrochen() {
				SynchronizeServiceImpl_Test.this.synAbgebrochenGerufen = true;
			}
		};

		synchronizeService.sychronizeAlarmSystem(callback);

		assertTrue("Der Service hat die Synchronisation abgebrochen", synAbgebrochenGerufen);
	}

	@Test
	public void testErfolgreicheSynchronization() {

	}

	@Override
	protected SynchronizeService getNewInstanceOfClassUnderTest() {
		return new SynchronizeServiceImpl();
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected SynchronizeService[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		SynchronizeService[] result = new SynchronizeService[3];
		result[0] = getNewInstanceOfClassUnderTest();
		result[1] = getNewInstanceOfClassUnderTest();
		result[2] = getNewInstanceOfClassUnderTest();
		return result;
	}
}
