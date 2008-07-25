package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("7")
public class AlarmbearbeiterEmailFilterActionDTO extends
		AbstractAlarmbearbeiterFilterActionDTO {

	public AlarmbearbeiterEmailFilterActionDTO(){
		filterActionType = AlarmbearbeiterFilterActionType.EMAIL;
	}
}
