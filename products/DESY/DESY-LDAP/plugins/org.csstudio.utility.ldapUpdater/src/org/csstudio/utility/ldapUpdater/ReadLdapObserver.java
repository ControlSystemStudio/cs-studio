package org.csstudio.utility.ldapUpdater;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.reader.IocFinder;
import org.csstudio.utility.ldap.reader.LdapResultList;
import org.csstudio.utility.ldap.reader.IocFinder.LdapQueryResult;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;

/**
 * TODO (kvalett) : Documentation missing.
 * @author valett
 */
public class ReadLdapObserver implements Observer {

	private final Logger LOGGER = CentralLogger.getInstance().getLogger(this);
	private final LDAPContentModel _model;
	private LdapResultList _result;
	private boolean _ready = false;

	public ReadLdapObserver(final LDAPContentModel model) {
		_model = model;
	}

	public boolean isReady() {
		return _ready;
	}

	public void setReady(final boolean ready) {
		_ready = ready;
	}

	public void setResult(final LdapResultList result) {
		_result = result;
	}

	/**
	 * Retrieves the contents of the currents result from the LDAP lookup.
	 * Invokes parser for any LDAP entry and stores the results in the
	 * {@link LDAPContentModel}.
	 */
	@Override
	public void update(final Observable o, final Object arg) {
		LOGGER.debug("Fill LDAP model with IOCs.");

		final List<String> ldapAnswer = _result.getAnswer();

		for (final String ldapPath : ldapAnswer) {
			final LdapQueryResult ldapEntry = IocFinder.parseLdapQueryResult(ldapPath);

			final String efan = ldapEntry.getEfan();
			if (StringUtil.hasLength(efan)) {
				_model.addFacility(efan);

				final String econ = ldapEntry.getEcon();
				if (StringUtil.hasLength(econ)) {
					_model.addIOC(efan, econ);

					final String eren = ldapEntry.getEren();
					if (StringUtil.hasLength(eren)) {
						_model.addRecord(efan, econ, eren);
					}
				}
			}
		}
		LOGGER.debug("LDAP entries retrieved: " + ldapAnswer.size());

		setReady(true);
		LOGGER.debug("Observer update finished.");
	}

}
