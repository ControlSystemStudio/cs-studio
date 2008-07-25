package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("4")
public class AlarmbearbeiterVoiceMailFilterActionDTO extends
		AbstractAlarmbearbeiterFilterActionDTO {

	public AlarmbearbeiterVoiceMailFilterActionDTO(){
		filterActionType = AlarmbearbeiterFilterActionType.VMAIL;
	}
}
