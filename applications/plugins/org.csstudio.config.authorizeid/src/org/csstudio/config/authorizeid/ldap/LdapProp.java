package org.csstudio.config.authorizeid.ldap;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.config.authorizeid.AuthorizeIdEntry;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class LdapProp implements Observer {

	private LDAPReader _ldapr;
	
	private ErgebnisListe _ergebnisListe = new ErgebnisListe();
	
	private ArrayList<String> er;
	
	private String[] stringArray;
	
	ArrayList<AuthorizeIdEntry> al = new ArrayList<AuthorizeIdEntry>();
	
	/**
	 * Return attributes from LDAP.
	 * @param eain the name
	 * @param ou the group
	 * @return attributes
	 */
	public AuthorizeIdEntry[] getProp(String eain, String ou) {
		
		/**
		 * Group to search in.
		 */
		String string_search_root = "eain="+ eain +",ou=" + ou +",ou=EpicsAuthorizeID";

		/**
		 * Search for this.
		 */
		String filter = "eain=*"; //$NON-NLS-1$
		
		_ergebnisListe.addObserver(this);
      
		_ldapr = new LDAPReader(string_search_root,filter, _ergebnisListe);
		_ldapr.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK())
					LdapProp.this._ergebnisListe.notifyView();
			}
		});
		_ldapr.schedule();

      
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// change ArrayList to Array
		AuthorizeIdEntry[] returnArray = (AuthorizeIdEntry[])al.toArray(new AuthorizeIdEntry[al.size()]);
      
		return returnArray;
	}
	
	/**
	 * Update() is executed when notifyView() is called.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		er = _ergebnisListe.getAnswer();

		// change ArrayList to Array
        stringArray = (String[])er.toArray(new String[er.size()]);
        
		for (int i = 0; i < stringArray.length; i++) {
			String eaig = null;
			String eair = null;
			
			if(stringArray[i].substring(0, 4).equals("eaig")) {
				eaig = stringArray[i].substring(5);
				eaig = eaig.split("\\+")[0];
				
				eair = stringArray[i].split("=")[3].split(",")[0];
				
				AuthorizeIdEntry entry = new AuthorizeIdEntry(eaig, eair);
				al.add(entry);
			}
		}
	}
}
