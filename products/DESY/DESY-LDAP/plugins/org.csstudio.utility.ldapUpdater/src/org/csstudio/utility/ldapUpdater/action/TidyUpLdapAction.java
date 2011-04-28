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
package org.csstudio.utility.ldapUpdater.action;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldapUpdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapUpdater.LdapUpdaterUtil;
import org.csstudio.utility.ldapUpdater.files.RecordsFileTimeStampParser;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapUpdater.service.LdapFacadeException;
import org.csstudio.utility.treemodel.ContentModel;

/**
 * Command to start the tidy up mechanism for LDAP.
 * This action scans the IOC file directory for existing IOC record files and compares to the IOC entries in the LDAP system.
 * IOC names found in the LDAP that are not present in the directory are removed from LDAP including all records.
 *
 * @author bknerr
 */
public class TidyUpLdapAction implements IManagementCommand {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(TidyUpLdapAction.class);

    private static final String TIDYUP_ACTION_NAME = "LDAP Tidy Up Action";

    /**
     * Singleton approach to make the service public to the action, a pain to test...
     */
    private static final LdapUpdaterUtil UPDATER = LdapUpdaterUtil.INSTANCE;

    /**
     * Singleton approach to make the service public to the action, a pain to test...
     */
    private ILdapUpdaterService _service;

    /**
     * Constructor.
     */
    public TidyUpLdapAction() {
        try {
            _service = LdapUpdaterActivator.getDefault().getLdapUpdaterService();
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("LDAP service is not available!");
            throw new RuntimeException("LDAP service is not available!", e);
        }
    }

	/* (non-Javadoc)
	 * @see org.csstudio.platform.management.IManagementCommand#execute(org.csstudio.platform.management.CommandParameters)
	 */
	@Override
	@Nonnull
    public final CommandResult execute(@Nonnull final CommandParameters parameters) {
	        try {
	            if (!UPDATER.isBusy()){
	                UPDATER.setBusy(true);
	                tidyUpLdapFromIOCFiles();
	            } else{
	                return CommandResult.createMessageResult("ldapUpdater is busy for max. 300 s (was probably started by timer). Try later!");
	            }
	        } catch (final Exception e) {
	            LOG.error("\"" + e.getCause() + "\"" + "-" + "Exception while running ldapUpdater", e);
	            return CommandResult.createFailureResult("\"" + e.getCause() + "\"" + "-" + "Exception while running ldapUpdater");
	        } finally {
	            UPDATER.setBusy(false);
	        }
	        return CommandResult.createSuccessResult();
	}


    private void tidyUpLdapFromIOCFiles() {

        final TimeInstant startTime = UPDATER.logHeader(TIDYUP_ACTION_NAME);

        final File value = IOC_DBL_DUMP_PATH.getValue();
        try {
            final ContentModel<LdapEpicsControlsConfiguration> model = _service.retrieveIOCs();
            if (model.isEmpty()) {
                LOG.warn("LDAP search result is empty. No IOCs found.");
                return;
            }
            final RecordsFileTimeStampParser parser = new RecordsFileTimeStampParser(value, 1);
            final Map<String, IOC> iocMapFromFS = parser.getIocFileMap();

            _service.tidyUpLDAPFromIOCList(model.getByType(IOC), iocMapFromFS);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException("File dir " + value + " could not be parsed for IOC files!", e);
        } catch (final LdapFacadeException e) {
            throw new RuntimeException("Retrieval of IOCs from LDAP failed!", e);
        } finally {
            UPDATER.setBusy(false);
            UPDATER.logFooter(TIDYUP_ACTION_NAME, startTime);
        }
    }

}
