package org.csstudio.config.authorizeid.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.naming.directory.SearchResult;

import org.csstudio.config.authorizeid.AuthorizeIdEntry;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class LdapProp implements Observer {

	private final int time_for_timeout = 10; // multiply this by 10 (if you set 10, it will be 100 miliseconds)

	private LDAPReader _ldapr;

	private final LdapSearchResult _ldapSearchResult = new LdapSearchResult();


	List<AuthorizeIdEntry> al = new ArrayList<AuthorizeIdEntry>();

	/**
	 * Return attributes from LDAP.
	 * @param eain the name
	 * @param ou the group
	 * @return attributes
	 */
	public AuthorizeIdEntry[] getProp(final String eain, final String ou) {

		/**
		 * Group to search in.
		 */
		final String string_search_root = "eain="+ eain +",ou=" + ou +",ou=EpicsAuthorizeID";

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
                    LdapProp.this._ldapSearchResult.notifyView();
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
		final AuthorizeIdEntry[] returnArray = al.toArray(new AuthorizeIdEntry[al.size()]);

		return returnArray;
	}

	/**
	 * Update() is executed when notifyView() is called.
	 */
	public void update(final Observable arg0, final Object arg1) {

	    for (final SearchResult row : _ldapSearchResult.getAnswerSet()) {
	        final String name = row.getName();

            if(name.substring(0, 4).equals("eaig")) {
                // TODO (hrickens) : unsafe access - NPEs
                final String eaig = name.substring(5).split("\\+")[0];
                final String eair = name.split("=")[3].split(",")[0];

                final AuthorizeIdEntry entry = new AuthorizeIdEntry(eaig, eair);
                al.add(entry);
            }
	    }
	}
}
