package org.csstudio.sds.components.ui.internal.utils;

import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.PropertyTypesEnum;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.runtime.IPath;

/**
 * The service performs the action depending on the given {@link ActionType}. 
 * @author Kai Meyer
 *
 */
public final class WidgetActionHandlerService {
	
	/**
	 * The instance of the service.
	 */
	private static WidgetActionHandlerService _instance;
	
	/**
	 * Constructor.
	 */
	private WidgetActionHandlerService() {
	}
	
	/**
	 * Returns the instance of the {@link WidgetActionHandlerService}.
	 * @return The instance.
	 */
	public static WidgetActionHandlerService getInstance() {
		if (_instance==null) {
			_instance = new WidgetActionHandlerService();
		}
		return _instance;
	}
	
	/**
	 * Performs the action depending on the given {@link ActionType}.
	 * @param property The {@link WidgetProperty} to use (when needed)
	 * @param type The type of the action
	 */
	public void performAction(final WidgetProperty property, final ActionType type) {
		if (type.equals(ActionType.OPEN_SHELL)) {
			this.openShell(type);
		} else if (type.equals(ActionType.OPEN_VIEW)) {
			this.openView(type);
		} else if (type.equals(ActionType.COMMIT_VALUE)) {
			this.commitValue(property, type);
		} else {
			this.doUnknownAction();
		}
	}
	
	/**
	 * Performs the unspecified  action.
	 */
	private void doUnknownAction() {
		CentralLogger.getInstance().info(this, "Unknown WidgetAction performed!");
	}
	
	/**
	 * Opens a display in a shell.
	 * @param type The {@link ActionType} of the action
	 */
	@SuppressWarnings("unchecked")
	private void openShell(final ActionType type) {
		WidgetProperty prop = type.getProperty(PropertyTypesEnum.RESOURCE);
		IPath path = (IPath) prop.getPropertyValue();
		prop = (WidgetProperty)type.getProperty(PropertyTypesEnum.MAP);
		Map<String, String> newAlias = (Map<String, String>) prop.getPropertyValue();
		RunModeService.getInstance().openDisplayShellInRunMode(path, newAlias);
	}
	
	/**
	 * Opens a display in a view.
	 * @param type The {@link ActionType} of the action
	 */
	@SuppressWarnings("unchecked")
	private void openView(final ActionType type) {
		WidgetProperty prop = type.getProperty(PropertyTypesEnum.RESOURCE);
		IPath path = (IPath) prop.getPropertyValue();
		prop = (WidgetProperty)type.getProperty(PropertyTypesEnum.MAP);
		Map<String, String> newAlias = (Map<String, String>) prop.getPropertyValue();
		RunModeService.getInstance().openDisplayViewInRunMode(path, newAlias);
	}
	
	/**
	 * Commits a value to the given {@link WidgetProperty}.
	 * @param property The {@link WidgetProperty} where the value is set
	 * @param type The {@link ActionType} of the action
	 */
	private void commitValue(final WidgetProperty property, final ActionType type) {
		WidgetProperty prop = type.getProperty(PropertyTypesEnum.STRING);
		property.setManualValue(prop.getPropertyValue());
	}

}
