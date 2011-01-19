package org.csstudio.config.authorizeid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.GroupRoleTableViewerFactory.GroupRoleTableColumns;
import org.csstudio.platform.logging.CentralLogger;
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
        String result = null;
        
        try {
            DirContext ctx = new InitialDirContext(createEnvironment());
            
            SearchControls ctrls = new SearchControls();
            ctrls.setReturningAttributes(new String[] {"associatedName"});
            ctrls.setReturningObjFlag(true);
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            String filter = "(eagn=" + entry.getEair() + ")";
            NamingEnumeration<SearchResult> results =
                ctx.search("ou=" + entry.getEaig() + ",ou=Css,ou=EpicsAuthorize", filter, ctrls);
            while (results.hasMore()) {
                SearchResult r = results.next();
                Attribute attribute = r.getAttributes().get("associatedName");
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
            }
        } catch (NamingException e) {
            result = "<error retrieving users for this group / role: probably undefined in ldap>";
            CentralLogger.getInstance().error(this,
                    "Error retrieving users for group " + entry.getEaig() + " and role " + entry.getEair(), e);
        }
        
        return result;
    }
    
    
    /**
     * Creates the environment for the LDAP connection.
     */
    private Hashtable<String, String> createEnvironment() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://krynfs.desy.de:389/o=DESY,c=DE");
        env.put(Context.SECURITY_PRINCIPAL, "uid=css_user,ou=people,o=DESY,c=DE");
        env.put(Context.SECURITY_CREDENTIALS, "cssPass");
        return env;
    }

}
