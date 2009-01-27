package org.csstudio.display.pace.model;

import java.util.regex.Pattern;

/** A macro that replaces <pre>${name}</pre> or <pre>$(name)</pre> with a value.
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Macro
{
    /** Macro name and replacement value */
    final private String name, value;
    
    /** Compiled regular expression for macro ${name} */
    final private Pattern pattern;

    /** Parse macros from string
     *  @param macro_text Text of the format "name=value,name=value"
     *  @return Array of macros
     *  @throws Exception on error
     */
    public static Macro[] fromList(final String macro_text) throws Exception
    {
        final String[] mac_defs = macro_text.split(",\\s*");
        final Macro macros[] = new Macro[mac_defs.length];
        for (int i=0; i<macros.length; ++i)
        {
            final String name_value[] = mac_defs[i].split("\\s*=\\s*");
            if (name_value.length != 2)
                throw new Exception("Error in macro definition '" + macro_text + "'");
            macros[i] = new Macro(name_value[0], name_value[1]);
        }
        return macros;
    }

    /** Apply a list of macros
     *  @param macros
     *  @param input
     *  @return Input after applying all macros
     */
    public static String apply(final Macro[] macros, String input)
    {
        for (Macro macro : macros)
            input = macro.apply(input);
        return input;
    }

    /** Initialize
     *  @param name Macro name to be used as
     *              <pre>${name}</pre> or <pre>$(name)</pre>
     *  @param value Replacement value for the macro name
     */
    public Macro(final String name, final String value)
    {
        this.name = name;
        this.value = value;
        pattern = Pattern.compile("\\$[{(]" + name + "[)}]");
    }
    
    /** Apply macro to input
     *  @param input Input text that may contain macros
     *  @return Input with replaces macro values
     */
    public String apply(final String input)
    {
        return pattern.matcher(input).replaceAll(value);
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return name + "=" + value;
    }
}
