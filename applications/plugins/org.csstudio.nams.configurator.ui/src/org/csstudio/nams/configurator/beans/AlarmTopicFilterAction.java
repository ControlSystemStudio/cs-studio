
package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmTopicFilterActionType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.FilterActionType;

/**
 * (10,'Message an Topic',NULL)
 * 
 */
public class AlarmTopicFilterAction extends
		AbstractFilterAction<AlarmTopicFilterActionType> {

	public AlarmTopicFilterAction() {
		super(AlarmTopicFilterActionType.class);
		this.setType(AlarmTopicFilterActionType.TOPIC);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		final AlarmTopicFilterAction action = new AlarmTopicFilterAction();
		action.message = this.message;
		action.receiver = this.receiver;
		action.type = this.type;
		return action;
	}

	@Override
    public FilterActionType[] getFilterActionTypeValues() {
		return AlarmTopicFilterActionType.values();
	}
}
