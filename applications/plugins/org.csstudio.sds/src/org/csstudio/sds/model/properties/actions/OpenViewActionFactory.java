package org.csstudio.sds.model.properties.actions;

import org.csstudio.sds.model.properties.ActionType;

/**
 * Creates a {@link WidgetAction} for opening a display in a view.
 * @author Kai Meyer
 *
 */
public final class OpenViewActionFactory extends WidgetActionFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WidgetAction createWidgetAction() {
		return new OpenDisplayWidgetAction(ActionType.OPEN_VIEW);
	}

}
