/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.args;

/** Option that reads one integer argument.
 *  @author Kay Kasemir
 */
public class IntegerOption extends Option
{
    /** Current value */
    private int value;
    
    /** Construct String option
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param info Help string
     *  @param default_value Default value
     */
    public IntegerOption(final ArgParser parser,
                         final String option,
                         final String info,
                         final int default_value)
    {
        this(parser, option, "<number>", info, default_value); //$NON-NLS-1$
    }

    /** Construct String option
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param arg_info Information about argument
     *  @param info Help string
     *  @param default_value Default value
     */
    public IntegerOption(final ArgParser parser,
                         final String option,
                         final String arg_info,
                         final String info,
                         final int default_value)
    {
        super(parser, option, arg_info, info);
        value = default_value;
    }

    
    /** @return Value */
    public int get()
    {
        return value;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    public int handle(final String[] args, final int position) throws Exception
    {
        if (haveParameter(args, position))
        {
            try
            {
                value = Integer.parseInt(args[position+1]);
            }
            catch (Exception ex)
            {
                throw new Exception("Cannot parse number for option '"
                                    + getOption() + "'");
            }
            return 2;
        }
        return 0;
    }
    
    @Override
    public String toString()
    {
        return getOption() + "=" + value; //$NON-NLS-1$
    }
}
