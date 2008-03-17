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

package org.csstudio.utility.ldap.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * Utility class to find the IOC of a process variable.
 * 
 * @author Joerg Rathlev, Jan Hatje
 */
public final class IocFinder {
	
	/**
	 * Observer for the ErgebnisListe.
	 */
	private static class ErgebnisListeObserver implements Observer {
		
		/**
		 * The result received from the LDAP server.
		 */
		private String _ldapResult;
		
		/**
		 * Set to <code>true</code> when the update method was called. 
		 */
		private boolean _done = false;
		
		/**
		 * {@inheritDoc}
		 */
		public synchronized void update(final Observable o, final Object arg) {
			if (!(o instanceof ErgebnisListe)) {
				throw new IllegalArgumentException("Observed object must be of type ErgebnisListe");
			}
			
            ArrayList<String> l = ((ErgebnisListe) o).getAnswer();
            String answerLDAP = l.get(0);
            // The LDAP reader returns a list containing the single entry
            // "no entry found" if nothing was found, so we must check that
            // here.
            if (!answerLDAP.equals("no entry found")) {
                _ldapResult = answerLDAP;
            } else {
            	_ldapResult = null;
            }
            _done = true;
            notifyAll();
		}
		
		/**
		 * Returns the result received from the LDAP server.
		 * @return the result received from the LDAP server.
		 */
		public synchronized String getResult() {
			while (!_done) {
				try {
					wait();
				} catch (InterruptedException e) {
					// handle as if no result was found
					return null;
				}
			}
			return _ldapResult;
		}
	}
	
	/**
	 * private constructor.
	 */
	private IocFinder() {
	}
	
	/**
	 * Returns the name of the IOC of the given process variable.
	 * 
	 * @param pv the process variable.
	 * @return the name of the IOC.
	 */
	public static String getIoc(final IProcessVariable pv) {
		if (pv == null) {
			throw new NullPointerException("pv must not be null");
		}
		
		final ErgebnisListe ergebnisListe = new ErgebnisListe();
		ErgebnisListeObserver obs = new ErgebnisListeObserver();
		ergebnisListe.addObserver(obs);
		
        String filter = "eren=" + pv.getName();
        LDAPReader ldapr = new LDAPReader("ou=EpicsControls",
                filter, ergebnisListe);
        // For some reason the ErgebnisListe doesn't notify its observers
        // when the result is written to it. The job change listener works
        // around this problem by sending the notification when the LDAP
        // reader job is done.
        ldapr.addJobChangeListener(new JobChangeAdapter() {
            public void done(final IJobChangeEvent event) {
	            if (event.getResult().isOK()) {
	               ergebnisListe.notifyView();
	            }
            }
         });
        ldapr.schedule();
        
        String ldapPath = obs.getResult();
        if (ldapPath != null) {
            return ldapResultToControllerName(ldapPath);
        } else {
        	return null;
        }
	}
	
	/**
	 * Returns a list with the names of all IOCs configured in the LDAP
	 * directory.
	 * 
	 * @return a list with the names of all IOCS.
	 */
	public static List<String> getIocList() {
		final List<String> result = new ArrayList<String>();
		final ErgebnisListe el = new ErgebnisListe();
		LDAPReader lr = new LDAPReader("ou=EpicsControls", "econ=*", el);
		lr.addJobChangeListener(new JobChangeAdapter() {
			public void done(final IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					for (String ioc : el.getAnswer()) {
						result.add(ldapResultToControllerName(ioc));
					}
				}
			}
		});
		lr.schedule();
		try {
			lr.join();
		} catch (InterruptedException e1) {
			// ignore
		}
		return result;
	}

	/**
	 * Converts an LDAP path to an IOC name.
	 * 
	 * @param ldapPath the LDAP path.
	 * @return the IOC name.
	 */
	private static String ldapResultToControllerName(final String ldapPath) {
        String[] answerTmp = ldapPath.split("econ=");
        String[] answerTmp2 = answerTmp[1].split(",");
        String controllerName = answerTmp2[0];
        return controllerName;
	}

}
