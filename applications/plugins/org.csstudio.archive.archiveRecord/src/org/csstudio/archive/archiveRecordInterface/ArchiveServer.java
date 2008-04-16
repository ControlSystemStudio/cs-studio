/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.archive.archiveRecordInterface;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.platform.data.ITimestamp;


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

//    /* @see org.csstudio.archiveArchiveServer#getNames() */
/*	public ArchiveValues[] getSamples(int key, String[] names,
			ITimestamp start, ITimestamp end, int request_type,
            Object request_parms[]) 
        throws Exception
	{  
		ValuesRequest values = new ValuesRequest(this,key, names, start, end, request_type, request_parms);
		this.last_request_error = values.read();
		return values.getArchivedSamples();
		
	}
*/
//	@Override
//	public int getLastRequestError() {
//		return last_request_error;
//	}
	
	@Override
	public String getServerName() {
		return "archiveRecord://";
	}
	
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return "archiveRecord://";
	}
    int getRequestCode(String request_name) throws Exception
	 {
    	return 0;
 	}
	

    /* @see org.csstudio.archiveArchiveServer#getNames() */
	public ArchiveValues[] getSamples(int key, String[] names,
			ITimestamp start, ITimestamp end, String request_type,
            Object request_parms[]) 
        throws Exception
	{  
        final int request_code = getRequestCode(request_type);
		ValuesRequest values = new ValuesRequest(this,
				key, names, start, end, request_code, request_parms);
		this.last_request_error = values.read();
		return values.getArchivedSamples();
	}
}
