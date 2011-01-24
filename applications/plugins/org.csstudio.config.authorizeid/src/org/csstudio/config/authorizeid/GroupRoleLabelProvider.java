package org.csstudio.config.authorizeid;

import static org.csstudio.utility.ldap.utils.LdapUtils.any;
import static org.csstudio.utility.ldap.utils.LdapUtils.createLdapName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
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
    
    /**
     * {@inheritDoc}
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
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
    
    private String getUsers(GroupRoleTableEntry entry) {
        final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
        final ILdapSearchResult ldapResult = service
                .retrieveSearchResultSynchronously(createLdapName("eagn",
                                                                  entry.getEair(),
                                                                  "ou",
                                                                  entry.getEaig(),
                                                                  "ou",
                                                                  "Css",
                                                                  "ou",
                                                                  "EpicsAuthorize"),
                                                   any("associatedName"),
                                                   SearchControls.SUBTREE_SCOPE);
        
        String result = null;
        try {
            for (SearchResult searchResult : ldapResult.getAnswerSet()) {
                Attribute attribute = searchResult.getAttributes().get("associatedName");
                result = scanAttribute(result, attribute);
            }
        } catch (NamingException e) {
            result = "<error retrieving users for this group / role: probably undefined in ldap>";
            CentralLogger.getInstance().error(this,
                                              "Error retrieving users for group " + entry.getEaig()
                                                      + " and role " + entry.getEair(),
                                              e);
        }
        
        return result;
    }
    
    private String scanAttribute(String result, Attribute attribute) throws NamingException {
        if (attribute != null) {
            
            NamingEnumeration<?> allEAGNs = attribute.getAll();
            List<String> users = new ArrayList<String>();
            while (allEAGNs.hasMoreElements()) {
                String eaun = (String) allEAGNs.nextElement();
                if (eaun != null) {
                    users.add(eaun.substring(5));
                }
            }
            Collections.sort(users);
            
            StringBuilder usersAsString = new StringBuilder();
            for (String user : users) {
                usersAsString.append(user);
                usersAsString.append(' ');
            }
            result = usersAsString.toString();
        }
        return result;
    }
    
}
