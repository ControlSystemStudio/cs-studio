/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.scan.server.internal.MacroStack;
import org.junit.Test;

/** JUnit Test of the MacroStack
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MacroStackUnitTest
{
    @Test
	public void testMacros() throws Exception
	{
		final IMacroTableProvider macros = new MacroStack("x=Hello, y=Dolly");
		assertThat(MacroUtil.replaceMacros("$(x), ${y}!", macros), equalTo("Hello, Dolly!"));
	}

    @Test
    public void testStacking() throws Exception
    {
        final MacroStack macros = new MacroStack("x=Hello, y=Dolly");
        
        macros.push("y=Freddy");
        String text = MacroUtil.replaceMacros("$(x), ${y}!", macros);
        System.out.println(text);
        assertThat(text, equalTo("Hello, Freddy!"));
        
        macros.push("x=Bye,y=Jimmy");
        text = MacroUtil.replaceMacros("$(x), ${y}!", macros);
        System.out.println(text);
        assertThat(text, equalTo("Bye, Jimmy!"));
        System.out.println(macros);

        macros.pop();
        text = MacroUtil.replaceMacros("$(x), ${y}!", macros);
        System.out.println(text);
        assertThat(text, equalTo("Hello, Freddy!"));
        
        macros.pop();
        text = MacroUtil.replaceMacros("$(x), ${y}!", macros);
        System.out.println(text);
        assertThat(text, equalTo("Hello, Dolly!"));
        
        try
        {
            macros.pop();
            fail("Allowed pop?");
        }
        catch (IllegalStateException ex)
        {
            // Expected
        }
    }
}
