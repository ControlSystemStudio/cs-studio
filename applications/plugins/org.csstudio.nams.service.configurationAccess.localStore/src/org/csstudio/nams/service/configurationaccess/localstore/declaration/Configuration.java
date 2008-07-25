package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.logging.declaration.Logger;

@Entity
public class Configuration {
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private Collection<TopicDTO> alleAlarmtopics;
	private Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
	private Collection<FilterDTO> allFilters;
	private Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;

	private Collection<FilterConditionDTO> allFilterConditions;
	private Collection<RubrikDTO> alleRubriken;
	private List<User2UserGroupDTO> alleUser2UserGroupMappings;
	@SuppressWarnings("unused")
	private static Logger logger;
	private Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues;
	private final Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO;

	public Configuration(
			Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter,
			Collection<TopicDTO> alleAlarmtopics,
			Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen,
			Collection<FilterDTO> allFilters,
			Collection<FilterConditionsToFilterDTO> allFilterConditionMappings,
			Collection<FilterConditionDTO> allFilterConditions,
			Collection<RubrikDTO> alleRubriken,
			List<User2UserGroupDTO> alleUser2UserGroupMappings,
			Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues, 
			Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO) {
		super();
		this.alleAlarmbarbeiter = alleAlarmbarbeiter;
		this.alleAlarmtopics = alleAlarmtopics;
		this.alleAlarmbearbeiterGruppen = alleAlarmbearbeiterGruppen;
		this.allFilters = allFilters;
		this.allFilterConditionMappings = allFilterConditionMappings;
		this.allFilterConditions = allFilterConditions;
		this.alleRubriken = alleRubriken;
		this.alleUser2UserGroupMappings = alleUser2UserGroupMappings;
		this.allCompareValues = allCompareValues;
		this.allDefaultFilterTextDTO = allDefaultFilterTextDTO;
	}

	public Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter() {
		return alleAlarmbarbeiter;
	}

	public Collection<TopicDTO> gibAlleAlarmtopics() {
		return alleAlarmtopics;
	}

	public Collection<DefaultFilterTextDTO> getAllDefaultFilterTexts() {
		return allDefaultFilterTextDTO;
	}

	public Collection<AlarmbearbeiterGruppenDTO> gibAlleAlarmbearbeiterGruppen() {
		return alleAlarmbearbeiterGruppen;
	}

	/**
	 * Returns a list of all FilterDTO's
	 */
	public Collection<FilterDTO> gibAlleFilter() {
		return allFilters;
	}

	public Collection<FilterConditionDTO> gibAlleFilterConditions() {
		return allFilterConditions;
	}

	public Collection<FilterConditionsToFilterDTO> getAllFilterConditionMappings() {
		return allFilterConditionMappings;
	}

	public Collection<RubrikDTO> gibAlleRubriken() {
		return alleRubriken;
	}

	public List<User2UserGroupDTO> getAllUser2UserGroupDTOs() {
		return alleUser2UserGroupMappings;
	}

	public static void staticInject(Logger logger) {
		Configuration.logger = logger;
	}

	public Collection<StringArrayFilterConditionCompareValuesDTO> getAllStringArrayCompareValues() {
		return allCompareValues;
	}

}
