/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.args;

/** Option that sets a flag (no additional argument read).
 *  @author Kay Kasemir
 */
public class BooleanOption extends Option
{
    /** Current value */
    private boolean value;

    /**
     * Construct String option
     *
     * @param parser
     *            Parser to which to add
     * @param option
     *            Option name: "-something"
     * @param info
     *            Help string
     */
    public BooleanOption(final ArgParser parser, final String option, final String info, boolean default_val) {
        super(parser, option, info);
        value = default_val;
    }


    /** Construct String option
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param info Help string
     */
    public BooleanOption(final ArgParser parser,
                         final String option,
                         final String info)
    {
        this(parser, option, info, false);
    }

    /** @return Value */
    public boolean get()
    {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public int handle(final String[] args, final int position) throws Exception
    {
        if (matchesThisOption(args[position]))
        {
            value = true;
            return 1;
        }
        return 0;
    }

    @Override
    public String toString()
    {
        return getOption() + "=" + value; //$NON-NLS-1$
    }
}
