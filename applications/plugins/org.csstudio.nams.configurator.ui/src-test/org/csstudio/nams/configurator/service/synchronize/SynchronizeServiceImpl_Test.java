package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SynchronizeServiceImpl_Test extends
		AbstractObject_TestCase<SynchronizeService> {

	protected boolean synAbgebrochenGerufen;
	private LocalStoreConfigurationService localStoreConfigurationServiceMock;
	private SynchronizeService.Callback callback;

	@Before
	public void setUp() throws Exception {
		localStoreConfigurationServiceMock = EasyMock.createMock(LocalStoreConfigurationService.class);
		callback = EasyMock.createMock(SynchronizeService.Callback.class);
	}

	@After
	public void tearDown() throws Exception {
		// Verify in concrete TESTS: EasyMock.verify(localStoreConfigurationServiceMock);
		// Verify in concrete TESTS: EasyMock.verify(callback);
	}

	@Test
	public void testAbgebrocheneSynchronizationWegenUngesicherterAenderungen() {
		synAbgebrochenGerufen = false;

		EasyMock.replay(localStoreConfigurationServiceMock);
		
		EasyMock.expect(callback.pruefeObSynchronisationAusgefuehrtWerdenDarf()).andReturn(false).once();
		callback.synchronisationAbgebrochen();
		EasyMock.expectLastCall().once();
		EasyMock.replay(callback);
		
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		
		synchronizeService.sychronizeAlarmSystem(callback);

		
		EasyMock.verify(localStoreConfigurationServiceMock);
		EasyMock.verify(callback);
	}

	@Test
	public void testErfolgreicheSynchronization() {

		localStoreConfigurationServiceMock.prepareSynchonization();
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(localStoreConfigurationServiceMock);
		
		EasyMock.expect(callback.pruefeObSynchronisationAusgefuehrtWerdenDarf()).andReturn(true).once();
		EasyMock.replay(callback);
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		
		synchronizeService.sychronizeAlarmSystem(callback);
		
		EasyMock.verify(localStoreConfigurationServiceMock);
		EasyMock.verify(callback);
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
