package org.csstudio.nams.configurator.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationBeanServiceImpl_Test extends TestCase {

	private LocalStoreConfigurationService confService;

	@Before
	public void setUp() throws Exception {
		confService = EasyMock.createMock(LocalStoreConfigurationService.class);
	}

	@After
	public void tearDown() throws Exception {
		EasyMock.verify(confService);
		confService = null;
	}
	
	private Configuration createTestData() {
		Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter = new LinkedList<AlarmbearbeiterDTO>();
		Collection<TopicDTO> alleAlarmtopics = new LinkedList<TopicDTO>();
		Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen = new LinkedList<AlarmbearbeiterGruppenDTO>();
		Collection<FilterDTO> allFilters = new LinkedList<FilterDTO>();
		Collection<FilterConditionsToFilterDTO> allFilterConditionMappings = new LinkedList<FilterConditionsToFilterDTO>();
		Collection<FilterConditionDTO> allFilterConditions = new LinkedList<FilterConditionDTO>();
		Collection<RubrikDTO> alleRubriken = new LinkedList<RubrikDTO>();
		List<User2UserGroupDTO> alleUser2UserGroupMappings = new LinkedList<User2UserGroupDTO>();
		Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues = new LinkedList<StringArrayFilterConditionCompareValuesDTO>();
		Collection<DefaultFilterTextDTO> allDefaultFilterTexts = new LinkedList<DefaultFilterTextDTO>();
		
		// TODO Mit konsistenen!! daten füllen
		
		return new Configuration(alleAlarmbarbeiter, alleAlarmtopics, alleAlarmbearbeiterGruppen, allFilters, allFilterConditionMappings, allFilterConditions, alleRubriken, alleUser2UserGroupMappings, allCompareValues, allDefaultFilterTexts);
	}

	@Test
	public void testConfigurationBeanServiceImpl() throws Throwable {
		fail("Diesen Test implementieren, wenn der Conf-Service aufgeräumt ist, also das Configuration ein reines Material!");
		
		Configuration startConfiguration = createTestData();
		
		EasyMock.expect(confService.getEntireConfiguration()).andReturn(startConfiguration).once();
		EasyMock.replay(confService);
		
		ConfigurationBeanService service = new ConfigurationBeanServiceImpl();
		((ConfigurationBeanServiceImpl)service).setNewConfigurationStore(confService);
		
		service.refreshData();
		
		// TODO check here...
	}

}
