package org.csstudio.config.authorizeid.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class LdapEain implements Observer {

	private final int time_for_timeout = 10; // multiply this by 10 (if you set 10, it will be 100 miliseconds)

	private LDAPReader _ldapr;

	private final LdapSearchResult _ldapSearchResult = new LdapSearchResult();


	private String[] stringArray;

	List<String> al = new ArrayList<String>();

	/**
	 * Return name from LDAP
	 * @param ou the group
	 * @return name (eain)
	 */
	public String[] getEain(final String ou) {

		/**
		 * Group to search in.
		 */
		final String string_search_root = "ou=" + ou+",ou=EpicsAuthorizeID";

		/**
		 * Search for this.
		 */
		final String filter = "eain=*"; //$NON-NLS-1$

		_ldapSearchResult.addObserver(this);

		_ldapr = new LDAPReader(string_search_root,filter, _ldapSearchResult);
		_ldapr.addJobChangeListener(new JobChangeAdapter() {
			@Override
            public void done(final IJobChangeEvent event) {
				if (event.getResult().isOK()) {
                    LdapEain.this._ldapSearchResult.notifyView();
                }
			}
		});
		_ldapr.schedule();

        int times = 0;

        // makes sure there is enough time for LDAP to provide data
        while(true) {
        	// if al is not empty, it goes through
        	if(!al.isEmpty()) {
        		break;
        	}

        	// after two seconds, it breaks the loop
        	if(times > time_for_timeout) {
        		break;
        	}

	        try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			times++;
        }

		// change ArrayList to Array
		stringArray = al.toArray(new String[al.size()]);

		return stringArray;
	}

	/**
	 * Update() is executed when notifyView() is called.
	 */
	public void update(final Observable arg0, final Object arg1) {

	    for (final SearchResult searchResult : _ldapSearchResult.getAnswerSet()) {
            String row = searchResult.getName();
            // TODO (hrickens) : unsafe access - NPEs
            if(row.substring(0, 4).equals("eain")) {
                row = row.substring(5);

                al.add(row.split(",")[0]);
            }
        }
	}
}
