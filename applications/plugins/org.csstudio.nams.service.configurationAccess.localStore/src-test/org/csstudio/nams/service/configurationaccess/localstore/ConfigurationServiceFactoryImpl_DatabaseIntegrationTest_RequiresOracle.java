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
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.logging.declaration.LoggerMock.LogEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This TestCase tests the factory and the service for a real database (Oracle).
 * The service will be configured and created by the factory and some service
 * interactions will be perforemed. Do not forgett to reset the databse before
 * any run of this integration test!
 * 
 * @author gs, mz
 */
public class ConfigurationServiceFactoryImpl_DatabaseIntegrationTest_RequiresOracle
		extends TestCase {

	LocalStoreConfigurationService service;
	private LoggerMock loggerMock;
	
	@Before
	public void setUp() throws Exception {
		loggerMock = new LoggerMock();
		Configuration.staticInject(loggerMock);

		ConfigurationServiceFactoryImpl factory = new ConfigurationServiceFactoryImpl();
		
		service = factory.getConfigurationService(
				"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))",
				DatabaseType.Oracle10g,
				"DESY", 
				"DESY");
		
		assertNotNull(service);
	}

	@After
	public void tearDown() throws Exception {
		LogEntry[] mockGetCurrentLogEntries = loggerMock.mockGetCurrentLogEntries();
		for (LogEntry logEntry : mockGetCurrentLogEntries) {
			System.out.println(logEntry.toString());
		}
		
		service = null;
	}

	@Test
	public void testFactoryAndServiceOnOracleFuerAlarmbearbeiter() throws Throwable {
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
		assertFalse("neuer bearbeiter ist natürlich noch nicht da.", entireConfiguration.gibAlleAlarmbearbeiter().contains(neuerBearbeiter));
		
		service.saveDTO(neuerBearbeiter);
		
		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertTrue("neuer bearbeiter ist jetzt gespeichert.", entireConfiguration.gibAlleAlarmbearbeiter().contains(neuerBearbeiter));	
		
		// verändern
		neuerBearbeiter.setUserName("Hans Otto Detlef Struntz");
		service.saveDTO(neuerBearbeiter);
		
		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<AlarmbearbeiterDTO> loadedList = entireConfiguration.gibAlleAlarmbearbeiter();
		assertTrue("neuer bearbeiter ist jetzt gespeichert.", loadedList.contains(neuerBearbeiter));	
		for (AlarmbearbeiterDTO alarmbearbeiterDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden.
			assertFalse("Hans Otto Dietmar Struntz".equals(alarmbearbeiterDTO.getUserName()));
		}
		
		// loeschen
		service.deleteDTO(neuerBearbeiter);
		
		// neu laden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neuer bearbeiter ist jetzt nicht mehr da.", entireConfiguration.gibAlleAlarmbearbeiter().contains(neuerBearbeiter));
	}
	
	@Test
	public void testFactoryAndServiceOnOracleFuerFilterCondition() throws Throwable {
		StringFilterConditionDTO neueFilterCondition = new StringFilterConditionDTO();
		neueFilterCondition.setCName("Test");
		neueFilterCondition.setCompValue("TestValue");
		neueFilterCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		neueFilterCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist natürlich noch nicht da.", entireConfiguration.gibAlleFilterConditions().contains(neueFilterCondition));
		
		service.saveDTO(neueFilterCondition);
		
		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertTrue("neue fc ist jetzt gespeichert.", entireConfiguration.gibAlleFilterConditions().contains(neueFilterCondition));
		
		// verändern
		neueFilterCondition.setCName("Modified");
		service.saveDTO(neueFilterCondition);
		
		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterConditionDTO> loadedList = entireConfiguration.gibAlleFilterConditions();
		assertTrue("neue fc ist jetzt gespeichert.", loadedList.contains(neueFilterCondition));
		for (FilterConditionDTO filterConditionDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden.
			assertFalse("Test".equals(filterConditionDTO.getCName()));
		}
		
		// löschen
		service.deleteDTO(neueFilterCondition);
		
		// neu laden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist jetzt nicht mehr da.", entireConfiguration.gibAlleFilterConditions().contains(neueFilterCondition));
	}
	
	public void testReadAndWriteJunctorConditionForFilterTree() throws Throwable {
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
		assertFalse("neue fc ist natürlich noch nicht da.", entireConfiguration.gibAlleFilterConditions().contains(condition));
		
		service.saveDTO(condition);
		
		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		
		// search saved dto
		Collection<FilterConditionDTO> alleFilterConditions = entireConfiguration.gibAlleFilterConditions();
		JunctorConditionForFilterTreeDTO found = null;
		for (FilterConditionDTO filterConditionDTO : alleFilterConditions) {
			if( condition.equals(filterConditionDTO) )
			{
				found = condition;
			}
		}
		assertNotNull("neue fc ist jetzt gespeichert.", condition);
		condition = found;
		
		// verändern
		condition.setCName("Modified");
		service.saveDTO(condition);
		
		// neu laden....
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterConditionDTO> loadedList = entireConfiguration.gibAlleFilterConditions();
		assertTrue("neue fc ist jetzt gespeichert.", loadedList.contains(condition));
		for (FilterConditionDTO filterConditionDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden.
			assertFalse("Test-Con".equals(filterConditionDTO.getCName()));
		}
		
		// löschen
		service.deleteDTO(condition);
		
		// neu laden
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		assertFalse("neue fc ist jetzt nicht mehr da.", entireConfiguration.gibAlleFilterConditions().contains(condition));
		
		// clean up
		service.deleteDTO(leftCondition);
		service.deleteDTO(rightCondition);
	}
	
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
		
		JunctorConditionForFilterTreeDTO andCondition = new JunctorConditionForFilterTreeDTO();
		andCondition.setCName("TEST-Con");
		andCondition.setCDesc("Test-Description");
		andCondition.setOperator(JunctorConditionType.AND);
		andCondition.setOperands(operands);

		// Speicher die FC nicht(!) die Tree-FC (andCondition)
		service.saveDTO(leftCondition);
		service.saveDTO(rightCondition);
		
		// Filter 
		// Root-Ebene aufbauen
		List<FilterConditionDTO> filterConditions = new LinkedList<FilterConditionDTO>();
		filterConditions.add(andCondition);
		
		FilterDTO filter = new FilterDTO();
		filter.setName("Test Filter für JCFFT");
		filter.setDefaultMessage("Hallo Welt!");
		filter.setFilterConditions(filterConditions);

		// Pruefen dass noch kein entsprechender Filter da ist.
		Configuration entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		Collection<FilterDTO> alleFilter = entireConfiguration.gibAlleFilter();
		for (FilterDTO filterDTO : alleFilter) {
//			assertFalse("noch nicht enthalten", filterDTO.getName().equals("Test Filter für JCFFT"));
		}
		
		// Save
		service.saveFilterDTO(filter);
		
		// Pruefen dass Filter jetzt da ist.
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO found = null;
		for (FilterDTO filterDTO : alleFilter) {
			if( filter.equals(filterDTO) )
			{
				found = filterDTO;
			}
		}
		assertNotNull("enthalten", found);
		
		// löschen
		service.deleteDTO(filter);
		
		// Pruefen dass kein entsprechender Filter mehr da ist.
		entireConfiguration = service.getEntireConfiguration();
		assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		for (FilterDTO filterDTO : alleFilter) {
			assertFalse("nicht mehr enthalten", filterDTO.getName().equals("Test Filter für JCFFT"));
		}
		
		// clean up
		service.deleteDTO(leftCondition);
		service.deleteDTO(rightCondition);
	}
}
