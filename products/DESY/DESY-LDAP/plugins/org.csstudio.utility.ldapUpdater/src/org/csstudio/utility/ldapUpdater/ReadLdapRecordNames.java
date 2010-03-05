/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

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
        //_model.setLdapRecordNames(list);
        
        for (String record : list) {
        	CentralLogger.getInstance().debug(this, "LDAP: "+record);
        }
        _model.setReady(true);
        CentralLogger.getInstance().info(this, "ready="+_model.isReady());
        CentralLogger.getInstance().error(this, ""+_ergebnis.getResultList().size());
    }
}
