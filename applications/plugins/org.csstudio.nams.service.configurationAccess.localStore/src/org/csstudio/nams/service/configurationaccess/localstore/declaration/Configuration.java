package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.LinkedList;
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
	@SuppressWarnings("unused")
	private static Logger logger;

	public static void staticInject(final Logger logger) {
		Configuration.logger = logger;
	}

	private final Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private final Collection<TopicDTO> alleAlarmtopics;
	private final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;

	private final Collection<FilterDTO> allFilters;
	private final Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;
	private final Collection<FilterConditionDTO> allFilterConditions;
	private final Collection<RubrikDTO> alleRubriken;
	private final List<User2UserGroupDTO> alleUser2UserGroupMappings;
	private final Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues;

	private final Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO;

	public Configuration(
			final Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter,
			final Collection<TopicDTO> alleAlarmtopics,
			final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen,
			final Collection<FilterDTO> allFilters,
			final Collection<FilterConditionDTO> allFilterConditions,
			final Collection<RubrikDTO> alleRubriken,
			final Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO) {
		super();
		this.alleAlarmbarbeiter = alleAlarmbarbeiter;
		this.alleAlarmtopics = alleAlarmtopics;
		this.alleAlarmbearbeiterGruppen = alleAlarmbearbeiterGruppen;
		this.allFilters = allFilters;
		this.allFilterConditionMappings = new LinkedList<FilterConditionsToFilterDTO>();
		this.allFilterConditions = allFilterConditions;
		this.alleRubriken = alleRubriken;
		this.alleUser2UserGroupMappings = new LinkedList<User2UserGroupDTO>();
		this.allCompareValues = new LinkedList<StringArrayFilterConditionCompareValuesDTO>();
		this.allDefaultFilterTextDTO = allDefaultFilterTextDTO;
	}

	public Collection<DefaultFilterTextDTO> getAllDefaultFilterTexts() {
		return this.allDefaultFilterTextDTO;
	}

	@Deprecated
	public Collection<FilterConditionsToFilterDTO> getAllFilterConditionMappings() {
		return this.allFilterConditionMappings;
	}

	@Deprecated
	public Collection<StringArrayFilterConditionCompareValuesDTO> getAllStringArrayCompareValues() {
		return this.allCompareValues;
	}

	@Deprecated
	public List<User2UserGroupDTO> getAllUser2UserGroupDTOs() {
		return this.alleUser2UserGroupMappings;
	}

	public Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter() {
		return this.alleAlarmbarbeiter;
	}

	public Collection<AlarmbearbeiterGruppenDTO> gibAlleAlarmbearbeiterGruppen() {
		return this.alleAlarmbearbeiterGruppen;
	}

	public Collection<TopicDTO> gibAlleAlarmtopics() {
		return this.alleAlarmtopics;
	}

	/**
	 * Returns a list of all FilterDTO's
	 */
	public Collection<FilterDTO> gibAlleFilter() {
		return this.allFilters;
	}

	public Collection<FilterConditionDTO> gibAlleFilterConditions() {
		return this.allFilterConditions;
	}

	public Collection<RubrikDTO> gibAlleRubriken() {
		return this.alleRubriken;
	}

}
