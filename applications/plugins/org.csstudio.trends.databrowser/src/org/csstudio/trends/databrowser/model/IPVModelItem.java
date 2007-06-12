package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.model.IArchiveDataSource;

/** Interface to a model item with archive data sources.
 *  @see Model
 *  @author Kay Kasemir
 */
public interface IPVModelItem extends IModelItem
{
    /** Add samples obtained from the archive.
     *  <p>
     *  Called from a non-GUI thread!
     */
	public abstract void addArchiveSamples(ArchiveValues samples);

    /** @return The archive data source descriptions. */
    public abstract IArchiveDataSource[] getArchiveDataSources();
    
    /** Add another archive data source. */
    public abstract void addArchiveDataSource(IArchiveDataSource archive);

    /** Remove given archive data source. */
    public abstract void removeArchiveDataSource(IArchiveDataSource archive);
    
    /** Move given archive data source 'up' in the list. */
    public abstract void moveArchiveDataSourceUp(IArchiveDataSource archive);
    
    /** Move given archive data source 'down' in the list. */
    public abstract void moveArchiveDataSourceDown(IArchiveDataSource archive);
}