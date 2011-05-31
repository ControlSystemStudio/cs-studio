/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import java.util.Arrays;

import org.csstudio.data.values.IEnumeratedMetaData;

/** Implementation of {@link IEnumeratedMetaData}.
 *  @author Kay Kasemir
 */
public class EnumeratedMetaData implements IEnumeratedMetaData
{
    private static final long serialVersionUID = 1L;

    /** The enumeration strings for the possible values of an EnumSample. */
	private final String states[];

	/** Constructor for meta data from pieces.
     *  @param states array of states. Must not be <code>null</code>
     */
	public EnumeratedMetaData(final String states[])
	{
        if (states == null)
            throw new IllegalArgumentException("Zero state array"); //$NON-NLS-1$
		this.states = states;
	}

    /** {@inheritDoc} */
	@Override
    public final String[] getStates()
	{	return states;	}

    /** {@inheritDoc} */
	@Override
    public String getState(final int state)
	{
		if (state < 0  ||  state >= states.length)
			return "<enum " + state + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		return states[state];
	}

    /** @return <code>true</code> if given meta data equals this */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
            return true;
        if (! (obj instanceof IEnumeratedMetaData))
            return false;
        final IEnumeratedMetaData other = (IEnumeratedMetaData) obj;
        return Arrays.equals(states, other.getStates());
    }

    /** @return Hash code based on state strings */
    @Override
    public int hashCode()
    {
    	return Arrays.hashCode(states);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
	public String toString()
	{
        final StringBuffer buf = new StringBuffer();
        buf.append("EnumeratedMetaData: " + states.length + " states:\n");
        for (String state : states)
            buf.append("    '" + state + "'\n");
        return buf.toString();
	}
}
