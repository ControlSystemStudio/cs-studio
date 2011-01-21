/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.archive;

import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.trends.databrowser.model.ArchiveDataSource;

/** Archive search result, information about one channel
 *  @author Kay Kasemir
 */
public class ChannelInfo implements IProcessVariableWithArchive
{
    final private ArchiveDataSource archive;
    final private String name;

    /** Initialize
     *  @param archive IArchiveDataSource for channel
     *  @param name    Channel name
     */
    public ChannelInfo(final ArchiveDataSource archive, final String name)
    {
        this.archive = archive;
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeId()
    {
        return IProcessVariableWithArchive.TYPE_ID;
    }

    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public ArchiveDataSource getArchiveDataSource()
    {
        return archive;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(final Class adapter)
    {
        if (adapter == IArchiveDataSource.class)
            return getArchiveDataSource();
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof ChannelInfo))
            return false;
        final ChannelInfo other = (ChannelInfo) obj;
        return other.name.equals(name) && other.getArchiveDataSource().equals(archive);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = prime + name.hashCode();
	    result = prime * result + archive.hashCode();
	    return result;
    }

    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return name + "[" + archive.getName() + "]";
    }
}
