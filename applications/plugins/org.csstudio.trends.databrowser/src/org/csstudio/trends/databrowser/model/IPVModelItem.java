package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.model.IArchiveDataSource;

/** Interface to a model item with archive data sources.
 *  @see Model
 *  @author Kay Kasemir
 */
public interface IPVModelItem extends IModelItem
{
    /** How to request data from the archive server. */
    enum RequestType
    {
        /** If possible, get the raw data. */
        RAW,
        
        /** If possible, get data optimized for plotting. */
        OPTIMIZED
    };
    
    /** @return current request type */
    public RequestType getRequestType();
    
    /** Set the request type. */
    public void setRequestType(RequestType new_request_type);
    
    /** Add samples obtained from the archive.
     *  <p>
     *  Called from a non-GUI thread!
     */
	public void addArchiveSamples(ArchiveValues samples);

    /** @return The archive data source descriptions. */
    public IArchiveDataSource[] getArchiveDataSources();
    
    /** Add another archive data source. */
    public void addArchiveDataSource(IArchiveDataSource archive);

    /** Remove given archive data source. */
    public void removeArchiveDataSource(IArchiveDataSource archive);
    
    /** Move given archive data source 'up' in the list. */
    public void moveArchiveDataSourceUp(IArchiveDataSource archive);
    
    /** Move given archive data source 'down' in the list. */
    public void moveArchiveDataSourceDown(IArchiveDataSource archive);
}