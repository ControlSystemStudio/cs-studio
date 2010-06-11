package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EAIN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_AUTH_ID_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdActivator;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;

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
	        service.retrieveSearchResultSynchronously(LdapUtils.createLdapQuery(OU_FIELD_NAME, ou,
	                                                                            OU_FIELD_NAME, EPICS_AUTH_ID_FIELD_VALUE),
	                                                  any(EAIN_FIELD_NAME),
	                                                  LDAPReader.DEFAULT_SCOPE);

	    final List<String> al = new ArrayList<String>();
	    for (final SearchResult searchResult : result.getAnswerSet()) {
	        String row = searchResult.getName();
	        // TODO (rpovsic) : unsafe access - NPEs
	        if(row.substring(0, 4).equals(EAIN_FIELD_NAME)) {
	            row = row.substring(5);

	            al.add(row.split(",")[0]);
	        }
	    }

	    // change ArrayList to Array
	    return al.toArray(new String[al.size()]);
	}
}
