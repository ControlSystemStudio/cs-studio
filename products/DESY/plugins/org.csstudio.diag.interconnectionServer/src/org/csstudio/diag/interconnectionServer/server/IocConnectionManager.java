/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.diag.interconnectionServer.server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;

import org.csstudio.diag.interconnectionServer.internal.time.TimeUtil;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of the connections to the IOCs. Also provides statistical
 * information.
 *
 * @author Matthias Clausen, Joerg Rathlev
 */
public class IocConnectionManager  implements IIocConnectionManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(IocConnectionManager.class);
    
    /*
     * TODO: This class currently has two responsibilities, keeping track of the
     * connections and providing statistical information. Maybe these should
     * be separated.
     */

    private Hashtable<String, IocConnection> _iocId2Connection = null; // accessed by BeaconWatchdog, InterconnectionServer
    private int totalNumberOfIncomingMessages = 0;
    private int totalNumberOfOutgoingMessages = 0;
    
    public IocConnectionManager() {
        _iocId2Connection = new Hashtable<String, IocConnection>();
    }
    
    // 2009-07-06 MCL
    // change internal ID from hostName to hostAddress:port
    // 2012-06-01 jp
    // When a command is given, the ioc is identified by its name (logical name, hostname or ip address in that order), but no port
    // is available - so multiple iocs on a single machine cannot be distinguished, ie. the port is of no use.
    synchronized public IocConnection getIocConnection(final InetAddress iocInetAddress,
                                                       final int port) throws NamingException, LdapServiceException {
        final String internalId = iocInetAddress.getHostAddress() + ":" + port;
        if (_iocId2Connection.containsKey(internalId)) {
            return _iocId2Connection.get(internalId);
        } else {
            final IocNameDefinitions iocNameDefinitions = ServiceLocator.getService(ILdapServiceFacade.class).newIocNameDefinition(iocInetAddress);
            final IocConnection connection = new IocConnection(iocNameDefinitions,
                                                               TimeUtil.systemClock());
            LOG.info("Add connection with id '" + internalId + "' for ioc " + connection.getNames().getHostName()
                    + " to ioc connection manager");
            _iocId2Connection.put(internalId, connection);
            return connection;
        }
    }
    
    @Nonnull
    public Enumeration<IocConnection> getIocConnectionEnumeration() {
        return _iocId2Connection.elements();
    }

    public Collection<IocConnection> getIocConnections() {
        return new ArrayList<IocConnection>(_iocId2Connection.values());
    }
    
    public String getStatisticAsString() {
        String result = "";
        result += "\nTotal incoming messages     	= " + this.totalNumberOfIncomingMessages;
        result += "\nTotal outgoing messages     	= " + this.totalNumberOfOutgoingMessages;
        result += "\n";
        
        final Enumeration<IocConnection> connections = this._iocId2Connection.elements();
        while (connections.hasMoreElements()) {
            final IocConnection thisContent = connections.nextElement();
            result += "\n---------- statistische Auswertung ---------------\n";
            final StringBuilder buf = new StringBuilder();
            thisContent.appendStatisticInformationTo(buf);
            result += buf.toString();
        }
        return result;
    }
    
    @CheckForNull
    public IocConnection getIocConnectionFromName(@CheckForNull final String iocName) {
        IocConnection result = null;
        
        // guard
        if (iocName == null) {
            return null;
        }
        
        result = getIocConnectionFromLogicalName(iocName);
        if (result == null) {
            result = getIocConnectionFromDnsName(iocName);
            if (result == null) {
                result = getIocConnectionFromIpAddress(iocName);
            }
        }
        
        return result;
    }
    
    @CheckForNull
    public IocConnection getIocConnectionOfPartner(@CheckForNull final IocConnection iocConnection) {
        IocConnection partnerIocConnection = getIocConnectionFromName(iocConnection.getNames()
                .getPartnerIpAddress());
        return partnerIocConnection;
    }

    @CheckForNull
    private IocConnection getIocConnectionFromLogicalName(@Nonnull final String iocName) {
        IocConnection result = null;
        
        Collection<IocConnection> iocConnections = _iocId2Connection.values();
        for (IocConnection iocConnection : iocConnections) {
            boolean found = iocConnection.getNames().getLogicalIocName().equals(iocName);
            if (found) {
                result = iocConnection;
                break;
            }
        }
        
        return result;
    }
    
    @CheckForNull
    private IocConnection getIocConnectionFromDnsName(@Nonnull final String iocName) {
        IocConnection result = null;
        
        Collection<IocConnection> iocConnections = _iocId2Connection.values();
        for (IocConnection iocConnection : iocConnections) {
            boolean found = iocConnection.getInetAddress().getHostName().equals(iocName);
            if (found) {
                result = iocConnection;
                break;
            }
        }
        
        return result;
    }
    
    @CheckForNull
    private IocConnection getIocConnectionFromIpAddress(@Nonnull final String iocName) {
        IocConnection result = null;
        
        Collection<IocConnection> iocConnections = _iocId2Connection.values();
        for (IocConnection iocConnection : iocConnections) {
            boolean found = iocConnection.getInetAddress().getHostAddress().equals(iocName);
            if (found) {
                result = iocConnection;
                break;
            }
        }
        
        return result;
    }
    
    public void refreshIocNameDefinition(final String iocHostname) throws NamingException,
                                                                  LdapServiceException {
        
        // XXX: Why does this method have its own logic for finding the
        // IocConnection object?
        final Enumeration<IocConnection> connections = this._iocId2Connection.elements();
        while (connections.hasMoreElements()) {
            final IocConnection iocConnection = connections.nextElement();
            if (iocConnection.getNames().getHostName().equals(iocHostname)) {
                final IocNameDefinitions iocNameDefinitions = ServiceLocator
                        .getService(ILdapServiceFacade.class)
                        .newIocNameDefinition(iocConnection.getInetAddress());
                iocConnection.refreshIocNameDefinitions(iocNameDefinitions);
                LOG.info("Logical name of IOC " + iocHostname + " refreshed, new name is: "
                        + iocNameDefinitions.getLogicalIocName());
            }
        }
    }
    
    public String[] getNodeNameStatusArray() {
        final List<String> nodeNames = new ArrayList<String>();
        
        // just in case no enum is possible
        final Enumeration<IocConnection> connections = this._iocId2Connection.elements();
        while (connections.hasMoreElements()) {
            final IocConnection thisContent = connections.nextElement();
            nodeNames.add(thisContent.getNames().getHostName() + " | " + thisContent.getNames().getLogicalIocName() + "  "
                    + thisContent.getCurrentConnectState() + "  "
                    + thisContent.getCurrentSelectState());
        }
        return nodeNames.toArray(new String[0]);
        
    }
    
    public void incNoOfIncomingMessages() {
        totalNumberOfIncomingMessages++;
    }
    
    public void incNoOfOutgoingMessages() {
        totalNumberOfOutgoingMessages++;
    }

}
