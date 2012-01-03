
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
public class AlarmbSMSFilterActionDTO extends
		AbstAlarmbFilterActionDTO {

	public AlarmbSMSFilterActionDTO() {
		this.filterActionType = AlarmbFilterActionType.SMS;
	}
}
