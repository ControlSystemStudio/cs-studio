package org.csstudio.sds.ui.widgetactionhandler;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.actions.WidgetAction;

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
	 * The map of known {@link IWidgetActionHandler}.
	 */
	private Map<ActionType, IWidgetActionHandler> _handler;
	
	/**
	 * Constructor.
	 */
	private WidgetActionHandlerService() {
		_handler = new HashMap<ActionType, IWidgetActionHandler>();
		this.createHandler();
	}
	
	/**
	 * Creates and registers {@link IWidgetActionHandler}.
	 */
	private void createHandler() {
		_handler.put(ActionType.OPEN_SHELL, new OpenShellActionHandler());
		_handler.put(ActionType.OPEN_VIEW, new OpenViewActionHandler());
		_handler.put(ActionType.COMMIT_VALUE, new CommitValueActionHandler());
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
	 * @param action The type of the action
	 */
	public void performAction(final WidgetProperty property, final WidgetAction action) {
		if (action.isEnabled() && _handler.containsKey(action.getType())) {
			_handler.get(action.getType()).executeAction(property, action);
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

}
