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
import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.data.values.ITimestamp;


/** Main access point to the EPICS CA local history cash
 *  @author Albert Kagarmanov
 */
public class ArchiveServer extends org.csstudio.archive.ArchiveServer
{
	private final ServerInfoRequest server_info_request;
	private final ArchivesRequest archives_request;
	/** Factory method.
     *  @return Returns initialized instance of the Archive interface
     *
     *  @see #Archive(String)
     */
	@SuppressWarnings("nls")
    public static org.csstudio.archive.ArchiveServer getInstance(final String url_text)
        throws Exception
	{
        if (! url_text.startsWith("archiveRecord://")) {
            throw new Exception("Need archiveRecord:// URL");
        }
        return new ArchiveServer();
	}

    private ArchiveServer()
    {
        server_info_request = new ServerInfoRequest();
        server_info_request.read();
        // .. and archive keys
        archives_request = new ArchivesRequest();
        archives_request.read();
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

    /* @see org.csstudio.archive.Archive#getSeverity(int) */
    public SeverityImpl getSeverity(final int severity)
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

    /**
     * {@inheritDoc}
     */
	@Override
    public final ArchiveInfo[] getArchiveInfos()
	{
		return archives_request.getArchiveInfos();
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public final NameInfo[] getNames(final int key, final String pattern)  throws ArchiveAccessException
	{
		return null; // no info
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getServerName() {
		return "archiveRecord://";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return "archiveRecord://";
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public ArchiveValues[] getSamples(final int key, final String[] names,
			final ITimestamp start, final ITimestamp end, final String request_type,
            final Object request_parms[])
        throws ArchiveAccessException
	{
        final int request_code = 0;
		final ValuesRequest values = new ValuesRequest(this,
				key, names, start, end, request_code, request_parms);
		values.read();
		return values.getArchivedSamples();
	}
}
