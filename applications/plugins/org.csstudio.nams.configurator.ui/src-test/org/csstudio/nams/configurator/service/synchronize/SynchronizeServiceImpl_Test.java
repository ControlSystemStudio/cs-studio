package org.csstudio.nams.configurator.service.synchronize;

import junit.framework.Assert;

import org.csstudio.nams.common.service.ExecutionServiceMock;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.preferenceservice.declaration.HoldsAPreferenceId;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
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

	@Override
	@Before
	public void setUp() throws Exception {
		this.localStoreConfigurationServiceMock = EasyMock
				.createStrictMock(LocalStoreConfigurationService.class);
		this.callback = EasyMock
				.createStrictMock(SynchronizeService.Callback.class);
		this.executionServiceMock = new ExecutionServiceMock();
		this.executionServiceMock.registerGroup(
				SynchronizeService.ThreadTypes.SYNCHRONIZER, null);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		// Verify in concrete TESTS:
		// EasyMock.verify(localStoreConfigurationServiceMock);
		// Verify in concrete TESTS: EasyMock.verify(callback);
		this.localStoreConfigurationServiceMock = null;
		this.callback = null;
		this.executionServiceMock = null;
	}

	@Test
	public void testAbgebrocheneBeimVorbereitenDerSynchronization()
			throws Throwable {
		this.synAbgebrochenGerufen = false;
		final StorageException expectedSorageException = new StorageException(
				"Test");

		this.localStoreConfigurationServiceMock.prepareSynchonization();
		EasyMock.expectLastCall().andThrow(expectedSorageException).once();
		EasyMock.replay(this.localStoreConfigurationServiceMock);

		EasyMock.expect(
				this.callback.pruefeObSynchronisationAusgefuehrtWerdenDarf())
				.andReturn(true).once();
		this.callback.bereiteSynchronisationVor();
		EasyMock.expectLastCall().once();
		this.callback
				.fehlerBeimVorbereitenDerSynchronisation(expectedSorageException);
		EasyMock.expectLastCall().once();
		this.callback.synchronisationAbgebrochen();
		EasyMock.expectLastCall().once();
		EasyMock.replay(this.callback);

		final SynchronizeService synchronizeService = this
				.getNewInstanceOfClassUnderTest();

		synchronizeService.sychronizeAlarmSystem(this.callback);
		this.executionServiceMock
				.mockExecuteOneStepOf(SynchronizeService.ThreadTypes.SYNCHRONIZER);

		EasyMock.verify(this.localStoreConfigurationServiceMock, this.callback);
	}

	@Test
	public void testAbgebrocheneSynchronizationWegenUngesicherterAenderungen()
			throws Throwable {
		this.synAbgebrochenGerufen = false;

		EasyMock.replay(this.localStoreConfigurationServiceMock);

		EasyMock.expect(
				this.callback.pruefeObSynchronisationAusgefuehrtWerdenDarf())
				.andReturn(false).once();
		this.callback.synchronisationAbgebrochen();
		EasyMock.expectLastCall().once();
		EasyMock.replay(this.callback);

		final SynchronizeService synchronizeService = this
				.getNewInstanceOfClassUnderTest();

		synchronizeService.sychronizeAlarmSystem(this.callback);
		this.executionServiceMock
				.mockExecuteOneStepOf(SynchronizeService.ThreadTypes.SYNCHRONIZER);

		EasyMock.verify(this.localStoreConfigurationServiceMock);
		EasyMock.verify(this.callback);
	}

	@Test
	public void testErfolgreicheSynchronization() throws Throwable {

		this.localStoreConfigurationServiceMock.prepareSynchonization();
		EasyMock.expectLastCall().once();

		EasyMock.replay(this.localStoreConfigurationServiceMock);

		EasyMock.expect(
				this.callback.pruefeObSynchronisationAusgefuehrtWerdenDarf())
				.andReturn(true).once();
		this.callback.bereiteSynchronisationVor();
		EasyMock.expectLastCall().once();
		this.callback.sendeNachrichtAnHintergrundSystem();
		EasyMock.expectLastCall().once();
		this.callback.wartetAufAntowrtDesHintergrundSystems();
		EasyMock.expectLastCall().once();
		this.callback.synchronisationsDurchHintergrundsystemsErfolgreich();
		EasyMock.expectLastCall().once();
		EasyMock.replay(this.callback);

		final SynchronizeService synchronizeService = this
				.getNewInstanceOfClassUnderTest();

		synchronizeService.sychronizeAlarmSystem(this.callback);
		this.executionServiceMock
				.mockExecuteOneStepOf(SynchronizeService.ThreadTypes.SYNCHRONIZER);

		/*-
		 * FIXME mz 2008-07-10: TODOS Hier und im der Impl!:
		 * - Neue SystemNachricht 
		 * - ProducerMock mit eq für die Sys-Nachricht, alles andere verboten
		 * - Consumer für die Antwort, neue Systemnachricht
		 * - TESTFAELLE:
		 *   # für JMS-Fehler beim senden
		 *   # für JMS Fehler beim empfangen
		 */

		EasyMock.verify(this.localStoreConfigurationServiceMock);
		EasyMock.verify(this.callback);
	}

	@Override
	protected SynchronizeService getNewInstanceOfClassUnderTest() {
		return new SynchronizeServiceImpl(new LoggerMock(),
				this.executionServiceMock, new PreferenceService() {

					public <T extends Enum<?> & HoldsAPreferenceId> void addPreferenceChangeListenerFor(
							final T[] preferenceIds,
							final PreferenceChangeListener changeListener) {
						Assert.fail("unexpectedmethod call!");
					}

					public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(
							final T key) {
						Assert.fail("unexpectedmethod call!");
						return false;
					}

					public <T extends Enum<?> & HoldsAPreferenceId> int getInt(
							final T key) {
						Assert.fail("unexpectedmethod call!");
						return 0;
					}

					public <T extends Enum<?> & HoldsAPreferenceId> String getString(
							final T key) {
						return "A Test Setting";
					}

				}, new ConfigurationServiceFactory() {

					public LocalStoreConfigurationService getConfigurationService(
							final String connectionURL,
							final DatabaseType dbType, final String username,
							final String password) throws StorageError {
						return SynchronizeServiceImpl_Test.this.localStoreConfigurationServiceMock;
					}

				});
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected SynchronizeService[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final SynchronizeService[] result = new SynchronizeService[3];
		result[0] = this.getNewInstanceOfClassUnderTest();
		result[1] = this.getNewInstanceOfClassUnderTest();
		result[2] = this.getNewInstanceOfClassUnderTest();
		return result;
	}
}
