package org.csstudio.nams.configurator.service.synchronize;

import org.csstudio.nams.common.service.ExecutionServiceMock;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SynchronizeServiceImpl_Test extends
		AbstractObject_TestCase<SynchronizeService> {

	protected boolean synAbgebrochenGerufen;
	private LocalStoreConfigurationService localStoreConfigurationServiceMock;
	private SynchronizeService.Callback callback;
	private ExecutionServiceMock executionServiceMock;

	@Before
	public void setUp() throws Exception {
		localStoreConfigurationServiceMock = EasyMock.createStrictMock(LocalStoreConfigurationService.class);
		callback = EasyMock.createStrictMock(SynchronizeService.Callback.class);
		executionServiceMock = new ExecutionServiceMock();
		executionServiceMock.registerGroup(SynchronizeService.ThreadTypes.SYNCHRONIZER, null);
	}

	@After
	public void tearDown() throws Exception {
		// Verify in concrete TESTS: EasyMock.verify(localStoreConfigurationServiceMock);
		// Verify in concrete TESTS: EasyMock.verify(callback);
		localStoreConfigurationServiceMock = null;
		callback = null;
		executionServiceMock = null;
	}

	@Test
	public void testAbgebrocheneSynchronizationWegenUngesicherterAenderungen() throws Throwable {
		synAbgebrochenGerufen = false;

		EasyMock.replay(localStoreConfigurationServiceMock);
		
		EasyMock.expect(callback.pruefeObSynchronisationAusgefuehrtWerdenDarf()).andReturn(false).once();
		callback.synchronisationAbgebrochen();
		EasyMock.expectLastCall().once();
		EasyMock.replay(callback);
		
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		
		synchronizeService.sychronizeAlarmSystem(callback);
		executionServiceMock.mockExecuteOneStepOf(SynchronizeService.ThreadTypes.SYNCHRONIZER);

		
		EasyMock.verify(localStoreConfigurationServiceMock);
		EasyMock.verify(callback);
	}
	
	@Test
	public void testAbgebrocheneBeimVorbereitenDerSynchronization() throws Throwable {
		synAbgebrochenGerufen = false;
		StorageException expectedSorageException = new StorageException("Test");

		localStoreConfigurationServiceMock.prepareSynchonization();
		EasyMock.expectLastCall().andThrow(expectedSorageException).once();
		EasyMock.replay(localStoreConfigurationServiceMock);
		
		EasyMock.expect(callback.pruefeObSynchronisationAusgefuehrtWerdenDarf()).andReturn(true).once();
		callback.bereiteSynchronisationVor();
		EasyMock.expectLastCall().once();
		callback.fehlerBeimVorbereitenDerSynchronisation(expectedSorageException);
		EasyMock.expectLastCall().once();
		callback.synchronisationAbgebrochen();
		EasyMock.expectLastCall().once();
		EasyMock.replay(callback);
		
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		
		synchronizeService.sychronizeAlarmSystem(callback);
		executionServiceMock.mockExecuteOneStepOf(SynchronizeService.ThreadTypes.SYNCHRONIZER);
		
		EasyMock.verify(localStoreConfigurationServiceMock, callback);
	}

	@Test
	public void testErfolgreicheSynchronization() throws Throwable {

		localStoreConfigurationServiceMock.prepareSynchonization();
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(localStoreConfigurationServiceMock);
		
		EasyMock.expect(callback.pruefeObSynchronisationAusgefuehrtWerdenDarf()).andReturn(true).once();
		callback.bereiteSynchronisationVor();
		EasyMock.expectLastCall().once();
		callback.sendeNachrichtAnHintergrundSystem();
		EasyMock.expectLastCall().once();
		callback.wartetAufAntowrtDesHintergrundSystems();
		EasyMock.expectLastCall().once();
		callback.synchronisationsDurchHintergrundsystemsErfolgreich();
		EasyMock.expectLastCall().once();
		EasyMock.replay(callback);
		
		SynchronizeService synchronizeService = getNewInstanceOfClassUnderTest();
		
		synchronizeService.sychronizeAlarmSystem(callback);
		executionServiceMock.mockExecuteOneStepOf(SynchronizeService.ThreadTypes.SYNCHRONIZER);
		
		/*-
		 * FIXME mz 2008-07-10: TODOS Hier und im der Impl!:
		 * - Neue SystemNachricht 
		 * - ProducerMock mit eq für die Sys-Nachricht, alles andere verboten
		 * - Consumer für die Antwort, neue Systemnachricht
		 * - TESTFAELLE:
		 *   # für JMS-Fehler beim senden
		 *   # für JMS Fehler beim empfangen
		 */
		
		EasyMock.verify(localStoreConfigurationServiceMock);
		EasyMock.verify(callback);
	}

	@Override
	protected SynchronizeService getNewInstanceOfClassUnderTest() {
		return new SynchronizeServiceImpl(new LoggerMock(), executionServiceMock, localStoreConfigurationServiceMock);
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
