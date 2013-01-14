/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.channelarchiver;

import java.net.UnknownHostException;

import org.apache.xmlrpc.XmlRpcClient;
import org.csstudio.apputil.text.RegExHelper;
import org.csstudio.archive.reader.ArchiveInfo;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ValueIterator;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/** Main access point to the ChannelArchiver network data server.
 *  @author Kay Kasemir
 *  @author Blaz Lipuscek worked on org.csstudio.archive.channelarchiver.ArchiveServer
 */
@SuppressWarnings("nls")
public class ChannelArchiverReader implements ArchiveReader
{
    final private String url;
    final private XmlRpcClient xmlrpc;
    final private ServerInfoRequest server_info_request;
    final private ArchivesRequest archives_request;

    /** Connect to a ChannelArchiver's network data server.
     *
     * @param url_text For example
     *        "xnds://my_server.org/archive/cgi/ArchiveDataServer.cgi"
     * @throws Exception
     */
    ChannelArchiverReader(final String url) throws Exception
    {
        this.url = url;
        try
        {
            // Patch 'xdns' into http, but keep the 'official' URL
            // as received.
            final String real_url;
            if (url.startsWith("xnds://"))
                real_url = "http://" + url.substring(7);
            else
                real_url = url;
            // Create client
            xmlrpc = new XmlRpcClient(real_url);

            // Get server info
            server_info_request = new ServerInfoRequest();
            server_info_request.read(xmlrpc);

            // .. and archive keys
            archives_request = new ArchivesRequest();
            archives_request.read(xmlrpc);
        }
        catch (UnknownHostException ex)
        {
            throw new Exception("Unknown host in URL " + url);
        }
    }

    /** {@inheritDoc}*/
    @Override
    public String getServerName()
    {
        return "Channel Archiver";
    }

    /** {@inheritDoc}*/
    @Override
    public String getURL()
    {
        return url;
    }

    /** {@inheritDoc}*/
    @Override
    public String getDescription()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(server_info_request.getDescription());
        buf.append("Request Types:\n");
        for (String req : server_info_request.getRequestTypes())
        {
            buf.append(req + "\n");
        }
        return buf.toString();
    }

    /** {@inheritDoc}*/
    @Override
    public int getVersion()
    {
        return server_info_request.getVersion();
    }

    /** {@inheritDoc}*/
    @Override
    public ArchiveInfo[] getArchiveInfos()
    {
        return archives_request.getArchiveInfos();
    }

    /** {@inheritDoc}*/
    @Override
    public String[] getNamesByPattern(final int key, final String glob_pattern)
            throws Exception
    {
        return getNamesByRegExp(key, RegExHelper.fullRegexFromGlob(glob_pattern));
    }

    /** {@inheritDoc}*/
    @Override
    public String[] getNamesByRegExp(final int key, final String reg_exp) throws Exception
    {
        final NamesRequest infos = new NamesRequest(key, reg_exp);
        infos.read(xmlrpc);
        return infos.getNameInfos();
    }

    /** Helper for locating a request code by name.
     *  <p>
     * @param request_name For example: GET_RAW.
     * @return The 'request_type' ID for a given request type string.
     * @throws Exception when asking for unsupported request type.
     * @see #getRequestTypes()
     */
    int getRequestCode(String request_name) throws Exception
    {
        final String request_types[] = server_info_request.getRequestTypes();
        for (int i=0; i<request_types.length; ++i)
            if (request_types[i].equalsIgnoreCase(request_name)) // add  IgnoreCase Albert
                return i;
        throw new Exception("Unsupported request type '" + request_name + "'");
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
            if (status >= 0  &&  status < status_strings.length)
                return status_strings[status];
            // else: Fall through...
        }
        // return the number as a string
        return severity.getText() + " " + Integer.toString(status);
    }

    /** Active request. Synchronize on this for access */
    private ValueRequest current_request = null;

    /** Issue request for values for one channel to the data server.
     *  @return Samples
     *  @throws Exception
     */
    public VType[] getSamples(final int key, final String name,
            final Timestamp start, final Timestamp end,
            final boolean optimized, final int count) throws Exception
    {
    	final ValueRequest request;
        synchronized (this)
        {
            current_request =
                new ValueRequest(this, key, name, start, end, optimized, count);
            request = current_request;
        }
        request.read(xmlrpc);
        final VType result[] = request.getSamples();
        synchronized (this)
        {
            current_request = null;
        }
        return result;
    }

    /** {@inheritDoc}*/
    @Override
    public ValueIterator getRawValues(final int key, final String name,
            final Timestamp start, final Timestamp end) throws Exception
    {
        return new ValueRequestIterator(this, key, name, start, end, false, 10);
    }

    /** {@inheritDoc}*/
    @Override
    public ValueIterator getOptimizedValues(final int key, final String name,
         final Timestamp start, final Timestamp end, final int count) throws Exception
    {
        return new ValueRequestIterator(this, key, name, start, end, true, count);
    }

    /** {@inheritDoc}*/
    @Override
    public void cancel()
    {
        synchronized (this)
        {
            if (current_request != null)
                current_request.cancel();
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void close()
    {
        cancel();
    }
}
