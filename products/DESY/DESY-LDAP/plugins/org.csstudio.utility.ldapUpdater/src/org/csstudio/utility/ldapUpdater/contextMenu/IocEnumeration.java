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

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameterEnumValue;
import org.csstudio.platform.management.IDynamicParameterValues;
import org.csstudio.utility.ldap.service.ILdapContentModelBuilder;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.utils.LdapNameUtils;
import org.csstudio.utility.ldapUpdater.Activator;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;


/**
 * Enumeration of IOCs as retrieved from LDAP for the management commands.
 *
 * @author bknerr 17.03.2010
 */
public class IocEnumeration implements IDynamicParameterValues {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(IocEnumeration.class);

    @Override
    @Nonnull
    public CommandParameterEnumValue[] getEnumerationValues() {

        final ILdapService service = Activator.getDefault().getLdapService();

        if (service == null) {
            LOG.error("LDAP service is not available! Here a service tracker would have made sense.");
            return new CommandParameterEnumValue[0];
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                      any(IOC.getNodeTypeName()),
                                                      SearchControls.SUBTREE_SCOPE);
        if (result == null) {
            LOG.error("Search result was empty");
            return new CommandParameterEnumValue[0];
        }

        try {
            final ILdapContentModelBuilder<LdapEpicsControlsConfiguration> builder =
                service.getLdapContentModelBuilder(LdapEpicsControlsConfiguration.VIRTUAL_ROOT, result);

            builder.build();
            final ContentModel<LdapEpicsControlsConfiguration> model = builder.getModel();

            if (model == null) {
                return new CommandParameterEnumValue[0];
            }

            final Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocs =
                model.getChildrenByTypeAndLdapName(IOC);

            final List<CommandParameterEnumValue> params = generateCommandParamList(iocs);

            Collections.sort(params, new Comparator<CommandParameterEnumValue>() {
                @Override
                public int compare(@Nonnull final CommandParameterEnumValue arg0, @Nonnull final CommandParameterEnumValue arg1) {
                    return arg0.getLabel().compareToIgnoreCase(arg1.getLabel());
                }
            });

            return params.toArray(new CommandParameterEnumValue[params.size()]);

        } catch (final CreateContentModelException e) {
            LOG.error("Content model could not be constructed. Root element has invalid LDAP name.", e);
            return new CommandParameterEnumValue[0];
        }

    }

    @Nonnull
    private List<CommandParameterEnumValue> generateCommandParamList(@Nonnull final Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocs) {
        final List<CommandParameterEnumValue> params = new ArrayList<CommandParameterEnumValue>(iocs.size());

        for (final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> ioc : iocs.values()) {
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
