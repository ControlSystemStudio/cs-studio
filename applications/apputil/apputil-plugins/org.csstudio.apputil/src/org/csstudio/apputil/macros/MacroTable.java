/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.macros;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.java.string.StringSplitter;

/** A table of macros that's initialized from a string or a hash map,
 *  keeping the macro names and values in a hash
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MacroTable extends Macros implements IMacroTableProvider
{

   /** Initialize
    *  @param macros Map with macro name/value entries
    */
   public MacroTable(final Map<String, String> macros)
   {
       super(macros);
   }

    /** Initialize
     *  @param names_and_values String of the form "macro=value, macro=value"
     *  @throws Exception on malformed input
     */
    public MacroTable(final String names_and_values) throws Exception
    {
        super();
        final String pairs[] = StringSplitter.splitIgnoreInQuotes(names_and_values, ',', true);
        for (String pair : pairs)
        {
            final String name_value[] = StringSplitter.splitIgnoreInQuotes(pair, '=', true);
            if (name_value.length != 2)
                throw new Exception("Input '" + pair + "' does not match 'name=value'");
            macrosMap.put(name_value[0], name_value[1]);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getMacroValue(final String name)
    {
        return get(name);
    }

}
