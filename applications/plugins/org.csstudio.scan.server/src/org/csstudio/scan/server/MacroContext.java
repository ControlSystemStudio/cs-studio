/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.java.string.StringSplitter;

/** Stack of macros, allows pushing new values and popping back to previous macros
 *  @author Kay Kasemir
 */
public class MacroContext implements IMacroTableProvider
{
    /** Map of macro names to values */
    final private Stack<Map<String, String>> stack = new Stack<>();

    /** Initialize
     *  @param names_and_values String of the form "macro=value, macro=value"
     *  @throws Exception on malformed input
     */
    public MacroContext(final String names_and_values) throws Exception
    {
        pushMacros(names_and_values);
    }

    /** Add new macros, replacing macros of same name
     *  @param names_and_values String of the form "macro=value, macro=value"
     *  @throws Exception on malformed input
     *  @see #popMacros()
     */
    public void pushMacros(final String names_and_values) throws Exception
    {
        final Map<String, String> macros = new HashMap<String, String>();
        if (! stack.isEmpty())
            macros.putAll(stack.peek());
        final String pairs[] = StringSplitter.splitIgnoreInQuotes(names_and_values, ',', true);
        for (String pair : pairs)
        {
            final String name_value[] = StringSplitter.splitIgnoreInQuotes(pair, '=', true);
            if (name_value.length != 2)
                throw new Exception("Input '" + pair + "' does not match 'name=value'");
            macros.put(name_value[0], name_value[1]);
        }
        stack.push(macros);
    }

    /** Restore macros as they were before last <code>push</code>
     *  @see #pushMacros(String)
     *  @throws IllegalStateException is <code>push</code> has not been called
     */
    public void popMacros()
    {
        if (stack.size() > 1)
            stack.pop();
        else
            throw new IllegalStateException("No macros have been pushed");
    } 

    /** {@inheritDoc} */
    @Override
    public String getMacroValue(final String name)
    {
        return stack.peek().get(name);
    }
    
    /** @param text Text that may contain "$(macro)"
     *  @return Text where macros have been replaced by their values
     *  @throws Exception on error in macros
     */
    public String resolveMacros(final String text) throws Exception
    {
        return MacroUtil.replaceMacros(text, this);
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        int level = 0;
        for (Map<String, String> macros : stack)
        {
            buf.append(level + ": ");
            final String names[] = macros.keySet().toArray(new String[macros.size()]);
            boolean first = true;
            for (String name: names)
            {
                if (first)
                    first = false;
                else
                    buf.append(", ");
                buf.append(name + "=\"" + macros.get(name) + "\"");
            }
            ++level;
            if (level < stack.size())
                buf.append("\n");
        }
        return buf.toString();
    }
}