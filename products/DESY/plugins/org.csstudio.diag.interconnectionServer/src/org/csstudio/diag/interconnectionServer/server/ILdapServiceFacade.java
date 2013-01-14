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

import javax.annotation.Nonnull;
import javax.naming.NamingException;

import org.csstudio.utility.ldap.service.LdapServiceException;

/**
 * Facade for the ldap service as used internally in the InterConnectionServer.
 * 
 * @author jpenning
 * @since 05.06.2012
 */
public interface ILdapServiceFacade {
    
    /**
     * Creates a new ioc name definition for the given inet address. The inet address is expected to address an ioc for which a dns entry is present
     * in the dns name server and for which an ldap entry is also present. 
     * There is a fallback mechanism implemented in the ioc name definition if some of these are missing.
     *
     * @param iocInetAddress the IOC's inet address.
     * @return a new ioc name definition
     *  
     * @throws NamingException
     * @throws LdapServiceException 
     */
    @Nonnull
    IocNameDefinitions newIocNameDefinition(@Nonnull final InetAddress iocInetAddress) throws NamingException,
                                                                                      LdapServiceException;
    
    void setAllRecordsToConnected(final String ldapIocName);
    
    void setAllRecordsToDisconnected(final String ldapIocName);
    
}
