/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import org.csstudio.alarm.beast.notifier.actions.CommandActionImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link CommandActionImpl}. Execute a command on system.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class CommandActionImplUnitTest {

	/**
	 * Test that execution of a fake command return CommandState.ERROR status.
	 */
	@Test
	public void testFakeCommand() {
		final String dir = ".";
		final String command = "dtc";
		CommandActionImpl ca = new CommandActionImpl();
		ca.execCmd(dir, command, 1);
		Assert.assertTrue(ca.getCommandState().equals(
				CommandActionImpl.CommandState.ERROR));
	}

	/**
	 * Test that execution of a fake command return CommandState.FINISHED_OK
	 * status.
	 */
	@Test
	public void testSimpleCommand() {
		final String dir = ".";
		final String command = "dirname .";
		CommandActionImpl ca = new CommandActionImpl();
		ca.execCmd(dir, command, 1);
		Assert.assertTrue(ca.getCommandState().equals(
				CommandActionImpl.CommandState.FINISHED_OK));
	}

}
