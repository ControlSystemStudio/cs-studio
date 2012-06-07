/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.args;

import java.util.ArrayList;
import java.util.List;

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
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArgParser
{
    /** Allow extra params or consider that an error? */
    final private boolean allow_extra_parameters;

    /** All the allowed options */
    final private List<Option> options = new ArrayList<Option>();

    /** Options that are allowed, but not displayed in the help */
    final private List<Option> ignored_options = new ArrayList<Option>();

    /** Extra parameters, non-options */
    final private List<String> extra = new ArrayList<String>();

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

    /** Add options handled by Eclipse
     *
     *  <p>This adds the "pluginCustomization" and "data"
     *  options.
     *  The code that calls the parser will not use them,
     *  but this way they show up in the help message
     *  and can also be checked for spelling
     *  (to avoid "plukinCustomisation")
     */
    public void addEclipseParameters()
    {
        new StringOption(this, "-pluginCustomization",
        		"/path/to/my/settings.ini",
                "Eclipse plugin defaults", null);
        new StringOption(this, "-data",
        		"/home/fred/Workspace",
        		"Eclipse workspace location", null);
        // On OS X, the application will have a file
        // WhateverAppName.app/Contents/Info.plist
        // that includes a default option "-showlocation",
        // which the parser will see but not understand.
        ignore(new BooleanOption(this, "-showlocation", "Show location in window title"));
        // Option used by command-line tools to suppress the launcher's error GUI
        ignore(new BooleanOption(this, "--launcher.suppressErrors", "Suppress launcher's error dialog"));
    }

    /** Add an option that is allowed, but not displayed in the help */
    public void ignore(final Option option)
    {
        ignored_options.add(option);
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
    final public String getHelp()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Options:\n");
        for (Option option : options)
        {
            if (ignored_options.contains(option))
                continue;
            buf.append(String.format("  %-30s: %s\n",
                                     option.getOption() + " " + option.getArgument(),
                                     option.getInfo()));
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
