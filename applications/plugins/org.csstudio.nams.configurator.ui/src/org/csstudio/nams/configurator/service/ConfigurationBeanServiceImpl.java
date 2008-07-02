package org.csstudio.nams.configurator.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.configurator.beans.AbstractConfigurationBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.beans.filters.AddOnBean;
import org.csstudio.nams.configurator.beans.filters.JunctorConditionBean;
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
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.Logger;

public class ConfigurationBeanServiceImpl implements ConfigurationBeanService {

	private static Logger logger;
	private final LocalStoreConfigurationService configurationService;
	private Configuration entireConfiguration;
	private List<ConfigurationBeanServiceListener> listeners = new LinkedList<ConfigurationBeanServiceListener>();

	public ConfigurationBeanServiceImpl(LocalStoreConfigurationService localStore) {
		this.configurationService = localStore;
		loadConfiguration();
	}
	
	private void loadConfiguration() {
		try {
			entireConfiguration = configurationService.getEntireConfiguration();
			// TODO Folgendes Exception-Handling überdenken....
		} catch (StorageError e) {
			logger.logErrorMessage(this, "Could not load Eniter Configuration because of: " + e.getMessage());
			e.printStackTrace();
		} catch (StorageException e) {
			logger.logErrorMessage(this, "Could not load Eniter Configuration because of: " + e.getMessage());
			e.printStackTrace();
		} catch (InconsistentConfigurationException e) {
			logger.logErrorMessage(this, "Could not load Eniter Configuration because of: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void addConfigurationBeanServiceListener(
			ConfigurationBeanServiceListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterBeans()
	 */
	public AlarmbearbeiterBean[] getAlarmBearbeiterBeans(){
		Collection<AlarmbearbeiterDTO> alarmBearbeiterDTOs = entireConfiguration.gibAlleAlarmbearbeiter();
		List<AlarmbearbeiterBean> beans = new LinkedList<AlarmbearbeiterBean>();
		for (AlarmbearbeiterDTO alarmbearbeitergruppe : alarmBearbeiterDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmbearbeiterBean[beans.size()]);
	}

	AlarmbearbeiterBean DTO2Bean(
			AlarmbearbeiterDTO alarmbearbeiter) {
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
		
		//FIXME for testing only
		if (new Random().nextBoolean()) {
			bean.setRubrikName("Random Test Rubrik");
		}
		if (bean.getDisplayName().startsWith("Alexander")) {
			bean.setRubrikName("Alex");
		}
		
		
		return bean;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterGruppenBeans()
	 */
	public AlarmbearbeiterGruppenBean[] getAlarmBearbeiterGruppenBeans() {
		Collection<AlarmbearbeiterGruppenDTO> alarmBearbeiterGruppenDTOs = entireConfiguration.gibAlleAlarmbearbeiterGruppen();
		List<AlarmbearbeiterGruppenBean> beans = new LinkedList<AlarmbearbeiterGruppenBean>();
		for (AlarmbearbeiterGruppenDTO alarmbearbeitergruppe : alarmBearbeiterGruppenDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmbearbeiterGruppenBean[beans.size()]);
	}
	
	AlarmbearbeiterGruppenBean DTO2Bean(AlarmbearbeiterGruppenDTO dto){
		AlarmbearbeiterGruppenBean bean = new AlarmbearbeiterGruppenBean();
		bean.setActive(dto.isActive());
		bean.setGroupID(dto.getUserGroupId());
		bean.setMinGroupMember(dto.getMinGroupMember());
		bean.setName(dto.getUserGroupName());
		bean.setTimeOutSec(dto.getTimeOutSec());
		return bean;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmTopicBeans()
	 */
	public AlarmtopicBean[] getAlarmTopicBeans() {
		Collection<TopicDTO> alarmtopicsDTOs = entireConfiguration.gibAlleAlarmtopics();
		List<AlarmtopicBean> beans = new LinkedList<AlarmtopicBean>();
		for (TopicDTO alarmbearbeitergruppe : alarmtopicsDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmtopicBean[beans.size()]);
	}

	AlarmtopicBean DTO2Bean(TopicDTO dto) {
		AlarmtopicBean bean = new AlarmtopicBean();
		bean.setDescription(dto.getDescription());
		bean.setHumanReadableName(dto.getName());
		bean.setTopicID(dto.getId());
		bean.setTopicName(dto.getTopicName());
		return bean;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterBeans()
	 */
	public FilterBean[] getFilterBeans() {
		Collection<FilterDTO> filterDTOs = entireConfiguration.gibAlleFilter();
		List<FilterBean> beans = new LinkedList<FilterBean>();
		for (FilterDTO filter : filterDTOs) {
			beans.add(DTO2Bean(filter));
		}
		return beans.toArray(new FilterBean[beans.size()]);
	}

	FilterBean DTO2Bean(FilterDTO filter) {
		FilterBean bean = new FilterBean();
		bean.setDefaultMessage(filter.getDefaultMessage());
		bean.setFilterID(filter.getIFilterID());
		bean.setName(filter.getName());
		return bean;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getFilterConditionBeans()
	 */
	public FilterbedingungBean[] getFilterConditionBeans() {
		Collection<FilterConditionDTO> filterDTOs = entireConfiguration.gibAlleFilterConditions();
		List<FilterbedingungBean> beans = new LinkedList<FilterbedingungBean>();
		for (FilterConditionDTO filter : filterDTOs) {
			beans.add(DTO2Bean(filter));
		}
		return beans.toArray(new FilterbedingungBean[beans.size()]);
	}

	private FilterbedingungBean DTO2Bean(FilterConditionDTO filter) {
		FilterbedingungBean bean = new FilterbedingungBean();
		bean.setFilterbedinungID(filter.getIFilterConditionID());
		bean.setDescription(filter.getCDesc());
		bean.setName(filter.getCName());
		AddOnBean filterSpecificBean = null;
		if (filter instanceof JunctorConditionDTO) {
			JunctorConditionBean junctorConditionBean = new JunctorConditionBean();
			junctorConditionBean
					.setFirstCondition(DTO2Bean(((JunctorConditionDTO) filter)
							.getFirstFilterCondition()));
			junctorConditionBean.setJunctor(((JunctorConditionDTO) filter)
					.getJunctor());
			junctorConditionBean.setRubrikName(""); // RubrikName is set by the
													// main Bean.
			junctorConditionBean
					.setSecondCondition(DTO2Bean(((JunctorConditionDTO) filter)
							.getSecondFilterCondition()));
			filterSpecificBean = junctorConditionBean;
		} else if (filter instanceof ProcessVariableFilterConditionDTO) {
			PVFilterConditionBean filterbedingungBean = new PVFilterConditionBean();
			filterbedingungBean.setRubrikName("");
			filterbedingungBean
					.setChannelName(((ProcessVariableFilterConditionDTO) filter)
							.getCName());
			filterbedingungBean
					.setCompareValue(((ProcessVariableFilterConditionDTO) filter)
							.getCCompValue());
			filterbedingungBean
					.setOperator(((ProcessVariableFilterConditionDTO) filter)
							.getPVOperator());
			filterbedingungBean
					.setSuggestedType(((ProcessVariableFilterConditionDTO) filter)
							.getSuggestedPVType());
			filterSpecificBean = filterbedingungBean;
		} else if (filter instanceof StringArrayFilterConditionDTO) {
			StringArrayFilterConditionBean stringArrayFilterConditionBean = new StringArrayFilterConditionBean();
			stringArrayFilterConditionBean.setRubrikName("");
			stringArrayFilterConditionBean
					.setCompareValues(((StringArrayFilterConditionDTO) filter)
							.getCompareValueList());
			stringArrayFilterConditionBean
					.setKeyValue(((StringArrayFilterConditionDTO) filter)
							.getKeyValueEnum());
			stringArrayFilterConditionBean
					.setOperator(((StringArrayFilterConditionDTO) filter)
							.getOperatorEnum());
			filterSpecificBean = stringArrayFilterConditionBean;
		} else if (filter instanceof StringFilterConditionDTO) {
			StringFilterConditionBean stringFilterConditionBean = new StringFilterConditionBean();
			stringFilterConditionBean.setRubrikName("");
			stringFilterConditionBean
					.setCompValue(((StringFilterConditionDTO) filter)
							.getCompValue());
			stringFilterConditionBean
					.setKeyValue(((StringFilterConditionDTO) filter)
							.getKeyValue());
			stringFilterConditionBean
					.setOperator(StringRegelOperator
							.valueOf(((StringFilterConditionDTO) filter)
									.getOperator()));
			filterSpecificBean = stringFilterConditionBean;
		} else if (filter instanceof TimeBasedFilterConditionDTO) {
			TimeBasedFilterConditionBean timeBasedConditionBean = new TimeBasedFilterConditionBean();
			timeBasedConditionBean.setRubrikName("");
			timeBasedConditionBean
					.setCConfirmCompValue(((TimeBasedFilterConditionDTO) filter)
							.getCConfirmCompValue());
			timeBasedConditionBean
					.setCConfirmKeyValue(((TimeBasedFilterConditionDTO) filter)
							.getConfirmKeyValue());
			timeBasedConditionBean
					.setCStartCompValue(((TimeBasedFilterConditionDTO) filter)
							.getCStartCompValue());
			timeBasedConditionBean
					.setCStartKeyValue(((TimeBasedFilterConditionDTO) filter)
							.getCStartKeyValue());
			timeBasedConditionBean
					.setSConfirmOperator(((TimeBasedFilterConditionDTO) filter)
							.getTBConfirmOperator());
			timeBasedConditionBean
					.setSStartOperator(((TimeBasedFilterConditionDTO) filter)
							.getTBStartOperator());
			timeBasedConditionBean
					.setSTimeBehavior(((TimeBasedFilterConditionDTO) filter)
							.getTimeBehavior());
			timeBasedConditionBean
					.setSTimePeriod(((TimeBasedFilterConditionDTO) filter)
							.getTimePeriod());
			filterSpecificBean = timeBasedConditionBean;
		}
		
		
		if (filterSpecificBean != null) {
			bean.setFilterSpecificBean(filterSpecificBean);
		} else {
			throw new IllegalArgumentException("Unrecognized FilterConditionDTO: " + filter);
		}
		return bean;
	}
	
	public void removeConfigurationBeanServiceListener(
			ConfigurationBeanServiceListener listener) {
		listeners.remove(listener);
	}

	@SuppressWarnings("unchecked")
	public <T extends IConfigurationBean> T save(T bean) {
		if (bean instanceof AlarmbearbeiterBean)
			return (T)saveAlarmbearbeiterBean((AlarmbearbeiterBean) bean);
		if (bean instanceof AlarmbearbeiterGruppenBean)
			return (T)saveAlarmbearbeiterGruppenBean((AlarmbearbeiterGruppenBean)bean); 
		if (bean instanceof AlarmtopicBean)
			return (T)saveAlarmtopicBean((AlarmtopicBean)bean);
		if (bean instanceof FilterBean)
			return (T)saveFilterBean((FilterBean)bean);
		if (bean instanceof FilterbedingungBean)
			return (T)saveFilterbedingungBean((FilterbedingungBean)bean);
		throw new RuntimeException("Failed saving unsupported bean " + bean.getClass());
	}
	
	private AlarmbearbeiterBean saveAlarmbearbeiterBean(AlarmbearbeiterBean bean) {
		AlarmbearbeiterDTO dto = null;
		for (AlarmbearbeiterDTO potentialdto : entireConfiguration.gibAlleAlarmbearbeiter()) {
			if (potentialdto.getUserId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto == null) {
			dto = new AlarmbearbeiterDTO();
		}
		dto.setActive(bean.isActive());
		dto.setConfirmCode(bean.getConfirmCode());
		dto.setEmail(bean.getEmail());
		dto.setMobilePhone(bean.getMobilePhone());
		dto.setUserName(bean.getName());
		dto.setPhone(bean.getPhone());
		dto.setPreferedAlarmType(bean.getPreferedAlarmType());
		dto.setStatusCode(bean.getStatusCode());

		dto = configurationService.saveAlarmbearbeiterDTO(dto);
		AlarmbearbeiterBean resultBean = DTO2Bean(dto);

		loadConfiguration(); 
		
		if (bean.getUserID() != -1) {
			for (ConfigurationBeanServiceListener listener : listeners) {
				listener.onBeanUpdate(resultBean);
			}
		} else {
			for (ConfigurationBeanServiceListener listener : listeners) {
				listener.onBeanInsert(resultBean);
			}
		}
		return resultBean;
	}

	public AlarmbearbeiterGruppenBean saveAlarmbearbeiterGruppenBean(AlarmbearbeiterGruppenBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	public AlarmtopicBean saveAlarmtopicBean(AlarmtopicBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	public FilterBean saveFilterBean(FilterBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public FilterbedingungBean saveFilterbedingungBean(FilterbedingungBean bean) {
		
		FilterConditionDTO filterConditionDTO = null;
		Class<? extends AbstractConfigurationBean> beanClass = bean.getFilterSpecificBean().getClass();
		if (JunctorConditionBean.class.equals(beanClass)) {
			JunctorConditionBean specificBean = (JunctorConditionBean) bean.getFilterSpecificBean();
			
			JunctorConditionDTO junctorConditionDTO = null;
			FilterConditionDTO dto4Bean = getDTO4Bean(bean);
			if (dto4Bean != null && (dto4Bean instanceof JunctorConditionDTO)) {
				junctorConditionDTO = (JunctorConditionDTO) dto4Bean;
			} else {
				junctorConditionDTO = new JunctorConditionDTO();
			}
				
			junctorConditionDTO.setFirstFilterCondition(getDTO4Bean(specificBean.getFirstCondition()));
			junctorConditionDTO.setIFilterConditionID(bean.getFilterbedinungID());
			junctorConditionDTO.setJunctor(specificBean.getJunctor());
			junctorConditionDTO.setSecondFilterCondition(getDTO4Bean(specificBean.getSecondCondition()));
			
			// result to be saved with configurationService
			filterConditionDTO = junctorConditionDTO;
		}
		else if (PVFilterConditionBean.class.equals(beanClass)) {
			PVFilterConditionBean specificBean = (PVFilterConditionBean) bean.getFilterSpecificBean();
			
			ProcessVariableFilterConditionDTO pvFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = getDTO4Bean(bean);
			if (dto4Bean != null && (dto4Bean instanceof ProcessVariableFilterConditionDTO)) {
				pvFilterConditionDTO = (ProcessVariableFilterConditionDTO) dto4Bean;
			} else {
				pvFilterConditionDTO = new ProcessVariableFilterConditionDTO();
			}
			
			pvFilterConditionDTO.setCCompValue(specificBean.getCompareValue());
			pvFilterConditionDTO.setPVOperator(specificBean.getOperator());
			pvFilterConditionDTO.setSuggestedPVType(specificBean.getSuggestedType());
			
			// result to be saved with configurationService
			filterConditionDTO = pvFilterConditionDTO;
		}
		else if (StringFilterConditionBean.class.equals(beanClass)) {
			StringFilterConditionBean specificBean = (StringFilterConditionBean) bean.getFilterSpecificBean();
			
			StringFilterConditionDTO stringFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = getDTO4Bean(bean);
			if (dto4Bean != null && (dto4Bean instanceof StringFilterConditionDTO)) {
				stringFilterConditionDTO = (StringFilterConditionDTO) dto4Bean;
			} else {
				stringFilterConditionDTO = new StringFilterConditionDTO();
			}
			
			stringFilterConditionDTO.setCompValue(specificBean.getCompValue());
			stringFilterConditionDTO.setKeyValue(specificBean.getKeyValue());
			stringFilterConditionDTO.setOperator(Short.parseShort(specificBean.getOperator().name()));
			stringFilterConditionDTO.setOperatorEnum(specificBean.getOperator());
			
			// result to be saved with configurationService
			filterConditionDTO = stringFilterConditionDTO;
		}
		else if (StringArrayFilterConditionBean.class.equals(beanClass)) {
			StringArrayFilterConditionBean specificBean = (StringArrayFilterConditionBean) bean.getFilterSpecificBean();
			
			StringArrayFilterConditionDTO stringArrayFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = getDTO4Bean(bean);
			if (dto4Bean != null && (dto4Bean instanceof StringArrayFilterConditionDTO)) {
				stringArrayFilterConditionDTO = (StringArrayFilterConditionDTO) dto4Bean;
			} else {
				stringArrayFilterConditionDTO = new StringArrayFilterConditionDTO();
			}
			
			stringArrayFilterConditionDTO.setCompareValues(specificBean.getCompareValues());
			stringArrayFilterConditionDTO.setKeyValue(specificBean.getKeyValue().name());
			stringArrayFilterConditionDTO.setOperator(Short.parseShort(specificBean.getOperator().name()));
		
			// result to be saved with configurationService
			filterConditionDTO = stringArrayFilterConditionDTO;
		}
		else if (TimeBasedFilterConditionBean.class.equals(beanClass)) {
			TimeBasedFilterConditionBean specificBean = (TimeBasedFilterConditionBean) bean.getFilterSpecificBean();
			
			TimeBasedFilterConditionDTO timeBasedFilterConditionDTO = null;
			FilterConditionDTO dto4Bean = getDTO4Bean(bean);
			if (dto4Bean != null && (dto4Bean instanceof TimeBasedFilterConditionDTO)) {
				timeBasedFilterConditionDTO = (TimeBasedFilterConditionDTO) dto4Bean;
			} else {
				timeBasedFilterConditionDTO = new TimeBasedFilterConditionDTO();
			}
			
			timeBasedFilterConditionDTO.setCConfirmCompValue(specificBean.getCConfirmCompValue());
			timeBasedFilterConditionDTO.setCConfirmKeyValue(specificBean.getCStartKeyValue());
			timeBasedFilterConditionDTO.setCStartCompValue(specificBean.getCStartCompValue());
			
			// TODO mw: Check the Operators in TimeBasedFilterCondition. get(StringRegelOperator) but set(Operator) 
			// => only ordinal 1 and 2 match, other differ.
			timeBasedFilterConditionDTO.setTBConfirmOperator(Operator.valueOf(specificBean.getSConfirmOperator().name()));
			timeBasedFilterConditionDTO.setTBStartOperator(Operator.valueOf(specificBean.getSStartOperator().name()));

			timeBasedFilterConditionDTO.setTimeBehavior(specificBean.getSTimeBehavior());
			timeBasedFilterConditionDTO.setTimePeriod(specificBean.getSTimePeriod());
			
			// result to be saved with configurationService
			filterConditionDTO = timeBasedFilterConditionDTO;
		}
		
		filterConditionDTO = configurationService.saveFilterCondtionDTO(filterConditionDTO);
		FilterbedingungBean resultBean = DTO2Bean(filterConditionDTO);

		loadConfiguration(); 
		
		if (bean.getFilterbedinungID() != -1) {
			for (ConfigurationBeanServiceListener listener : listeners) {
				listener.onBeanUpdate(bean);
			}
		} else {
			for (ConfigurationBeanServiceListener listener : listeners) {
				listener.onBeanInsert(bean);
			}
		}
		return resultBean;
	}

	/**
	 * Use only for JunctorConditions
	 * @param condition
	 * @return
	 */
	private FilterConditionDTO getDTO4Bean(FilterbedingungBean bean) {
		FilterConditionDTO filterConditionDTO = null;
		for (FilterConditionDTO potentialdto : entireConfiguration.gibAlleFilterConditions()) {
			if (potentialdto.getIFilterConditionID() == bean.getFilterbedinungID()) {
				filterConditionDTO = (JunctorConditionDTO) potentialdto;
				break;
			}
		}
		return filterConditionDTO;
	}

	public void delete(IConfigurationBean bean) {
		try {
		if (bean instanceof AlarmbearbeiterBean)
			deleteAlarmbearbeiterBean((AlarmbearbeiterBean) bean);
		if (bean instanceof AlarmbearbeiterGruppenBean)
			deleteAlarmbearbeiterGruppenBean((AlarmbearbeiterGruppenBean)bean); 
		if (bean instanceof AlarmtopicBean)
			deleteAlarmtopicBean((AlarmtopicBean)bean);
		if (bean instanceof FilterBean)
			deleteFilterBean((FilterBean)bean);
		if (bean instanceof FilterbedingungBean)
			deleteFilterbedingungBean((FilterbedingungBean)bean);
		loadConfiguration();
		notifyDeleteListeners(bean);
		} catch (InconsistentConfigurationException e) {
			logger.logErrorMessage(this, "Could not Delete Entry. Entry-Type not recognized: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void deleteAlarmbearbeiterBean(AlarmbearbeiterBean bean) throws InconsistentConfigurationException {

		AlarmbearbeiterDTO dto = null;
		for (AlarmbearbeiterDTO potentialdto : entireConfiguration.gibAlleAlarmbearbeiter()) {
			if (potentialdto.getUserId() == bean.getID()) {
				dto = potentialdto;
				break;
			}
		}
		if (dto != null) {
			configurationService.deleteAlarmbearbeiterDTO(dto);
			
			logger.logInfoMessage(this, "ConfigurationBeanServiceImpl.delete() " + dto.getUserId() + " " + dto.getUserName());
		}		
	}

	private void deleteAlarmbearbeiterGruppenBean(
			AlarmbearbeiterGruppenBean bean) {
		// TODO Auto-generated method stub
		
	}

	private void deleteAlarmtopicBean(AlarmtopicBean bean) {
		// TODO Auto-generated method stub
		
	}

	private void deleteFilterBean(FilterBean bean) {
		// TODO Auto-generated method stub
		
	}

	private void deleteFilterbedingungBean(FilterbedingungBean bean) {
		// TODO Auto-generated method stub
		
	}
	private void notifyDeleteListeners(IConfigurationBean bean){
		for (ConfigurationBeanServiceListener listener : listeners) {
			listener.onBeanDeleted(bean);
		}
	}
	
	public static void staticInject(Logger logger) {
		ConfigurationBeanServiceImpl.logger = logger;
	}
}
