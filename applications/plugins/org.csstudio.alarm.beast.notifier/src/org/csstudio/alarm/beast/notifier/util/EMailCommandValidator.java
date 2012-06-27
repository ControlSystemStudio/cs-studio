package org.csstudio.alarm.beast.notifier.util;

import org.csstudio.alarm.beast.notifier.actions.EmailNotificationAction;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;

/**
 * Validator for {@link EmailNotificationAction}.
 * Uses a {@link EMailCommandHandler} to validate an EMail command.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class EMailCommandValidator implements IActionValidator {

	private String details;
	private EMailCommandHandler handler;
	
	public void init(String details) {
		this.details = details.trim();
		handler = new EMailCommandHandler(details);
		handler.parse();
	}
	
	/** @return handler for EMail command */
	public EMailCommandHandler getHandler() {
		return handler;
	}
	
	@Override
	public boolean validate() throws Exception {
		if (details == null || "".equals(details))
			throw new Exception("Missing details from automated action");
		if (handler.getTo() == null || handler.getTo().isEmpty())
			throw new Exception("Missing recipient");
		return true;
	}
	
	/** @return <code>true</code> if the EMail command define a recipient, a subject and a body */
	public boolean isComplete() {
		return ((handler.getTo() != null && !handler.getTo().isEmpty())
				&& (handler.getSubject() != null && !"".equals(handler.getSubject().trim())) 
				&& (handler.getBody() != null && !"".equals(handler.getBody().trim())));
	}

}
