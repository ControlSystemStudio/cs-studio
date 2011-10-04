
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("6")
public class AlarmbGruppenVMailBestFilterActionDTO extends
		AbstAlarmbGruppenFilterActionDTO {

	public AlarmbGruppenVMailBestFilterActionDTO() {
		this.filterActionType = AlarmbGruppenFilterActionType.VMAIL_Best;
	}
}
