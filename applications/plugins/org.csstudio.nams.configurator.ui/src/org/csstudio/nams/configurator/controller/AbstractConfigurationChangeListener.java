package org.csstudio.nams.configurator.controller;

import org.csstudio.nams.configurator.beans.AlarmbearbeiterBean;
import org.csstudio.nams.configurator.beans.AlarmbearbeiterGruppenBean;
import org.csstudio.nams.configurator.beans.AlarmtopicBean;
import org.csstudio.nams.configurator.beans.FilterBean;
import org.csstudio.nams.configurator.beans.FilterbedingungBean;
import org.csstudio.nams.configurator.modelmapping.IConfigurationBean;

public class AbstractConfigurationChangeListener implements IConfigurationChangeListener {

	public void update(Class<IConfigurationBean> cls) {
		if (cls.equals(AlarmbearbeiterGruppenBean.class)){
			updateAlarmBearbeiterGruppe();
		}
		if (cls.equals(FilterbedingungBean.class)){
			updateFilterBedingung();
		}
		if (cls.equals(FilterBean.class)){
			updateFilter();
		}
		if (cls.equals(AlarmbearbeiterBean.class)){
			updateAlarmBearbeiter();
		}
		if (cls.equals(AlarmtopicBean.class)){
			updateAlarmTopic();
		}
	}

	public void updateAll() {
		updateAlarmBearbeiterGruppe();
		updateFilterBedingung();
		updateFilter();
		updateAlarmBearbeiter();
		updateAlarmTopic();
		
	}

	protected void updateAlarmBearbeiterGruppe(){
		
	}
	
	protected void updateFilterBedingung(){
		
	}
	
	protected void updateFilter(){
		
	}
	
	protected void updateAlarmBearbeiter(){
		
	}
	
	protected void updateAlarmTopic(){
		
	}
}