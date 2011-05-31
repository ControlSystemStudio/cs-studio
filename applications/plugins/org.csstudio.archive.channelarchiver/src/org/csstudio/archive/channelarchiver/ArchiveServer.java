package org.csstudio.archive.channelarchiver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.archive.ArchiveAccessException;
import org.csstudio.archive.ArchiveInfo;
import org.csstudio.archive.ArchiveValues;
import org.csstudio.archive.NameInfo;
import org.csstudio.data.values.ITimestamp;

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
     * @throws MalformedURLException
     * @throws ArchiveAccessException
     */
    @SuppressWarnings("nls")
    public ArchiveServer(String url_text) throws MalformedURLException, ArchiveAccessException
    {

        url = url_text;

        // Patch 'xdns' into http, but keep the 'official' URL
        // as received.
        if (url_text.startsWith("xnds://")) {
            url_text = "http://" + url_text.substring(7);
        }
        // Create client
        xmlrpc = new XmlRpcClient(url_text);

        // Get server info
        server_info_request = new ServerInfoRequest();
        server_info_request.read(xmlrpc);

        // .. and archive keys
        archives_request = new ArchivesRequest();
        archives_request.read(xmlrpc);

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
     * @throws ArchiveAccessException when asking for unsupported request type.
     * @see #getRequestTypes()
     */
    @SuppressWarnings("nls")
    int getRequestCode(final String request_name) throws ArchiveAccessException
    {
        final String request_types[] = getRequestTypes();
        for (int i=0; i<request_types.length; ++i) {
            if (request_types[i].equalsIgnoreCase(request_name)) {
                return i;
            }
        }
        throw new ArchiveAccessException("Unsupported request type '" + request_name + "'");
    }

    /** @return Severity for an EPICS severity code. */
    SeverityImpl getSeverity(final int severity)
    {
        return server_info_request.getSeverity(severity);
    }

    /** @return EPICS/ChannelArchiver status string for given code */
    String getStatus(final SeverityImpl severity, final int status)
    {
        if (severity.statusIsText())
        {
            final String[] status_strings = server_info_request.getStatusStrings();
            if ((status >= 0)  &&  (status < status_strings.length)) {
                return status_strings[status];
                // else: Fall through...
            }
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

    /** {@inheritDoc}
     * @throws ArchiveAccessException */
    @Override
    public final NameInfo[] getNames(final int key, final String pattern) throws ArchiveAccessException
    {
        final NamesRequest infos = new NamesRequest(key, pattern);
        infos.read(xmlrpc);
        return infos.getNameInfos();
    }

    /**
     * Active requests. Synchronize for access
     */
    final private List<ValuesRequest> requests = new ArrayList<ValuesRequest>();

    /**
     * {@inheritDoc}
     */
    @Override
    public ArchiveValues[] getSamples(final int key, final String[] names,
                                      final ITimestamp start, final ITimestamp end,
                                      final String request_type, final Object request_parms[]) throws ArchiveAccessException
                                      {
        final int request_code = getRequestCode(request_type);
        final ValuesRequest values = new ValuesRequest(this,
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
            for (final ValuesRequest request : requests) {
                request.cancel();
            }
        }
    }
}
