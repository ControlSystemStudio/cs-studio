/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.utility.ldap.reader;

import static org.csstudio.utility.ldap.LdapUtils.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.EREN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_SEPARATOR;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_WILDCARD;
import static org.csstudio.utility.ldap.LdapUtils.OU_FIELD_NAME;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * Utility class to find the IOC of a process variable.
 * 
 * @author Joerg Rathlev, Jan Hatje
 */
public final class IocFinder {
    
    /**
     * private constructor.
     */
    private IocFinder() {
        // Empty
    }
    
    /**
     * Returns the name of the IOC of the given process variable.
     * 
     * @param pv the name of the process variable.
     * @return the name of the IOC.
     */
    public static String getEconForEren(final String pv) {
        if (pv == null) {
            throw new NullPointerException("pv must not be null");
        }
        
        final LdapResultListObserver obs = new LdapResultListObserver();
        final LdapResultList resultList = new LdapResultList();
        resultList.addObserver(obs);
        
        final LDAPReader ldapr =
            new LDAPReader(OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
                           EREN_FIELD_NAME + FIELD_ASSIGNMENT + pvNameToRecordName(pv),
                           resultList);
        ldapr.schedule();
        
        final String ldapPath = obs.getResult();
        if (ldapPath != null) {
            return ldapResultToControllerName(ldapPath);
        }
        return null;
    }
    
    /**
     * Converts the given process variable name into a record name which can be
     * looked up in the LDAP directory. If the default control system is EPICS,
     * this will truncate everything after the first dot in the PV name.
     * 
     * @param pv
     *            the name of the process variable.
     * @return the name of the record in the LDAP directory.
     */
    private static String pvNameToRecordName(final String pv) {
        if (pv.contains(".") && isEpicsDefaultControlSystem()) {
            return pv.substring(0, pv.indexOf("."));
        }
        return pv;
    }
    
    /**
     * Returns <code>true</code> if EPICS is the default control system.
     * 
     * @return <code>true</code> if EPICS is the default control system,
     *         <code>false</code> otherwise.
     */
    private static boolean isEpicsDefaultControlSystem() {
        final ControlSystemEnum controlSystem = ProcessVariableAdressFactory
        .getInstance().getDefaultControlSystem();
        return controlSystem == ControlSystemEnum.EPICS;
        //				|| controlSystem == ControlSystemEnum.DAL_EPICS;
    }
    
    /**
     * Returns a list with the names of all IOCs configured in the LDAP
     * directory.
     * 
     * @return a list with the names of all IOCS.
     */
    public static List<String> getIocList() {
        final List<String> result = new ArrayList<String>();
        final LdapResultList el = new LdapResultList();
        final LDAPReader lr =
            new LDAPReader(OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
                           ECON_FIELD_NAME + FIELD_ASSIGNMENT + FIELD_WILDCARD,
                           el);
        lr.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(final IJobChangeEvent event) {
                if (event.getResult().isOK()) {
                    for (final String ioc : el.getAnswer()) {
                        result.add(ldapResultToControllerName(ioc));
                    }
                }
            }
        });
        lr.schedule();
        try {
            lr.join();
        } catch (final InterruptedException e1) {
            // ignore
        }
        return result;
    }
    
    /**
     * Converts an LDAP path to an IOC name.
     * 
     * @param ldapPath the LDAP path.
     * @return the IOC name.
     */
    public static String ldapResultToControllerName(final String ldapPath) {
        final String[] answerTmp = ldapPath.split(ECON_FIELD_NAME + FIELD_ASSIGNMENT);
        final String[] answerTmp2 = answerTmp[1].split(FIELD_SEPARATOR);
        final String controllerName = answerTmp2[0];
        return controllerName;
    }
    
    public static class LdapQueryResult {
        
        private String _eren;
        private String _econ;
        private String _efan;
        
        /**
         * Constructor.
         * @param efan
         * @param econ
		 //* @param eren
         */
        public LdapQueryResult(
                               final String efan,
                               final String econ,
                               final String eren) {
            _efan = efan;
            _econ = econ;
            //_eren = eren;
        }
        
        public LdapQueryResult() {
            // Empty
        }
        
        public String getEren() {
            return _eren;
        }
        
        public String getEcon() {
            return _econ;
        }
        
        public String getEfan() {
            return _efan;
        }
        
        public void setEcon(final String econ) {
            _econ = econ;
        }
        
        public void setEfan(final String efan) {
            _efan = efan;
        }
        
        public void setEren(final String eren) {
            _eren = eren;
        }
    }
    
    public static LdapQueryResult parseLdapQueryResult(final String ldapPath) {
        
        final String[] fields = ldapPath.split(FIELD_SEPARATOR);
        
        final LdapQueryResult entry = new LdapQueryResult();
        
        final String econPrefix = ECON_FIELD_NAME + FIELD_ASSIGNMENT;
        final String efanPrefix = EFAN_FIELD_NAME + FIELD_ASSIGNMENT;
        final String erenPrefix = EREN_FIELD_NAME + FIELD_ASSIGNMENT;
        
        for (final String field : fields) {
            if (field.startsWith(econPrefix)) {
                entry.setEcon(field.substring(econPrefix.length()));
            } else if (field.startsWith(efanPrefix)){
                entry.setEfan(field.substring(efanPrefix.length()));
            }
            else if (field.startsWith(erenPrefix)) {
                entry.setEren(field.substring(erenPrefix.length()));
            }
        }
        return entry;
    }
    
}
