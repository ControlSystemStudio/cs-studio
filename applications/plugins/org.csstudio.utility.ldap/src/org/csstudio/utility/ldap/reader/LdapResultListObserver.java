package org.csstudio.utility.ldap.reader;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Observer for the LdapResultList.
 */
public class LdapResultListObserver implements Observer {
	
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
		if (!(o instanceof LdapResultList)) {
			throw new IllegalArgumentException("Observed object must be of type ErgebnisListe");
		}
		
        List<String> l = ((LdapResultList) o).getAnswer();
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