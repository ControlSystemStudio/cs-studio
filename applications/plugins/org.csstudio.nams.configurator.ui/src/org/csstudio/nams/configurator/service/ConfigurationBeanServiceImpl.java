package org.csstudio.nams.configurator.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.User2GroupBean;
import org.csstudio.nams.configurator.beans.filters.FilterConditionAddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionForFilterTreeBean;
import org.csstudio.nams.configurator.beans.filters.PVFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringArrayFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.StringFilterConditionBean;
import org.csstudio.nams.configurator.beans.filters.TimeBasedFilterConditionBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO_PK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO_PK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.Logger;

public class ConfigurationBeanServiceImpl implements ConfigurationBeanService {

	private static Logger logger;
	private final LocalStoreConfigurationService configurationService;
	private Configuration entireConfiguration;
	private List<ConfigurationBeanServiceListener> listeners = new LinkedList<ConfigurationBeanServiceListener>();

	private Map<Integer, AlarmbearbeiterBean> alarmbearbeiterBeans = new HashMap<Integer, AlarmbearbeiterBean>();
	private Map<Integer, AlarmbearbeiterGruppenBean> alarmbearbeitergruppenBeans = new HashMap<Integer, AlarmbearbeiterGruppenBean>();
	private Map<Integer, AlarmtopicBean> alarmtopicBeans = new HashMap<Integer, AlarmtopicBean>();
	private Map<Integer, FilterbedingungBean> filterbedingungBeans = new HashMap<Integer, FilterbedingungBean>();
	private Map<Integer, FilterBean> filterBeans = new HashMap<Integer, FilterBean>();

	// Rubriks don't need to be beans.
	private Collection<RubrikDTO> rubrikDTOs = new LinkedList<RubrikDTO>();

	public ConfigurationBeanServiceImpl(
			LocalStoreConfigurationService localStore) {
		this.configurationService = localStore;
		loadConfiguration();
	}

	// private <BeanType extends AbstractConfigurationBean<BeanType>, DTOType>
	// void updateMaps(Map<Integer, BeanType> map, Collection<DTOType> dtoList)
	// {
	// for (DTOType dto : dtoList) {
	// BeanType bean = DTO2Bean(dto);
	// BeanType origBean = map.get(new Integer(bean.getID()));
	// if (origBean != null) {
	// origBean.updateState(bean);
	// } else {
	// map.put(bean.getID(), bean);
	// }
	// }
	// }

	private void loadConfiguration() {
		try {
			entireConfiguration = configurationService.getEntireConfiguration();
			// TODO Folgendes Exception-Handling überdenken....
			if (entireConfiguration == null) {
				throw new RuntimeException("Couldn't load the Configuration");
			}
			rubrikDTOs = entireConfiguration.gibAlleRubriken();

			Collection<AlarmbearbeiterDTO> alarmbearbeiter = entireConfiguration
					.gibAlleAlarmbearbeiter();
			for (AlarmbearbeiterDTO alarmbearbeiterDTO : alarmbearbeiter) {
				AlarmbearbeiterBean bean = DTO2Bean(alarmbearbeiterDTO);
				AlarmbearbeiterBean origBean = alarmbearbeiterBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					alarmbearbeiterBeans.put(bean.getID(), bean);
				}
			}

			Collection<AlarmbearbeiterGruppenDTO> alarmbearbeiterGruppen = entireConfiguration
					.gibAlleAlarmbearbeiterGruppen();
			for (AlarmbearbeiterGruppenDTO alarmbearbeiterGruppenDTO : alarmbearbeiterGruppen) {
				AlarmbearbeiterGruppenBean bean = DTO2Bean(alarmbearbeiterGruppenDTO);
				AlarmbearbeiterGruppenBean origBean = alarmbearbeitergruppenBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					alarmbearbeitergruppenBeans.put(bean.getID(), bean);
				}
			}

			Collection<TopicDTO> alarmtopics = entireConfiguration
					.gibAlleAlarmtopics();
			for (TopicDTO topicDTO : alarmtopics) {
				AlarmtopicBean bean = DTO2Bean(topicDTO);
				AlarmtopicBean origBean = alarmtopicBeans.get(new Integer(bean
						.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					alarmtopicBeans.put(bean.getID(), bean);
				}
			}

			Collection<FilterConditionDTO> filterConditions = entireConfiguration
					.gibAlleFilterConditions();
			for (FilterConditionDTO filterConditionDTO : filterConditions) {
				FilterbedingungBean bean = DTO2Bean(filterConditionDTO);
				FilterbedingungBean origBean = filterbedingungBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					filterbedingungBeans.put(bean.getID(), bean);
				}
			}

			Collection<FilterDTO> filters = entireConfiguration.gibAlleFilter();
			for (FilterDTO filter : filters) {
				FilterBean bean = DTO2Bean(filter);
				FilterBean origBean = filterBeans
						.get(new Integer(bean.getID()));
				if (origBean != null) {
					origBean.updateState(bean);
				} else {
					filterBeans.put(bean.getID(), bean);
				}
			}

		} catch (StorageError e) {
			logger.logErrorMessage(this,
					"Could not load Eniter Configuration because of: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (StorageException e) {
			logger.logErrorMessage(this,
					"Could not load Eniter Configuration because of: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (InconsistentConfigurationException e) {
			logger.logErrorMessage(this,
					"Could not load Eniter Configuration because of: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void addConfigurationBeanServiceListener(
			ConfigurationBeanServiceListener listener) {
		listeners.add(listener);
	}

	public FilterbedingungBean[] getFilterConditionsForView() {
		// TODO may cache results
		List<FilterbedingungBean> lists = new LinkedList<FilterbedingungBean>();
		for (FilterbedingungBean bean : getFilterConditionBeans()) {
			if (!(bean.getFilterSpecificBean() instanceof JunctorConditionForFilterTreeBean))
				lists.add(bean);
		}
		return lists.toArray(new FilterbedingungBean[lists.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterBeans()
	 */
	public AlarmbearbeiterBean[] getAlarmBearbeiterBeans() {
		// Collection<AlarmbearbeiterDTO> alarmBearbeiterDTOs =
		// entireConfiguration.gibAlleAlarmbearbeiter();
		// List<AlarmbearbeiterBean> beans = new
		// LinkedList<AlarmbearbeiterBean>();
		// for (AlarmbearbeiterDTO alarmbearbeitergruppe : alarmBearbeiterDTOs)
		// {
		// beans.add(DTO2Bean(alarmbearbeitergruppe));
		// }
		// return beans.toArray(new AlarmbearbeiterBean[beans.size()]);
		Collection<AlarmbearbeiterBean> values = alarmbearbeiterBeans.values();
		return values.toArray(new AlarmbearbeiterBean[values.size()]);
	}

	AlarmbearbeiterBean DTO2Bean(AlarmbearbeiterDTO alarmbearbeiter) {
		AlarmbearbeiterBean bean = new AlarmbearbeiterBean();
		bean.setActive(alarmbearbeiter.isActive());
		bean.setConfirmCode(alarmbearbeiter.getConfirmCode());
		bean.setEmail(alarmbearbeiter.getEmail());
		bean.setMobilePhone(alarmbearbeiter.getMobilePhone());
		bean.setName(alarmbearbeiter.getUserName());
		bean.setPhone(alarmbearbeiter.getPhone());
		bean.setPreferedAlarmType(alarmbearbeiter.getPreferedAlarmType());
		bean.setStatusCode(alarmbearbeiter.getStatusCode());
		bean.setUserID(alarmbearbeiter.getUserId());
		bean.setRubrikName(getRubrikNameForId(alarmbearbeiter.getGroupRef())); // GUI-Group
		// =
		// Rubrik

		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterGruppenBeans()
	 */
	public AlarmbearbeiterGruppenBean[] getAlarmBearbeiterGruppenBeans() {
		// Collection<AlarmbearbeiterGruppenDTO> alarmBearbeiterGruppenDTOs =
		// entireConfiguration.gibAlleAlarmbearbeiterGruppen();
		// List<AlarmbearbeiterGruppenBean> beans = new
		// LinkedList<AlarmbearbeiterGruppenBean>();
		// for (AlarmbearbeiterGruppenDTO alarmbearbeitergruppe :
		// alarmBearbeiterGruppenDTOs) {
		// beans.add(DTO2Bean(alarmbearbeitergruppe));
		// }
		// return beans.toArray(new AlarmbearbeiterGruppenBean[beans.size()]);
		Collection<AlarmbearbeiterGruppenBean> values = alarmbearbeitergruppenBeans
				.values();
		return values.toArray(new AlarmbearbeiterGruppenBean[values.size()]);
	}

	AlarmbearbeiterGruppenBean DTO2Bean(AlarmbearbeiterGruppenDTO dto) {

		AlarmbearbeiterGruppenBean bean = new AlarmbearbeiterGruppenBean();
		bean.setActive(dto.isActive());
		bean.setGroupID(dto.getUserGroupId());
		bean.setMinGroupMember(dto.getMinGroupMember());
		bean.setName(dto.getUserGroupName());
		bean.setTimeOutSec(dto.getTimeOutSec());
		bean.setRubrikName(getRubrikNameForId(dto.getGroupRef())); // GUI-Group
		// = Rubrik

		List<User2GroupBean> list = new LinkedList<User2GroupBean>();
		final Map<User2GroupBean, User2UserGroupDTO> beanDTOMap = new HashMap<User2GroupBean, User2UserGroupDTO>();
		for (User2UserGroupDTO map : dto.gibZugehoerigeAlarmbearbeiter()) {
			User2GroupBean bean2 = DTO2Bean(map, bean);
			list.add(bean2);
			beanDTOMap.put(bean2, map);
		}
		Collections.sort(list, new Comparator<User2GroupBean>() {
			public int compare(User2GroupBean o1, User2GroupBean o2) {
				return beanDTOMap.get(o1).getPosition()
						- beanDTOMap.get(o2).getPosition();
			}
		});
		bean.setUsers(list);
		return bean;
	}

	private User2GroupBean DTO2Bean(User2UserGroupDTO map,
			AlarmbearbeiterGruppenBean groupBean) {
		AlarmbearbeiterBean userBean = alarmbearbeiterBeans.get(map
				.getUser2UserGroupPK().getIUserRef());
		User2GroupBean result = new User2GroupBean(userBean);

		result.setActive(map.isActive());
		result.setLastChange(new Date(map.getLastchange()));
		result.setRubrikName("");
		result.setActiveReason(map.getActiveReason());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmTopicBeans()
	 */
	public AlarmtopicBean[] getAlarmTopicBeans() {
		// Collection<TopicDTO> alarmtopicsDTOs =
		// entireConfiguration.gibAlleAlarmtopics();
		// List<AlarmtopicBean> beans = new LinkedList<AlarmtopicBean>();
		// for (TopicDTO alarmbearbeitergruppe : alarmtopicsDTOs) {
		// beans.add(DTO2Bean(alarmbearbeitergruppe));
		// }
		// return beans.toArray(new AlarmtopicBean[beans.size()]);
		Collection<AlarmtopicBean> values = alarmtopicBeans.values();
		return values.toArray(new AlarmtopicBean[values.size()]);
	}

	AlarmtopicBean DTO2Bean(TopicDTO dto) {
		AlarmtopicBean bean = new AlarmtopicBean();
		bean.setDescription(dto.getDescription());
		bean.setHumanReadableName(dto.getName());
		bean.setTopicID(dto.getId());
		bean.setTopicName(dto.getTopicName());
		bean.setRubrikName(getRubrikNameForId(dto.getGroupRef())); // GUI-Group
		// = Rubrik
		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterBeans()
	 */
	public FilterBean[] getFilterBeans() {
		// Collection<FilterDTO> filterDTOs =
		// entireConfiguration.gibAlleFilter();
		// List<FilterBean> beans = new LinkedList<FilterBean>();
		// for (FilterDTO filter : filterDTOs) {
		// beans.add(DTO2Bean(filter));
		// }
		// return beans.toArray(new FilterBean[beans.size()]);
		Collection<FilterBean> values = filterBeans.values();
		return values.toArray(new FilterBean[values.size()]);
	}

	FilterBean DTO2Bean(FilterDTO filterDTO) {
		FilterBean bean = new FilterBean();
		bean.setDefaultMessage(filterDTO.getDefaultMessage());
		bean.setFilterID(filterDTO.getIFilterID());
		bean.setName(filterDTO.getName());

		List<FilterbedingungBean> conditions = bean.getConditions();
		conditions.clear();
		for (FilterConditionDTO condition : filterDTO.getFilterConditions()) {
			int filterConditionID = condition.getIFilterConditionID();
			FilterbedingungBean filterbedingungBean = filterbedingungBeans.get(filterConditionID);
			conditions.add(filterbedingungBean);
		}
		bean.setConditions(conditions);
		bean.setRubrikName(getRubrikNameForId(filterDTO.getIGroupRef()));

		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterBeans()
	 */
	public String[] getRubrikNamesForType(RubrikTypeEnum type) {
		Collection<String> specificRubriks = new ArrayList<String>();
		for (Iterator<RubrikDTO> iter = rubrikDTOs.iterator(); iter.hasNext();) {
			RubrikDTO rubrikDTO = (RubrikDTO) iter.next();
			if (rubrikDTO.getType().equals(type)) {
				specificRubriks.add(rubrikDTO.getCGroupName());
			}
		}

		return specificRubriks.toArray(new String[specificRubriks.size()]);
	}

	private String getRubrikNameForId(int groupRef) {
		String result = "";
		for (Iterator<RubrikDTO> iter = rubrikDTOs.iterator(); iter.hasNext();) {
			RubrikDTO rubrikDTO = (RubrikDTO) iter.next();
			if (rubrikDTO.getIGroupId() == groupRef) {
				result = rubrikDTO.getCGroupName();
				break;
			}
		}
		return result;
	}

	private int getRubrikIDForName(String rubrikName, RubrikTypeEnum type) {
		if (rubrikName == null || rubrikName.length() == 0) {
			return 0;
		}
		int result = 0;
		for (Iterator<RubrikDTO> iter = rubrikDTOs.iterator(); iter.hasNext();) {
			RubrikDTO rubrikDTO = (RubrikDTO) iter.next();
			if (rubrikDTO.getCGroupName().equals(rubrikName)
					&& rubrikDTO.getType().equals(type)) {
				result = rubrikDTO.getIGroupId();
				break;
			}
		}
		if ((result == 0) && !("".equals(rubrikName))) {
			RubrikDTO newRubrikDTO = new RubrikDTO();
			newRubrikDTO.setCGroupName(rubrikName);
			newRubrikDTO.setType(type);
			result = configurationService.saveRubrikDTO(newRubrikDTO)
					.getIGroupId();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterConditionBeans()
	 */
	public FilterbedingungBean[] getFilterConditionBeans() {
		// Collection<FilterConditionDTO> filterDTOs =
		// entireConfiguration.gibAlleFilterConditions();
		// List<FilterbedingungBean> beans = new
		// LinkedList<FilterbedingungBean>();
		// for (FilterConditionDTO filter : filterDTOs) {
		// beans.add(DTO2Bean(filter));
		// }
		// return beans.toArray(new FilterbedingungBean[beans.size()]);
		Collection<FilterbedingungBean> values = filterbedingungBeans.values();
		return values.toArray(new FilterbedingungBean[values.size()]);
	}

	private FilterbedingungBean DTO2Bean(FilterConditionDTO filterCondtionDTO) {
		FilterbedingungBean bean = new FilterbedingungBean();
		
		FilterConditionAddOnBean filterSpecificBean = null;
		if (filterCondtionDTO instanceof JunctorConditionDTO) {
			JunctorConditionBean junctorConditionBean = new JunctorConditionBean();
			junctorConditionBean
					.setFirstCondition(DTO2Bean(((JunctorConditionDTO) filterCondtionDTO)
							.getFirstFilterCondition()));
			junctorConditionBean.setJunctor(((JunctorConditionDTO) filterCondtionDTO)
					.getJunctor());
			junctorConditionBean.setRubrikName(""); // RubrikName is set by the
			// main Bean.
			junctorConditionBean
					.setSecondCondition(DTO2Bean(((JunctorConditionDTO) filterCondtionDTO)
							.getSecondFilterCondition()));
			filterSpecificBean = junctorConditionBean;
		} else if (filterCondtionDTO instanceof ProcessVariableFilterConditionDTO) {
			PVFilterConditionBean filterbedingungBean = new PVFilterConditionBean();
			filterbedingungBean.setRubrikName("");
			filterbedingungBean
					.setChannelName(((ProcessVariableFilterConditionDTO) filterCondtionDTO)
							.getCPvChannelName());
			filterbedingungBean
					.setCompareValue(((ProcessVariableFilterConditionDTO) filterCondtionDTO)
							.getCCompValue());
			filterbedingungBean
					.setOperator(((ProcessVariableFilterConditionDTO) filterCondtionDTO)
							.getPVOperator());
			filterbedingungBean
					.setSuggestedType(((ProcessVariableFilterConditionDTO) filterCondtionDTO)
							.getSuggestedPVType());
			filterSpecificBean = filterbedingungBean;
		} else if (filterCondtionDTO instanceof StringArrayFilterConditionDTO) {
			StringArrayFilterConditionBean stringArrayFilterConditionBean = new StringArrayFilterConditionBean();
			stringArrayFilterConditionBean.setRubrikName("");

			stringArrayFilterConditionBean
					.setCompareValues(((StringArrayFilterConditionDTO) filterCondtionDTO)
							.getCompareValueStringList());
			stringArrayFilterConditionBean
					.setKeyValue(((StringArrayFilterConditionDTO) filterCondtionDTO)
							.getKeyValueEnum());
			stringArrayFilterConditionBean
					.setOperator(((StringArrayFilterConditionDTO) filterCondtionDTO)
							.getOperatorEnum());
			filterSpecificBean = stringArrayFilterConditionBean;
		} else if (filterCondtionDTO instanceof StringFilterConditionDTO) {
			StringFilterConditionBean stringFilterConditionBean = new StringFilterConditionBean();
			stringFilterConditionBean.setRubrikName("");
			stringFilterConditionBean
					.setCompValue(((StringFilterConditionDTO) filterCondtionDTO)
							.getCompValue());
			stringFilterConditionBean
					.setKeyValue(((StringFilterConditionDTO) filterCondtionDTO)
							.getKeyValueEnum());
			stringFilterConditionBean
					.setOperator(((StringFilterConditionDTO) filterCondtionDTO)
							.getOperatorEnum());
			filterSpecificBean = stringFilterConditionBean;
		} else if (filterCondtionDTO instanceof TimeBasedFilterConditionDTO) {
			TimeBasedFilterConditionBean timeBasedConditionBean = new TimeBasedFilterConditionBean();
			timeBasedConditionBean.setRubrikName("");
			timeBasedConditionBean
					.setConfirmCompValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getCConfirmCompValue());
			timeBasedConditionBean
					.setConfirmKeyValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getConfirmKeyValue());
			timeBasedConditionBean
					.setStartCompValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getCStartCompValue());
			timeBasedConditionBean
					.setStartKeyValue(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getStartKeyValue());
			timeBasedConditionBean
					.setConfirmOperator(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTBConfirmOperator());
			timeBasedConditionBean
					.setStartOperator(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTBStartOperator());
			timeBasedConditionBean
					.setTimeBehavior(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTimeBehavior());
			timeBasedConditionBean
					.setTimePeriod(((TimeBasedFilterConditionDTO) filterCondtionDTO)
							.getTimePeriod());
			filterSpecificBean = timeBasedConditionBean;
		} else if (filterCondtionDTO instanceof JunctorConditionForFilterTreeDTO) {
			JunctorConditionForFilterTreeDTO fcdto = (JunctorConditionForFilterTreeDTO) filterCondtionDTO;
			JunctorConditionForFilterTreeBean jcfftBean = new JunctorConditionForFilterTreeBean();
			jcfftBean.setJunctorConditionType(fcdto.getOperator());
			for (FilterConditionDTO innerFC : fcdto.getOperands()) {
				jcfftBean.addOperand(
						DTO2Bean(innerFC)		
				);
			}
			bean = jcfftBean;
		}

		bean.setFilterbedinungID(filterCondtionDTO.getIFilterConditionID());
		bean.setDescription(filterCondtionDTO.getCDesc());
		bean.setName(filterCondtionDTO.getCName());
		bean.setRubrikName(getRubrikNameForId(filterCondtionDTO.getIGroupRef())); // GUI-Group
		// =
		// Rubrik
		if (filterSpecificBean != null) {
			bean.setFilterSpecificBean(filterSpecificBean);
		}  else {
			// FCFFT haben keine speciefic beans!
			if(!(filterCondtionDTO instanceof JunctorConditionForFilterTreeDTO)) {
				throw new IllegalArgumentException(
						"Unrecognized FilterConditionDTO: " + filterCondtionDTO);
			}
		}
		return bean;
	}

	public void removeConfigurationBeanServiceListener(
			ConfigurationBeanServiceListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	public <T extends IConfigurationBean> T save(T bean)
			throws InconsistentConfigurationException {
		if (bean instanceof AlarmbearbeiterBean)
			return (T) saveAlarmbearbeiterBean((AlarmbearbeiterBean) bean);
		if (bean instanceof AlarmbearbeiterGruppenBean)
			return (T) saveAlarmbearbeiterGruppenBean((AlarmbearbeiterGruppenBean) bean);
		if (bean instanceof AlarmtopicBean)
			return (T) saveAlarmtopicBean((AlarmtopicBean) bean);
		if (bean instanceof FilterBean)
			return (T) saveFilterBean((FilterBean) bean);
		if (bean instanceof FilterbedingungBean)
			return (T) saveFilterbedingungBean((FilterbedingungBean) bean);
		throw new RuntimeException("Failed saving unsupported bean "
				+ bean.getClass());
	}

	private AlarmbearbeiterBean saveAlarmbearbeiterBean(AlarmbearbeiterBean bean) {
		boolean inserted = false;
		AlarmbearbeiterDTO dto = getDTO4Bean(bean);
		if (dto == null) {
			dto = new AlarmbearbeiterDTO();
			inserted = true;
		}
		dto.setActive(bean.isActive());
		dto.setConfirmCode(bean.getConfirmCode());
		dto.setEmail(bean.getEmail());
		dto.setMobilePhone(bean.getMobilePhone());
		dto.setUserName(bean.getName());
		dto.setPhone(bean.getPhone());
		dto.setPreferedAlarmType(bean.getPreferedAlarmType());
		dto.setStatusCode(bean.getStatusCode());
		dto.setGroupRef(getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.USER));

		dto = configurationService.saveAlarmbearbeiterDTO(dto);
		loadConfiguration();

		AlarmbearbeiterBean resultBean = alarmbearbeiterBeans.get(new Integer(
				dto.getUserId()));
		// if (inserted) {
		// bean.updateState(resultBean);
		// alarmbearbeiterBeans.put(new Integer(dto.getUserId()), bean);
		// resultBean = bean;
		// }

		insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private AlarmbearbeiterGruppenBean saveAlarmbearbeiterGruppenBean(
			AlarmbearbeiterGruppenBean bean) {
		boolean inserted = false;
		AlarmbearbeiterGruppenDTO dto = getDTO4Bean(bean);
		if (dto == null) {
			dto = new AlarmbearbeiterGruppenDTO();
			inserted = true;
		}
		dto.setActive(bean.isActive());
		dto.setMinGroupMember(bean.getMinGroupMember());
		dto.setTimeOutSec(bean.getTimeOutSec());
		dto.setUserGroupName(bean.getName());
		dto.setGroupRef(getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.USER_GROUP));

		List<User2UserGroupDTO> list = new LinkedList<User2UserGroupDTO>();
		List<User2GroupBean> users = bean.getUsers();
		int positionCount = 0;
		for (User2GroupBean bean2 : users) {
			User2UserGroupDTO mapDTO = getDTO4Bean(bean2, bean);
			mapDTO.setActive(bean2.isActive());
			mapDTO.setActiveReason(bean2.getActiveReason());
			mapDTO.setLastchange(bean2.getLastChange().getTime());
			mapDTO.setPosition(positionCount);
			positionCount++;
			list.add(mapDTO);
		}
		dto.setAlarmbearbeiter(list);

		try {
			dto = configurationService.saveAlarmbearbeiterGruppenDTO(dto);
		} catch (InconsistentConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadConfiguration();
		AlarmbearbeiterGruppenBean resultBean = alarmbearbeitergruppenBeans
				.get(new Integer(dto.getUserGroupId()));
		insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private User2UserGroupDTO getDTO4Bean(User2GroupBean bean2,
			AlarmbearbeiterGruppenBean parentBean) {
		User2UserGroupDTO map = null;
		for (User2UserGroupDTO potentialdto : entireConfiguration
				.getAllUser2UserGroupDTOs()) {
			if (potentialdto.getUser2UserGroupPK().getIUserGroupRef() == parentBean
					.getGroupID()
					&& potentialdto.getUser2UserGroupPK().getIUserRef() == bean2
							.getUserBean().getUserID()) {
				map = potentialdto;
			}
		}
		if (map == null) {
			map = new User2UserGroupDTO();
			User2UserGroupDTO_PK user2UserGroupDTO_PK = new User2UserGroupDTO_PK();
			user2UserGroupDTO_PK.setIUserGroupRef(parentBean.getGroupID());
			user2UserGroupDTO_PK.setIUserRef(bean2.getUserBean().getUserID());
			map.setUser2UserGroupPK(user2UserGroupDTO_PK);
		}
		return map;
	}

	private AlarmtopicBean saveAlarmtopicBean(AlarmtopicBean bean) {
		boolean inserted = false;
		TopicDTO dto = getDTO4Bean(bean);
		if (dto == null) {
			dto = new TopicDTO();
			inserted = true;
		}
		dto.setDescription(bean.getDescription());
		dto.setName(bean.getHumanReadableName());
		dto.setTopicName(bean.getTopicName());
		dto.setGroupRef(getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.TOPIC));

		dto = configurationService.saveTopicDTO(dto);
		loadConfiguration();
		AlarmtopicBean resultBean = alarmtopicBeans
				.get(new Integer(dto.getId()));
		insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private FilterBean saveFilterBean(FilterBean bean)
			throws InconsistentConfigurationException {
		boolean inserted = false;

		FilterDTO dto = findDTO4Bean(bean);
		if (dto == null) {
			dto = new FilterDTO();
			inserted = true;
		} else {
			for (FilterConditionDTO filterConditionDTO : dto.getFilterConditions()) {
				if (filterConditionDTO instanceof JunctorConditionForFilterTreeDTO) {
					filterbedingungBeans.remove(new Integer(filterConditionDTO.getIFilterConditionID()));
				}
			}
		}
		dto.setDefaultMessage(bean.getDefaultMessage());

		List<FilterConditionDTO> list = createFilterConditionDTOListForFilter(bean.getConditions());
		
		dto.setFilterConditions(list);
		dto.setName(bean.getName());
		dto.setIGroupRef(getRubrikIDForName(bean.getRubrikName(),
				RubrikTypeEnum.FILTER));

		dto = configurationService.saveFilterDTO(dto);
		loadConfiguration();
		FilterBean resultBean = filterBeans
				.get(new Integer(dto.getIFilterID()));
		
		insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private List<FilterConditionDTO> createFilterConditionDTOListForFilter(
			Collection<FilterbedingungBean> conditions) throws InconsistentConfigurationException {
		
		List<FilterConditionDTO> result = new LinkedList<FilterConditionDTO>();
		
		for (FilterbedingungBean filterbedingungBean : conditions) {
			
			FilterConditionDTO conditionDTO = findDTO4Bean(filterbedingungBean);
			
			if (filterbedingungBean instanceof JunctorConditionForFilterTreeBean) {
				JunctorConditionForFilterTreeBean junctorBean = (JunctorConditionForFilterTreeBean) filterbedingungBean;
				List<FilterConditionDTO> listForFilter = createFilterConditionDTOListForFilter(junctorBean.getOperands());
				
				if (conditionDTO == null) {
					JunctorConditionForFilterTreeDTO newDTO = new JunctorConditionForFilterTreeDTO();
					newDTO.setCName(junctorBean.getJunctorConditionType().toString());
					newDTO.setCDesc("");
					newDTO.setIGroupRef(getRubrikIDForName(junctorBean.getRubrikName(),
							RubrikTypeEnum.FILTER_COND));
					newDTO.setOperator(junctorBean.getJunctorConditionType());
					
					conditionDTO = newDTO;
				} else {
					filterbedingungBeans.remove(new Integer(conditionDTO.getIFilterConditionID()));
					for (FilterConditionDTO filterDTO : ((JunctorConditionForFilterTreeDTO)conditionDTO).getOperands()) {
						filterbedingungBeans.remove(new Integer(filterDTO.getIFilterConditionID()));
					}
				}
				((JunctorConditionForFilterTreeDTO)conditionDTO).setOperands(new HashSet<FilterConditionDTO>(listForFilter));
			}
			
			if (conditionDTO == null) {
				throw new InconsistentConfigurationException("No DTO found for " + filterbedingungBean.toString());
			}
			
			result.add(conditionDTO);
			
		}
		
		return result;
	}

//	private FilterConditionDTO findOrCreateFilterConditionDTO(FilterbedingungBean condBean) {
//		FilterConditionDTO found = findDTO4Bean(condBean);
//		if (condBean instanceof JunctorConditionForFilterTreeBean) {
//			if (found == null) {
//				found = createJunctorConditionForFilterTreeDTO((JunctorConditionForFilterTreeBean)condBean);
//			} else {
//				JunctorConditionForFilterTreeBean junctorBean = (JunctorConditionForFilterTreeBean) condBean;
//				JunctorConditionForFilterTreeDTO junctorDTO = (JunctorConditionForFilterTreeDTO) found;
//				
//				Set<FilterConditionDTO> operands;
//				junctorDTO.setOperands(operands);
//			}
//		}
//		return found;
//	}
//
//	JunctorConditionForFilterTreeDTO createJunctorConditionForFilterTreeDTO(
//			JunctorConditionForFilterTreeBean bean) {
//		JunctorConditionForFilterTreeDTO dto = new JunctorConditionForFilterTreeDTO();
//		
//		dto.setCName(bean.getJunctorConditionType().toString());
//		dto.setCDesc("");
//		dto.setIGroupRef(getRubrikIDForName(bean.getRubrikName(),
//				RubrikTypeEnum.FILTER_COND));
//		dto.setOperator(bean.getJunctorConditionType());
//		
//		Set<FilterConditionDTO> operands = new HashSet<FilterConditionDTO>();
//		for (FilterbedingungBean fcBean : bean.getOperands()) {
//			if( fcBean instanceof JunctorConditionForFilterTreeBean ) {
//				JunctorConditionForFilterTreeDTO innerDTO = createJunctorConditionForFilterTreeDTO((JunctorConditionForFilterTreeBean)fcBean);
//				operands.add(innerDTO);
//			} else {
//				operands.add(findDTO4Bean(fcBean));
//			}
//		}
//		dto.setOperands(operands);
//		return dto;
//	}

	@SuppressWarnings("unchecked")
	private FilterbedingungBean saveFilterbedingungBean(FilterbedingungBean bean) {
		boolean inserted = false;

		FilterConditionDTO filterConditionDTO = null;
		Class<? extends AbstractConfigurationBean> beanClass = bean
				.getFilterSpecificBean().getClass();
		if (JunctorConditionBean.class.equals(beanClass)) {
			JunctorConditionBean specificBean = (JunctorConditionBean) bean
					.getFilterSpecificBean();

			JunctorConditionDTO junctorConditionDTO = null;
			FilterConditionDTO dto4Bean = findDTO4Bean(bean);
			if (dto4Bean != null && (dto4Bean instanceof JunctorConditionDTO)) {
				junctorConditionDTO = (JunctorConditionDTO) dto4Bean;
			} else {
				junctorConditionDTO = new JunctorConditionDTO();
				inserted = true;
			}

			junctorConditionDTO.setFirstFilterConditionRef(specificBean
					.getFirstCondition().getFilterbedinungID());
			junctorConditionDTO.setSecondFilterConditionRef(specificBean
					.getSecondCondition().getFilterbedinungID());
			junctorConditionDTO.injectYourselfYourChildren(entireConfiguration);
			junctorConditionDTO.setJunctor(specificBean.getJunctor());

			// result to be saved with configurationService
			filterConditionDTO = junctorConditionDTO;
		} else if (PVFilterConditionBean.class.equals(beanClass)) {
			PVFilterConditionBean specificBean = (PVFilterConditionBean) bean
					.getFilterSpecificBean();

			ProcessVariableFilterConditionDTO pvFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = findDTO4Bean(bean);
			if (dto4Bean != null
					&& (dto4Bean instanceof ProcessVariableFilterConditionDTO)) {
				pvFilterConditionDTO = (ProcessVariableFilterConditionDTO) dto4Bean;
			} else {
				pvFilterConditionDTO = new ProcessVariableFilterConditionDTO();
				inserted = true;
			}
			pvFilterConditionDTO.setCPvChannelName(specificBean.getChannelName());
			pvFilterConditionDTO.setCCompValue(specificBean.getCompareValue());
			pvFilterConditionDTO.setPVOperator(specificBean.getOperator());
			pvFilterConditionDTO.setSuggestedPVType(specificBean
					.getSuggestedType());

			// result to be saved with configurationService
			filterConditionDTO = pvFilterConditionDTO;
		} else if (StringFilterConditionBean.class.equals(beanClass)) {
			StringFilterConditionBean specificBean = (StringFilterConditionBean) bean
					.getFilterSpecificBean();

			StringFilterConditionDTO stringFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = findDTO4Bean(bean);
			if (dto4Bean != null
					&& (dto4Bean instanceof StringFilterConditionDTO)) {
				stringFilterConditionDTO = (StringFilterConditionDTO) dto4Bean;
			} else {
				stringFilterConditionDTO = new StringFilterConditionDTO();
				inserted = true;
			}

			stringFilterConditionDTO.setCompValue(specificBean.getCompValue());
			stringFilterConditionDTO.setKeyValue(specificBean.getKeyValue());
			stringFilterConditionDTO
					.setOperatorEnum(specificBean.getOperator());

			// result to be saved with configurationService
			filterConditionDTO = stringFilterConditionDTO;
		} else if (StringArrayFilterConditionBean.class.equals(beanClass)) {
			StringArrayFilterConditionBean specificBean = (StringArrayFilterConditionBean) bean
					.getFilterSpecificBean();

			StringArrayFilterConditionDTO stringArrayFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = findDTO4Bean(bean);
			if (dto4Bean != null
					&& (dto4Bean instanceof StringArrayFilterConditionDTO)) {
				stringArrayFilterConditionDTO = (StringArrayFilterConditionDTO) dto4Bean;
				stringArrayFilterConditionDTO.setCompareValues(new LinkedList<StringArrayFilterConditionCompareValuesDTO>());
			} else {
				stringArrayFilterConditionDTO = new StringArrayFilterConditionDTO();
				inserted = true;
			}

			List<StringArrayFilterConditionCompareValuesDTO> oldCompareValues = new LinkedList<StringArrayFilterConditionCompareValuesDTO>();
			Collection<StringArrayFilterConditionCompareValuesDTO> allStringArrayCompareValues = entireConfiguration
					.getAllStringArrayCompareValues();
			for (StringArrayFilterConditionCompareValuesDTO stringArrayFilterConditionCompareValuesDTO : allStringArrayCompareValues) {
				if (stringArrayFilterConditionCompareValuesDTO.getFilterConditionRef() == bean.getFilterbedinungID()){
					oldCompareValues.add(stringArrayFilterConditionCompareValuesDTO);
				}
			}
			List<StringArrayFilterConditionCompareValuesDTO> currentCompareValues = new LinkedList<StringArrayFilterConditionCompareValuesDTO>();
			for (String compValue : specificBean.getCompareValues()) {
				currentCompareValues.add(getCompareValueDTO(oldCompareValues, bean, compValue));
			}
			
			
			stringArrayFilterConditionDTO.setCompareValues(currentCompareValues);

			stringArrayFilterConditionDTO.setKeyValue(specificBean
					.getKeyValue());
			stringArrayFilterConditionDTO.setOperatorEnum(specificBean
					.getOperator());

			// result to be saved with configurationService
			filterConditionDTO = stringArrayFilterConditionDTO;
		} else if (TimeBasedFilterConditionBean.class.equals(beanClass)) {
			TimeBasedFilterConditionBean specificBean = (TimeBasedFilterConditionBean) bean
					.getFilterSpecificBean();

			TimeBasedFilterConditionDTO timeBasedFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = findDTO4Bean(bean);
			if (dto4Bean != null
					&& (dto4Bean instanceof TimeBasedFilterConditionDTO)) {
				timeBasedFilterConditionDTO = (TimeBasedFilterConditionDTO) dto4Bean;
			} else {
				timeBasedFilterConditionDTO = new TimeBasedFilterConditionDTO();
				inserted = true;
			}

			timeBasedFilterConditionDTO.setCConfirmCompValue(specificBean
					.getConfirmCompValue());
			timeBasedFilterConditionDTO.setConfirmKeyValue(specificBean
					.getStartKeyValue());
			timeBasedFilterConditionDTO.setCStartCompValue(specificBean
					.getStartCompValue());

			timeBasedFilterConditionDTO.setTBConfirmOperator(specificBean.getConfirmOperator());
			timeBasedFilterConditionDTO.setTBStartOperator(specificBean.getStartOperator());

			timeBasedFilterConditionDTO.setTimeBehavior(specificBean
					.getTimeBehavior());
			timeBasedFilterConditionDTO.setTimePeriod(specificBean
					.getTimePeriod());

			// result to be saved with configurationService
			filterConditionDTO = timeBasedFilterConditionDTO;
		}

		filterConditionDTO.setIGroupRef(getRubrikIDForName(
				bean.getRubrikName(), RubrikTypeEnum.FILTER_COND));
		filterConditionDTO.setCName(bean.getName());
		filterConditionDTO.setCDesc(bean.getDescription());
		try {
			configurationService.saveDTO(filterConditionDTO);

		} catch (Throwable t) {
			// FIXME mz20080710 Handle throwable!
			logger.logFatalMessage(this, "failed to save filter condition", t);
		}
		loadConfiguration();

		FilterbedingungBean resultBean = filterbedingungBeans.get(new Integer(
				filterConditionDTO.getIFilterConditionID()));

		insertOrUpdateNotification(resultBean, inserted);
		return resultBean;
	}

	private StringArrayFilterConditionCompareValuesDTO getCompareValueDTO(
			List<StringArrayFilterConditionCompareValuesDTO> oldCompareValues,
			FilterbedingungBean bean, String compValue) {
		for (StringArrayFilterConditionCompareValuesDTO stringArrayFilterConditionCompareValuesDTO : oldCompareValues) {
			if (stringArrayFilterConditionCompareValuesDTO.getCompValue() == compValue){
				return stringArrayFilterConditionCompareValuesDTO;
			}
		}
		StringArrayFilterConditionCompareValuesDTO result = new StringArrayFilterConditionCompareValuesDTO();
		StringArrayFilterConditionCompareValuesDTO_PK pk = new StringArrayFilterConditionCompareValuesDTO_PK();
		pk.setCompValue(compValue);
		pk.setFilterConditionRef(bean.getFilterbedinungID());
		result.setPk(pk);
		
		return result;
	}

	private void insertOrUpdateNotification(IConfigurationBean bean,
			boolean inserted) {
		if (inserted)
			insertNotification(bean);
		else
			updateNotification(bean);
	}

	private void updateNotification(IConfigurationBean bean) {
		for (ConfigurationBeanServiceListener listener : listeners) {
			listener.onBeanUpdate(bean);
		}
	}

	private void insertNotification(IConfigurationBean bean) {
		for (ConfigurationBeanServiceListener listener : listeners) {
			listener.onBeanInsert(bean);
		}
	}

	/**
	 * @param condition
	 * @return
	 */
	private FilterConditionDTO findDTO4Bean(FilterbedingungBean bean) {
		FilterConditionDTO filterConditionDTO = null;
		for (FilterConditionDTO potentialdto : entireConfiguration
				.gibAlleFilterConditions()) {
			if (potentialdto.getIFilterConditionID() == bean
					.getFilterbedinungID()) {
				filterConditionDTO = potentialdto;
				break;
			}
		}
		return filterConditionDTO;
	}

	private TopicDTO getDTO4Bean(AlarmtopicBean bean) {
		TopicDTO dto = null;
		for (TopicDTO potentialdto : entireConfiguration.gibAlleAlarmtopics()) {
			if (potentialdto.getId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	private FilterDTO findDTO4Bean(FilterBean bean) {
		FilterDTO dto = null;
		for (FilterDTO potentialdto : entireConfiguration.gibAlleFilter()) {
			if (potentialdto.getIFilterID() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	private AlarmbearbeiterGruppenDTO getDTO4Bean(
			AlarmbearbeiterGruppenBean bean) {
		AlarmbearbeiterGruppenDTO dto = null;
		for (AlarmbearbeiterGruppenDTO potentialdto : entireConfiguration
				.gibAlleAlarmbearbeiterGruppen()) {
			if (potentialdto.getUserGroupId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	private AlarmbearbeiterDTO getDTO4Bean(AlarmbearbeiterBean bean) {
		AlarmbearbeiterDTO dto = null;
		for (AlarmbearbeiterDTO potentialdto : entireConfiguration
				.gibAlleAlarmbearbeiter()) {
			if (potentialdto.getUserId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		return dto;
	}

	public void delete(IConfigurationBean bean) {
		try {
			if (bean instanceof AlarmbearbeiterBean)
				deleteAlarmbearbeiterBean((AlarmbearbeiterBean) bean);
			if (bean instanceof AlarmbearbeiterGruppenBean)
				deleteAlarmbearbeiterGruppenBean((AlarmbearbeiterGruppenBean) bean);
			if (bean instanceof AlarmtopicBean)
				deleteAlarmtopicBean((AlarmtopicBean) bean);
			if (bean instanceof FilterBean)
				deleteFilterBean((FilterBean) bean);
			if (bean instanceof FilterbedingungBean)
				deleteFilterbedingungBean((FilterbedingungBean) bean);
			loadConfiguration();
			notifyDeleteListeners(bean);
		} catch (InconsistentConfigurationException e) {
			logger.logErrorMessage(this,
					"Could not Delete Entry. Entry-Type not recognized: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	private void deleteAlarmbearbeiterBean(AlarmbearbeiterBean bean)
			throws InconsistentConfigurationException {

		AlarmbearbeiterDTO dto = null;
		for (AlarmbearbeiterDTO potentialdto : entireConfiguration
				.gibAlleAlarmbearbeiter()) {
			if (potentialdto.getUserId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			configurationService.deleteAlarmbearbeiterDTO(dto);
			alarmbearbeiterBeans.remove(dto.getUserId());
			logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " + dto.getUserId()
							+ " " + dto.getUserName());
		}
	}

	private void deleteAlarmbearbeiterGruppenBean(
			AlarmbearbeiterGruppenBean bean)
			throws InconsistentConfigurationException {

		AlarmbearbeiterGruppenDTO dto = null;
		for (AlarmbearbeiterGruppenDTO potentialdto : entireConfiguration
				.gibAlleAlarmbearbeiterGruppen()) {
			if (potentialdto.getUserGroupId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			configurationService.deleteAlarmbearbeiterGruppenDTO(dto);
			alarmbearbeitergruppenBeans.remove(dto.getUserGroupId());
			logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() "
							+ dto.getUserGroupId() + " "
							+ dto.getUserGroupName());
		}
	}

	private void deleteAlarmtopicBean(AlarmtopicBean bean)
			throws InconsistentConfigurationException {
		TopicDTO dto = null;
		for (TopicDTO potentialdto : entireConfiguration.gibAlleAlarmtopics()) {
			if (potentialdto.getId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			configurationService.deleteAlarmtopicDTO(dto);
			alarmtopicBeans.remove(dto.getId());
			logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() " + dto.getId()
							+ " " + dto.getTopicName());
		}

	}

	private void deleteFilterBean(FilterBean bean)
			throws InconsistentConfigurationException {
		FilterDTO dto = null;
		for (FilterDTO potentialdto : entireConfiguration.gibAlleFilter()) {
			if (potentialdto.getIFilterID() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			try {
				configurationService.deleteFilterDTO(dto);
			} catch (StorageError e) {
				throw new InconsistentConfigurationException(
						"failed to deleteFilter()", e);
			} catch (StorageException e) {
				throw new InconsistentConfigurationException(
						"failed to deleteFilter()", e);
			}
			filterbedingungBeans.remove(dto.getIFilterID());
			logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() "
							+ dto.getIFilterID() + " " + dto.getName());
		}
	}

	private void deleteFilterbedingungBean(FilterbedingungBean bean)
			throws InconsistentConfigurationException {
		FilterConditionDTO dto = null;
		for (FilterConditionDTO potentialdto : entireConfiguration
				.gibAlleFilterConditions()) {
			if (potentialdto.getIFilterConditionID() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			configurationService.deleteFilterConditionDTO(dto);
			filterbedingungBeans.remove(dto.getIFilterConditionID());
			logger.logInfoMessage(this,
					"ConfigurationBeanServiceImpl.delete() "
							+ dto.getIFilterConditionID() + " "
							+ dto.getCName());
		}
	}

	private void notifyDeleteListeners(IConfigurationBean bean) {
		for (ConfigurationBeanServiceListener listener : listeners) {
			listener.onBeanDeleted(bean);
		}
	}

	public static void staticInject(Logger logger) {
		ConfigurationBeanServiceImpl.logger = logger;
	}
}
