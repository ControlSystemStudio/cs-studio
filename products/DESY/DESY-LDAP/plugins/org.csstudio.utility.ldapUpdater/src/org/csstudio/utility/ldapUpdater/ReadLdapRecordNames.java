package org.csstudio.utility.ldapUpdater;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class ReadLdapRecordNames implements Observer{

	private DataModel _model;
	private ErgebnisListe _ergebnis;
 
	public ReadLdapRecordNames(DataModel model) {
	 _model=model;
	}

	public void readLdapRecs(IOC ioc) {
        _ergebnis = new ErgebnisListe();
        _ergebnis.addObserver(this);

//        String filter = "(&(econ="+ioc.getGroup()+") (eren=*))";
//        CentralLogger.getInstance().info(this, "Filter: "+filter);
//        LDAPReader reader = new LDAPReader( "ou=EpicsControls","(&(econ="+ldapIocName+") (eren=*))",_ergebnis);
        Formatter f = new Formatter();
        f.format("econ=%s, ecom=EPICS-IOC, efan=%s, ou=EpicsControls",ioc.getName(), ioc.getGroup());
        CentralLogger.getInstance().info(this, "Ldap Path: "+f.toString() );
        LDAPReader reader = new LDAPReader( f.toString(),"eren=*",_ergebnis);
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
//       _ready=true;	
	}

//  @Override
    public void update(Observable o, Object arg) {
    	CentralLogger.getInstance().info(this, "Start Update");
        ArrayList<String> list = _ergebnis.getAnswer();
        _model.setLdapRecordNames(list);
        
        for (String record : list) {
        	CentralLogger.getInstance().debug(this, "LDAP: "+record);
        }
        _model.setReady(true);
        CentralLogger.getInstance().info(this, "ready="+_model.isReady());
        CentralLogger.getInstance().error(this, ""+_ergebnis.getResultList().size());
    }
}
