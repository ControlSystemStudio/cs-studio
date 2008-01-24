package org.csstudio.sds.model.properties.actions;

/**
 * The superclass for all {@link WidgetActionFactory}s.
 * @author Kai Meyer
 *
 */
public abstract class WidgetActionFactory {
	
	/**
	 * Creates the {@link WidgetAction}.
	 * @return The {@link WidgetAction}
	 */
	public abstract WidgetAction createWidgetAction();

}
