package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.TopicFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.logging.declaration.LoggerMock.LogEntry;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
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
public class ConfServImplDbIntReqHSQLTest
		extends TestCase {

	LocalStoreConfigurationService service;
	private LoggerMock loggerMock;

	/**
	 * Erstellt einen Service für die Oracle-DB. Die Konfiguration ist fest
	 * codiert und muss ggf. in dieser Klasse angepasst werden!
	 */
	public LocalStoreConfigurationService createAServiceForOracleTests() {
		final ConfigurationServiceFactoryImpl factory = new ConfigurationServiceFactoryImpl(
				this.loggerMock);

		final LocalStoreConfigurationService result = factory
				.getConfigurationService("jdbc:hsqldb:mem:namscfg",
						DatabaseType.HSQL_2_2_8, "sa", "");

		return result;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		this.loggerMock = new LoggerMock();
		Configuration.staticInject(this.loggerMock);

		this.service = this.createAServiceForOracleTests();

		Assert.assertNotNull(this.service);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		final LogEntry[] mockGetCurrentLogEntries = this.loggerMock
				.mockGetCurrentLogEntries();
		for (final LogEntry logEntry : mockGetCurrentLogEntries) {
			System.out.println(logEntry.toString());
		}

		this.service = null;

		Thread.sleep(500);
	}

	@Test
	public void testDefaultFilterTexts() throws Throwable {
		final DefaultFilterTextDTO dto = new DefaultFilterTextDTO();
		dto.setMessageName("TEST-Halöle Welt");
		dto.setText("Hallo Welt!");

		this.service.saveDTO(dto);

		DefaultFilterTextDTO found = null;
		final Collection<DefaultFilterTextDTO> allDefaultFilterTexts = this.service
				.getEntireConfiguration().getAllDefaultFilterTexts();
		for (final DefaultFilterTextDTO defaultFilterTextDTO : allDefaultFilterTexts) {
			if (defaultFilterTextDTO.getMessageName()
					.equals("TEST-Halöle Welt")) {
				found = defaultFilterTextDTO;
				break;
			}
		}

		Assert.assertNotNull(found);
		Assert.assertEquals("Hallo Welt!", found.getText());
	}

	@Test
	public void testFactoryAndServiceOnOracleFuerAlarmbearbeiter()
			throws Throwable {
		final AlarmbearbeiterDTO neuerBearbeiter = new AlarmbearbeiterDTO();
		neuerBearbeiter.setActive(true);
		neuerBearbeiter.setConfirmCode("123");
		neuerBearbeiter.setEmail("test@testbar.test");
		neuerBearbeiter.setMobilePhone("0900 123");
		neuerBearbeiter.setPhone("01805 456");
		neuerBearbeiter.setPreferedAlarmType(PreferedAlarmType.EMAIL);
		neuerBearbeiter.setStatusCode("987");
		neuerBearbeiter.setUserName("Hans Otto Dietmar Struntz");

		Configuration entireConfiguration = this.service
				.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Assert.assertFalse("neuer bearbeiter ist natürlich noch nicht da.",
				entireConfiguration.gibAlleAlarmbearbeiter().contains(
						neuerBearbeiter));

		this.service.saveDTO(neuerBearbeiter);

		// neu laden....
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Assert.assertTrue("neuer bearbeiter ist jetzt gespeichert.",
				entireConfiguration.gibAlleAlarmbearbeiter().contains(
						neuerBearbeiter));

		// verändern
		neuerBearbeiter.setUserName("Hans Otto Detlef Struntz");
		this.service.saveDTO(neuerBearbeiter);

		// neu laden....
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		final Collection<AlarmbearbeiterDTO> loadedList = entireConfiguration
				.gibAlleAlarmbearbeiter();
		Assert.assertTrue("neuer bearbeiter ist jetzt gespeichert.", loadedList
				.contains(neuerBearbeiter));
		for (final AlarmbearbeiterDTO alarmbearbeiterDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden.
			Assert.assertFalse("Hans Otto Dietmar Struntz"
					.equals(alarmbearbeiterDTO.getUserName()));
		}

		// loeschen
		this.service.deleteDTO(neuerBearbeiter);

		// neu laden
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Assert.assertFalse("neuer bearbeiter ist jetzt nicht mehr da.",
				entireConfiguration.gibAlleAlarmbearbeiter().contains(
						neuerBearbeiter));
	}

	@Test
	public void testFactoryAndServiceOnOracleFuerFilterCondition()
			throws Throwable {
		final StringFilterConditionDTO neueFilterCondition = new StringFilterConditionDTO();
		neueFilterCondition.setCName("Test");
		neueFilterCondition.setCompValue("TestValue");
		neueFilterCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		neueFilterCondition
				.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		Configuration entireConfiguration = this.service
				.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Assert.assertFalse("neue fc ist natürlich noch nicht da.",
				entireConfiguration.gibAlleFilterConditions().contains(
						neueFilterCondition));

		this.service.saveDTO(neueFilterCondition);

		// neu laden....
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Assert.assertTrue("neue fc ist jetzt gespeichert.", entireConfiguration
				.gibAlleFilterConditions().contains(neueFilterCondition));

		// verändern
		neueFilterCondition.setCName("Modified");
		this.service.saveDTO(neueFilterCondition);

		// neu laden....
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		final Collection<FilterConditionDTO> loadedList = entireConfiguration
				.gibAlleFilterConditions();
		Assert.assertTrue("neue fc ist jetzt gespeichert.", loadedList
				.contains(neueFilterCondition));
		for (final FilterConditionDTO filterConditionDTO : loadedList) {
			// Keine Benutzer mit altem Namen vorhanden. (ID check muss sein,
			// wegen nebenläufigen Zugriffen auf der DB)
			Assert
					.assertFalse("Test".equals(filterConditionDTO.getCName())
							&& (filterConditionDTO.getIFilterConditionID() == neueFilterCondition
									.getIFilterConditionID()));
		}

		// löschen
		this.service.deleteDTO(neueFilterCondition);

		// neu laden
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Assert.assertFalse("neue fc ist jetzt nicht mehr da.",
				entireConfiguration.gibAlleFilterConditions().contains(
						neueFilterCondition));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testIdentitaet() throws Exception {

		final AlarmbearbeiterDTO bearbeiter = new AlarmbearbeiterDTO();
		bearbeiter.setActive(true);
		bearbeiter.setConfirmCode("1234");
		bearbeiter.setEmail("abc@testland.de");
		bearbeiter.setMobilePhone("0123456789");
		bearbeiter.setPhone("987654321");
		bearbeiter.setPreferedAlarmType(PreferedAlarmType.EMAIL);
		bearbeiter.setStatusCode("42");
		bearbeiter.setUserName("ABC");

		final SessionFactory factory = ((LocalStoreConfigurationServiceImpl) this.service)
				.getSessionFactory();

		Session session = factory.openSession();
		Transaction transaction = session.beginTransaction();
		transaction.begin();

		session.save(bearbeiter);

		transaction.commit();
		session.close();

		// Laden und pruefen

		session = factory.openSession();
		transaction = session.beginTransaction();
		transaction.begin();

		List<AlarmbearbeiterDTO> list = session.createCriteria(
				AlarmbearbeiterDTO.class).list();
		Assert.assertEquals(1, list.size());

		final AlarmbearbeiterDTO found1 = list.get(0);

		list = session.createCriteria(AlarmbearbeiterDTO.class).list();
		Assert.assertEquals(1, list.size());

		final AlarmbearbeiterDTO found2 = list.get(0);

		Assert.assertSame("Identität", found1, found2);

		transaction.commit();
		session.close();
	}

	@Test
	public void testLoadAndSaveAlarmbearbeiterGruppen() throws Throwable {
		final AlarmbearbeiterDTO bearbeiterEins = new AlarmbearbeiterDTO();
		bearbeiterEins.setActive(true);
		bearbeiterEins.setConfirmCode("1234");
		bearbeiterEins.setEmail("abc@testland.de");
		bearbeiterEins.setMobilePhone("0123456789");
		bearbeiterEins.setPhone("987654321");
		bearbeiterEins.setPreferedAlarmType(PreferedAlarmType.EMAIL);
		bearbeiterEins.setStatusCode("42");
		bearbeiterEins.setUserName("ABC");

		final AlarmbearbeiterDTO bearbeiterZwei = new AlarmbearbeiterDTO();
		bearbeiterZwei.setActive(true);
		bearbeiterZwei.setConfirmCode("4321");
		bearbeiterZwei.setEmail("efg@testland.de");
		bearbeiterZwei.setMobilePhone("987654321");
		bearbeiterZwei.setPhone("123456789");
		bearbeiterZwei.setPreferedAlarmType(PreferedAlarmType.VOICE);
		bearbeiterZwei.setStatusCode("23");
		bearbeiterZwei.setUserName("EFG");

		this.service.saveDTO(bearbeiterEins);
		this.service.saveDTO(bearbeiterZwei);

		final AlarmbearbeiterGruppenDTO gruppe = new AlarmbearbeiterGruppenDTO();
		gruppe.setActive(true);
		gruppe.alarmbearbeiterZuordnen(bearbeiterEins, true, "", new Date(42));
		gruppe.alarmbearbeiterZuordnen(bearbeiterZwei, false, "Urlaub",
				new Date(23));
		gruppe.setMinGroupMember((short) 1);
		gruppe.setTimeOutSec(100);
		gruppe.setUserGroupName("Testland-Group");

		this.service.saveDTO(gruppe);

		this.service.deleteDTO(gruppe);
		this.service.deleteDTO(bearbeiterEins);
		this.service.deleteDTO(bearbeiterZwei);
	}

	@Test
	public void testLoadAndStoreFilterActions() throws Throwable {
		final TopicDTO topic = new TopicDTO();
		topic.setTopicName("TEST");
		topic.setName("TEST-NAME");
		topic.setDescription("TEst");
		this.service.saveDTO(topic);

		final TopicFilterActionDTO topicAction = new TopicFilterActionDTO();
		topicAction.setMessage("Hallo Welt!");
		topicAction.setReceiver(topic);

		this.service.saveDTO(topicAction);

		this.service.deleteDTO(topicAction);
		this.service.deleteDTO(topic);
	}

	// @Test -- NICHT relevant, da diese FCs niemals ohne Filter genutzt werden
	// sollen!
	// public void testReadAndWriteJunctorConditionForFilterTree()
	// throws Throwable {
	// StringFilterConditionDTO leftCondition = new StringFilterConditionDTO();
	// leftCondition.setCName("Test-LeftCond");
	// leftCondition.setCompValue("TestValue");
	// leftCondition.setKeyValue(MessageKeyEnum.DESTINATION);
	// leftCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
	// service.saveDTO(leftCondition);
	//
	// StringFilterConditionDTO rightCondition = new StringFilterConditionDTO();
	// rightCondition.setCName("Test-RightCond");
	// rightCondition.setCompValue("TestValue2");
	// rightCondition.setKeyValue(MessageKeyEnum.DESTINATION);
	// rightCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);
	// service.saveDTO(rightCondition);
	//
	// Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
	// operands.add(leftCondition);
	// operands.add(rightCondition);
	//
	// JunctorConditionForFilterTreeDTO condition = new
	// JunctorConditionForFilterTreeDTO();
	// condition.setCName("TEST-Con");
	// condition.setCDesc("Test-Description");
	// condition.setOperator(JunctorConditionType.AND);
	// condition.setOperands(operands);
	//
	// Configuration entireConfiguration = service.getEntireConfiguration();
	// assertNotNull(entireConfiguration);
	// assertFalse("neue fc ist natürlich noch nicht da.", entireConfiguration
	// .gibAlleFilterConditions().contains(condition));
	//
	// service.saveDTO(condition);
	//
	// // neu laden....
	// entireConfiguration = service.getEntireConfiguration();
	// assertNotNull(entireConfiguration);
	//
	// // search saved dto
	// Collection<FilterConditionDTO> alleFilterConditions = entireConfiguration
	// .gibAlleFilterConditions();
	// JunctorConditionForFilterTreeDTO found = null;
	// for (FilterConditionDTO filterConditionDTO : alleFilterConditions) {
	// if (condition.getCName().equals(filterConditionDTO.getCName())) {
	// found = condition;
	// }
	// }
	// condition = found;
	// assertNotNull("neue fc ist jetzt gespeichert.", condition);
	//
	// // verändern
	// condition.setCName("Modified");
	// service.saveDTO(condition);
	//
	// // neu laden....
	// entireConfiguration = service.getEntireConfiguration();
	// assertNotNull(entireConfiguration);
	// Collection<FilterConditionDTO> loadedList = entireConfiguration
	// .gibAlleFilterConditions();
	// assertTrue("neue fc ist jetzt gespeichert.", loadedList
	// .contains(condition));
	// for (FilterConditionDTO filterConditionDTO : loadedList) {
	// // Keine Benutzer mit altem Namen vorhanden.
	// assertFalse("Test-Con".equals(filterConditionDTO.getCName()));
	// }
	//
	// // verändern
	// Set<FilterConditionDTO> operands2 = new HashSet<FilterConditionDTO>();
	// operands2.add(leftCondition);
	// condition.setOperands(operands2);
	// service.saveDTO(condition);
	//
	// // neu laden....
	// entireConfiguration = service.getEntireConfiguration();
	// assertNotNull(entireConfiguration);
	// loadedList = entireConfiguration.gibAlleFilterConditions();
	// assertTrue("neue fc ist jetzt gespeichert.", loadedList
	// .contains(condition));
	// JunctorConditionForFilterTreeDTO result = null;
	// for (FilterConditionDTO filterConditionDTO : loadedList) {
	// if (filterConditionDTO.getIFilterConditionID() == condition
	// .getIFilterConditionID()) {
	// assertEquals("Modified", filterConditionDTO.getCName());
	// result = (JunctorConditionForFilterTreeDTO) filterConditionDTO;
	// }
	// }
	// assertNotNull(result);
	// assertEquals(1, result.getOperands().size());
	// assertTrue(result.getOperands().contains(leftCondition));
	// condition = result;
	//		
	// // löschen
	// service.deleteDTO(condition);
	//
	// // neu laden
	// entireConfiguration = service.getEntireConfiguration();
	// assertNotNull(entireConfiguration);
	// assertFalse("neue fc ist jetzt nicht mehr da.", entireConfiguration
	// .gibAlleFilterConditions().contains(condition));
	//
	// // clean up
	// service.deleteDTO(leftCondition);
	// service.deleteDTO(rightCondition);
	// }

	@Test
	public void testReloadAndRefresh() throws Throwable {
		final LocalStoreConfigurationService secondService = this
				.createAServiceForOracleTests();

		final AlarmbearbeiterDTO dto = new AlarmbearbeiterDTO();
		dto.setUserName("Test-Troll");
		this.service.saveDTO(dto);
		Assert.assertTrue(dto.getUserId() != 0);
		Assert.assertEquals("Test-Troll", dto.getUserName());

		final Collection<AlarmbearbeiterDTO> alleAlarmbearbeiter = secondService
				.getEntireConfiguration().gibAlleAlarmbearbeiter();
		AlarmbearbeiterDTO found = null;
		for (final AlarmbearbeiterDTO alarmbearbeiterDTO : alleAlarmbearbeiter) {
			if (alarmbearbeiterDTO.getUserId() == dto.getUserId()) { // Set
				// by
				// save
				// op
				Assert.assertEquals(dto.getUserName(), alarmbearbeiterDTO
						.getUserName());
				found = alarmbearbeiterDTO;
				break;
			}
		}
		Assert.assertNotNull(found);

		final int oldId = dto.getUserId();
		dto.setUserName("Test-Dummy");
		this.service.saveDTO(dto);
		Assert.assertEquals(oldId, dto.getUserId());
		Assert.assertEquals("Test-Dummy", dto.getUserName());

		final Collection<AlarmbearbeiterDTO> alleAlarmbearbeiterNow = secondService
				.getEntireConfiguration().gibAlleAlarmbearbeiter();
		AlarmbearbeiterDTO foundNow = null;
		for (final AlarmbearbeiterDTO alarmbearbeiterDTO : alleAlarmbearbeiterNow) {
			if (alarmbearbeiterDTO.getUserId() == dto.getUserId()) { // Set
				// by
				// save
				// op
				Assert.assertEquals(
						"Der Datensatz ist auch für andere sichtbar verändert",
						dto.getUserName(), alarmbearbeiterDTO.getUserName());
				foundNow = alarmbearbeiterDTO;
				break;
			}
		}
		Assert.assertNotNull(foundNow);

		this.service.deleteDTO(dto);
	}

	@Test
	public void testSaveFilter() throws Throwable {
		// Conditions
		final StringFilterConditionDTO leftCondition = new StringFilterConditionDTO();
		leftCondition.setCName("Test-LeftCond");
		leftCondition.setCompValue("TestValue");
		leftCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		leftCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		final StringFilterConditionDTO rightCondition = new StringFilterConditionDTO();
		rightCondition.setCName("Test-RightCond");
		rightCondition.setCompValue("TestValue2");
		rightCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		rightCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		// Speicher die FC nicht(!) die Tree-FC (andCondition)
		this.service.saveDTO(leftCondition);
		this.service.saveDTO(rightCondition);

		final Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
		operands.add(leftCondition);
		operands.add(rightCondition);

		final JunctorCondForFilterTreeDTO orCondition = new JunctorCondForFilterTreeDTO();
		orCondition.setCName("TEST-Con JCFFT");
		orCondition.setCDesc("Test-Description");
		orCondition.setOperator(JunctorConditionType.OR);
		orCondition.setOperands(operands);

		// Filter
		// Root-Ebene aufbauen
		final List<FilterConditionDTO> filterConditions = new LinkedList<FilterConditionDTO>();
		filterConditions.add(orCondition);

		final FilterDTO filter = new FilterDTO();
		filter.setName("Test Filter für JCFFT");
		filter.setDefaultMessage("Hallo Welt!");
		filter.setFilterConditions(filterConditions);

		// Pruefen dass noch kein entsprechender Filter da ist.
		Configuration entireConfiguration = this.service
				.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Collection<FilterDTO> alleFilter = entireConfiguration.gibAlleFilter();
		for (final FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				this.service.deleteDTO(filterDTO);
			}
			// assertFalse("noch nicht enthalten", filterDTO.getName().equals(
			// "Test Filter für JCFFT"));
		}

		// Save
		this.service.saveDTO(filter);

		// Pruefen dass Filter jetzt da ist.
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO found = null;
		for (final FilterDTO filterDTO : alleFilter) {
			if (filter.equals(filterDTO)) {
				found = filterDTO;
			}
		}
		Assert.assertNotNull("enthalten", found);
		Assert.assertEquals(filter, found);

		// verändern
		final Set<FilterConditionDTO> operands2 = new HashSet<FilterConditionDTO>();
		operands2.add(leftCondition);

		orCondition.setOperands(operands2);
		this.service.saveDTO(filter);

		// Filter finden
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO foundFilter = null;
		for (final FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getIFilterID() == filter.getIFilterID()) {
				foundFilter = filterDTO;
			}
		}
		final List<FilterConditionDTO> list = foundFilter.getFilterConditions();
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(orCondition, list.get(0));
		final JunctorCondForFilterTreeDTO junctionDTO = (JunctorCondForFilterTreeDTO) list
				.get(0);
		final Set<FilterConditionDTO> operands3 = junctionDTO.getOperands();
		Assert.assertEquals(1, operands3.size());
		Assert.assertEquals(leftCondition, operands3.iterator().next());

		// löschen
		this.service.deleteDTO(filter);

		// Pruefen dass kein entsprechender Filter mehr da ist.
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		for (final FilterDTO filterDTO : alleFilter) {
			Assert.assertFalse("nicht mehr enthalten", filterDTO.getName()
					.equals("Test Filter für JCFFT"));
		}

		// clean up
		this.service.deleteDTO(leftCondition);
		this.service.deleteDTO(rightCondition);
	}

	@Test
	public void testStoreAndLoadFilterWithConditionNegations() throws Throwable {
		// Conditions
		final StringFilterConditionDTO leftCondition = new StringFilterConditionDTO();
		leftCondition.setCName("Test-LeftCond");
		leftCondition.setCompValue("TestValue");
		leftCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		leftCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		final StringFilterConditionDTO rightCondition = new StringFilterConditionDTO();
		rightCondition.setCName("Test-RightCond");
		rightCondition.setCompValue("TestValue2");
		rightCondition.setKeyValue(MessageKeyEnum.DESTINATION);
		rightCondition.setOperatorEnum(StringRegelOperator.OPERATOR_TEXT_EQUAL);

		final Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
		operands.add(leftCondition);
		operands.add(rightCondition);

		// Speicher die FC nicht(!) die Tree-FC (andCondition)
		this.service.saveDTO(leftCondition);
		this.service.saveDTO(rightCondition);

		final JunctorCondForFilterTreeDTO orCondition = new JunctorCondForFilterTreeDTO();
		orCondition.setCName("TEST-Con JCFFT");
		orCondition.setCDesc("Test-Description");
		orCondition.setOperator(JunctorConditionType.OR);
		orCondition.setOperands(operands);

		final NegationCondForFilterTreeDTO notOR = new NegationCondForFilterTreeDTO();
		notOR.setNegatedFilterCondition(orCondition);

		// Filter
		// Root-Ebene aufbauen
		final List<FilterConditionDTO> filterConditions = new LinkedList<FilterConditionDTO>();
		filterConditions.add(notOR);

		final FilterDTO filter = new FilterDTO();
		filter.setName("Test Filter für JCFFT");
		filter.setDefaultMessage("Hallo Welt!");
		filter.setFilterConditions(filterConditions);

		// Pruefen dass noch kein entsprechender Filter da ist.
		Configuration entireConfiguration = this.service
				.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		Collection<FilterDTO> alleFilter = entireConfiguration.gibAlleFilter();
		for (final FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				this.service.deleteDTO(filterDTO);
			}
			Assert.assertFalse("noch nicht enthalten", filterDTO.getName()
					.equals("Test Filter für JCFFT"));
		}

		// Save
		this.service.saveDTO(filter);

		// Pruefen dass Filter jetzt da ist.
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO found = null;
		for (final FilterDTO filterDTO : alleFilter) {
			if (filter.equals(filterDTO)) {
				found = filterDTO;
			}
		}
		Assert.assertNotNull("enthalten", found);
		Assert.assertEquals(filter, found);

		// verändern
		final Set<FilterConditionDTO> operands2 = new HashSet<FilterConditionDTO>();
		operands2.add(leftCondition);

		orCondition.setOperands(operands2);
		this.service.saveDTO(filter);

		// Filter finden
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		FilterDTO foundFilter = null;
		for (final FilterDTO filterDTO : alleFilter) {
			if (filterDTO.getName().equals("Test Filter für JCFFT")) {
				foundFilter = filterDTO;
			}
		}
		final List<FilterConditionDTO> list = foundFilter.getFilterConditions();
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(notOR, list.get(0));
		final NegationCondForFilterTreeDTO notOrFound = (NegationCondForFilterTreeDTO) list
				.get(0);
		final FilterConditionDTO foundNotOr = notOrFound
				.getNegatedFilterCondition();
		Assert
				.assertTrue(foundNotOr instanceof JunctorCondForFilterTreeDTO);
		final JunctorCondForFilterTreeDTO junctionDTO = (JunctorCondForFilterTreeDTO) foundNotOr;
		final Set<FilterConditionDTO> operands3 = junctionDTO.getOperands();
		Assert.assertEquals(1, operands3.size());
		Assert.assertEquals(leftCondition, operands3.iterator().next());

		// löschen
		this.service.deleteDTO(filter);

		// Pruefen dass kein entsprechender Filter mehr da ist.
		entireConfiguration = this.service.getEntireConfiguration();
		Assert.assertNotNull(entireConfiguration);
		alleFilter = entireConfiguration.gibAlleFilter();
		for (final FilterDTO filterDTO : alleFilter) {
			Assert.assertFalse("nicht mehr enthalten", filterDTO.getName()
					.equals("Test Filter für JCFFT"));
		}

		// clean up
		this.service.deleteDTO(leftCondition);
		this.service.deleteDTO(rightCondition);
	}
}
