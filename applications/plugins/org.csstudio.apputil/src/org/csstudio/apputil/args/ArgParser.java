package org.csstudio.apputil.args;

import java.util.ArrayList;

/** Argument parser.
 *  <p>
 *  To use, create a bunch of <code>Option</code>s and run <code>parse()</code>.
 *  <p>
 *  In an Eclipse Application, the command-line args that weren't processed
 *  by the framework are accessible like this:
 *  <pre>
 *  final String args[] =
 *          (String []) context.getArguments().get("application.args");
 *  </pre>
 *  
 *  @see BooleanOption
 *  @see IntegerOption
 *  @see StringOption

 *  @author Kay Kasemir
 */
public class ArgParser
{
    /** Allow extra params or consider that an error? */
    final private boolean allow_extra_parameters;
    
    /** All the allowed options */
    final private ArrayList<Option> options = new ArrayList<Option>();
    
    /** Extra parameters, non-options */
    final private ArrayList<String> extra = new ArrayList<String>();
    
    /** Initialize, allowing only registered Options */
    public ArgParser()
    {
        this(false);
    }
    
    /** Initialize Parser.
     *  Next step is to add <code>Option</code>s.
     *  
     *  @param allow_extra_parameters Allow extra parameters, or is
     *         any argument that does not match a registered Option
     *         an error?
     *  @see #parse(String[])
     *  @see #getExtraParameters()
     */
    public ArgParser(boolean allow_extra_parameters)
    {
        this.allow_extra_parameters = allow_extra_parameters;
    }

    /** Parse given list of arguments.
     *  <p>
     *  All arguments that start with "-" with be checked against the
     *  registered options.
     *  <p>
     *  Remaining, non-option arguments generate
     *  an error, unless <code>allow_extra_parameters</code> was set
     *  when constructing the parser.
     *  @param args Arguments
     *  @throws Exception if argument not handled
     */
    @SuppressWarnings("nls")
    final public void parse(final String[] args) throws Exception
    {
        int pos = 0;
        while (pos < args.length)
        {
            if (args[pos].startsWith("-"))
            {
                boolean ok = false;
                for (Option option : options)
                {   // See if this option handles the current argument
                    final int handle = option.handle(args, pos);
                    if (handle > 0)
                    {   // Yes, it digested some of the arguments
                        pos += handle;
                        ok = true;
                        break;
                    }
                }
                if (! ok)
                    throw new Exception("Unknown option '" + args[pos] + "'");
            }
            else if (allow_extra_parameters)
            {   // Not an "-..." option, remember as extra argument
                extra.add(args[pos++]);
            }
            else
                throw new Exception("Extra, non-option parameter '" + args[pos] + "'");
        }
    }

    /** @return Help string that describes all the registered options */
    @SuppressWarnings("nls")
    final public String getHelp()
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("Options:\n");
        for (Option option : options)
        {
            buf.append(String.format("  %-20s : %s\n",
                                     option.getOption(), option.getInfo()));
        }
        return buf.toString();
    }

    /** Add an option */
    final void add(final Option option)
    {
        options.add(option);
    }

    /** Obtain extra parameters that were provided to parse() but
     *  did not look like options
     *  @return String array of extra parameters
     *  @see #parse(String[])
     */
    public String[] getExtraParameters()
    {
        return extra.toArray(new String[extra.size()]);
    }
}
