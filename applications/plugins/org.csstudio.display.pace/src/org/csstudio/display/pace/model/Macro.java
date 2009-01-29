package org.csstudio.display.pace.model;

import java.util.ArrayList;
import java.util.regex.Pattern;

/** A macro that replaces <pre>${name}</pre> or <pre>$(name)</pre> with a value.
 *  
 *  In order to be flexible, it supports both 'curly' and 'normal' braces
 *  in the macro name syntax.
 *  
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/29/09
 */
@SuppressWarnings("nls")
public class Macro
{
    /** Macro name and replacement value */
    final private String name, value;
    
    /** Compiled regular expression for macro ${name} */
    final private Pattern pattern;

    /** Parse macros from string
     *  @param macro_text Text of the format "macroName=value,macroName=value"
     *  @return Array of macros
     *  @throws Exception on error
     */
    public static Macro[] fromList(final String macro_text) throws Exception
    {
        // Split multiple "macroName=value" on comma, maybe comma-and-space
        final String[] mac_defs = macro_text.split(",\\s*");
        final ArrayList<Macro> macros = new ArrayList<Macro>();
        for (int i=0; i<mac_defs.length; ++i)
        {
            // Skip empty macros
            if (mac_defs[i].trim().length() <= 0)
                continue;
            // Split "macroName=value" on "=", maybe with spaces as in "  =  "
            final String name_value[] = mac_defs[i].split("\\s*=\\s*");
            // Should result in 1) macroName 2) value
            if (name_value.length != 2)
                throw new Exception("Error in macro definition '" + macro_text +
                                    "', expecting 'macroName=value'");
            macros.add(new Macro(name_value[0], name_value[1]));
        }
        return (Macro[]) macros.toArray(new Macro[macros.size()]);
    }

    /** Apply a list of macros
     *  @param macros List of macros to apply to input
     *  @param input Text that may contain "${macroName}" or "$(macroName)"
     *               to be replaced by macro values
     *  @return Input after applying all macros
     */
    public static String apply(final Macro[] macros, String input)
    {
        for (Macro macro : macros)
            input = macro.apply(input);
        return input;
    }

    /** Initialize
     *  @param name Macro name to be used in either the
     *              <pre>${name}</pre> or <pre>$(name)</pre>
     *              replacement syntax.
     *  @param value Replacement value for the macro name
     */
    public Macro(final String name, final String value)
    {
        this.name = name;
        this.value = value;
        // Create pattern to match either "${name}" or "$(name)".
        // Will actually also match a mixed case like "${name)".
        pattern = Pattern.compile("\\$[{(]" + name + "[)}]");
    }
    
    /** Apply macro to input
     *  @param input Input text that may contain macros
     *  @return Input with replaces macro values
     */
    public String apply(final String input)
    {
        // Replace all occurrences of the macro name pattern
        // with the replacement value
        return pattern.matcher(input).replaceAll(value);
    }

    /** @return String representation for debugging,
     *          something like "macroName=value"
     */
    @Override
    public String toString()
    {
        return name + "=" + value;
    }
}
