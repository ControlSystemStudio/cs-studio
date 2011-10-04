package org.csstudio.config.authorizeid;

import static org.csstudio.utility.ldap.service.util.LdapUtils.any;
import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.GroupRoleTableViewerFactory.GroupRoleTableColumns;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides data for group / role table
 * @author Rok Povsic / Jörg Penning
 */
class GroupRoleLabelProvider extends LabelProvider implements ITableLabelProvider {

    private final String ROLE_ATTRIBUTE_KEY = "eagn";
    private final String ORGANIZATION_ATTRIBUTE_KEY = "ou";
    private final String ASSOCIATED_NAME_ATTRIBUTE_KEY = "associatedName";

    private final String CSS_ATTRIBUTE_VALUE = "Css";
    private final String AUTHORIZE_ATTRIBUTE_VALUE = "EpicsAuthorize";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        
        String result = null;
        
        if (element instanceof GroupRoleTableEntry) {
            GroupRoleTableEntry entry = (GroupRoleTableEntry) element;
            GroupRoleTableColumns colIndex = GroupRoleTableColumns.values()[columnIndex];
            switch (colIndex) {
                case GROUP:
                    result = entry.getEaig();
                    break;
                case ROLE:
                    result = entry.getEair();
                    break;
                case USER:
                    result = getUsers(entry);
                    break;
            }
        }
        return result;
    }
    
    @Nonnull
    private String getUsers(@Nonnull final GroupRoleTableEntry entry) {
        // (jpenning) why do this here in label provider? could be done at retrieval of entry.
        final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
        final ILdapSearchResult ldapResult = service.retrieveSearchResultSynchronously(
            createLdapName(ROLE_ATTRIBUTE_KEY, entry.getEair(), ORGANIZATION_ATTRIBUTE_KEY, entry.getEaig(),
                           ORGANIZATION_ATTRIBUTE_KEY, CSS_ATTRIBUTE_VALUE, ORGANIZATION_ATTRIBUTE_KEY, AUTHORIZE_ATTRIBUTE_VALUE),
                           any(ASSOCIATED_NAME_ATTRIBUTE_KEY), SearchControls.SUBTREE_SCOPE);
        
        StringBuilder result = new StringBuilder();
        if (ldapResult != null) {
            for (SearchResult searchResult : ldapResult.getAnswerSet()) {
                Attribute userList = searchResult.getAttributes().get(ASSOCIATED_NAME_ATTRIBUTE_KEY);
                if (userList != null) {
                    result.append(getSortedListOfUsers(userList, entry));
                }
            }
        }
        
        return result.toString();
    }
    
    @Nonnull
    private String getSortedListOfUsers(@Nonnull final Attribute userList,
                                        @Nonnull final GroupRoleTableEntry entry) {
        String result = "";
        try {
            NamingEnumeration<?> allEAGNs = userList.getAll();
            List<String> users = new ArrayList<String>();
            while (allEAGNs.hasMoreElements()) {
                String userAttribute = allEAGNs.nextElement().toString();
                users.add(getUserNameWithoutPrefix(userAttribute));
            }
            Collections.sort(users);
            result = createStringOfUsers(users);
        } catch (NamingException e) {
            result = "<error retrieving users for this group / role: probably undefined in ldap>";
            CentralLogger.getInstance().error("Error retrieving users for group " + entry.getEaig()
                      + " and role " + entry.getEair(),
                                              e);
        }
        return result;
    }

    @Nonnull
    private String createStringOfUsers(@Nonnull final List<String> users) {
        StringBuilder usersAsString = new StringBuilder();
        for (String user : users) {
            usersAsString.append(user);
            usersAsString.append(' ');
        }
        return usersAsString.toString();
    }

    @Nonnull
    private String getUserNameWithoutPrefix(@CheckForNull final String userAttribute) {
        String result = "";
        if (userAttribute != null) {
            // the key 'eaun=' is removed
            int beginIndex = userAttribute.indexOf('=') + 1;
            if (beginIndex < userAttribute.length()) {
                result = userAttribute.substring(beginIndex);
            }
            else {
                result = "<problem scanning attribute: " + userAttribute + ">";
           }
        }
        return result;
    }
    
}
