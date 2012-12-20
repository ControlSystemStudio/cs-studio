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
import java.util.Collections;

import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.apache.log4j.PropertyConfigurator;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class LdapServiceFacadeImplTest {
    
    @Before
    public void setUp() throws Exception {
        String configFilename = "./log4j_test.properties";
        PropertyConfigurator.configure(configFilename);
    }
    
    @Test
    public void testNewIocNameDefinitionWithoutDnsEntry() throws Exception {
        ILdapService ldapService = mock(ILdapService.class);
        ServiceLocator.registerService(ILdapService.class, ldapService);

        LdapServiceFacadeImpl objectUnderTest = new LdapServiceFacadeImpl();
        IocNameDefinitions iocNameDefinitions = objectUnderTest.newIocNameDefinition(InetAddress.getByName("127.0.0.2"));
        assertNotNull(iocNameDefinitions);
        assertEquals("~127.0.0.2~", iocNameDefinitions.getLogicalIocName());
        assertEquals("~127.0.0.2~", iocNameDefinitions.getLdapIocName());
        assertEquals("127.0.0.2", iocNameDefinitions.getHostName());
        assertFalse(iocNameDefinitions.isRedundant());
        assertNull(iocNameDefinitions.getPartnerIpAddress());
    }
    
    @Test
    public void testNewIocNameDefinitionWithoutSearchResult() throws Exception {
        ILdapService ldapService = mock(ILdapService.class);
        ServiceLocator.registerService(ILdapService.class, ldapService);

        LdapServiceFacadeImpl objectUnderTest = new LdapServiceFacadeImpl();
        IocNameDefinitions iocNameDefinitions = objectUnderTest.newIocNameDefinition(InetAddress.getByName("127.0.0.1"));
        assertNotNull(iocNameDefinitions);
        assertEquals("~localhost~", iocNameDefinitions.getLogicalIocName());
        assertEquals("~localhost~", iocNameDefinitions.getLdapIocName());
        assertEquals("localhost", iocNameDefinitions.getHostName());
        assertFalse(iocNameDefinitions.isRedundant());
        assertNull(iocNameDefinitions.getPartnerIpAddress());
    }
    
    @Test
    public void testNewIocNameDefinitionForIoc() throws Exception {
        LdapName ldapName = new LdapName("econ=myLogicalIocName"); // other than econ is not required
        ILdapSearchResult ldapSearchResult = mock(ILdapSearchResult.class);
        defineLdapService(ldapName, ldapSearchResult);

        // the ldap name is mocked, we only need one arbitrary entry in the search result because it is accessed in the ldap service facade
        SearchResult searchResult = new SearchResult("econ=dontcare", null, newEmptyAttributes());
        when(ldapSearchResult.getAnswerSet()).thenReturn(Collections.singleton(searchResult));
        
        LdapServiceFacadeImpl objectUnderTest = new LdapServiceFacadeImpl();
        IocNameDefinitions iocNameDefinitions = objectUnderTest.newIocNameDefinition(InetAddress.getByName("127.0.0.1"));
        assertNotNull(iocNameDefinitions);
        assertEquals("myLogicalIocName", iocNameDefinitions.getLogicalIocName());
        assertEquals("econ=myLogicalIocName", iocNameDefinitions.getLdapIocName());
        assertEquals("localhost", iocNameDefinitions.getHostName());
        assertFalse(iocNameDefinitions.isRedundant());
        assertNull(iocNameDefinitions.getPartnerIpAddress());
    }

    @Test
    public void testNewIocNameDefinitionForRedundantIoc() throws Exception {
        LdapName ldapName = new LdapName("econ=myLogicalIocName"); // other than econ is not required
        ILdapSearchResult ldapSearchResult = mock(ILdapSearchResult.class);
        defineLdapService(ldapName, ldapSearchResult);

        // the ldap name is mocked, we only need one arbitrary entry in the search result because it is accessed in the ldap service facade
        SearchResult searchResult = new SearchResult("econ=dontcare", null, newRedundantAttributes());
        when(ldapSearchResult.getAnswerSet()).thenReturn(Collections.singleton(searchResult));
        
        LdapServiceFacadeImpl objectUnderTest = new LdapServiceFacadeImpl();
        IocNameDefinitions iocNameDefinitions = objectUnderTest.newIocNameDefinition(InetAddress.getByName("127.0.0.1"));
        assertNotNull(iocNameDefinitions);
        assertEquals("myLogicalIocName", iocNameDefinitions.getLogicalIocName());
        assertEquals("econ=myLogicalIocName", iocNameDefinitions.getLdapIocName());
        assertEquals("localhost", iocNameDefinitions.getHostName());
        assertTrue(iocNameDefinitions.isRedundant());
        assertEquals("192.168.0.0", iocNameDefinitions.getPartnerIpAddress());
    }

    private void defineLdapService(LdapName ldapName, ILdapSearchResult ldapSearchResult) throws Exception {
        ILdapService ldapService = mock(ILdapService.class);
        ServiceLocator.registerService(ILdapService.class, ldapService);

        when(ldapService.retrieveSearchResultSynchronously(any(LdapName.class),
                                                            anyString(),
                                                            anyInt())).thenReturn(ldapSearchResult);

        NameParser nameParser = newNameParser(ldapName);
        when(ldapService.getLdapNameParser()).thenReturn(nameParser);
    }

    private NameParser newNameParser(LdapName ldapName) throws NamingException {
        NameParser nameParser = mock(NameParser.class);
        when(nameParser.parse(anyString())).thenReturn(ldapName);
        return nameParser;
    }

    private Attributes newEmptyAttributes() throws NamingException {
        Attributes attrs = mock(Attributes.class);
        return attrs;
    }

    private Attributes newRedundantAttributes() throws NamingException {
        Attributes attrs = mock(Attributes.class);
        Attribute newAttrIsRedundant = newAttrIsRedundant();
        when(attrs.get("epicsCsIsRedundant")).thenReturn(newAttrIsRedundant);
        Attribute newAttrIpAddressB = newAttrIpAddressB();
        when(attrs.get("epicsIPAddressR")).thenReturn(newAttrIpAddressB);
        return attrs;
    }
    
    private Attribute newAttrIpAddressB() throws NamingException {
        Attribute attrIpAddressB = mock(Attribute.class);
        when(attrIpAddressB.get()).thenReturn("192.168.0.0");
        return attrIpAddressB;
    }

    private Attribute newAttrIsRedundant() throws NamingException {
        Attribute attrIsRedundant = mock(Attribute.class);
        when(attrIsRedundant.get()).thenReturn("TRUE");
        return attrIsRedundant;
    }
}

/**
Rdn rdn = mock(Rdn.class);
when(rdn.getType()).thenReturn("econ");
when(rdn.getValue()).thenReturn("myLogicalIocName");

//LdapName ldapName = mock(LdapName.class);
//when(ldapName.getRdn(anyInt())).thenReturn(rdn);
//LdapName ldapName = new LdapName("econ=myLogicalIocName,ecom=EPICS-IOC,efan=TEST,ou=EpicsControls,o=DESY,c=DE");

*/