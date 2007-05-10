package org.csstudio.archive.desy.epics_local;
import gov.aps.jca.dbr.Severity;

import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.archive.desy.epics_local.ValuesRequest;
import org.csstudio.platform.util.ITimestamp;
/** Main access point to the EPICS CA local history cash 
 *  @author Albert Kagarmanov
 */
public class ArchiveServer extends org.csstudio.archive.ArchiveServer
{
	private ServerInfoRequest server_info_request;
	private ArchivesRequest archives_request;
	private int last_request_error = 0;		
    /** Factory method.
     *  @return Returns initialized instance of the Archive interface
     *          
     *  @see #Archive(String)
     */
	@SuppressWarnings("nls")
    public static org.csstudio.archive.ArchiveServer getInstance(String url_text)
        throws Exception
	{
        if (! url_text.startsWith("archiveRecord://"))
            throw new Exception("Need archiveRecord:// URL");
        return new ArchiveServer(url_text);
	}
    private ArchiveServer(String url_text) throws Exception
    {
        server_info_request = new ServerInfoRequest();
        server_info_request.read();  
        // .. and archive keys
        archives_request = new ArchivesRequest();
        archives_request.read(); 
    }
    
    /* @see org.csstudio.archive.Archive#getDescription() */
    public String getDescription()
    {
    	return server_info_request.getDescription();
    }

    /* @see org.csstudio.archive.Archive#getVersion() */
    public int getVersion()
    {
    	return server_info_request.getVersion();

    }
    
    /* @see org.csstudio.archive.Archive#getRequestTypes() */
    public String[] getRequestTypes()
    {
    	 return server_info_request.getRequestTypes();
    }
    
    /* @see org.csstudio.archive.Archive#getSeverity(int) */
    public SeverityImpl getSeverity(int severity)
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
		return null; // no info
	}

    /* @see org.csstudio.archiveArchiveServer#getNames() */
	public ArchiveValues[] getSamples(int key, String[] names,
			ITimestamp start, ITimestamp end, int request_type,
            Object request_parms[]) 
        throws Exception
	{  
		ValuesRequest values = new ValuesRequest(this,key, names, start, end, request_type, request_parms);
		this.last_request_error = values.read();
		return values.getArchivedSamples();
		
	}

	@Override
	public int getLastRequestError() {
		return last_request_error;
	}
	
	@Override
	public String getServerName() {
		return "archiveRecord://";
	}
	
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return "archiveRecord://";
	}
	
	public int getRequestType(String request_name) {return 0;}
/*	 @Override
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
	*/
}
