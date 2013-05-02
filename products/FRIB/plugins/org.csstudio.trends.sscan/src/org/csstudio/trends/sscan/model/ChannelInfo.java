/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import java.io.Serializable;

import org.csstudio.csdata.ProcessVariable;

/** Archive search result, information about one channel
 *  @author Kay Kasemir
 */
public class ChannelInfo implements Serializable
{
    /** Default ID for {@link Serializable} */
    private static final long serialVersionUID = 1L;

    final private ProcessVariable name;


    /** Initialize
     *  @param archive IArchiveDataSource for channel
     *  @param name    Channel name
     */
    public ChannelInfo(final String name)
    {
        this.name = new ProcessVariable(name);

    }

    /** @return ProcessVariable */
    public ProcessVariable getProcessVariable()
    {
        return name;
    }


    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof ChannelInfo))
            return false;
        final ChannelInfo other = (ChannelInfo) obj;
        return other.name.equals(name);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = prime + name.hashCode();
	    return result;
    }

    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return name.getName();
    }
}
