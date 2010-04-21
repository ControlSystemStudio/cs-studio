package org.csstudio.config.authorizeid.ldap;

import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.EPICS_AUTH_ID_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapFieldsAndAttributes.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.LdapService;
import org.csstudio.utility.ldap.service.impl.LdapServiceImpl;


public class LdapGroups {

	private final int time_for_timeout = 100; // multiply this by 10 (if you set 100, it will be 1000 miliseconds)


	private final LdapService _service = LdapServiceImpl.getInstance();

	private String[] stringArray;

	List<String> _al = new ArrayList<String>();

	/**
	 * Returns groups from LDAP.
	 * @return groups
	 */
	public String[] getGroups() {

		/**
		 * Search for this.
		 */
        //final String filter = "ou=*"; //$NON-NLS-1$

        final LdapSearchResult result =
            _service.retrieveSearchResult(OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_AUTH_ID_FIELD_VALUE,
                                          any(OU_FIELD_NAME),
                                          LDAPReader.DEFAULT_SCOPE);



//        _ldapr = new LDAPReader(string_search_root,filter, _ldapSearchResult, new JobCompletedCallBack() {
//            @Override
//            public void onLdapReadComplete() {
                for (final SearchResult row : result.getAnswerSet()) {
                    String name = row.getName();
                    // TODO (rpovsic) : unsafe access - NPEs
                    if(!(name.split(",")[0].equals(""))) {
                        name = name.substring(3);
                        _al.add(name.split(",")[0]);
                    }
                }
//            }
//        });
//        _ldapr.schedule();
//
//
//        int times = 0;
//
//        // makes sure there is enough time for LDAP to provide data
//        while(true) {
//        	// if al is not empty, it goes through
//        	if(!_al.isEmpty()) {
//        		break;
//        	}
//
//        	// after some time, it breaks the loop
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
		stringArray = _al.toArray(new String[_al.size()]);

        return stringArray;
	}
}
