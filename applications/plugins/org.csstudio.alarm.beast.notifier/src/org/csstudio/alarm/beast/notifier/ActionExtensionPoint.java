package org.csstudio.alarm.beast.notifier;

import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;

/**
 * Define implementation of automated actions.
 * Each action includes a scheme, a implementation class which implements {@link INotificationAction}
 * and an optional validator which implements {@link IActionValidator}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class ActionExtensionPoint {

	/** URI scheme of the action (example: mailto, smsto) */
	private String scheme;
	
	private String actionClass;
	private String validatorClass;
	
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getActionClass() {
		return actionClass;
	}
	public void setActionClass(String actionClass) {
		this.actionClass = actionClass;
	}
	public String getValidatorClass() {
		return validatorClass;
	}
	public void setValidatorClass(String validatorClass) {
		this.validatorClass = validatorClass;
	}
	
	
}
