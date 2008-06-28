package org.csstudio.nams.configurator.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

public class ConfigurationBeanServiceImpl implements ConfigurationBeanService {

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
		} catch (StorageError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InconsistentConfiguration e) {
			// TODO Auto-generated catch block
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

	FilterbedingungBean DTO2Bean(FilterConditionDTO filter) {
		FilterbedingungBean bean = new FilterbedingungBean();
		bean.setFilterbedinungID(filter.getIFilterConditionID());
		bean.setDescription(filter.getCDesc());
		bean.setName(filter.getCName());
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
		return null;
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
				listener.onAlarmbearbeiterBeanUpdate(bean);
			}
		} else {
			for (ConfigurationBeanServiceListener listener : listeners) {
				listener.onAlarmbearbeiterBeanInsert(bean);
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

	public FilterbedingungBean saveFilterbedingungBean(FilterbedingungBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

}
