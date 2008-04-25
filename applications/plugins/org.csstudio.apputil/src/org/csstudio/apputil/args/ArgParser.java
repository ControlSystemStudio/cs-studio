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
 *  @author Kay Kasemir
 */
public class ArgParser
{
    /** All the allowed options */
    final private ArrayList<Option> options = new ArrayList<Option>();
    
    /** Parse given list of arguments.
     *  @param args Arguments
     *  @throws Exception if argument not handled
     */
    @SuppressWarnings("nls")
    final public void parse(final String[] args) throws Exception
    {
        int pos = 0;
        while (pos < args.length)
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
    }

    /** @return Help string that describes all the options */
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

}
