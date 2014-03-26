/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier;

/**
 * Priority for automated actions.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public enum EActionPriority {
	
	/** OK/NO_ALARM/normal/good */
    OK(Messages.Priority_OK, 0),

    /** Defined as important by the user */
    IMPORTANT(Messages.Priority_IMPORTANT, 3),

    /** Minor issue */
    MINOR(Messages.Priority_MINOR, 1),

    /** Major issue */
    MAJOR(Messages.Priority_MAJOR, 2);
    
	final private String display_name;
    final private int priority;
	
	EActionPriority(final String display_name, final int priority) {
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
		return "EActionPriority " + name() + " (" + display_name + ",  "
				+ ordinal() + ")";
	}
}
