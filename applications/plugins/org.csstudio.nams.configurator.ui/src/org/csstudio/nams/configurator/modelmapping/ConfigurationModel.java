package org.csstudio.nams.configurator.modelmapping;

import java.util.Collection;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;

public class ConfigurationModel implements IConfigurationModel {

	private static LocalStoreConfigurationService localStore;
	private static ModelFactory modelFactory;

	public Collection<String> getSortgroupNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public IConfigurationBean save(IConfigurationBean bean) {
		if (bean instanceof AlarmbearbeiterBean) {
			return save((AlarmbearbeiterBean) bean);
		}
		throw new IllegalArgumentException("Failed saving unsupported bean.");
	}

	private IConfigurationBean save(AlarmbearbeiterBean bean) {
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
				if (alarmbearbeiterDTO.getUserId() == bean.getUserID())
				{
					dto = alarmbearbeiterDTO;
					dto.setUserId(bean.getUserID());
					break;
				}
		}
		if (dto == null){
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

	public static void staticInject(LocalStoreConfigurationService localStore, ModelFactory modelFactory) {
		ConfigurationModel.localStore = localStore;
		ConfigurationModel.modelFactory = modelFactory;
	}

}
