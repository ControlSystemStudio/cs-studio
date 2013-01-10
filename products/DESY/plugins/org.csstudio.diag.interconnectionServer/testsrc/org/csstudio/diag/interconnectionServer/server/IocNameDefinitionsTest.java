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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the ioc name definition value container
 * 
 * @author jpenning
 * @since 13.06.2012
 */
public class IocNameDefinitionsTest {
    
    @Test
    public void testMinimalCreation() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        IocNameDefinitions objectUnderTest = new IocNameDefinitions(inetAddress,
                                                                    false,
                                                                    null,
                                                                    null,
                                                                    null);
        
        assertEquals("127.0.0.1", objectUnderTest.getInetAddress().getHostAddress());
        assertEquals("localhost", objectUnderTest.getInetAddress().getHostName());
        assertEquals("localhost", objectUnderTest.getHostName());
        // expect fallback names because no values have been provided
        assertEquals("~localhost~", objectUnderTest.getLogicalIocName());
        assertEquals("~localhost~", objectUnderTest.getLdapIocName());
        assertNull(objectUnderTest.getPartnerIpAddress());
        assertFalse(objectUnderTest.isRedundant());
    }

    @Test
    public void testCreationForIoc() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        IocNameDefinitions objectUnderTest = new IocNameDefinitions(inetAddress,
                                                                    false,
                                                                    null,
                                                                    "myLogicalIocName",
                                                                    "econ=myLogicalIocName");
        
        assertEquals("127.0.0.1", objectUnderTest.getInetAddress().getHostAddress());
        assertEquals("localhost", objectUnderTest.getInetAddress().getHostName());
        assertEquals("localhost", objectUnderTest.getHostName());
        assertEquals("myLogicalIocName", objectUnderTest.getLogicalIocName());
        assertEquals("econ=myLogicalIocName", objectUnderTest.getLdapIocName());
        assertNull(objectUnderTest.getPartnerIpAddress());
        assertFalse(objectUnderTest.isRedundant());
    }

    @Test
    public void testCreationForRedundantIoc() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        IocNameDefinitions objectUnderTest = new IocNameDefinitions(inetAddress,
                                                                    true,
                                                                    "192.168.0.0",
                                                                    "myLogicalIocName",
                                                                    "econ=myLogicalIocName");
        
        assertEquals("127.0.0.1", objectUnderTest.getInetAddress().getHostAddress());
        assertEquals("localhost", objectUnderTest.getInetAddress().getHostName());
        assertEquals("localhost", objectUnderTest.getHostName());
        assertEquals("myLogicalIocName", objectUnderTest.getLogicalIocName());
        assertEquals("econ=myLogicalIocName", objectUnderTest.getLdapIocName());
        assertEquals("192.168.0.0", objectUnderTest.getPartnerIpAddress());
        assertTrue(objectUnderTest.isRedundant());
    }
}
