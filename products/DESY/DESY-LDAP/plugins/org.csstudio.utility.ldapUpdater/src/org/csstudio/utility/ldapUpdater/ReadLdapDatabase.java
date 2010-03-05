package org.csstudio.utility.ldapUpdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.IocFinder.LdapFullEntry;
import org.csstudio.utility.ldapUpdater.model.DataModel;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * TODO (kvalett) : Documentation missing.
 * @author valett
 */
public class ReadLdapDatabase implements Observer {

	private static final CentralLogger LOGGER = CentralLogger.getInstance();
	private DataModel _model;
	private ErgebnisListe _ergebnis = new ErgebnisListe();

	public ReadLdapDatabase(DataModel model) {
		_model = model;
	}



	public void readLdapEcons() {
		_ergebnis.addObserver(this);

		LDAPReader reader = 
			new LDAPReader("ou=EpicsControls", "econ=*", _ergebnis);
		reader.addJobChangeListener(new JobChangeAdapter() {
			public void done(final IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					_ergebnis.notifyView();
				} else {
					LOGGER.error(this,
							"econ LDAP: NOT event.getResult().isOK() ");
				}
			}
		});
		reader.schedule();
	}
	


	/**
	 * TODO (kvalett) : Docu missing.
	 * @throws IllegalStateException
	 *             If LDAPReader does not list any econs or efans
	 */
	@Override
	public void update(Observable o, Object arg) {
		LOGGER.info(this, "Start Update");
		
		
		List<String> econAnswer = _ergebnis.getAnswer();
		
		Map<String, String> econToEfanMap = new HashMap<String, String>(econAnswer.size());
		
		for (String ldapPath : econAnswer) {
			LdapFullEntry ldapEntry = IocFinder.getLdapEntry(ldapPath);
			econToEfanMap.put(ldapEntry.getEcon(), ldapEntry.getEfan());
		}
		
		_model.setEconToEfanMap(econToEfanMap);
		LOGGER.info(this, econToEfanMap.keySet().size() + " econ entries found and put into model.");
		
		_model.setReady(true);
		LOGGER.info(this, "ready=" + _model.isReady());
	}
	
	

	public void readLdap() {
        _ergebnis.addObserver(this);

        final LDAPReader reader = new LDAPReader("ou=EpicsControls","econ=*",_ergebnis);
        reader.addJobChangeListener(new JobChangeAdapter() {
            public void done(final IJobChangeEvent event) {
                int trys =3;
                if (event.getResult().isOK()) {
                    _ergebnis.notifyView();
                }
                else {
                    if(trys<3) {
                        CentralLogger.getInstance().debug(this, "No LDAP connection! Try: "+trys);
                        trys++;
                        reader.schedule();
                    }else {
                        CentralLogger.getInstance().error(this, "LDAP: NOT event.getResult().isOK() ");
                    }
                }             
            }
         });
        reader.schedule();
    	}

}
