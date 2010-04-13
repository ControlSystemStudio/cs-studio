package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.utility.ldap.LdapUtils.EAIN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_AUTH_ID_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapUtils.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;

import service.LdapService;
import service.impl.LdapServiceImpl;

public class LdapEain {

	private final int time_for_timeout = 10; // multiply this by 10 (if you set 10, it will be 100 miliseconds)

    private final LdapService _service = LdapServiceImpl.getInstance();

	private String[] stringArray;

	List<String> al = new ArrayList<String>();

	/**
	 * Return name from LDAP
	 * @param ou the group
	 * @return name (eain)
	 */
	public String[] getEain(final String ou) {

		final LdapSearchResult result =
		    _service.retrieveSearchResult(OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_AUTH_ID_FIELD_VALUE,
		                                  any(EAIN_FIELD_NAME),
		                                  LDAPReader.DEFAULT_SCOPE);


//		_ldapr = new LDAPReader(string_search_root, filter, _ldapSearchResult, new JobCompletedCallBack() {
//            @Override
//            public void onLdapReadComplete() {
                for (final SearchResult searchResult : result.getAnswerSet()) {
                    String row = searchResult.getName();
                    // TODO (rpovsic) : unsafe access - NPEs
                    if(row.substring(0, 4).equals("eain")) {
                        row = row.substring(5);

                        al.add(row.split(",")[0]);
                    }
                }
//            }
//		});
//
//		_ldapr.schedule();
//
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
		stringArray = al.toArray(new String[al.size()]);

		return stringArray;
	}
}
