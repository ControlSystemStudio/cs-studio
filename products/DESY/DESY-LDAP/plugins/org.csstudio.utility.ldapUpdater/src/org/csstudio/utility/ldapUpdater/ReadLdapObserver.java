package org.csstudio.utility.ldapUpdater;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.LdapUtils.LdapQueryResult;
import org.csstudio.utility.ldap.reader.LdapResultList;
import org.csstudio.utility.ldapUpdater.model.LDAPContentModel;

/**
 * TODO (kvalett) : Documentation missing.
 * @author valett
 */
public class ReadLdapObserver implements Observer {

    private final Logger LOGGER = CentralLogger.getInstance().getLogger(this);
    private final LDAPContentModel _model;

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


    /**
     * Retrieves the contents of the currents result from the LDAP lookup.
     * Invokes parser for any LDAP entry and stores the results in the
     * {@link LDAPContentModel}.
     */
    @Override
    public synchronized void update(final Observable o, final Object arg) {
        if (!(o instanceof LdapResultList)) {
            throw new IllegalArgumentException("Observed object must be of type " + LdapResultList.class.getName());
        }
        LOGGER.info("Observer update: fill LDAP model.");

        final Set<SearchResult> answerSet = ((LdapResultList) o).getAnswerSet();

        for (final SearchResult entry : answerSet) {
            entry.setRelative(false);

            final LdapQueryResult ldapEntry = LdapUtils.parseLdapQueryResult(entry.getNameInNamespace());
            final String efan = ldapEntry.getEfan();
            if (StringUtil.hasLength(efan)) {
                _model.addFacility(efan);

                final String econ = ldapEntry.getEcon();
                if (StringUtil.hasLength(econ)) {
                    _model.addIOC(efan, econ, entry.getAttributes());

                    final String eren = ldapEntry.getEren();
                    if (StringUtil.hasLength(eren)) {
                        _model.addRecord(efan, econ, eren);
                    }
                }
            }
        }

        LOGGER.info("LDAP entries retrieved: " + answerSet.size());

        setReady(true);
        LOGGER.info("Observer update finished.");
    }

    /**
     * @return the backing model from the ldap update filled with values from the last ready request.
     */
    public LDAPContentModel getLdapModel() {
        return _model;
    }

}
