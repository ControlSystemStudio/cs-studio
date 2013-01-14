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

/**
 * Container for the bunch of names related to an ioc and the inet address too.
 * Immutable.
 * 
 * @author jpenning
 * @since 12.06.2012
 */
public class IocNameDefinitions {
    
//    private static final Logger LOG = LoggerFactory.getLogger(IocNameDefinitions.class);
    
    private final InetAddress _inetAddress;
    private String _logicalIocName = null;
    private String _hostName = null;
    private String _ldapIocName = null;

    private boolean _isRedundant;
    private String _partnerIpAddress;
    
    public IocNameDefinitions(final InetAddress inetAddress, boolean isRedundant, String redundantIpAddress, String logicalIocName, String ldapIocName) {
        _hostName = inetAddress.getHostName(); // host name or ip-address will be returned
        _inetAddress = inetAddress;
        _isRedundant = isRedundant;
        _partnerIpAddress = redundantIpAddress;

        _logicalIocName = logicalIocName;
        if (_logicalIocName == null) {
            _logicalIocName = "~" + _hostName + "~";
        }
        _ldapIocName = ldapIocName;
        if (_ldapIocName == null) {
            _ldapIocName = "~" + _hostName + "~";
        }
    }
    
    /**
     * Returns the logical ioc name (eg. "kryoKS2").
     * The logical ioc name may fall back to the host name (or to the ip address if the name lookup failed too), in
     * this case the name is surrounded with '~'
     * 
     * @return the ldap ioc name, possibly falling back to host name or ip address
     */
    public String getLogicalIocName() {
        return _logicalIocName;
    }
    
    /**
     * Returns the ldap ioc name (eg. "econ=kryoKS2").
     * The ldap ioc name may fall back to the host name (or to the ip address if the name lookup failed too), in
     * this case the name is surrounded with '~'
     * 
     * @return the ldap ioc name, possibly falling back to host name or ip address
     */
    public String getLdapIocName() {
        return _ldapIocName;
    }
    
    /**
     * @return the inet address
     */
    public InetAddress getInetAddress() {
        return _inetAddress;
    }
    
    /**
     * Returns the host name (eg. "epicscpci03.desy.de") or the ip-address if the host name cannot be retrieved
     * 
     * @return the host name or ip-address
     */
    public String getHostName() {
        return _hostName;
    }

    public boolean isRedundant() {
        return _isRedundant;
    }

    public String getPartnerIpAddress() {
        return _partnerIpAddress;
    }
    
}