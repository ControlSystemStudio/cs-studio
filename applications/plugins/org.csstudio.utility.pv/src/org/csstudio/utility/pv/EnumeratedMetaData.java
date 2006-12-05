package org.csstudio.utility.pv;

/** Meta Data for enum Value types.
 *  @author Kay Kasemir
 */
public class EnumeratedMetaData implements MetaData
{
    private final String states[];

    /** Constructor for meta data from pieces. */
    public EnumeratedMetaData(String states[])
    {
        this.states = states;
    }

    /** @return The state strings. */
    public final String[] getStates()
    {   return states;  }
    
    /** Convenience routine for getting a state string.
     *  <p>
     *  Also allows getting undefined states.
     *  @param state The state to get.
     *  @return A state string.
     */
    public String getState(int state)
    {
        if (state < 0  ||  state >= states.length)
            return "<enum " + state + ">"; //$NON-NLS-1$ //$NON-NLS-2$
        return states[state];
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "EnumeratedMetaData:\n" + states + "\n";     
    }
}
