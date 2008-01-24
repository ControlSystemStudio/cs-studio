package org.csstudio.sds.ui.widgetactionhandler;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.CommitValueWidgetAction;
import org.csstudio.sds.model.properties.actions.WidgetAction;

/**
 * Commits a value to the given {@link WidgetProperty}.
 * 
 * @author Kai Meyer
 */
public final class CommitValueActionHandler implements IWidgetActionHandler {

	/**
	 * {@inheritDoc}
	 * @required action instanceof CommitValueWidgetAction
	 */
	public void executeAction(final WidgetProperty property, final WidgetAction action) {
		assert action instanceof CommitValueWidgetAction : "Precondition violated: action instanceof CommitValueWidgetAction";
		CommitValueWidgetAction valueAction = (CommitValueWidgetAction) action;
		property.setManualValue(valueAction.getValue());
	}

}
