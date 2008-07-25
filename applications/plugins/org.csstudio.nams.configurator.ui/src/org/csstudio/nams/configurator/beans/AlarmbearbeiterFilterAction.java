package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterFilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;

/**
 * (1,'SMS an Person',NULL)
 * (4,'VMail an Person',NULL)
 * (7,'EMail an Person',NULL)
 * @param <AlarmbearbeiterFilterActionType>
 *  
 */
public class AlarmbearbeiterFilterAction extends AbstractFilterAction<AlarmbearbeiterFilterActionType> {

	public AlarmbearbeiterFilterAction() {
		super(AlarmbearbeiterFilterActionType.class);
	}

	public FilterActionType[] getFilterActionTypeValues() {
		return AlarmbearbeiterFilterActionType.values();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AlarmbearbeiterFilterAction action = new AlarmbearbeiterFilterAction();
		action.message = this.message;
		action.receiver = this.receiver;
		action.type = this.type;
		return action;
	}	
}
