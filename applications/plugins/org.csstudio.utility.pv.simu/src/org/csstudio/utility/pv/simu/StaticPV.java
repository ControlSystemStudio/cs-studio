package org.csstudio.utility.pv.simu;


/** Static PV.
 *  <p>
 *  PV that displays the underlying Value, but never changes.
 *  
 *  @author Kay Kasemir
 */
public class StaticPV extends BasicPV<Value>
{
    /** Initialize
     *  @param prefix PV type prefix
     *  @param value PV name
     */
    public StaticPV(final String prefix, final Value value)
    {
        super(prefix, value);
    }

    /** {@inheritDoc} */
    public synchronized void start() throws Exception
    {
        running = true;
        // Send initial update
        changed(value);
    }

    /** {@inheritDoc} */
    public void stop()
    {
        running = false;
    }
}
