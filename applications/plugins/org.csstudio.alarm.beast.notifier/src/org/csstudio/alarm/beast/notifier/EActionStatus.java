/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

/**
 * Status for automated actions.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public enum EActionStatus {
	
    OK(Messages.Status_OK, 0),
    CANCELED(Messages.Status_CANCELED, 1),
    FAILED(Messages.Status_FAILED, 2);

	final private String display_name;
    final private int priority;
	
	EActionStatus(final String display_name, final int priority) {
		this.display_name = display_name;
		this.priority = priority;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public String toString() {
		return "EActionStatus " + name() + " (" + display_name + ",  " + ordinal()
				+ ")";
	}
}
