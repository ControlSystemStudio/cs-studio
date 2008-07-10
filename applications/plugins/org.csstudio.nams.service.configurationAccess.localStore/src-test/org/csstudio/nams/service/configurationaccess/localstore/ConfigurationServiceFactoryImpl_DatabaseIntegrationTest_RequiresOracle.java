package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Collection;

import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
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
	
	@Before
	public void setUp() throws Exception {
		ConfigurationServiceFactoryImpl factory = new ConfigurationServiceFactoryImpl();
		assertNotNull(factory);
		
		service = factory.getConfigurationService(
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))",
				"org.hibernate.dialect.Oracle10gDialect",
				"DESY", 
				"DESY");
		
		assertNotNull(service);
	}

	@After
	public void tearDown() throws Exception {
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
	
//	@Test
//	public void testFactoryAndServiceOnOracleFuerFilterCondition() throws Throwable {
//		StringFilterConditionDTO neueFilterCondition = new StringFilterConditionDTO();
//		neueFilterCondition.setCName("Test");
//		neueFilterCondition.setCompValue("TestValue");
//		neueFilterCondition.setKeyValue(MessageKeyEnum.DESTINATION);
//		neueFilterCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
//
//		Configuration entireConfiguration = service.getEntireConfiguration();
//		assertNotNull(entireConfiguration);
//		assertFalse("neue fc ist natürlich noch nicht da.", entireConfiguration.gibAlleFilterConditions().contains(neueFilterCondition));
//		
//		service.saveFilterCondtionDTO(neueFilterCondition);
//		
//		// neu laden....
//		entireConfiguration = service.getEntireConfiguration();
//		assertNotNull(entireConfiguration);
//		assertTrue("neue fc ist jetzt gespeichert.", entireConfiguration.gibAlleFilterConditions().contains(neueFilterCondition));
//		
//		service.deleteFilterConditionDTO(neueFilterCondition);
//		
//		// neu laden
//		entireConfiguration = service.getEntireConfiguration();
//		assertNotNull(entireConfiguration);
//		assertFalse("neue fc ist jetzt nicht mehr da.", entireConfiguration.gibAlleFilterConditions().contains(neueFilterCondition));
//	}
}
