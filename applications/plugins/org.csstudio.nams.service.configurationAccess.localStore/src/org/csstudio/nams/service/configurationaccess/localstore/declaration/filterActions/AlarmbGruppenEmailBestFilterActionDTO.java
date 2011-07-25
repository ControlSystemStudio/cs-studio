
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("9")
public class AlarmbGruppenEmailBestFilterActionDTO extends
		AbstAlarmbGruppenFilterActionDTO {

	public AlarmbGruppenEmailBestFilterActionDTO() {
		this.filterActionType = AlarmbGruppenFilterActionType.EMAIL_Best;
	}
}
