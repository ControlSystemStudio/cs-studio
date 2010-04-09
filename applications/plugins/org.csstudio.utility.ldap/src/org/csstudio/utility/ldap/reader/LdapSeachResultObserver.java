package org.csstudio.utility.ldap.reader;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.LdapUtils;
import org.csstudio.utility.ldap.LdapUtils.LdapQueryResult;
import org.csstudio.utility.ldap.model.LdapContentModel;

/**
 * Observes the result of an LDAP query and updates the current content model with
 * the result.
 *
 * @author bknerr
 */
public class LdapSeachResultObserver implements Observer {

    private final Logger _log = CentralLogger.getInstance().getLogger(this);
    private LdapContentModel _model;

    private boolean _modelReady = false;

    /**
     * Constructor.
     */
    public LdapSeachResultObserver() {

        _model = new LdapContentModel();
    }

    /**
     * Constructor.
     * @param model the content model to be filled by the observable.
     */
    public LdapSeachResultObserver(@Nonnull final LdapContentModel model) {

        _model = model;
    }

    public boolean isModelReady() {
        return _modelReady;
    }

    public void setModelReady(final boolean ready) {
        _modelReady = ready;
    }

    public void clearModel() {
        _model = new LdapContentModel();
    }


    /**
     * Retrieves the contents of the currents result from the LDAP lookup.
     * Invokes parser for any LDAP entry and stores the results in the
     * {@link LdapContentModel}.
     */
    @Override
    public final synchronized void update(final Observable o, final Object arg) {
        if (!(o instanceof LdapSearchResult)) {
            throw new IllegalArgumentException("Observed object must be of type " + LdapSearchResult.class.getName());
        }
        _log.info("Observer update: fill LDAP model.");

        final LdapSearchResult result = (LdapSearchResult) o;
        _model.setCurrentLdapSearchResult(result);

        final Set<SearchResult> answerSet = result.getAnswerSet();

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

        _log.info("LDAP entries retrieved: " + answerSet.size());

        setModelReady(true);
        _log.info("Observer update finished.");
    }

    /**
     * @return the backing model from the ldap update filled with values from the last ready request.
     */
    public LdapContentModel getLdapModel() {
        return _model;
    }

}
