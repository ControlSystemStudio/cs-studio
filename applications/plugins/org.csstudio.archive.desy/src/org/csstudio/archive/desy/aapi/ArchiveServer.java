package org.csstudio.archive.desy.aapi;

//import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

import AAPI.AAPI;


/** Main access point to the DESY Archive API (AAPI) data 
 *  @author Albert Kagarmanov
 */
public class ArchiveServer extends org.csstudio.archive.ArchiveServer
{
	private final static String defaultAAPIhost = "krynfs.desy.de";
	private final static int    defaultAAPIport = 4054;
	
	private String AAPIhost;
	private int AAPIport;
	private AAPI aapiClient;
	private ServerInfoRequest server_info_request;
	private ArchivesRequest archives_request;
	private int last_request_error = 0;
		
    /** Factory method.
     *  @return Returns initialized instance of the Archive interface
     *          for the AAPI data server.
     *  @see #Archive(String)
     */
	@SuppressWarnings("nls")
    public static org.csstudio.archive.ArchiveServer getInstance(String url_text)
        throws Exception
	{
        if (! url_text.startsWith("aapi://"))
            throw new Exception("Need aapi:// URL");
        return new ArchiveServer(url_text.substring(7));
	}
    
    /** Connect to a AAPI-data server.
     *  
     * @param host:port For example
     *        "krynfs.desy.de:4050"
     * @throws Exception
     */
    private ArchiveServer(String url_text) throws Exception
    {
        // Parser url
    	AAPIport = defaultAAPIport;
    	AAPIhost = defaultAAPIhost;
    	if((url_text != null) && (url_text.length()> 0 )) { 
    		int pos = url_text.indexOf(':');
    		if(pos != -1) {
    			AAPIhost = url_text.substring(0,pos); 
    			try {AAPIport = Integer.parseInt(url_text.substring(pos+1));}
    			catch (Exception e) {
    				System.out.println(url_text + ": -error in HOST:PORT format! Will use default one");
    				AAPIport = defaultAAPIport; 
    				AAPIhost = defaultAAPIhost;}
    		} else System.out.println(url_text + ": -wrong HOST:PORT format. Will use default one");
    	}	  else System.out.println(url_text + ": -empty HOST:PORT format. Will use default one");
    	
    	 System.out.println("DESY Host="+AAPIhost +" port=" + AAPIport);
    	
    	aapiClient = new AAPI(AAPIhost,AAPIport); 
        /* xmlrpc = new XmlRpcClient(url_text);*/
        // Get server info
        server_info_request = new ServerInfoRequest();
        server_info_request.read(aapiClient);  
        // .. and archive keys
        archives_request = new ArchivesRequest();
        archives_request.read(); 
    }
    
	
    /* @see org.csstudio.archive.Archive#getDescription() */
    public String getDescription()
    {
    	return server_info_request.getDescription();
        //return aapiClient.getDescription();
    }

    /* @see org.csstudio.archive.Archive#getVersion() */
    public int getVersion()
    {
    	return server_info_request.getVersion();
//    	return aapiClient.getVersion();
    }
    
    /* @see org.csstudio.archive.Archive#getRequestTypes() */
    public String[] getRequestTypes()
    {
    	 return server_info_request.getRequestTypes();
//        return aapiClient.getRequestedTypeList();
    }
    
    /* @see org.csstudio.archive.Archive#getSeverity(int) */
    public ISeverity getSeverity(int severity)
    { 
    	return server_info_request.getSeverity(severity);  	
    }

    /* @see org.csstudio.archive.Archive#getStatus(int) */
    public String getStatus(int status)
    {

        final String[] status_strings = server_info_request.getStatusStrings();
        if (status >= 0  &&  status < status_strings.length)
            return status_strings[status];
        return "<status " + Integer.toString(status) + ">";
        
    }
    
	/* @see org.csstudio.archiveArchiveServer#getArchiveInfos() */
	public final ArchiveInfo[] getArchiveInfos()
	{
		
		return archives_request.getArchiveInfos();
	}
 	
    /* @see org.csstudio.archiveArchiveServer#getNames() */
	public final NameInfo[] getNames(int key, String pattern)  throws Exception
	{

		NamesRequest infos = new NamesRequest(key, pattern);
		infos.read(aapiClient);
		return infos.getNameInfos();

	}

    /* @see org.csstudio.archiveArchiveServer#getNames() */
	public ArchiveValues[] getSamples(int key, String[] names,
			ITimestamp start, ITimestamp end, int request_type,
            Object request_parms[]) 
        throws Exception
	{  
		ValuesRequest values = new ValuesRequest(this,
				key, names, start, end, request_type, request_parms);
		this.last_request_error = values.read(aapiClient);
		return values.getArchivedSamples();
	}

	@Override
	public int getLastRequestError() {
		return last_request_error;
	}
	
	@Override
	public String getServerName() {
		return "AAPIServer";
	}
	
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return "aapi://krynfs.desy.de:4053";
	}
	
	 @Override
	 public int getRequestType(String request_name) throws Exception
	 {
		if (request_name == "AVERAGE") {
			return AAPI.AVERAGE_M;
		} else if (request_name == "RAW") {
			return AAPI.NO_FILTERING_M;
		} else if (request_name == "MIN_MAX_AVERAGE") {
			return AAPI.MIN_MAX_AVERAGE_M;
		} else if (request_name == "SHARP") {
			return AAPI.SHARP_M;
		} else if (request_name == "SPLINE") {
			return AAPI.SPLINE_M;
		}  
		return 1;
	}
}
