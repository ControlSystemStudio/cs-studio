package org.csstudio.apputil.macros;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.platform.util.StringUtil;

/** A table of macros that's initialized from a string,
 *  keeping the macro names and values in a hash
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MacroTable implements IMacroTableProvider
{
    /** Map of macro names to values */
    final private Map<String, String> macros = new HashMap<String, String>();
    
    /** Initialize
     *  @param names_and_values String of the form "macro=value, macro=value"
     *  @throws Exception on malformed input
     */
    public MacroTable(final String names_and_values) throws Exception
    {
        final String pairs[] = StringUtil.splitIgnoreInQuotes(names_and_values, ',', true);
        for (String pair : pairs)
        {
            final String name_value[] = StringUtil.splitIgnoreInQuotes(pair, '=', true);
            if (name_value.length != 2)
                throw new Exception("Input '" + pair + "' does not match 'name=value'");
            macros.put(name_value[0], name_value[1]);
        }
    }

    /** {@inheritDoc} */
    public String getMacroValue(final String name)
    {
        return macros.get(name);
    }
}
