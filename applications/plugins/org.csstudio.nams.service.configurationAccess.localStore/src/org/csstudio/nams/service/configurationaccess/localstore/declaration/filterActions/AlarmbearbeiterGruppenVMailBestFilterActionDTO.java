package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("6")
public class AlarmbearbeiterGruppenVMailBestFilterActionDTO extends
		AbstractAlarmbearbeiterGruppenFilterActionDTO {

	public AlarmbearbeiterGruppenVMailBestFilterActionDTO() {
		this.filterActionType = AlarmbearbeitergruppenFilterActionType.VMAIL_Best;
	}
}
