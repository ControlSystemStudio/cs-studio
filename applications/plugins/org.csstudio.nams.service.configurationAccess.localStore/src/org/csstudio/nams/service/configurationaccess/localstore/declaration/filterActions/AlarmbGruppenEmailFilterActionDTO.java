
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("8")
public class AlarmbGruppenEmailFilterActionDTO extends
		AbstAlarmbGruppenFilterActionDTO {
	public AlarmbGruppenEmailFilterActionDTO() {
		this.filterActionType = AlarmbGruppenFilterActionType.EMAIL;
	}

}
