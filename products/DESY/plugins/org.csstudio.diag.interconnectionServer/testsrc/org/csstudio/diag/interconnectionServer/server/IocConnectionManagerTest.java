/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

import org.csstudio.servicelocator.ServiceLocator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * @author Joerg Rathlev
 */
public class IocConnectionManagerTest {
    
    private ILdapServiceFacade _ldapServiceFacade;

    @Before
    public void setUp() {
        _ldapServiceFacade = mock(ILdapServiceFacade.class);
        ServiceLocator.registerService(ILdapServiceFacade.class, _ldapServiceFacade);
    }
    
    @Test
    public void testGetIocConnection() throws Exception {
        final IocConnectionManager cm = new IocConnectionManager();
        final InetAddress ipAddress = InetAddress.getByName("127.0.0.1");
        final String hostname = ipAddress.getHostName();
        
        // mock the ldap lookup
        final IocNameDefinitions iocNameDefinitions = new IocNameDefinitions(ipAddress, false, null, "logicalName", "ldapName"); 
        when(_ldapServiceFacade.newIocNameDefinition(ipAddress)).thenReturn(iocNameDefinitions);
        
        // this call actually stores the ioc connection in the ioc connection manager
        final IocConnection conn = cm.getIocConnection(ipAddress, 123);
        assertNotNull(conn);
        assertEquals(hostname, conn.getNames().getHostName());
        assertEquals("logicalName", conn.getNames().getLogicalIocName());
        assertEquals("ldapName", conn.getNames().getLdapIocName());
        
        // Multiple requests must return the same IocConnection instance
        assertSame(conn, cm.getIocConnection(ipAddress, 123));
    }

    @Test
    public void testGetIocConnectionFromName() throws Exception {
        final IocConnectionManager cm = new IocConnectionManager();
        final InetAddress ipAddress = InetAddress.getByName("127.0.0.1");
        final String hostname = ipAddress.getHostName();

        // mock the ldap lookup
        final IocNameDefinitions iocNameDefinitions = new IocNameDefinitions(ipAddress, false, null, "someLogicalName", "ldapName"); 
        when(_ldapServiceFacade.newIocNameDefinition(ipAddress)).thenReturn(iocNameDefinitions);
        
        // this call actually stores the ioc connection in the ioc connection manager
        final IocConnection storedConnection = cm.getIocConnection(ipAddress, 123);

        IocConnection retrievedConnection = cm.getIocConnectionFromName("someLogicalName");
        assertSame(retrievedConnection, storedConnection);

        retrievedConnection = cm.getIocConnectionFromName(hostname);
        assertSame(retrievedConnection, storedConnection);

        retrievedConnection = cm.getIocConnectionFromName("127.0.0.1");
        assertSame(retrievedConnection, storedConnection);
    }

}
