/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.args;

/** Base for all option types
 *  @author Kay Kasemir
 */
abstract class Option
{
    /** Option name */
    final private String option;

    /** Information about argument */
    final private String arg_info;
    
    /** Information for help */
    final private String info;

    /** Constructor
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param info Information for help
     */
    public Option(final ArgParser parser,
                  final String option,
                  final String info)
    {
        this(parser, option, "", info); //$NON-NLS-1$
    }
    
    /** Constructor
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param arg_info Information about argument
     *  @param info Information for help
     */
    public Option(final ArgParser parser,
            final String option,
            final String arg_info,
            final String info)
    {
        this.option = option;
        this.arg_info = arg_info;
        this.info = info;
        parser.add(this);
    }

    /** @return Option name */
    final public String getOption()
    {
        return option;
    }

    /** @return Argument that the option might take, or "" */
    final public String getArgument()
    {
        return arg_info;
    }
    
    /** @return Argument information (description) for help */
    final public String getInfo()
    {
        return info;
    }

    /** Handle arguments from position on.
     *  @param args All the arguments
     *  @param position Index of argument to check
     *  @return 0 or less if not handled. 1 if handled,
     *          2 if handled and also read another parameter from
     *          <code>args</code> and so on
     *  @throws Exception if parameters were wrong or missing.
     */
    abstract public int handle(String[] args, int position) throws Exception;
    
    /** Check if there is a string parameter for this option.
     *  @param args Argument list
     *  @param position Current position
     *  @return <code>true</code> if <code>args[position]</code> matches
     *          this option and there is one more parameter,
     *          <code>false</code> if it's a different option.
     *  @throws Exception if option matches, but there is no argument
     */
    @SuppressWarnings("nls")
    protected boolean haveParameter(final String[] args, final int position) throws Exception
    {
        if (matchesThisOption(args[position]))
        {
            if (position + 1 >= args.length)
                throw new Exception("Missing argument for option '"
                                    + getOption() + "'");
            return true;
        }
        return false;
    }
    
    /** @return <code>true</code> if given text matches this option,
     *          checking the full option as well as shorter versions.
     */
    protected boolean matchesThisOption(final String text)
    {
        for (int l=option.length(); l>=2; --l)
            if (option.substring(0, l).equals(text))
                return true;
        return false;
    }
}
