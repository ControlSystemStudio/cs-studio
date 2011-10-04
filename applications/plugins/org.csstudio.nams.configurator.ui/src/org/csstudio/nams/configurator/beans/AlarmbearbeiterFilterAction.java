
package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbFilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;

/**
 * (1,'SMS an Person',NULL) (4,'VMail an Person',NULL) (7,'EMail an
 * Person',NULL)
 * 
 * 
 */
public class AlarmbearbeiterFilterAction extends
		AbstractFilterAction<AlarmbFilterActionType> {

	public AlarmbearbeiterFilterAction() {
		super(AlarmbFilterActionType.class);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		final AlarmbearbeiterFilterAction action = new AlarmbearbeiterFilterAction();
		action.message = this.message;
		action.receiver = this.receiver;
		action.type = this.type;
		return action;
	}

	@Override
    public FilterActionType[] getFilterActionTypeValues() {
		return AlarmbFilterActionType.values();
	}
}
