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

/** Option that reads one string argument.
 *  @author Kay Kasemir
 */
public class StringOption extends Option
{
    /** Current value */
    private List<String> values = new ArrayList<String>();
    private final String default_value;

    /** Construct String option
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param info Help string
     *  @param default_value Default value
     */
    public StringOption(final ArgParser parser,
                        final String option,
                        final String info,
                        final String default_value)
    {
        this(parser, option, "<string>", info, default_value); //$NON-NLS-1$
    }

    /** Construct String option
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param arg_info Information about argument
     *  @param info Help string
     *  @param default_value Default value
     */
    public StringOption(final ArgParser parser,
                        final String option,
                        final String arg_info,
                        final String info,
                        final String default_value)
    {
        super(parser, option, arg_info, info);
        this.default_value = default_value;
    }

    /** @return Value */
    public String get()
    {
        if (values.size() < 1)
            return default_value;
        return values.get(values.size() - 1);
    }

    public List<String> getMany() {
        return values;
    }

    /** {@inheritDoc} */
    @Override
    public int handle(final String[] args, final int position) throws Exception
    {
        if (haveParameter(args, position))
        {
            values.add(args[position + 1]);
            return 2;
        }
        return 0;
    }

    @Override
    public String toString()
    {
        return getOption() + "=" + get(); //$NON-NLS-1$
    }
}
