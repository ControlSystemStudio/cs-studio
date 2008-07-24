package org.csstudio.nams.service.configurationaccess.localstore;


import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.JunctorConditionForFilterTreeConditionJoinDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationStoreService2_Test {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadAnsSaveAlarmtopics() throws Throwable {
		
		ConfigurationStoreService2Impl service = new ConfigurationStoreService2Impl(buildSessionFactory(), new LoggerMock());
		
		Collection<RubrikDTO> allRubrikDTOs = service.findAll(RubrikDTO.class);
		Assert.assertNotNull(allRubrikDTOs);
		
		RubrikDTO newRubrik = service.createNewCategory("Rubrik name", RubrikTypeEnum.TOPIC);
		
		for (RubrikDTO rubrikDTO : allRubrikDTOs) {
			System.out.println("found: " + rubrikDTO);
		}
	}
	
	private SessionFactory buildSessionFactory() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration
				.addAnnotatedClass(ReplicationStateDTO.class)
				.addAnnotatedClass(AlarmbearbeiterDTO.class)
				.addAnnotatedClass(AlarmbearbeiterGruppenDTO.class)
				.addAnnotatedClass(FilterConditionDTO.class)
				.addAnnotatedClass(FilterConditionTypeDTO.class)
				.addAnnotatedClass(TopicDTO.class)
				.addAnnotatedClass(FilterConditionsToFilterDTO.class)

				.addAnnotatedClass(JunctorConditionDTO.class)
				.addAnnotatedClass(StringFilterConditionDTO.class)
				.addAnnotatedClass(StringArrayFilterConditionDTO.class)
				
				.addAnnotatedClass(StringArrayFilterConditionCompareValuesDTO.class) // TODO Mapping in Configuration!!!
				.addAnnotatedClass(TimeBasedFilterConditionDTO.class)
				.addAnnotatedClass(ProcessVariableFilterConditionDTO.class)
				.addAnnotatedClass(FilterDTO.class)
				.addAnnotatedClass(HistoryDTO.class)
				.addAnnotatedClass(RubrikDTO.class)
				.addAnnotatedClass(NegationConditionForFilterTreeDTO.class)
				.addAnnotatedClass(JunctorConditionForFilterTreeDTO.class)
				.addAnnotatedClass(JunctorConditionForFilterTreeConditionJoinDTO.class)
				.addAnnotatedClass(User2UserGroupDTO.class)
				.addAnnotatedClass(NegationConditionForFilterTreeDTO.class)
				
				.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
				.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))")
				.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
				.setProperty("hibernate.connection.username", "DESY")
				.setProperty("hibernate.connection.password", "DESY")

				.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider")
				.setProperty("hibernate.cache.use_minimal_puts", "false")
				.setProperty("hibernate.cache.use_query_cache", "false")
				.setProperty("hibernate.cache.use_second_level_cache", "false")

				.setProperty("hibernate.connection.pool_size", "1")
				.setProperty("current_session_context_class", "thread")
				.setProperty("show_sql", "true")
				.setProperty("hbm2ddl.auto", "update") 
				.setProperty("hibernate.mapping.precedence", "class");

		//TODO in die config auslagern
		Logger.getLogger("org.hibernate").setLevel(Level.WARN);
		
		return configuration.buildSessionFactory();
	}
}
