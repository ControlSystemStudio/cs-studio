package org.csstudio.archive.rdb.engineconfig;

import java.net.URL;

import org.csstudio.archive.rdb.Retention;
import org.csstudio.archive.rdb.internal.RDBArchiveImpl;

/** Info for a sample engine.
 *  @author Kay Kasemir
 */
public class SampleEngineConfig
{
    final private RDBArchiveImpl archive;
    final private int id;
    final private String name;
    final private String description;
    final private URL url;

    public SampleEngineConfig(final RDBArchiveImpl archive,
            final int id, final String name,
            final String description, final URL url)
    {
        this.archive = archive;
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
    }
    
    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public URL getUrl()
    {
        return url;
    }

    /** Add group of channels
     *  @param group_name Name that identifies the group
     *  @param retention How to retain the values over time
     *  @return ChannelGroup
     *  @throws Exception on error
     */
    public ChannelGroupConfig addGroup(String group_name, Retention retention)
        throws Exception
    {
        return archive.addGroup(this, group_name,retention);
    }

    /** Get all groups under this engine.
     *  @return ChannelGroup array
     */
    public ChannelGroupConfig[] getGroups() throws Exception
    {
        ChannelGroupHelper groups = new ChannelGroupHelper(archive);
        return groups.get(getId());
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return String.format("Engine %s (%s): %s, URL %s",
                name, id, description, url.toString());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SampleEngineConfig))
            return false;
        final SampleEngineConfig other = (SampleEngineConfig) obj;
        return id == other.id && 
            name.equals(other.name) &&
            description.equals(other.description) &&
            url.equals(other.url);
    }
}
