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
package org.csstudio.utility.ldapUpdater.contextMenu;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameterEnumValue;
import org.csstudio.platform.management.IDynamicParameterValues;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.csstudio.utility.ldapUpdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapUpdater.service.LdapFacadeException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;


/**
 * Enumeration of IOCs as retrieved from LDAP for the management commands.
 *
 * @author bknerr 17.03.2010
 */
public class IocEnumeration implements IDynamicParameterValues {

    private static final CommandParameterEnumValue[] EMPTY_VALUES = new CommandParameterEnumValue[] {};

    private static final Logger LOG = CentralLogger.getInstance().getLogger(IocEnumeration.class);

    private ILdapUpdaterService _service;

    /**
     * Constructor.
     */
    public IocEnumeration() {
        try {
            _service = LdapUpdaterActivator.getDefault().getLdapUpdaterService();
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("LDAP service is not available!");
            throw new RuntimeException("LDAP service is not available!", e);
        }
    }

    @Override
    @Nonnull
    public CommandParameterEnumValue[] getEnumerationValues() {

        Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocs;
        try {
            iocs = _service.retrieveIOCs();
        } catch (final LdapFacadeException e) {
            LOG.error("IOC retrieval failed.", e);
            return EMPTY_VALUES;
        }

        final List<CommandParameterEnumValue> params = generateCommandParamList(iocs.values());

        Collections.sort(params, new Comparator<CommandParameterEnumValue>() {
            @Override
            public int compare(@Nonnull final CommandParameterEnumValue arg0, @Nonnull final CommandParameterEnumValue arg1) {
                return arg0.getLabel().compareToIgnoreCase(arg1.getLabel());
            }
        });

        return params.toArray(new CommandParameterEnumValue[params.size()]);
    }

    @Nonnull
    private List<CommandParameterEnumValue> generateCommandParamList(@Nonnull final Collection<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocs) {
        final List<CommandParameterEnumValue> params = new ArrayList<CommandParameterEnumValue>(iocs.size());

        for (final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> ioc : iocs) {
            final LdapName ldapName = ioc.getLdapName();
            final String efanName =
                LdapNameUtils.getValueOfRdnType(ldapName,
                                                FACILITY.getNodeTypeName());

            final HashMap<String, String> map = new HashMap<String, String>();
            map.put(IOC.getNodeTypeName(), ioc.getName());
            map.put(FACILITY.getNodeTypeName(), efanName);
            params.add(new CommandParameterEnumValue(map, ioc.getName()));
        }
        return params;
    }

}
