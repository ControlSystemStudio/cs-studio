package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ID_NAME;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ID_ROLE;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.OU;
import static org.csstudio.config.authorizeid.LdapEpicsAuthorizeIdConfiguration.ROOT;
import static org.csstudio.utility.ldap.utils.LdapUtils.any;
import static org.csstudio.utility.ldap.utils.LdapUtils.createLdapQuery;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.config.authorizeid.AuthorizeIdEntry;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;


/**
 * Retrieve props from LDAP.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 29.04.2010
 */
public class LdapProp {


	/**
	 * Return attributes from LDAP.
	 * @param eain the name
	 * @param ou the group
	 * @return attributes
	 */
    public AuthorizeIdEntry[] getProp(final String eain, final String ou) {

	    final ILdapService service = AuthorizeIdActivator.getDefault().getLdapService();


	    final LdapSearchResult result =
	        service.retrieveSearchResultSynchronously(createLdapQuery(ID_NAME.getNodeTypeName(), eain,
	                                                                  OU.getNodeTypeName(), ou,
	                                                                  ROOT.getNodeTypeName(), ROOT.getNodeTypeName()),
	                                                  any(ID_ROLE.getNodeTypeName()),
	                                                  LDAPReader.DEFAULT_SCOPE);

		final List<AuthorizeIdEntry> al = new ArrayList<AuthorizeIdEntry>();
		for (final SearchResult row : result.getAnswerSet()) {
		    final String name = row.getName();

		    if(name.substring(0, 4).equals(ID_ROLE.getNodeTypeName())) {
		        // TODO (rpovsic) : unsafe access - NPEs
		        final String eaig = name.substring(5).split("\\+")[0];
		        final String eair = name.split("=")[3].split(",")[0];

		        final AuthorizeIdEntry entry = new AuthorizeIdEntry(eaig, eair);
		        al.add(entry);
		    }
		}

		return al.toArray(new AuthorizeIdEntry[al.size()]);
	}
}
