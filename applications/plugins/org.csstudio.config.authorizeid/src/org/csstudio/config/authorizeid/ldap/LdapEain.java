package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ID_NAME;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.OU;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ROOT;
import static org.csstudio.utility.ldap.utils.LdapUtils.any;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.utils.LdapUtils;

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
	    final LdapSearchResult result =
	        service.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU.getNodeTypeName(), ou,
	                                                                            ROOT.getNodeTypeName(), ROOT.getRootTypeValue()),
	                                                  any(ID_NAME.getNodeTypeName()),
	                                                  LDAPReader.DEFAULT_SCOPE);

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
