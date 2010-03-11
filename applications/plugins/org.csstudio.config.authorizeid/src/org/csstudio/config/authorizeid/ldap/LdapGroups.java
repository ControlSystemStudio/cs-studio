package org.csstudio.config.authorizeid.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.utility.ldap.reader.LdapResultList;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class LdapGroups implements Observer {

	private int time_for_timeout = 100; // multiply this by 10 (if you set 100, it will be 1000 miliseconds)
	
	private LDAPReader _ldapr;
	
	private LdapResultList _ergebnisListe = new LdapResultList();
	
	/**
	 * Group to search in.
	 */
	private String string_search_root = "ou=EpicsAuthorizeID";
	
	private List<String> _er;
	
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
        String filter = "ou=*"; //$NON-NLS-1$
		
        _ergebnisListe.addObserver(this);
        
        _ldapr = new LDAPReader(string_search_root,filter, _ergebnisListe);
        _ldapr.addJobChangeListener(new JobChangeAdapter() {
            public void done(IJobChangeEvent event) {
            if (event.getResult().isOK())
                LdapGroups.this._ergebnisListe.notifyView();
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			times++;
        }

		// change ArrayList to Array
		stringArray = (String[])_al.toArray(new String[_al.size()]);
        
        return stringArray;
	}

	/**
	 * Update() is executed when notifyView() is called.
	 */
	public void update(Observable arg0, Object arg1) {
		_er = _ergebnisListe.getAnswer();

        // change ArrayList to Array
        stringArray = (String[])_er.toArray(new String[_er.size()]);
        
		for (int i = 0; i < stringArray.length; i++) {
					
			if(!(stringArray[i].split(",")[0].equals(""))) {
								
				stringArray[i] = stringArray[i].substring(3);
				
				_al.add(stringArray[i].split(",")[0]);

			}
		}
		
	}
}
