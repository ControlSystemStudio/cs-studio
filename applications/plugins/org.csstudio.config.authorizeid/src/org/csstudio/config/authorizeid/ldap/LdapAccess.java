/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration.ID_NAME;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration.ID_ROLE;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration.OU;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration.UNIT;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.config.authorizeid.GroupRoleTableEntry;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.service.util.LdapUtils;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAuthorizeIdFieldsAndAttributes;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.eclipse.jface.dialogs.MessageDialog;

public final class LdapAccess {

    /**
     * Don't instantate
     */
    private LdapAccess() {
        // EMPTY
    }

    /**
     * Return attributes from LDAP.
     * @param eain the name
     * @param ou the group
     * @return attributes
     */
    public static GroupRoleTableEntry[] getProp(final String eain, final String ou) {

        final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
        if (service == null) {
            MessageDialog.openError(null,
                                    "LDAP Access failed",
                                    "No LDAP service available. Try again later.");
            return new GroupRoleTableEntry[0];
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(createLdapName(ID_NAME.getNodeTypeName(), eain,
                                                                      OU.getNodeTypeName(), ou,
                                                                      UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                      any(ID_ROLE.getNodeTypeName()),
                                                      SearchControls.SUBTREE_SCOPE);
        if (result == null || result.getAnswerSet().isEmpty()) {
            MessageDialog.openInformation(null,
                                          "LDAP Access",
                                          "No search result.");
            return new GroupRoleTableEntry[0];
        }

        final List<GroupRoleTableEntry> al = new ArrayList<GroupRoleTableEntry>();
        for (final SearchResult row : result.getAnswerSet()) {
            final String name = row.getName();
            final String type = row.getClassName();

            String eaig = null;
            String eair = null;
            final String[] components = name.split("\\+");
            for (final String keyValue : components) {
                final String[] keyValueArray = keyValue.split(LdapFieldsAndAttributes.FIELD_ASSIGNMENT);
                final String key = keyValueArray[0];
                final String value = keyValueArray[1];

                if (LdapEpicsAuthorizeIdFieldsAndAttributes.ATTR_EAIG_FIELD_NAME.equals(key)) {
                    eaig = value;
                } else if (LdapEpicsAuthorizeIdConfiguration.ID_ROLE.getNodeTypeName().equals(key)) {
                    eair = value;
                }
            }
            if (!StringUtil.isBlank(eaig) && !StringUtil.isBlank(eair)) {
                final GroupRoleTableEntry entry = new GroupRoleTableEntry(eaig, eair);
                al.add(entry);
            }
        }

        return al.toArray(new GroupRoleTableEntry[al.size()]);
    }

    /**
     * Return name from LDAP
     * @param ou the group
     * @return name (eain)
     */
    public static String[] getEain(final String ou) {

        final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
        if (service == null) {
            MessageDialog.openError(null,
                                    "LDAP Access failed",
                                    "No LDAP service available. Try again later.");
            return new String[0];
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(LdapUtils.createLdapName(OU.getNodeTypeName(), ou,
                                                                                UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                      any(ID_NAME.getNodeTypeName()),
                                                      SearchControls.SUBTREE_SCOPE);
           if (result == null || result.getAnswerSet().isEmpty()) {
                MessageDialog.openInformation(null,
                                              "LDAP Access",
                                              "No search result.");
                return new String[0];
            }

        final List<String> al = new ArrayList<String>();
        for (final SearchResult row : result.getAnswerSet()) {
            String name = row.getName();
            // TODO (rpovsic) : unsafe access - NPEs
            if(name.substring(0, 4).equals(ID_NAME.getNodeTypeName())) {
                name = name.substring(5);

                al.add(name.split(",")[0]);
            }
        }

        // change ArrayList to Array
        return al.toArray(new String[al.size()]);
    }

    /**
     * Returns groups from LDAP.
     * @return groups
     */
    public static String[] getGroups() {

        final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
        if (service == null) {
            MessageDialog.openError(null,
                                    "LDAP Access failed",
                                    "No LDAP service available. Try again later.");
            return new String[0];
        }

        final ILdapSearchResult result =
            service.retrieveSearchResultSynchronously(LdapUtils.createLdapName(UNIT.getNodeTypeName(), UNIT.getUnitTypeValue()),
                                                      any(OU.getNodeTypeName()),
                                                      SearchControls.SUBTREE_SCOPE);

        if (result == null || result.getAnswerSet().isEmpty()) {
            MessageDialog.openInformation(null,
                                          "LDAP Access",
                                          "No search result.");
            return new String[0];
        }

        final List<String> al = new ArrayList<String>();
        for (final SearchResult row : result.getAnswerSet()) {
            String name = row.getName();
            // TODO (rpovsic) : unsafe access - NPEs
            if(!name.split(",")[0].equals("")) {
                name = name.substring(3);
                al.add(name.split(",")[0]);
            }
        }
        return al.toArray(new String[al.size()]);
    }
}
