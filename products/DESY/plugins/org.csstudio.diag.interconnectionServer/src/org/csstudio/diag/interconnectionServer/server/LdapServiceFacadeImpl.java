package org.csstudio.diag.interconnectionServer.server;

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

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for local LDAP support.
 *
 * @author Matthias Clausen
 * @author Bastian Knerr
 * @author Jörg Penning
 */
public class LdapServiceFacadeImpl implements ILdapServiceFacade {
    
    private static final Logger LOG = LoggerFactory.getLogger(LdapServiceFacadeImpl.class);
    
    public LdapServiceFacadeImpl() {
        // EMPTY
    }
    
    public synchronized IocNameDefinitions newIocNameDefinition(@Nonnull final InetAddress inetAddress) throws NamingException,
                                                                                                       LdapServiceException {
        final ILdapService ldapService = ServiceLocator.getService(ILdapService.class);
        final ILdapSearchResult ldapSearchResult = retrieveLdapSearchResult(ldapService,
                                                                            inetAddress);
        final SearchResult searchResult = retrieveSearchResult(inetAddress, ldapSearchResult);
        
        String logicalIocName = null;
        String ldapIocName = null;
        boolean isRedundant = false;
        String partnerIpAddress = null;
        if (searchResult != null) {
            isRedundant = getIsRedundantAttribute(searchResult.getAttributes());
            partnerIpAddress = getPartnerIpAddress(searchResult.getAttributes(), isRedundant, inetAddress.getHostAddress());
            LdapName ldapName = newLdapName(ldapService, searchResult);
            LOG.info("Identified IOC entry '" + ldapName + "' for IP address "
                    + inetAddress.getHostAddress());
            
            /*
             * fortunately a valid name was found
             * the string returned looks like: econ=iocName, ....
             * make sure the string is a valid LDAP address - must start with "econ"
             */
            final int pos = ldapName.size() - 1;
            final Rdn rdn = ldapName.getRdn(pos);
            if (rdn.getType().equals(LdapEpicsControlsConfiguration.IOC.getNodeTypeName())) {
                logicalIocName = (String) rdn.getValue();
                ldapIocName = ldapName.get(pos);
            } else {
                LOG.warn("LdapName had wrong type for IOC at " + inetAddress.getHostAddress());
            }
        } else {
            LOG.warn("No logical name configured in LDAP for IOC at "
                    + inetAddress.getHostAddress());
        }
        
        return new IocNameDefinitions(inetAddress,
                                      isRedundant,
                                      partnerIpAddress,
                                      logicalIocName,
                                      ldapIocName);
    }
    
    /*
     * TODO
     * MCL - 2010-06-10
     * In case LDAP is NOT available we should provide a FILE from where we can get the logical names resolved
     */
    //        /*
    //         * in the meantime ...
    //         */
    //
    //        if ( ipAddress.equals("131.169.112.56")) {
    //        	stringReturnArray[0] = stringReturnArray[1] = "mkk10KVB1";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.146")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "mthKryoStand";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.155")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoCMTB";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.68")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "utilityIOC";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.80")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoLinac";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.52")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "krykWetter";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.108")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoLinac";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.104")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoSK47a";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.54")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoCB";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.68")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "utilityIOC";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.144")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "heraKryoFel";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.109")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoVC2";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.178")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "mthKryoStand";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.225")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfDiagLinac";
    //        	return stringReturnArray;
    //        } else if ( ipAddress.equals("131.169.112.101")) {
    //        	stringReturnArray[0] = stringReturnArray[1] =  "ttfKryoFV";
    //        	return stringReturnArray;
    //        } else {
    //        	return new String[]{"~" + ipName + "~","~" + ipName + "~"};
    //        }
    //
    //
    //
    //		/*
    //		 * es fehlen: 131.169.112.178 und 131.169.112.108
    //		 *
    //		 *epicsGPFC01       mkk10KVA1       : Keine Datei Y:\directoryServer\mkk10KVA1.BootLine.dat gefunden
    //epicsGPFC02       mkk10KVB1       131.169.112.56
    //epicsGPFC03       mkk10KVC1       131.169.112.69
    //epicsGPFC04       mkk10KVC2       131.169.112.87
    //epicsGPFC05       mkk10KV6A       131.169.112.153
    //epicsGPFC06       mkk10KV2B       131.169.112.154
    //epicsGPFC07       mkk10KV3B       131.169.112.157
    //epicsPC21         mthKryoStand    131.169.112.146
    //epicsPC24         wienerVME       131.169.112.150
    //epicsPC25         ttfKryoCMTB     131.169.112.155
    //epicsPC26         ttfKryoXCB      131.169.112.170
    //epicsPPC02        mkkPPC02        131.169.112.224
    //epicsPPC11        mkkSender       : Keine Datei Y:\directoryServer\mkkSender.BootLine.dat gefunden
    //epicsPPC12        ttfKryoSK47a    131.169.112.104
    //epicsPPC13        mkkPPC03        : Keine Datei Y:\directoryServer\mkkPPC03.BootLine.dat gefunden
    //epicsPPC14        ttfKryoVC2      131.169.112.109
    //epicsPPC18        mkkModbus       131.169.113.52
    //epicsPPC19        ttfKryoVC1      131.169.113.53
    //epicsVME00        utilityIOC      131.169.112.68
    //epicsVME01        mkkTempPuls     : Keine Datei Y:\directoryServer\mkkTempPuls.BootLine.dat gefunden
    //epicsVME02        kryoCta         131.169.112.94
    //epicsVME04        ttfKryoLinac    131.169.112.80
    //epicsVME08        analyze         131.169.112.228
    //epicsVME11        heraKryoKoMag   131.169.112.92
    //epicsVME12        modulator       : Keine Datei Y:\directoryServer\modulator.BootLine.dat gefunden
    //epicsVME14        ttfDiagLinac    131.169.112.225
    //epicsVME15        mhf-irm-a       : Keine Datei Y:\directoryServer\mhf-irm-a.BootLine.dat gefunden
    //epicsVME16        mkkKlima3       131.169.112.227
    //epicsVME17        mkkPowStatC_B   131.169.112.176
    //epicsVME18        mkk-irm-b       131.169.112.177
    //epicsVME20        krykWetter      131.169.112.52
    //epicsVME22        ttfKryoCB       131.169.112.54
    //epicsVME27        heraKryoRefmag  : Keine Datei Y:\directoryServer\heraKryoRefmag.BootLine.dat gefunden
    //epicsVME28        heraKryoCavity  : Keine Datei Y:\directoryServer\heraKryoCavity.BootLine.dat gefunden
    //epicsVME29        tineDataSrv     131.169.112.229
    //epicsVME34        mkkKlima2       131.169.112.138
    //epicsVME35        heraKryoFel     131.169.112.144
    //epicsVME36        mkkKlima1       131.169.112.145
    //epicsVME37        mkk-irm-a       131.169.112.114
    //epicsVME40        ttfKryoFV       131.169.112.101
    //epicsVME62        mkkPowStatC_A   131.169.112.142
    //epicsVME62.irm-c  mkk-irm-c       : Keine Datei Y:\directoryServer\mkk-irm-c.BootLine.dat gefunden
    //		 */
    //	}
    
    public void setAllRecordsToConnected(final String ldapIocName) {
        /*
         * just a convenience method
         */
        final String status = "ONLINE";
        final String severity = "NO_ALARM";
        LOG.debug("IocChangeState: setAllRecordsToConnected");
        setAllRecordsInLdapServerAndJms(ldapIocName, status, severity);
    }

    public void setAllRecordsToDisconnected(final String ldapIocName) {
        /*
         * just a convenience method
         */
        final String status = "DISCONNECTED";
        final String severity = "INVALID";
        LOG.debug("IocChangeState: setAllRecordsToDisconnected");
        setAllRecordsInLdapServerAndJms(ldapIocName, status, severity);
        
    }

    @Nonnull
    private LdapName newLdapName(@Nonnull final ILdapService ldapService,
                                 @Nonnull final SearchResult searchResult) throws InvalidNameException,
                                                                          LdapServiceException,
                                                                          NamingException {
        final Name cname = new CompositeName(searchResult.getName());
        final NameParser nameParser = ldapService.getLdapNameParser();
        final LdapName ldapName = (LdapName) nameParser.parse(cname.get(0));
        ldapName.add(0, new Rdn(LdapEpicsControlsConfiguration.UNIT.getNodeTypeName(),
                                LdapEpicsControlsConfiguration.UNIT.getUnitTypeValue()));
        return ldapName;
    }
    
    @CheckForNull
    private ILdapSearchResult retrieveLdapSearchResult(@Nonnull final ILdapService ldapService,
                                                       @Nonnull final InetAddress inetAddress) {
        final ILdapSearchResult ldapSearchResult = ldapService
                .retrieveSearchResultSynchronously(LdapUtils
                                                           .createLdapName(LdapEpicsControlsConfiguration.UNIT
                                                                                   .getNodeTypeName(),
                                                                           LdapEpicsControlsConfiguration.UNIT
                                                                                   .getUnitTypeValue()),
                                                   "(&(objectClass=epicsController)"
                                                           + "(|(epicsIPAddress="
                                                           + inetAddress.getHostAddress() + ")"
                                                           + "(epicsIPAddressR="
                                                           + inetAddress.getHostAddress() + ")))",
                                                   SearchControls.SUBTREE_SCOPE);
        return ldapSearchResult;
    }
    
    @CheckForNull
    private SearchResult retrieveSearchResult(@Nonnull final InetAddress inetAddress,
                                              @CheckForNull final ILdapSearchResult ldapSearchResult) {
        SearchResult searchResult = null;
        if (ldapSearchResult != null) {
            Set<SearchResult> answerSet = ldapSearchResult.getAnswerSet();
            if (answerSet.size() > 1) {
                LOG.warn("More than one IOC entry in LDAP directory for IP address "
                        + inetAddress.getHostAddress());
            } else if (answerSet.isEmpty()) {
                LOG.debug("No search result for IP address " + inetAddress.getHostAddress());
            } else {
                searchResult = answerSet.iterator().next();
            }
        }
        return searchResult;
    }
    
    @CheckForNull
    private String getPartnerIpAddress(@Nonnull final Attributes attributes, final boolean isRedundant, @Nonnull final String currentHostAddress) {
        String result = null;
        if (isRedundant) {
            // ldap has only one entry for both redundant iocs, therefore we have to find out which one we are
            String partnerIpAdress = getStringForKeyFromAttributes(attributes, "epicsIPAddressR");
            String ownIpAddress = getStringForKeyFromAttributes(attributes, "epicsIPAddress");
            if (thisIsPreferredIoc(ownIpAddress, currentHostAddress)) {
                result = partnerIpAdress;
            } else {
                result = ownIpAddress;
            }
            
        }
        return result;
    }

    private boolean thisIsPreferredIoc(@Nonnull final String ownIpAddress, @Nonnull final String currentHostAddress) {
        return ownIpAddress.equals(currentHostAddress);
    }

    @CheckForNull
    private String getStringForKeyFromAttributes(@Nonnull final Attributes attributes, @Nonnull final String key) {
        String result = null;
        Attribute attribute = attributes.get(key);
        if (attribute != null) {
            try {
                Object value = attribute.get();
                if (value != null) {
                    result = value.toString();
                }
            } catch (NamingException e) {
                LOG.warn("Could not retrieve value from attribute " + attribute.getID(), e);
            }
        }
        return result;
    }
    
    private boolean getIsRedundantAttribute(@Nonnull final Attributes attributes) {
        boolean result = false;
        Attribute attribute = attributes.get("epicsCsIsRedundant");
        if (attribute != null) {
            Object value;
            try {
                value = attribute.get();
                if (value != null) {
                    result = Boolean.valueOf(value.toString());
                }
            } catch (NamingException e) {
                LOG.warn("Could not retrieve attribute 'epicsCsIsRedundant'.", e);
            }
        }
        return result;
    }
    
    /*
     * method is synchronized: In case several IOCs disconnect the threads to enter the changes in the LDAP server will be
     * started. But they will write in parallel to the LDAP server - but in sequence!
     * This will (partly) avoid congestion on the send queue in addLdapWriteRequest()
     */
    synchronized private void setAllRecordsInLdapServerAndJms(@CheckForNull final String ldapIocName,
                                                              final String status,
                                                              final String severity) {
        
        if (ldapIocName == null) {
            return;
        }
        
        String logicalIocName = ldapIocName;
        /*
         * find all records belonging to the IOC: logicalIocName
         * -> search for econ = logicalIocName
         * -> create list for all eren (record) entries
         *
         * for each eren entry set the epicsAlarmStatus to 'OFFLINE' and the epicsAlarmTimeStamp to the actual time
         */

        //
        // create time stamp written to epicsAlarmTimeStamp
        // this is a copy from the class ClientRequest - na grosssartig!
        //
        final SimpleDateFormat sdf = new SimpleDateFormat(PreferenceProperties.JMS_DATE_FORMAT);
        final Date currentDate = new Date();
        final String eventTime = sdf.format(currentDate);
        
        final List<String> allRecordsList = getAllRecordsOfIOC(ldapIocName,
                                                               severity,
                                                               status,
                                                               eventTime);
        
        //
        // check for a valid record list
        //
        if (allRecordsList == null) {
            LOG.warn("IOC OFFLINE - NO channels found in LDAP for : " + ldapIocName);
            return;
        }
        
        if (logicalIocName.contains("=")) {
            logicalIocName = logicalIocName.split("[=,]")[1];
        }
        for (final String channelName : allRecordsList) {
            LOG.debug("Found Channelname: " + channelName);
            if (channelName != null) {
                /*
                 * set values in LDAP and create JMS message
                 */
                setSingleChannel(channelName, status, severity, eventTime, logicalIocName);
            }
        }
    }
    
    /**
     * Set the severity, status and eventTime to a record.
     *
     * @param ldapPath
     *            the LDAP-Path to the record.
     * @param severity
     *            the severity to set.
     * @param status
     *            the status to set.
     * @param eventTime
     *            the event time to set.
     * @return list with all channels of the ioc
     */
    private List<String> getAllRecordsOfIOC(@Nonnull final String ldapPath,
                                           final String severity,
                                           final String status,
                                           final String eventTime) {
        
        LdapName ldapName;
        try {
            ldapName = new LdapName(ldapPath);
        } catch (final InvalidNameException e) {
            LOG.warn("LDAP path name not valid: " + ldapPath
                    + ". Cannot retrieve records for this IOC from LDAP.");
            return Collections.emptyList();
        }
        
        final String ldapIocPrefix = LdapEpicsControlsConfiguration.IOC.getNodeTypeName()
                + LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
        if (!ldapName.get(ldapName.size() - 1).startsWith(ldapIocPrefix)) {
            LOG.error("Unknown LDAP Path! Path is " + ldapPath);
            return Collections.emptyList();
        }
        
        try {
            if (ldapName.get(0).equals(LdapFieldsAndAttributes.LDAP_ROOT.get(0))) {
                ldapName.remove(0);
            } // remove country rdn
            if (ldapName.get(0).equals(LdapFieldsAndAttributes.LDAP_ROOT.get(1))) {
                ldapName.remove(0); // remove organization rdn
            }
        } catch (final InvalidNameException e) {
            LOG.error("LDAP name modification failed" + ldapPath);
            return Collections.emptyList();
        }
        
        final ILdapService service = ServiceLocator.getService(ILdapService.class);
        if (service == null) {
            LOG.error("LDAP service unavailable.");
            return Collections.emptyList();
        }
        final ILdapSearchResult result = service
                .retrieveSearchResultSynchronously(ldapName,
                                                   LdapUtils
                                                           .any(LdapEpicsControlsConfiguration.RECORD
                                                                   .getNodeTypeName()),
                                                   SearchControls.ONELEVEL_SCOPE);
        Set<SearchResult> answerSet;
        if (result == null) {
            LOG.info("No search result for LDAP query.");
            return Collections.emptyList();
        } else {
            answerSet = result.getAnswerSet();
            if (answerSet.isEmpty()) {
                LOG.info("No search result for LDAP query.");
                return Collections.emptyList();
            }
        }
        final List<String> list = new ArrayList<String>(answerSet.size());
        for (final SearchResult row : answerSet) {
            final String name = row.getName() + "," + ldapPath;
            list.add(name);
        }
        return list;
    }
    
    private void setSingleChannel(String channelName,
                                  final String status,
                                  final String severity,
                                  final String eventTime,
                                  final String logicalIocName) {
        if (channelName == null) {
            LOG.error("no channel name set");
            return;
        }
        
        /*
         * up to this point the channelName is still the LDAP address
         * eren=alarmTest:RAMPA_calc,econ=Bernds_Test_IOC,ecom=EPICS-IOC,efan=TEST,ou=epicsControls
         * we need to extract the record name from this LDAP string
         */
        if (channelName.contains("=")) {
            channelName = channelName.split("[=,]")[1];
        }
        
        JmsMessage.INSTANCE.sendMessage(JmsMessage.JMS_MESSAGE_TYPE_ALARM,
                                        JmsMessage.MESSAGE_TYPE_STATUS, // type
                                        channelName, // name
                                        null, // value
                                        severity, // severity
                                        status, // status
                                        logicalIocName, // host
                                        null, // facility
                                        "alarm set by IC-Server"); // howTo
    }
}
