package org.csstudio.archive.channelarchiver;

import java.net.UnknownHostException;
import java.util.ArrayList;

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
        try
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
        catch (UnknownHostException ex)
        {
            throw new Exception("Unknown host in URL " + url_text);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public String getServerName() 
    {
    	return "Channel Archiver"; //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    public String getURL()
    {
        return url;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription()
    {
        return server_info_request.getDescription();
    }

    /** {@inheritDoc} */
    @Override
    public int getVersion()
    {
        return server_info_request.getVersion();
    }
    
    /** {@inheritDoc} */
    @Override
    public String[] getRequestTypes()
    {
        return server_info_request.getRequestTypes();
    }
    
    /** Helper for locating a request code by name.
     *  <p> 
     * @param request_name For example: GET_RAW.
     * @return The 'request_type' ID for a given request type string.
     * @throws Exception when asking for unsupported request type.
     * @see #getRequestTypes()
     */
    @SuppressWarnings("nls")
    int getRequestCode(String request_name) throws Exception
    {
        final String request_types[] = getRequestTypes();
        for (int i=0; i<request_types.length; ++i)
            if (request_types[i].equalsIgnoreCase(request_name)) // add  IgnoreCase Albert
                return i;
        throw new Exception("Unsupported request type '" + request_name + "'");
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
 	
    /** {@inheritDoc} */
    @Override
	public final NameInfo[] getNames(int key, String pattern) throws Exception
	{
		NamesRequest infos = new NamesRequest(key, pattern);
		infos.read(xmlrpc);
		return infos.getNameInfos();
	}

    /** Active requests. Synchronize for access */
    final private ArrayList<ValuesRequest> requests = new ArrayList<ValuesRequest>();
    
    /** {@inheritDoc} */
    @Override
	public ArchiveValues[] getSamples(int key, String[] names,
			ITimestamp start, ITimestamp end,
            String request_type, Object request_parms[]) 
        throws Exception
	{
        final int request_code = getRequestCode(request_type);
		ValuesRequest values = new ValuesRequest(this,
				key, names, start, end, request_code, request_parms);
		synchronized (requests)
        {
		    requests.add(values);
        }
		try
		{
		    values.read(xmlrpc);
		}
		finally
		{
	        synchronized (requests)
	        {
	            requests.remove(values);
	        }
		}
		return values.getArchivedSamples();
	}

    /** {@inheritDoc} */
    @Override
    public void cancel()
    {
        synchronized (requests)
        {
            for (ValuesRequest request : requests)
                request.cancel();
        }
    }
}
