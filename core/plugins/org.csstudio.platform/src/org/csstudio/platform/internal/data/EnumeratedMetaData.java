/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
