/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
import java.util.Collection;
import java.util.Enumeration;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;

import org.csstudio.utility.ldap.service.LdapServiceException;

public interface IIocConnectionManager {
    
    /**
     * Returns the IOC connection object representing the connection to the IOC
     * on the specified host and port.
     * If the connection is not present already, it will be constructed and cached.
     *
     * @param hostAddress   the host address of the IOC.
     * @param port          the port from which messages from the IOC are received.
     * @return the IOC connection.
     * @throws NamingException
     * @throws LdapServiceException
     */
    IocConnection getIocConnection(final InetAddress iocInetAddress, final int port) throws NamingException, LdapServiceException;
    
    /**
     * Tries to retrieve the IOC connection for the given name.
     * The name may be the logical name / hostname / ip address.
     * The internally stored IOC connections are searched for a match in the order of the names given above.
     * 
     * @param iocName
     * @return the IOC connection for the given name or null if nothing matched.
     */
    @CheckForNull
    IocConnection getIocConnectionFromName(@CheckForNull final String iocName);
    
    /**
     * Tries to find the ioc connection of the partner, if there is any.
     * If the ioc is not redundant, there will be no partner.
     * 
     * @param iocConnection
     * @return the ioc connection of the partner or null if there is no partner
     */
    @CheckForNull
    IocConnection getIocConnectionOfPartner(@CheckForNull final IocConnection iocConnection);
    
    /**
     * @return the IOC connections managed by this manager.
     */
    Collection<IocConnection> getIocConnections();
    
    /**
     * @return an Enumeration of the stored connections
     */
    @Nonnull
    Enumeration<IocConnection> getIocConnectionEnumeration();
    
    String[] getNodeNameStatusArray();
    
    String getStatisticAsString();
    
    /**
     * Refreshes the logical name of an IOC from the directory server.
     *
     * @param iocHostname
     *            the hostname of the IOC.
     * @throws NamingException
     * @throws LdapServiceException 
     */
    void refreshIocNameDefinition(final String iocHostname) throws NamingException,
                                                           LdapServiceException;
    
    /**
     * Increases the count of incoming messages
     */
    void incNoOfIncomingMessages();
    
    /**
     * Increases the count of outgoing messages
     */
    void incNoOfOutgoingMessages();
    
}
