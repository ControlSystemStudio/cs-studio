package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("8")
public class AlarmbearbeiterGruppenEmailFilterActionDTO extends
		AbstractAlarmbearbeiterGruppenFilterActionDTO {
	public AlarmbearbeiterGruppenEmailFilterActionDTO(){
		filterActionType = AlarmbearbeitergruppenFilterActionType.EMAIL;
	}

}
