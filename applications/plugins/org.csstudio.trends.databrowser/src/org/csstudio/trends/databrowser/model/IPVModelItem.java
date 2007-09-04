package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IValue;
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
        /** If possible, get the raw data.
         *  <p>
         *  This was added at the request of DESY.
         *  Ordinarily, the OPTIMIZED request should
         *  provide RAW data automatically once we zoom
         *  in far enough, so all you get from this
         *  request is a waste of memory.
         */
        RAW(Messages.Request_raw),
        
        /** If possible, get data optimized for plotting. */
        OPTIMIZED(Messages.Request_optimized);
        
        final private String name;
        
        private static String [] type_strings = null;
        
        private RequestType(String name)
        {
            this.name = name;
        }
        
        /** @return Localized name of this request type */
        public String getName()
        {
            return name;
        }
        
        /** @return Array of localized names for all available request types */
        public static String [] getTypeStrings()
        {
            if (type_strings == null)
            {
                RequestType[] types = RequestType.values();
                type_strings = new String[types.length]; 
                for (int i = 0; i < types.length; i++)
                    type_strings[i] = types[i].getName();
            }
            return type_strings;
        }
        
        /** Obtain a request type from its ordinal
         *  @return RequestType for the given ordinal. 
         */
        public static RequestType fromOrdinal(int ordinal)
        {   // This is expensive, but java.lang.Enum offers no easy way...
            for (RequestType id : RequestType.values())
                if (id.ordinal() == ordinal)
                    return id;
            throw new Error("Invalid ordinal " + ordinal); //$NON-NLS-1$
        }
    };
    
    /** @return current request type */
    public RequestType getRequestType();
    
    /** Set the request type. */
    public void setRequestType(RequestType new_request_type);
    
    /** Add samples obtained from the archive.
     *  <p>
     *  Called from a non-GUI thread!
     *  @param source The archive data source description
     *  @param samples The samples (non-null, more than 0)
     */
	public void addArchiveSamples(final String source, final IValue samples[]);

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