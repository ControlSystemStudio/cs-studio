package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.logging.declaration.LoggerMock.LogEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This TestCase tests the factory and the service for a real database (Oracle).
 * The service will be configured and created by the factory and some service
 * interactions will be performed. Do not forget to reset the database before
 * any run of this integration test!
 * 
 * @author gs, mz
 */
public class ConfigurationServiceFactoryImpl_DatabaseIntegrationTest_RequiresHSQL
		extends TestCase {

	LocalStoreConfigurationService service;
	private LoggerMock loggerMock;

	@Before
	public void setUp() throws Exception {
		loggerMock = new LoggerMock();
		Configuration.staticInject(loggerMock);

		service = createAServiceForOracleTests();

		assertNotNull(service);
	}

	/**
	 * Erstellt einen Service für die Oracle-DB. Die Konfiguration ist fest
	 * codiert und muss ggf. in dieser Klasse angepasst werden!
	 */
	public LocalStoreConfigurationService createAServiceForOracleTests() {
		ConfigurationServiceFactoryImpl factory = new ConfigurationServiceFactoryImpl(
				loggerMock);

		LocalStoreConfigurationService result = factory
				.getConfigurationService("jdbc:hsqldb:mem:namscfg",
						DatabaseType.HSQL_1_8_0, "sa", "");

		return result;
	}

	@After
	public void tearDown() throws Exception {
		LogEntry[] mockGetCurrentLogEntries = loggerMock
				.mockGetCurrentLogEntries();
		for (LogEntry logEntry : mockGetCurrentLogEntries) {
			System.out.println(logEntry.toString());
		}

		service = null;
		
		Thread.sleep(500);
	}

	 @Test
	public void testReloadAndRefresh() throws Throwable {
		LocalStoreConfigurationService secondService = createAServiceForOracleTests();

		AlarmbearbeiterDTO dto = new AlarmbearbeiterDTO();
		dto.setUserName("Test-Troll");
		service.saveDTO(dto);
		assertTrue(dto.getUserId() != 0);
		assertEquals("Test-Troll", dto.getUserName());

		Collection<AlarmbearbeiterDTO> alleAlarmbearbeiter = secondService
				.getEntireConfiguration().gibAlleAlarmbearbeiter();
		AlarmbearbeiterDTO found = null;
		for (AlarmbearbeiterDTO alarmbearbeiterDTO : alleAlarmbearbeiter) {
			if (alarmbearbeiterDTO.getUserId() == dto.getUserId()) { // Set
				// by
				// save
				// op
				assertEquals(dto.getUserName(), alarmbearbeiterDTO
						.getUserName());
				found = alarmbearbeiterDTO;
				break;
			}
		}
		assertNotNull(found);

		int oldId = dto.getUserId();
		dto.setUserName("Test-Dummy");
		service.saveDTO(dto);
		assertEquals(oldId, dto.getUserId());
		assertEquals("Test-Dummy", dto.getUserName());

		Collection<AlarmbearbeiterDTO> alleAlarmbearbeiterNow = secondService
				.getEntireConfiguration().gibAlleAlarmbearbeiter();
		AlarmbearbeiterDTO foundNow = null;
		for (AlarmbearbeiterDTO alarmbearbeiterDTO : alleAlarmbearbeiterNow) {
			if (alarmbearbeiterDTO.getUserId() == dto.getUserId()) { // Set
				// by
				// save
				// op
				assertEquals(
						"Der Datensatz ist auch für andere sichtbar verändert",
						dto.getUserName(), alarmbearbeiterDTO.getUserName());
				foundNow = alarmbearbeiterDTO;
				break;
			}
		}
		assertNotNull(foundNow);

		service.deleteDTO(dto);
	}

	@Test
	public void testFactoryAndServiceOnOracleFuerAlarmbearbeiter()
			throws Throwable {
		AlarmbearbeiterDTO neuerBearbeiter = new AlarmbearbeiterDTO();
		neuerBearbeiter.setActive(true);
		neuerBearbeiter.setConfirmCode("123");
		neuerBearbeiter.setEmail("test@testbar.test");
		neuerBearbeiter.setMobilePhone("0900 123");
		neuerBearbeiter.setPhone("01805 456");
		neuerBearbeiter.setPreferedAlarmType(PreferedAlarmType.EMAIL);
		neuerBearbeiter.setStatusCode("987");
		neuerBearbeiter.setUserName("Hans Otto Dietmar Struntz");

		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neuer bearbeiter ist natürlich noch nicht da.",
				entireConfiguration.gibAlleAlarmbearbeiter().contains(
						neuerBearbeiter));

		service.saveDTO(neuerBearbeiter);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertTrue("neuer bearbeiter ist jetzt gespeichert.",
				entireConfiguration.gibAlleAlarmbearbeiter().contains(
						neuerBearbeiter));

		// verändern
		neuerBearbeiter.setUserName("Hans Otto Detlef Struntz");
		service.saveDTO(neuerBearbeiter);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<AlarmbearbeiterDTO> loadedList = entireConfiguration
				.gibAlleAlarmbearbeiter();
		assertTrue("neuer bearbeiter ist jetzt gespeichert.", loadedList
				.contains(neuerBearbeiter));
		for (AlarmbearbeiterDTO alarmbearbeiterDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden.
			// assertFalse("Hans Otto Dietmar Struntz".equals(alarmbearbeiterDTO
			// .getUserName()));
		}

		// loeschen
		service.deleteDTO(neuerBearbeiter);

		// neu laden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neuer bearbeiter ist jetzt nicht mehr da.",
				entireConfiguration.gibAlleAlarmbearbeiter().contains(
						neuerBearbeiter));
	}

	 @Test
	public void testFactoryAndServiceOnOracleFuerFilterCondition()
			throws Throwable {
		StringFilterConditionDTO neueFilterCondition = new StringFilterConditionDTO();
		neueFilterCondition.setCName("Test");
		neueFilterCondition.setCompValue("TestValue");
		neueFilterCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		neueFilterCondition
				.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist natürlich noch nicht da.", entireConfiguration
				.gibAlleFilterConditions().contains(neueFilterCondition));

		service.saveDTO(neueFilterCondition);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertTrue("neue fc ist jetzt gespeichert.", entireConfiguration
				.gibAlleFilterConditions().contains(neueFilterCondition));

		// verändern
		neueFilterCondition.setCName("Modified");
		service.saveDTO(neueFilterCondition);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterConditionDTO> loadedList = entireConfiguration
				.gibAlleFilterConditions();
		assertTrue("neue fc ist jetzt gespeichert.", loadedList
				.contains(neueFilterCondition));
		for (FilterConditionDTO filterConditionDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden. (ID check muss sein,
			// wegen nebenläufigen Zugriffen auf der DB)
			assertFalse("Test".equals(filterConditionDTO.getCName())
					&& filterConditionDTO.getIFilterConditionID() == neueFilterCondition
							.getIFilterConditionID());
		}

		// löschen
		service.deleteDTO(neueFilterCondition);

		// neu laden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist jetzt nicht mehr da.", entireConfiguration
				.gibAlleFilterConditions().contains(neueFilterCondition));
	}

	public void untestReadAndWriteJunctorConditionForFilterTree()
			throws Throwable {
		StringFilterConditionDTO leftCondition = new StringFilterConditionDTO();
		leftCondition.setCName("Test-LeftCond");
		leftCondition.setCompValue("TestValue");
		leftCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		leftCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		service.saveDTO(leftCondition);

		StringFilterConditionDTO rightCondition = new StringFilterConditionDTO();
		rightCondition.setCName("Test-RightCond");
		rightCondition.setCompValue("TestValue2");
		rightCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		rightCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
		service.saveDTO(rightCondition);

		Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
		operands.add(leftCondition);
		operands.add(rightCondition);

		JunctorConditionForFilterTreeDTO condition = new JunctorConditionForFilterTreeDTO();
		condition.setCName("TEST-Con");
		condition.setCDesc("Test-Description");
		condition.setOperator(JunctorConditionType.AND);
		condition.setOperands(operands);

		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist natürlich noch nicht da.", entireConfiguration
				.gibAlleFilterConditions().contains(condition));

		service.saveDTO(condition);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);

		// search saved dto
		Collection<FilterConditionDTO> alleFilterConditions = entireConfiguration
				.gibAlleFilterConditions();
		JunctorConditionForFilterTreeDTO found = null;
		for (FilterConditionDTO filterConditionDTO : alleFilterConditions) {
			if (condition.getCName().equals(filterConditionDTO.getCName())) {
				found = condition;
			}
		}
		condition = found;
		assertNotNull("neue fc ist jetzt gespeichert.", condition);

		// verändern
		condition.setCName("Modified");
		service.saveDTO(condition);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterConditionDTO> loadedList = entireConfiguration
				.gibAlleFilterConditions();
		assertTrue("neue fc ist jetzt gespeichert.", loadedList
				.contains(condition));
		for (FilterConditionDTO filterConditionDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden.
			assertFalse("Test-Con".equals(filterConditionDTO.getCName()));
		}

		// verändern
		Set<FilterConditionDTO> operands2 = new HashSet<FilterConditionDTO>();
		operands2.add(leftCondition);
		condition.setOperands(operands2);
		service.saveDTO(condition);

		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		loadedList = entireConfiguration.gibAlleFilterConditions();
		assertTrue("neue fc ist jetzt gespeichert.", loadedList
				.contains(condition));
		JunctorConditionForFilterTreeDTO result = null;
		for (FilterConditionDTO filterConditionDTO : loadedList) {
			if (filterConditionDTO.getIFilterConditionID() == condition
					.getIFilterConditionID()) {
				assertEquals("Modified", filterConditionDTO.getCName());
				result = (JunctorConditionForFilterTreeDTO) filterConditionDTO;
			}
		}
		assertNotNull(result);
		assertEquals(1, result.getOperands().size());
		assertTrue(result.getOperands().contains(leftCondition));

		// löschen
		service.deleteDTO(condition);

		// neu laden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist jetzt nicht mehr da.", entireConfiguration
				.gibAlleFilterConditions().contains(condition));

		// clean up
		service.deleteDTO(leftCondition);
		service.deleteDTO(rightCondition);
	}

	@Test
	public void testSaveFilter() throws Throwable {
		// Conditions
		StringFilterConditionDTO leftCondition = new StringFilterConditionDTO();
		leftCondition.setCName("Test-LeftCond");
		leftCondition.setCompValue("TestValue");
		leftCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		leftCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		StringFilterConditionDTO rightCondition = new StringFilterConditionDTO();
		rightCondition.setCName("Test-RightCond");
		rightCondition.setCompValue("TestValue2");
		rightCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		rightCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
		operands.add(leftCondition);
		operands.add(rightCondition);

		// Speicher die FC nicht(!) die Tree-FC (andCondition)
		service.saveDTO(leftCondition);
		service.saveDTO(rightCondition);

		JunctorConditionForFilterTreeDTO orCondition = new JunctorConditionForFilterTreeDTO();
		orCondition.setCName("TEST-Con JCFFT");
		orCondition.setCDesc("Test-Description");
		orCondition.setOperator(JunctorConditionType.OR);
		orCondition.setOperands(operands);

		// Filter
		// Root-Ebene aufbauen
		List<FilterConditionDTO> filterConditions = new LinkedList<FilterConditionDTO>();
		filterConditions.add(orCondition);

		FilterDTO filter = new FilterDTO();
		filter.setName("Test Filter für JCFFT");
		filter.setDefaultMessage("Hallo Welt!");
		filter.setFilterConditions(filterConditions);

		// Pruefen dass noch kein entsprechender Filter da ist.
		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterDTO> alleFilter = entireConfiguration.gibAlleFilter();
		for (FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				service.deleteFilterDTO(filterDTO);
			}
			// assertFalse("noch nicht enthalten", filterDTO.getName().equals(
			// "Test Filter für JCFFT"));
		}

		// Save
		service.saveFilterDTO(filter);

		// Pruefen dass Filter jetzt da ist.
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO found = null;
		for (FilterDTO filterDTO : alleFilter) {
			if (filter.equals(filterDTO)) {
				found = filterDTO;
			}
		}
		assertNotNull("enthalten", found);
		assertEquals(filter, found);

		// verändern
		Set<FilterConditionDTO> operands2 = new HashSet<FilterConditionDTO>();
		operands2.add(leftCondition);

		orCondition.setOperands(operands2);
		service.saveFilterDTO(filter);

		// Filter finden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO foundFilter = null;
		for (FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				foundFilter = filterDTO;
			}
		}
		List<FilterConditionDTO> list = foundFilter.getFilterConditions();
		assertEquals(1, list.size());
		assertEquals(orCondition, list.get(0));
		JunctorConditionForFilterTreeDTO junctionDTO = (JunctorConditionForFilterTreeDTO) list
				.get(0);
		Set<FilterConditionDTO> operands3 = junctionDTO.getOperands();
		assertEquals(1, operands3.size());
		assertEquals(leftCondition, operands3.iterator().next());

		// löschen
		service.deleteDTO(filter);

		// Pruefen dass kein entsprechender Filter mehr da ist.
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		for (FilterDTO filterDTO : alleFilter) {
			assertFalse("nicht mehr enthalten", filterDTO.getName().equals(
					"Test Filter für JCFFT"));
		}

		// clean up
		service.deleteDTO(leftCondition);
		service.deleteDTO(rightCondition);
	}

	 @Test
	public void testStoreAndLoadFilterWithConditionNegations()
			throws Throwable {
		// Conditions
		StringFilterConditionDTO leftCondition = new StringFilterConditionDTO();
		leftCondition.setCName("Test-LeftCond");
		leftCondition.setCompValue("TestValue");
		leftCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		leftCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		StringFilterConditionDTO rightCondition = new StringFilterConditionDTO();
		rightCondition.setCName("Test-RightCond");
		rightCondition.setCompValue("TestValue2");
		rightCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		rightCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
		operands.add(leftCondition);
		operands.add(rightCondition);

		// Speicher die FC nicht(!) die Tree-FC (andCondition)
		service.saveDTO(leftCondition);
		service.saveDTO(rightCondition);

		JunctorConditionForFilterTreeDTO orCondition = new JunctorConditionForFilterTreeDTO();
		orCondition.setCName("TEST-Con JCFFT");
		orCondition.setCDesc("Test-Description");
		orCondition.setOperator(JunctorConditionType.OR);
		orCondition.setOperands(operands);

		NegationConditionForFilterTreeDTO notOR = new NegationConditionForFilterTreeDTO();
		notOR.setNegatedFilterCondition(orCondition);

		// Filter
		// Root-Ebene aufbauen
		List<FilterConditionDTO> filterConditions = new LinkedList<FilterConditionDTO>();
		filterConditions.add(notOR);

		FilterDTO filter = new FilterDTO();
		filter.setName("Test Filter für JCFFT");
		filter.setDefaultMessage("Hallo Welt!");
		filter.setFilterConditions(filterConditions);

		// Pruefen dass noch kein entsprechender Filter da ist.
		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterDTO> alleFilter = entireConfiguration.gibAlleFilter();
		for (FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				service.deleteDTO(filterDTO);
			}
			// assertFalse("noch nicht enthalten", filterDTO.getName().equals(
			// "Test Filter für JCFFT"));
		}

		// Save
		service.saveFilterDTO(filter);

		// Pruefen dass Filter jetzt da ist.
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO found = null;
		for (FilterDTO filterDTO : alleFilter) {
			if (filter.equals(filterDTO)) {
				found = filterDTO;
			}
		}
		assertNotNull("enthalten", found);
		assertEquals(filter, found);

		// verändern
		Set<FilterConditionDTO> operands2 = new HashSet<FilterConditionDTO>();
		operands2.add(leftCondition);

		orCondition.setOperands(operands2);
		service.saveFilterDTO(filter);

		// Filter finden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO foundFilter = null;
		for (FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				foundFilter = filterDTO;
			}
		}
		List<FilterConditionDTO> list = foundFilter.getFilterConditions();
		assertEquals(1, list.size());
		assertEquals(notOR, list.get(0));
		NegationConditionForFilterTreeDTO notOrFound = (NegationConditionForFilterTreeDTO) list
				.get(0);
		FilterConditionDTO foundNotOr = notOrFound.getNegatedFilterCondition();
		assertTrue(foundNotOr instanceof JunctorConditionForFilterTreeDTO);
		JunctorConditionForFilterTreeDTO junctionDTO = (JunctorConditionForFilterTreeDTO) foundNotOr;
		Set<FilterConditionDTO> operands3 = junctionDTO.getOperands();
		assertEquals(1, operands3.size());
		assertEquals(leftCondition, operands3.iterator().next());

		// löschen
		service.deleteDTO(filter);

		// Pruefen dass kein entsprechender Filter mehr da ist.
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		for (FilterDTO filterDTO : alleFilter) {
			assertFalse("nicht mehr enthalten", filterDTO.getName().equals(
					"Test Filter für JCFFT"));
		}

		// clean up
		service.deleteDTO(leftCondition);
		service.deleteDTO(rightCondition);
	}
}
