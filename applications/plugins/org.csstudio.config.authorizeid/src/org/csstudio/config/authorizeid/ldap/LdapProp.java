package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.utility.ldap.LdapUtils.EAIN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_AUTH_ID_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.csstudio.utility.ldap.LdapUtils.createLdapQuery;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdEntry;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;

import service.LdapService;
import service.impl.LdapServiceImpl;

public class LdapProp {

	private final int time_for_timeout = 10; // multiply this by 10 (if you set 10, it will be 100 miliseconds)

	private final LdapService _service = LdapServiceImpl.getInstance();

	List<AuthorizeIdEntry> al = new ArrayList<AuthorizeIdEntry>();

	/**
	 * Return attributes from LDAP.
	 * @param eain the name
	 * @param ou the group
	 * @return attributes
	 */
	public AuthorizeIdEntry[] getProp(final String eain, final String ou) {

		final LdapSearchResult result =
		    _service.retrieveSearchResult(createLdapQuery(EAIN_FIELD_NAME, eain,
		                                                  OU_FIELD_NAME, ou,
		                                                  OU_FIELD_NAME, EPICS_AUTH_ID_FIELD_VALUE),
		                                  any(EAIN_FIELD_NAME),
		                                  LDAPReader.DEFAULT_SCOPE);

		for (final SearchResult row : result.getAnswerSet()) {
		    final String name = row.getName();

		    if(name.substring(0, 4).equals("eaig")) {
		        // TODO (rpovsic) : unsafe access - NPEs
		        final String eaig = name.substring(5).split("\\+")[0];
		        final String eair = name.split("=")[3].split(",")[0];

		        final AuthorizeIdEntry entry = new AuthorizeIdEntry(eaig, eair);
		        al.add(entry);
		    }
		}


//
//        int times = 0;
//
//        // makes sure there is enough time for LDAP to provide data
//        while(true) {
//        	// if al is not empty, it goes through
//        	if(!al.isEmpty()) {
//        		break;
//        	}
//
//        	// after two seconds, it breaks the loop
//        	if(times > time_for_timeout) {
//        		break;
//        	}
//
//	        try {
//				Thread.sleep(10);
//			} catch (final InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			times++;
//        }

		// change ArrayList to Array
		final AuthorizeIdEntry[] returnArray = al.toArray(new AuthorizeIdEntry[al.size()]);

		return returnArray;
	}
}
