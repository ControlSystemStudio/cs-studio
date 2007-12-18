package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.archive.ArchiveServer;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.model.IArchiveDataSource;
import org.csstudio.platform.model.IProcessVariableWithArchive;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.core.runtime.PlatformObject;

/** One item (row) of the name table.
 *  <p>
 *  Adapts to IProcessVariableName so that names can be 'dragged'.
 *  
 *  @author Kay Kasemir
 */
public class NameTableItem
    extends PlatformObject
    implements IProcessVariableWithArchive
{
    final private String pv;
    final private IArchiveDataSource archive;
    final private ArchiveServer server;
    final private ITimestamp start, end;
    
    /** Constructor from pieces. */
    public NameTableItem(final ArchiveServer server, final int key,
            final String pv,
            final ITimestamp start, final ITimestamp end) throws Exception
    {
        super();
        this.pv = pv;
        this.archive = CentralItemFactory.createArchiveDataSource(
                            server.getURL(), key, server.getArchiveName(key));
        this.server = server;
        this.start = start;
        this.end = end;
    }
    
    public String getName()
    {   return pv;    }

    public String getTypeId()
    {   return IProcessVariableWithArchive.TYPE_ID;    }

    public IArchiveDataSource getArchiveDataSource()
    {   return archive;   }

    /** @return The archive name. */
    public String getArchiveName()
    {
        try
        {
            return server.getArchiveName(archive.getKey());
        }
        catch (Exception e)
        {
            Plugin.getLogger().warn("Archive NameTableItem: No name for key " //$NON-NLS-1$
                            + archive.getKey(), e);
            return "<unknown>"; //$NON-NLS-1$
        }
    }

    public ITimestamp getStart()
    {   return start; }

    public ITimestamp getEnd()
    {   return end; }
}
