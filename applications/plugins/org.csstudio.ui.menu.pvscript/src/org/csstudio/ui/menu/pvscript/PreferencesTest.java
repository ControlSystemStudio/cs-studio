/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of preference encode/decode
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PreferencesTest
{
	final private String ENCODED =
		"\"Hello\"|\"cmd1 -opt\",\"Good bye\"|\"cmd2\"";

	@Test
	public void testEncode()
	{
		final ScriptInfo[] infos = new ScriptInfo[]
        {
			new ScriptInfo("Hello", "cmd1 -opt"),
			new ScriptInfo("Good bye", "cmd2"),
        };
		final String encoded = Preferences.encode(infos);
		System.out.println(encoded);
		assertEquals(ENCODED, encoded);
	}

	@Test
	public void testDecode() throws Exception
	{
		final ScriptInfo[] infos = Preferences.decode(ENCODED);
		assertEquals(2, infos.length);
		System.out.println(infos[0]);
		System.out.println(infos[1]);
		assertEquals("cmd1 -opt", infos[0].getScript());
		assertEquals("Good bye", infos[1].getDescription());
	}

}
