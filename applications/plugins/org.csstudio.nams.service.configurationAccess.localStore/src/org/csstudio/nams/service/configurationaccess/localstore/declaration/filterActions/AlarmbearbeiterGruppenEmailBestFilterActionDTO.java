package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("9")
public class AlarmbearbeiterGruppenEmailBestFilterActionDTO extends
		AbstractAlarmbearbeiterGruppenFilterActionDTO {

	public AlarmbearbeiterGruppenEmailBestFilterActionDTO() {
		this.filterActionType = AlarmbearbeitergruppenFilterActionType.EMAIL_Best;
	}
}
