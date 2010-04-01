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

public class LdapGroups implements Observer {

	private final int time_for_timeout = 100; // multiply this by 10 (if you set 100, it will be 1000 miliseconds)

	private LDAPReader _ldapr;

	private final LdapSearchResult _ldapSearchResult = new LdapSearchResult();

	/**
	 * Group to search in.
	 */
	private final String string_search_root = "ou=EpicsAuthorizeID";


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
        final String filter = "ou=*"; //$NON-NLS-1$

        _ldapSearchResult.addObserver(this);

        _ldapr = new LDAPReader(string_search_root,filter, _ldapSearchResult);
        _ldapr.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(final IJobChangeEvent event) {
            if (event.getResult().isOK()) {
                LdapGroups.this._ldapSearchResult.notifyView();
            }
            }
         });
        _ldapr.schedule();


        int times = 0;

        // makes sure there is enough time for LDAP to provide data
        while(true) {
        	// if al is not empty, it goes through
        	if(!_al.isEmpty()) {
        		break;
        	}

        	// after some time, it breaks the loop
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
		stringArray = _al.toArray(new String[_al.size()]);

        return stringArray;
	}

	/**
	 * Update() is executed when notifyView() is called.
	 */
	public void update(final Observable arg0, final Object arg1) {
	    for (final SearchResult row : _ldapSearchResult.getAnswerSet()) {
            String name = row.getName();
            // TODO (hrickens) : unsafe access - NPEs
            if(!(name.split(",")[0].equals(""))) {
                name = name.substring(3);
                _al.add(name.split(",")[0]);
            }
        }
	}
}
