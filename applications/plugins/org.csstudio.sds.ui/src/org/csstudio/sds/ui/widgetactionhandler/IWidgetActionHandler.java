package org.csstudio.sds.ui.widgetactionhandler;

import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.actions.WidgetAction;

/**
 * The interface for all WidgetActionHandler.
 * 
 * @author Kai Meyer
 */
public interface IWidgetActionHandler {
	
	/**
	 * Performs the to a {@link WidgetAction} belonging actions.
	 * @param property A {@link WidgetProperty} needed for the execution
	 * @param action The {@link WidgetAction}, which should be executed
	 */
	void executeAction(final WidgetProperty property, final WidgetAction action);

}
