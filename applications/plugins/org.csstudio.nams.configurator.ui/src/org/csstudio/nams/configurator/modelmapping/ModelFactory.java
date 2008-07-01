package org.csstudio.nams.configurator.modelmapping;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
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
@Deprecated
public class ModelFactory {
	@Deprecated
	private final LocalStoreConfigurationService localStore;
	@Deprecated
	private Configuration entireConfiguration;
	@Deprecated
	public ModelFactory(LocalStoreConfigurationService localStore){
		this.localStore = localStore;
		try {
			entireConfiguration = localStore.getEntireConfiguration();
		} catch (StorageError e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (InconsistentConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.csstudio.nams.configurator.modelmapping.Bubu#getAlarmBearbeiterBeans()
	 */
	@Deprecated
	public AlarmbearbeiterBean[] getAlarmBearbeiterBeans(){
		Collection<AlarmbearbeiterDTO> alarmBearbeiterDTOs = entireConfiguration.gibAlleAlarmbearbeiter();
		List<AlarmbearbeiterBean> beans = new LinkedList<AlarmbearbeiterBean>();
		for (AlarmbearbeiterDTO alarmbearbeitergruppe : alarmBearbeiterDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmbearbeiterBean[beans.size()]);
	}

	@Deprecated
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
	@Deprecated
	public AlarmbearbeiterGruppenBean[] getAlarmBearbeiterGruppenBeans() {
		Collection<AlarmbearbeiterGruppenDTO> alarmBearbeiterGruppenDTOs = entireConfiguration.gibAlleAlarmbearbeiterGruppen();
		List<AlarmbearbeiterGruppenBean> beans = new LinkedList<AlarmbearbeiterGruppenBean>();
		for (AlarmbearbeiterGruppenDTO alarmbearbeitergruppe : alarmBearbeiterGruppenDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmbearbeiterGruppenBean[beans.size()]);
	}
	
	@Deprecated
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
	@Deprecated
	public AlarmtopicBean[] getAlarmTopicBeans() {
		Collection<TopicDTO> alarmtopicsDTOs = entireConfiguration.gibAlleAlarmtopics();
		List<AlarmtopicBean> beans = new LinkedList<AlarmtopicBean>();
		for (TopicDTO alarmbearbeitergruppe : alarmtopicsDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmtopicBean[beans.size()]);
	}

	@Deprecated
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
	@Deprecated
	public FilterBean[] getFilterBeans() {
		Collection<FilterDTO> filterDTOs = entireConfiguration.gibAlleFilter();
		List<FilterBean> beans = new LinkedList<FilterBean>();
		for (FilterDTO filter : filterDTOs) {
			beans.add(DTO2Bean(filter));
		}
		return beans.toArray(new FilterBean[beans.size()]);
	}

	@Deprecated
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
	@Deprecated
	public FilterbedingungBean[] getFilterConditionBeans() {
		Collection<FilterConditionDTO> filterDTOs = entireConfiguration.gibAlleFilterConditions();
		List<FilterbedingungBean> beans = new LinkedList<FilterbedingungBean>();
		for (FilterConditionDTO filter : filterDTOs) {
			beans.add(DTO2Bean(filter));
		}
		return beans.toArray(new FilterbedingungBean[beans.size()]);
	}

	@Deprecated
	FilterbedingungBean DTO2Bean(FilterConditionDTO filter) {
		FilterbedingungBean bean = new FilterbedingungBean();
		bean.setFilterbedinungID(filter.getIFilterConditionID());
		bean.setDescription(filter.getCDesc());
		bean.setName(filter.getCName());
		return bean;
	}


}
