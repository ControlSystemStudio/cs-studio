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
    public String getTypeId()
    {
        return IProcessVariableWithArchive.TYPE_ID;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public ArchiveDataSource getArchiveDataSource()
    {
        return archive;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class adapter)
    {
        if (adapter == IArchiveDataSource.class)
            return getArchiveDataSource();
        return null;
    }
    
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof ChannelInfo))
            return false;
        final ChannelInfo other = (ChannelInfo) obj;
        return other.name.equals(name) && other.getArchiveDataSource().equals(archive);
    }
    
    /** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return name + "[" + archive.getName() + "]";
    }
}
