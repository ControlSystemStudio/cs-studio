package org.csstudio.config.authorizeid.ldap;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class LdapEain implements Observer {

	private LDAPReader _ldapr;
	
	private ErgebnisListe _ergebnisListe = new ErgebnisListe();
		
	private ArrayList<String> er;
	
	private String[] stringArray;
	
	ArrayList<String> al = new ArrayList<String>();
	
	/**
	 * Return name from LDAP
	 * @param ou the group
	 * @return name (eain)
	 */
	public String[] getEain(String ou) {

		/**
		 * Group to search in.
		 */
		String string_search_root = "ou=" + ou+",ou=EpicsAuthorizeID";
		
		/**
		 * Search for this.
		 */
		String filter = "eain=*"; //$NON-NLS-1$
		
		_ergebnisListe.addObserver(this);
      
		_ldapr = new LDAPReader(string_search_root,filter, _ergebnisListe);
		_ldapr.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK())
					LdapEain.this._ergebnisListe.notifyView();
			}
		});
		_ldapr.schedule();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			if(stringArray[i].substring(0, 4).equals("eain")) {
				stringArray[i] = stringArray[i].substring(5);
				
				al.add(stringArray[i].split(",")[0]);
			}
		}
	}
}
