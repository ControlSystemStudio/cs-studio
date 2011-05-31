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
 package org.csstudio.archive.desy.aapi;

//import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;

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
	private final AAPI aapiClient;
	private final ServerInfoRequest server_info_request;
	private final ArchivesRequest archives_request;
	private int last_request_error = 0;

    /** Factory method.
     *  @return Returns initialized instance of the Archive interface
     *          for the AAPI data server.
     *  @see #Archive(String)
     */
	@SuppressWarnings("nls")
    public static org.csstudio.archive.ArchiveServer getInstance(final String url_text)
        throws Exception
	{
        if (! url_text.startsWith("aapi://")) {
            throw new Exception("Need aapi:// URL");
        }
        return new ArchiveServer(url_text.substring(7));
	}

    /** Connect to a AAPI-data server.
     *
     * @param host:port For example
     *        "krynfs.desy.de:4050"
     * @throws Exception
     */
    private ArchiveServer(final String url_text) throws Exception
    {
        // Parser url
    	AAPIport = defaultAAPIport;
    	AAPIhost = defaultAAPIhost;
    	if((url_text != null) && (url_text.length()> 0 )) {
    		final int pos = url_text.indexOf(':');
    		if(pos != -1) {
    			AAPIhost = url_text.substring(0,pos);
    			try {AAPIport = Integer.parseInt(url_text.substring(pos+1));}
    			catch (final Exception e) {
    				System.out.println(url_text + ": -error in HOST:PORT format! Will use default one");
    				AAPIport = defaultAAPIport;
    				AAPIhost = defaultAAPIhost;}
    		} else {
                System.out.println(url_text + ": -wrong HOST:PORT format. Will use default one");
            }
    	} else {
            System.out.println(url_text + ": -empty HOST:PORT format. Will use default one");
        }

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
    @Override
    public String getDescription()
    {
    	return server_info_request.getDescription();
        //return aapiClient.getDescription();
    }

    /* @see org.csstudio.archive.Archive#getVersion() */
    @Override
    public int getVersion()
    {
    	return server_info_request.getVersion();
//    	return aapiClient.getVersion();
    }

    /* @see org.csstudio.archive.Archive#getRequestTypes() */
    @Override
    public String[] getRequestTypes()
    {
    	 return server_info_request.getRequestTypes();
//        return aapiClient.getRequestedTypeList();
    }

    /* @see org.csstudio.archive.Archive#getSeverity(int) */
    public ISeverity getSeverity(final int severity)
    {
    	return server_info_request.getSeverity(severity);
    }

    /* @see org.csstudio.archive.Archive#getStatus(int) */
    public String getStatus(final int status)
    {

        final String[] status_strings = server_info_request.getStatusStrings();
        if ((status >= 0)  &&  (status < status_strings.length)) {
            return status_strings[status];
        }
        return "<status " + Integer.toString(status) + ">";

    }

	/* @see org.csstudio.archiveArchiveServer#getArchiveInfos() */
	@Override
    public final ArchiveInfo[] getArchiveInfos()
	{

		return archives_request.getArchiveInfos();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
    public final NameInfo[] getNames(final int key, final String pattern) throws ArchiveAccessException
	{

		final NamesRequest infos = new NamesRequest(key, pattern);
		infos.read(aapiClient);
		return infos.getNameInfos();

	}

	@Override
    public ArchiveValues[] getSamples(final int key, final String[] names,
			final ITimestamp start, final ITimestamp end, final String request_type,
            final Object request_parms[])
        throws ArchiveAccessException
	{
        final int request_code = getRequestCode(request_type);
		final ValuesRequest values = new ValuesRequest(this,
				key, names, start, end, request_code, request_parms);
		this.last_request_error = values.read(aapiClient);
		return values.getArchivedSamples();
	}

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
		return "aapi://" + AAPIhost + ":" + AAPIport;
	}

     int getRequestCode(final String request_name)
	 {
    	 if( request_name.equalsIgnoreCase("AVERAGE") ) {
            return AAPI.MIN_MAX_AVERAGE_METHOD;
        }
    	 return AAPI.NO_FILTERING_M;
  	}
}
