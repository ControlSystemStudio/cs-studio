package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveInfo;
import org.csstudio.platform.model.IArchiveDataSource;
import org.eclipse.core.runtime.PlatformObject;

/** One item in the archive table.
 *  <p>
 *  From the archive server, we get <code>ArchiveInfo</code> data,
 *  and for Drag-and-Drop we need to support <code>IArchiveDataSource</code>,
 *  so these table items do both.
 *  @author Kay Kasemir
 */
public class ArchiveTableItem
    extends PlatformObject
    implements ArchiveInfo, IArchiveDataSource
{
    private String url;
    private int key;
    private String name;
    private String description;
    
    /** Constructor from pieces. */
    public ArchiveTableItem(String url, int key, String name, String description)
    {
        this.url = url;
        this.key = key;
        this.name = name;
        this.description = description;
    }

    /** @see org.csstudio.data.exchange.IArchiveDataSource */
    public String getUrl()
    {   return url;  }

    /** @see org.csstudio.archive.ArchiveInfo
     *  @see org.csstudio.data.exchange.IArchiveDataSource */
    public int getKey()
    {   return key;  }

    /** @see org.csstudio.archive.ArchiveInfo */
    public String getName()
    {   return name;  }

    public String getTypeId()
    {   return IArchiveDataSource.TYPE_ID;  }

    /** @see org.csstudio.archive.ArchiveInfo */
    public String getDescription()
    {   return description;  }
 }
