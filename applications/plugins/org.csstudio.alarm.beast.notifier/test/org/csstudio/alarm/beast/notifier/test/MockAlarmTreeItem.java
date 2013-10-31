/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import java.util.Arrays;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.GDCDataStructure;

/**
 * Mock for {@link AlarmTreeItem}. Overwrite package defined methods to allow
 * test classes to write guidance, displays, commands and automated actions.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
@SuppressWarnings("all")
public class MockAlarmTreeItem extends AlarmTreeItem {

	/** Guidance messages */
	private GDCDataStructure guidance[] = new GDCDataStructure[0];

	/** Related displays */
	private GDCDataStructure displays[] = new GDCDataStructure[0];

	/** Commands */
	private GDCDataStructure commands[] = new GDCDataStructure[0];

	/** Automated Actions */
	private AADataStructure automated_actions[] = new AADataStructure[0];

	public MockAlarmTreeItem(AlarmTreeItem parent, String name, int id) {
		super(parent, name, id);
	}

	public synchronized GDCDataStructure[] getGuidance() {
		return Arrays.copyOf(guidance, guidance.length);
	}

	public synchronized void setGuidance(final GDCDataStructure guidance[]) {
		if (guidance == null)
			throw new IllegalArgumentException();
		this.guidance = guidance;
	}

	public synchronized GDCDataStructure[] getDisplays() {
		return Arrays.copyOf(displays, displays.length);
	}

	public synchronized void setDisplays(final GDCDataStructure displays[]) {
		if (displays == null)
			throw new IllegalArgumentException();
		this.displays = displays;
	}

	public synchronized GDCDataStructure[] getCommands() {
		return Arrays.copyOf(commands, commands.length);
	}

	public synchronized void setCommands(final GDCDataStructure[] commands) {
		if (commands == null)
			throw new IllegalArgumentException();
		this.commands = commands;
	}

	public synchronized AADataStructure[] getAutomatedActions() {
		return Arrays.copyOf(automated_actions, automated_actions.length);
	}

	public synchronized void setAutomatedActions(
			final AADataStructure[] automated_actions) {
		if (automated_actions == null)
			throw new IllegalArgumentException();
		this.automated_actions = automated_actions;
	}

}
