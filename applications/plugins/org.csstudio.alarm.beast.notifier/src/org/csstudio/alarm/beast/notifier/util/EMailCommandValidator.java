/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import org.csstudio.alarm.beast.notifier.actions.EmailActionImpl;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;

/**
 * Validator for {@link EmailActionImpl}.
 * Uses a {@link EMailCommandHandler} to validate an EMail command.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class EMailCommandValidator implements IActionValidator {

	private String details;
	private EMailCommandHandler handler;
	
	public void init(String details) {
		this.details = details == null ? null : details.trim();
		handler = new EMailCommandHandler(details);
	}
	
	/** @return handler for EMail command */
	public EMailCommandHandler getHandler() {
		return handler;
	}
	
	@Override
	public boolean validate() throws Exception {
		if (details == null || "".equals(details)) {
			throw new Exception("Missing automated action details");
		}
		handler.parse();
		if (handler.getTo() == null || handler.getTo().isEmpty()) {
			throw new Exception("Missing email command recipient");
		}
		return true;
	}
	
}
