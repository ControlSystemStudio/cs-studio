package org.csstudio.apputil.args;

/** Option that reads one string argument.
 *  @author Kay Kasemir
 */
public class StringOption extends Option
{
    /** Current value */
    private String value;
    
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
        super(parser, option, info);
        value = default_value;
    }

    /** @return Value */
    public String get()
    {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public int handle(final String[] args, final int position) throws Exception
    {
        if (haveParameter(args, position))
        {
            value = args[position+1];
            return 2;
        }
        return 0;
    }
}
