package org.csstudio.archive.channelarchiver;

import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.ITimestamp;

/** Main access point to the ChannelArchiver network data server.
 *  @author Kay Kasemir
 *  @author Blaz Lipuscek
 */
public class ArchiveServer extends org.csstudio.archive.ArchiveServer
{
    private final String url;
	private final XmlRpcClient xmlrpc;
	private final ServerInfoRequest server_info_request;
	private final ArchivesRequest archives_request;
		
    /** Connect to a ChannelArchiver's network data server.
     *  
     * @param url_text For example
     *        "xnds://my_server.org/archive/cgi/ArchiveDataServer.cgi"
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public ArchiveServer(String url_text) throws Exception
    {
        url = url_text;
        
        // Patch 'xdns' into http, but keep the 'official' URL
        // as received.
        if (url_text.startsWith("xnds://"))
            url_text = "http://" + url_text.substring(7);
        // Create client
        xmlrpc = new XmlRpcClient(url_text);
        
        // Get server info
        server_info_request = new ServerInfoRequest();
        server_info_request.read(xmlrpc);
        
        // .. and archive keys
        archives_request = new ArchivesRequest();
        archives_request.read(xmlrpc);
    }
    
    /* @see org.csstudio.archive.Archive#getURL() */
    @Override
    public String getURL()
    {
        return url;
    }

    /* @see org.csstudio.archive.Archive#getDescription() */
    @Override
    public String getDescription()
    {
        return server_info_request.getDescription();
    }

    /* @see org.csstudio.archive.Archive#getVersion() */
    @Override
    public int getVersion()
    {
        return server_info_request.getVersion();
    }
    
    /* @see org.csstudio.archive.Archive#getRequestTypes() */
    @Override
    public String[] getRequestTypes()
    {
        return server_info_request.getRequestTypes();
    }
    
    public String getServerName() 
    {
    	return "XNDSServer"; //$NON-NLS-1$
    }
    
    /** @return Severity for an EPICS severity code. */
    SeverityImpl getSeverity(int severity)
    {
        return server_info_request.getSeverity(severity);
    }

    /** @return EPICS/ChannelArchiver status string for given code */
    String getStatus(SeverityImpl severity, int status)
    {
        if (severity.statusIsText())
        {
            final String[] status_strings = server_info_request.getStatusStrings();
            if (status >= 0  &&  status < status_strings.length)
                return status_strings[status];
            // else: Fall through...
        }
        // return the number as a string
        return Integer.toString(status);            
    }
    
	/** Get the archives under this server.
     *  <p>
     *  This implementation retrieves the list on connection,
     *  so this method returns immediately, but
     *  in general, that might not be the case.
     *  @see org.csstudio.archiveArchiveServer#getArchiveInfos()
     */
    @Override
    public final ArchiveInfo[] getArchiveInfos()
	{
		return archives_request.getArchiveInfos();
	}
 	
    /** @see #getArchiveInfos() 
     *  @see org.csstudio.archiveArchiveServer#getNames() 
     */
    @Override
	public final NameInfo[] getNames(int key, String pattern) throws Exception
	{
		NamesRequest infos = new NamesRequest(key, pattern);
		infos.read(xmlrpc);
		return infos.getNameInfos();
	}

    /* @see org.csstudio.archiveArchiveServer#getNames() */
    @Override
	public ArchiveValues[] getSamples(int key, String[] names,
			ITimestamp start, ITimestamp end,
            int request_type, Object request_parms[]) 
        throws Exception
	{
		ValuesRequest values = new ValuesRequest(this,
				key, names, start, end, request_type, request_parms);
		values.read(xmlrpc);
		return values.getArchivedSamples();
	}
}
