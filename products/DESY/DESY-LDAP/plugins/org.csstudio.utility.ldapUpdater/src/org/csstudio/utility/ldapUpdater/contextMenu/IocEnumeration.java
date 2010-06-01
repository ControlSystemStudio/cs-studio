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

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.ECON_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;

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
import org.csstudio.utility.ldap.LdapNameUtils;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.model.ContentModel;
import org.csstudio.utility.ldap.model.CreateContentModelException;
import org.csstudio.utility.ldap.model.ILdapBaseComponent;
import org.csstudio.utility.ldap.model.ILdapTreeComponent;
import org.csstudio.utility.ldap.model.LdapEpicsControlsObjectClass;
import org.csstudio.utility.ldap.model.builder.LdapContentModelBuilder;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldapUpdater.Activator;


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

        final LdapSearchResult result =
            service.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU_FIELD_NAME, EPICS_CTRL_FIELD_VALUE),
                                                      any(ECON_FIELD_NAME),
                                                      SearchControls.SUBTREE_SCOPE);

        try {
            final LdapContentModelBuilder<LdapEpicsControlsObjectClass> builder =
                new LdapContentModelBuilder<LdapEpicsControlsObjectClass>(LdapEpicsControlsObjectClass.ROOT, result);
            builder.build();
            final ContentModel<LdapEpicsControlsObjectClass> model = builder.getModel();

            final Map<String, ILdapTreeComponent<LdapEpicsControlsObjectClass>> iocs =
                model.getChildrenByTypeAndLdapName(LdapEpicsControlsObjectClass.IOC);

            final List<CommandParameterEnumValue> params = new ArrayList<CommandParameterEnumValue>(iocs.size());

            for (final ILdapBaseComponent<LdapEpicsControlsObjectClass> ioc : iocs.values()) {
                final LdapName ldapName = ioc.getLdapName();
                final String efanName =
                    LdapNameUtils.getValueOfRdnType(ldapName,
                                                    LdapEpicsControlsObjectClass.FACILITY.getRdnType());

                final HashMap<String, String> map = new HashMap<String, String>();
                map.put(ECON_FIELD_NAME, ioc.getName());
                map.put(EFAN_FIELD_NAME, efanName);
                params.add(new CommandParameterEnumValue(map, ioc.getName()));
            }

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

}
