package org.csstudio.config.authorizeid.ldap;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class LdapGroups implements Observer {

	private int time_for_timeout = 100; // multiply this by 10 (if you set 100, it will be 1000 miliseconds)
	
	private LDAPReader _ldapr;
	
	private ErgebnisListe _ergebnisListe = new ErgebnisListe();
	
	/**
	 * Group to search in.
	 */
	private String string_search_root = "ou=EpicsAuthorizeID";
	
	private ArrayList<String> er;
	
	private String[] stringArray;
	
	ArrayList<String> al = new ArrayList<String>();
	
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
        	if(!al.isEmpty()) {
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
		stringArray = (String[])al.toArray(new String[al.size()]);
        
        return stringArray;
	}

	/**
	 * Update() is executed when notifyView() is called.
	 */
	public void update(Observable arg0, Object arg1) {
		er = _ergebnisListe.getAnswer();

        // change ArrayList to Array
        stringArray = (String[])er.toArray(new String[er.size()]);
        
		for (int i = 0; i < stringArray.length; i++) {
					
			if(!(stringArray[i].split(",")[0].equals(""))) {
								
				stringArray[i] = stringArray[i].substring(3);
				
				al.add(stringArray[i].split(",")[0]);

			}
		}
		
	}
}
