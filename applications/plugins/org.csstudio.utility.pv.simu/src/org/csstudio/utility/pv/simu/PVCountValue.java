package org.csstudio.utility.pv.simu;

/** Dynamic value that holds the number of sim/sys/loc PVs
 *  @author Kay Kasemir
 */
public class PVCountValue extends DynamicValue
{
    /** Initialize
     *  @param name
     */
    public PVCountValue(final String name)
    {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void update()
    {
        final double count = LocalPVFactory.getValueCount() +
                             SimulatedPVFactory.getValueCount() +
                             SystemPVFactory.getValueCount();
        setValue((double )count);
    }
}
