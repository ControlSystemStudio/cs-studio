package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IEnumeratedMetaData;

/** Implementation of {@link IEnumeratedMetaData}.
 *  @author Kay Kasemir 
 */
public class EnumeratedMetaData implements IEnumeratedMetaData
{
    /** The enumeration strings for the possible values of an EnumSample. */
	private final String states[];

	/** Constructor for meta data from pieces.
     *  @param states array of states. Must not be <code>null</code>
     */
	public EnumeratedMetaData(String states[])
	{
        if (states == null)
            throw new IllegalArgumentException("Zero state array"); //$NON-NLS-1$
		this.states = states;
	}

    /** {@inheritDoc} */
	public final String[] getStates()
	{	return states;	}
	
    /** {@inheritDoc} */
	public String getState(int state)
	{
		if (state < 0  ||  state >= states.length)
			return "<enum " + state + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		return states[state];
	}
	
    /** @return <code>true</code> if given meta data equals this */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (! (obj instanceof IEnumeratedMetaData))
            return false;
        final IEnumeratedMetaData other = (IEnumeratedMetaData) obj;
        final String[] other_states = other.getStates();
        if (other_states.length != states.length)
            return false;
        for (int i=0; i<states.length; ++i)
        {
            if (!other_states[i].equals(states[i]))
                return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
	public String toString()
	{
        StringBuffer buf = new StringBuffer();
        buf.append("EnumeratedMetaData: " + states.length + " states:\n");
        for (String state : states)
            buf.append("    '" + state + "'\n");
        return buf.toString();
	}
}
