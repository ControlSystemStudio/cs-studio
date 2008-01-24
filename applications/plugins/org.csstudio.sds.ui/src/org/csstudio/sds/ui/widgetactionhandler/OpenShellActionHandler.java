package org.csstudio.sds.ui.widgetactionhandler;

import java.util.Map;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.OpenDisplayWidgetAction;
import org.csstudio.sds.model.properties.actions.WidgetAction;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.runtime.IPath;

/**
 * Opens a display in a shell.
 * 
 * @author Kai Meyer
 */
public final class OpenShellActionHandler implements IWidgetActionHandler {

	/**
	 * {@inheritDoc}
	 * @required action instanceof OpenDisplayWidgetAction
	 */
	public void executeAction(final WidgetProperty property, final WidgetAction action) {
		assert action instanceof OpenDisplayWidgetAction : "Precondition violated: action instanceof OpenDisplayWidgetAction";
		OpenDisplayWidgetAction displayAction = (OpenDisplayWidgetAction) action;
		IPath path = displayAction.getResource();
		Map<String, String> newAlias = displayAction.getAliases();
		RunModeService.getInstance().openDisplayShellInRunMode(path, newAlias);
	}

}
