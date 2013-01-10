/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.csstudio.csdata.ProcessVariable;
import org.junit.Test;

/** JUnit demo of the {@link CommandRunner}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScriptExecutorDemo
{
	@Test
	public void testScriptRunner() throws Exception
	{
		final ScriptInfo info = new ScriptInfo("Command 1", "notepad.exe");
		ScriptExecutor.runWithPVs(info.getScript(), new ProcessVariable("pv1"));
	}
}
