/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapUpdater.action;

import static org.csstudio.utility.ldap.LdapUtils.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapUtils.LDAP_OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.csstudio.platform.management.CommandParameterEnumValue;
import org.csstudio.platform.management.IDynamicParameterValues;
import org.csstudio.utility.ldapUpdater.LdapAccess;
import org.csstudio.utility.ldapUpdater.ReadLdapObserver;
import org.csstudio.utility.ldapUpdater.model.IOC;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;

/**
 * Enumeration of IOCs as retrieved from LDAP for the management commands.
 * 
 * @author bknerr 17.03.2010
 */
public class IocEnumeration implements IDynamicParameterValues {
    
    
    
    /**
     * (@inheritDoc)
     */
    @Override
    public CommandParameterEnumValue[] getEnumerationValues() {
        
        final LDAPContentModel ldapContentModel = new LDAPContentModel();
        final ReadLdapObserver ldapDataObserver = new ReadLdapObserver(ldapContentModel);
        
        Set<IOC> iocs = Collections.emptySet();
        try {
            final LDAPContentModel model =
                LdapAccess.fillModelFromLdap(ldapDataObserver,
                                             LDAP_OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
                                             any(ECON_FIELD_NAME));
            iocs = model.getIOCs();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        final List<CommandParameterEnumValue> params = new ArrayList<CommandParameterEnumValue>(iocs.size());
        for (final IOC ioc : iocs) {
            final HashMap<String, String> map = new HashMap<String, String>();
            map.put(ECON_FIELD_NAME, ioc.getName());
            map.put(EFAN_FIELD_NAME, ioc.getGroup());
            params.add(new CommandParameterEnumValue(map, ioc.getName()));
        }
        
        Collections.sort(params, new Comparator<CommandParameterEnumValue>() {
            @Override
            public int compare(final CommandParameterEnumValue arg0, final CommandParameterEnumValue arg1) {
                return arg0.getLabel().compareToIgnoreCase(arg1.getLabel());
            }
        });
        return params.toArray(new CommandParameterEnumValue[params.size()]);
    }
    
}
