package org.csstudio.nams.configurator.modelmapping;

import java.util.Collection;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.controller.ConfigurationBeanController;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;

public class ConfigurationModel implements IConfigurationModel {

	private static LocalStoreConfigurationService localStore;
	private static ConfigurationBeanController controller;

	@SuppressWarnings("unchecked")
	public <E extends IConfigurationBean> E save(E bean) {
		E result = null;
		if (bean instanceof AlarmbearbeiterBean) {
			result = (E) save((AlarmbearbeiterBean) bean);
		} else if (bean instanceof AlarmbearbeiterGruppenBean) {
			result = (E) save((AlarmbearbeiterGruppenBean) bean);
		} else if (bean instanceof AlarmtopicBean) {
			result = (E) save((AlarmtopicBean) bean);
		}
		if (result == null) {
			throw new IllegalArgumentException(
					"Failed saving unsupported bean.");
		} else {
			controller
					.fireChangeOn((Class<IConfigurationBean>) bean.getClass());
			return result;
		}
	}

	private AlarmtopicBean save(AlarmtopicBean bean) {
		Collection<TopicDTO> dtos = null;
		try {
			dtos = localStore.getEntireConfiguration().gibAlleAlarmtopics();
		} catch (StorageError e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (InconsistentConfiguration e) {
			e.printStackTrace();
		}
		TopicDTO dto = null;
		for (TopicDTO potentialDTO : dtos) {
			if (potentialDTO.getId() == bean.getTopicID()) {
				dto = potentialDTO;
				// TODO may be necessary see save(AlarmbearbeiterBean
				// dto.se(bean.getGroupID());
				break;
			}
		}
		if (dto == null) {
			dto = new TopicDTO();
		}
		dto.setDescription(bean.getDescription());
		// TODO do groupref stuff
		// dto.setGroupRef(groupRef);
		dto.setName(bean.getHumanReadableName());
		dto.setTopicName(bean.getTopicName());
		dto = localStore.saveTopicDTO(dto);
		bean.setTopicID(dto.getId());
		return bean;
	}

	private AlarmbearbeiterGruppenBean save(AlarmbearbeiterGruppenBean bean) {
		Collection<AlarmbearbeiterGruppenDTO> dtos = null;
		try {
			dtos = localStore.getEntireConfiguration()
					.gibAlleAlarmbearbeiterGruppen();
		} catch (StorageError e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (InconsistentConfiguration e) {
			e.printStackTrace();
		}
		AlarmbearbeiterGruppenDTO dto = null;
		for (AlarmbearbeiterGruppenDTO potentialDTO : dtos) {
			if (potentialDTO.getUserGroupId() == bean.getGroupID()) {
				dto = potentialDTO;
				dto.setUserGroupId(bean.getGroupID());
				break;
			}
		}
		if (dto == null) {
			dto = new AlarmbearbeiterGruppenDTO();
		}
		dto.setActive(bean.isActive());
		// TODO GroupRef is missing in bean
		// dto.setGroupRef(null);
		dto.setMinGroupMember(bean.getMinGroupMember());
		dto.setTimeOutSec(bean.getTimeOutSec());
		dto.setUserGroupName(bean.getName());
		dto = localStore.saveAlarmbearbeiterGruppenDTO(dto);
		bean.setGroupID(dto.getUserGroupId());
		return bean;
	}

	private AlarmbearbeiterBean save(AlarmbearbeiterBean bean) {
		Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter = null;
		try {
			gibAlleAlarmbearbeiter = localStore.getEntireConfiguration()
					.gibAlleAlarmbearbeiter();
		} catch (StorageError e) {
			e.printStackTrace();
		} catch (StorageException e) {
			e.printStackTrace();
		} catch (InconsistentConfiguration e) {
			e.printStackTrace();
		}
		AlarmbearbeiterDTO dto = null;
		for (AlarmbearbeiterDTO alarmbearbeiterDTO : gibAlleAlarmbearbeiter) {
			if (alarmbearbeiterDTO.getUserId() == bean.getUserID()) {
				dto = alarmbearbeiterDTO;
				dto.setUserId(bean.getUserID());
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
		dto = localStore.saveAlarmbearbeiterDTO(dto);
		bean.setUserID(dto.getUserId());
		return bean;
	}

	public static void staticInject(LocalStoreConfigurationService localStore,
			ConfigurationBeanController controller) {
		ConfigurationModel.localStore = localStore;
		ConfigurationModel.controller = controller;

	}

}
