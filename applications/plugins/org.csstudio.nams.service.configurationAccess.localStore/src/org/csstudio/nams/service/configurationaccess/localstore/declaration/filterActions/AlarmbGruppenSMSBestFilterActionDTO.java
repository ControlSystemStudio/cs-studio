
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("3")
public class AlarmbGruppenSMSBestFilterActionDTO extends
		AbstAlarmbGruppenFilterActionDTO {

	public AlarmbGruppenSMSBestFilterActionDTO() {
		this.filterActionType = AlarmbGruppenFilterActionType.SMS;
	}
}
