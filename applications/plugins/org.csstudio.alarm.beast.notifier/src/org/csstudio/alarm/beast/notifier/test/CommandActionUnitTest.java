/*******************************************************************************
* Copyright (c) 2010-2012 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.test;

import org.csstudio.alarm.beast.notifier.actions.CommandActionImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link CommandActionImpl}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class CommandActionUnitTest {

	@Test
	public void testFakeCommand() {
		final String dir = "/home/ITER/arnaudf/";
		final String command = "dtc";
		CommandActionImpl ca = new CommandActionImpl();
		ca.execCmd(dir, command, 1);
		Assert.assertTrue(ca.getCommandState().equals(CommandActionImpl.CommandState.ERROR));
	}

	@Test
	public void testSimpleCommand() {
		final String dir = "/home/ITER/arnaudf/";
		final String command = "dirname .";
		CommandActionImpl ca = new CommandActionImpl();
		ca.execCmd(dir, command, 1);
		Assert.assertTrue(ca.getCommandState().equals(CommandActionImpl.CommandState.FINISHED_OK));
	}
}
