package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ID_NAME;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.OU;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ROOT;
import static org.csstudio.utility.ldap.utils.LdapUtils.any;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.utils.LdapUtils;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Retrieves eain from LDAP.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 29.04.2010
 */
public class LdapEain {

	/**
	 * Return name from LDAP
	 * @param ou the group
	 * @return name (eain)
	 */
	public String[] getEain(final String ou) {

	    final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();
	    if (service == null) {
	        MessageDialog.openError(null,
	                                "LDAP Access failed",
	                                "No LDAP service available. Try again later.");
	        return new String[0];
	    }

	    final ILdapSearchResult result =
	        service.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU.getNodeTypeName(), ou,
	                                                                            ROOT.getNodeTypeName(), ROOT.getRootTypeValue()),
	                                                  any(ID_NAME.getNodeTypeName()),
	                                                  SearchControls.SUBTREE_SCOPE);

	    final List<String> al = new ArrayList<String>();
	    for (final SearchResult searchResult : result.getAnswerSet()) {
	        String row = searchResult.getName();
	        // TODO (rpovsic) : unsafe access - NPEs
	        if(row.substring(0, 4).equals(ID_NAME.getNodeTypeName())) {
	            row = row.substring(5);

	            al.add(row.split(",")[0]);
	        }
	    }

	    // change ArrayList to Array
	    return al.toArray(new String[al.size()]);
	}
}
