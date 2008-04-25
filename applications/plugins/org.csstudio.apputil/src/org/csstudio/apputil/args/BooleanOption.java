package org.csstudio.apputil.args;

/** Option that sets a flag (no additional argument read).
 *  @author Kay Kasemir
 */
public class BooleanOption extends Option
{
    /** Current value */
    private boolean value = false;
    
    /** Construct String option
     *  @param parser Parser to which to add
     *  @param option Option name: "-something"
     *  @param info Help string
     */
    public BooleanOption(final ArgParser parser,
                         final String option,
                         final String info)
    {
        super(parser, option, info);
    }

    /** @return Value */
    public boolean get()
    {
        return value;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
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
}
