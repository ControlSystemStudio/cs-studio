package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.IEnumeratedMetaData;

/** Implementation of {@link IEnumeratedMetaData}.
 *  @author Kay Kasemir 
 */
public class EnumeratedMetaData implements IEnumeratedMetaData
{
    /** The enumeration strings for the possible values of an EnumSample. */
	private final String states[];

	/** Constructor for meta data from pieces. */
	public EnumeratedMetaData(String states[])
	{
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
