package org.csstudio.nams.configurator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.treeviewer.model.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.treeviewer.model.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;

public class ModelFactory {
	
	private final LocalStoreConfigurationService localStore;
	private Configuration entireConfiguration;

	public ModelFactory(LocalStoreConfigurationService localStore){
		this.localStore = localStore;
		try {
			entireConfiguration = localStore.getEntireConfiguration();
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
	
	public AlarmbearbeiterBean[] getAlarmBearbeiterBeans(){
		Collection<AlarmbearbeiterDTO> alarmBearbeiterDTOs = entireConfiguration.gibAlleAlarmbearbeiter();
		List<AlarmbearbeiterBean> beans = new LinkedList<AlarmbearbeiterBean>();
		for (AlarmbearbeiterDTO alarmbearbeitergruppe : alarmBearbeiterDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmbearbeiterBean[beans.size()]);
	}

	private AlarmbearbeiterBean DTO2Bean(
			AlarmbearbeiterDTO alarmbearbeiter) {
		AlarmbearbeiterBean bean = new AlarmbearbeiterBean();
		bean.setActive(alarmbearbeiter.isActive());
		bean.setConfirmCode(alarmbearbeiter.getConfirmCode());
		bean.setEmail(alarmbearbeiter.getEmail());
		bean.setMobilePhone(alarmbearbeiter.getMobilePhone());
		bean.setName(alarmbearbeiter.getUserName());
		bean.setPhone(alarmbearbeiter.getPhone());
		//FIXME no idea!?
		//		bean.setPreferedAlarmType(alarmbearbeiter.get);
		bean.setStatusCode(alarmbearbeiter.getStatusCode());
		bean.setUserID(alarmbearbeiter.getUserId());
		return bean;
	}

	public AlarmbearbeiterGruppenBean[] getAlarmBearbeiterGruppenBeans() {
		Collection<AlarmbearbeiterGruppenDTO> alarmBearbeiterGruppenDTOs = entireConfiguration.gibAlleAlarmbearbeiterGruppen();
		List<AlarmbearbeiterGruppenBean> beans = new LinkedList<AlarmbearbeiterGruppenBean>();
		for (AlarmbearbeiterGruppenDTO alarmbearbeitergruppe : alarmBearbeiterGruppenDTOs) {
			beans.add(DTO2Bean(alarmbearbeitergruppe));
		}
		return beans.toArray(new AlarmbearbeiterGruppenBean[beans.size()]);
	}
	
	private AlarmbearbeiterGruppenBean DTO2Bean(AlarmbearbeiterGruppenDTO dto){
		AlarmbearbeiterGruppenBean bean = new AlarmbearbeiterGruppenBean();
		bean.setActive(dto.isActive());
		bean.setGroupID(dto.getUserGroupId());
		bean.setMinGroupMember(dto.getMinGroupMember());
		bean.setName(dto.getUserGroupName());
		bean.setTimeOutSec(dto.getTimeOutSec());
		return bean;
	}
}
