package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SynchronizeServiceImpl_Test extends
		AbstractObject_TestCase<SynchronizeService> {

	protected boolean synAbgebrochenGerufen;
	private LocalStoreConfigurationService localStoreConfigurationServiceMock;

	@Before
	public void setUp() throws Exception {
		localStoreConfigurationServiceMock = EasyMock.createMock(LocalStoreConfigurationService.class);
	}

	@After
	public void tearDown() throws Exception {
		// Verify in concrete TESTS: EasyMock.verify(localStoreConfigurationServiceMock);
	}

	@Test
	public void testAbgebrocheneSynchronizationWegenUngesicherterAenderungen() {
		synAbgebrochenGerufen = false;

		EasyMock.replay(localStoreConfigurationServiceMock);
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		SynchronizeService.Callback callback = new SynchronizeService.Callback() {
			@Override
			public boolean pruefeObSynchronisationAusgefuehrtWerdenDarf() {
				return false; // In diesem Test hier abrechen!
			}

			@Override
			public void synchronisationAbgebrochen() {
				SynchronizeServiceImpl_Test.this.synAbgebrochenGerufen = true;
			}
		};

		synchronizeService.sychronizeAlarmSystem(callback);

		assertTrue("Der Service hat die Synchronisation abgebrochen", synAbgebrochenGerufen);
		
		EasyMock.verify(localStoreConfigurationServiceMock);
	}

	@Test
	public void testErfolgreicheSynchronization() {

		EasyMock.replay(localStoreConfigurationServiceMock);
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		SynchronizeService.Callback callback = new SynchronizeService.Callback() {
			@Override
			public boolean pruefeObSynchronisationAusgefuehrtWerdenDarf() {
				return true;
			}

			@Override
			public void synchronisationAbgebrochen() {
				Assert.fail("Synchronisation unerwartet abgebrochen!");
			}
		};

		synchronizeService.sychronizeAlarmSystem(callback);
		
		EasyMock.verify(localStoreConfigurationServiceMock);
	}

	@Override
	protected SynchronizeService getNewInstanceOfClassUnderTest() {
		return new SynchronizeServiceImpl(localStoreConfigurationServiceMock);
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
