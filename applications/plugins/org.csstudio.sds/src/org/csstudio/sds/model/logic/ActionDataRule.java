package org.csstudio.sds.model.logic;

import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.actions.CommitValueWidgetAction;
import org.csstudio.sds.model.properties.actions.WidgetAction;

public class ActionDataRule implements IRule {

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(Object[] arguments) {
		ActionData result = new ActionData();
		
		if (arguments[0] instanceof  Long || arguments[0] instanceof  Double) {
			result.addAction(this.createWidgetAction(arguments[0]));
		} else {
			for (Object arg : (Object[])arguments[0]) {
				result.addAction(this.createWidgetAction(arg));
			}
		}
		return result;
	}
	
	/**
	 * Creates a {@link CommitValueWidgetAction} with the given object as property value.
	 * @param value The value for the property of the action
	 * @return The corresponding {@link WidgetAction} 
	 */
	private WidgetAction createWidgetAction(final Object value) {
		CommitValueWidgetAction action = (CommitValueWidgetAction) ActionType.COMMIT_VALUE
			.getActionFactory().createWidgetAction();
		action.getProperty(CommitValueWidgetAction.PROP_VALUE).setPropertyValue(value);
		return action;
	}

}
