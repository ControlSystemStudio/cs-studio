package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class AlarmbearbeiterSMSFilterActionDTO extends
		AbstractAlarmbearbeiterFilterActionDTO {

	public AlarmbearbeiterSMSFilterActionDTO() {
		this.filterActionType = AlarmbearbeiterFilterActionType.SMS;
	}
}
