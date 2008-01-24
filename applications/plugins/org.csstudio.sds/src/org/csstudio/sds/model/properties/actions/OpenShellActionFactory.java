package org.csstudio.sds.model.properties.actions;

import org.csstudio.sds.model.properties.ActionType;

/**
 * Creates a {@link WidgetAction} for opening a display in a shell.
 * @author Kai Meyer
 *
 */
public final class OpenShellActionFactory extends WidgetActionFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WidgetAction createWidgetAction() {
		return new OpenDisplayWidgetAction(ActionType.OPEN_SHELL);
	}

}
