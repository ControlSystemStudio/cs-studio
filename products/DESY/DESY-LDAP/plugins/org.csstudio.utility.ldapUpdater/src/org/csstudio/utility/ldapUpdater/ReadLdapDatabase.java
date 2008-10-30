package org.csstudio.utility.ldapUpdater;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class ReadLdapDatabase implements Observer{

	private DataModel _model;
	private ErgebnisListe _ergebnis;

	public ReadLdapDatabase(DataModel model) {
	 _model=model;
	}

	public void readLdap() {
        _ergebnis = new ErgebnisListe();
        _ergebnis.addObserver(this);

        LDAPReader reader = new LDAPReader("ou=EpicsControls","econ=*",_ergebnis);
        reader.addJobChangeListener(new JobChangeAdapter() {
            public void done(final IJobChangeEvent event) {
                if (event.getResult().isOK()) {
                    _ergebnis.notifyView();
                }
                else {
                    CentralLogger.getInstance().error(this, "LDAP: NOT event.getResult().isOK() ");
                }             
            }
         });
        reader.schedule();
    	}

//  @Override
    public void update(Observable o, Object arg) {
    	CentralLogger.getInstance().info(this, "Start Update");
        ArrayList<String> list = _ergebnis.getAnswer();
        _model.setLdapList(list);
        
        for (String record : list) {
            CentralLogger.getInstance().debug(this, "LDAP: "+record);
        }
        
        CentralLogger.getInstance().info(this, "IOC names in LDAP tree : "+list.size());   
        
        _model.setReady(true);
        CentralLogger.getInstance().info(this, "ready="+_model.isReady());
    }
}
