package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("2")
public class AlarmbearbeiterGruppenSMSFilterActionDTO extends
		AbstractAlarmbearbeiterGruppenFilterActionDTO {

	public AlarmbearbeiterGruppenSMSFilterActionDTO(){
		filterActionType = AlarmbearbeitergruppenFilterActionType.SMS;
	}
}
