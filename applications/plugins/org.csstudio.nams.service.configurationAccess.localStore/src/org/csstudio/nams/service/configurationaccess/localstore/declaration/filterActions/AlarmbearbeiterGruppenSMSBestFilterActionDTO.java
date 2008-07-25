package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("3")
public class AlarmbearbeiterGruppenSMSBestFilterActionDTO extends
		AbstractAlarmbearbeiterGruppenFilterActionDTO {

	public AlarmbearbeiterGruppenSMSBestFilterActionDTO(){
		filterActionType = AlarmbearbeitergruppenFilterActionType.SMS;
	}
}
