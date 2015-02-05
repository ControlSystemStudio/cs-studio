/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import org.csstudio.alarm.beast.notifier.actions.SmsActionImpl;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;

/**
 * Validator for {@link SmsActionImpl}.
 * Uses a {@link SmsCommandHandler} to validate an SMS command.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class SmsCommandValidator implements IActionValidator {

	private String details;
	private SmsCommandHandler handler;

	public void init(String details) {
		this.details = details == null ? null : details.trim();
		handler = new SmsCommandHandler(details);
	}

	/** @return handler for SMS command */
	public SmsCommandHandler getHandler() {
		return handler;
	}

	@Override
	public boolean validate() throws Exception {
		if (details == null || "".equals(details)) {
			throw new Exception("Missing automated action details");
		}
		handler.parse();
		if (handler.getTo() == null || handler.getTo().isEmpty()) {
			throw new Exception("Missing SMS command recipient");
		}
		return true;
	}

}
