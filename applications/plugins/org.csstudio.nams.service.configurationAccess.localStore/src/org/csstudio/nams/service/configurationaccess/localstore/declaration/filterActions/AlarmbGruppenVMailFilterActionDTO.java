
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("5")
public class AlarmbGruppenVMailFilterActionDTO extends
		AbstAlarmbGruppenFilterActionDTO {

	public AlarmbGruppenVMailFilterActionDTO() {
		this.filterActionType = AlarmbGruppenFilterActionType.VMAIL;
	}
}
