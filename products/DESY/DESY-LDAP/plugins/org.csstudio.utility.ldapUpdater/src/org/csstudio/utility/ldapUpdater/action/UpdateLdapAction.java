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

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldapUpdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapUpdater.LdapUpdaterUtil;
import org.csstudio.utility.ldapUpdater.files.HistoryFileAccess;
import org.csstudio.utility.ldapUpdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapUpdater.files.RecordsFileTimeStampParser;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapUpdater.service.LdapFacadeException;
import org.csstudio.utility.treemodel.ISubtreeNodeComponent;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;


/**
 * Starts the LDAP update from the context menu.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 09.04.2010
 */
public class UpdateLdapAction implements IManagementCommand {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(UpdateLdapAction.class);

    private static final String UPDATE_ACTION_NAME = "LDAP Update Action";

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
    public UpdateLdapAction() {
        try {
            _service = LdapUpdaterActivator.getDefault().getLdapUpdaterService();
        } catch (final OsgiServiceUnavailableException e) {
            LOG.error("LDAP service is not available!");
            throw new RuntimeException("LDAP service is not available!", e);
        }
    }

    @Override
    @Nonnull
    public final CommandResult execute(@Nonnull final CommandParameters parameters) {
        if (!UPDATER.isBusy()){
            UPDATER.setBusy(true);
            try {
                updateLdapFromIOCFiles();
            } catch (final Exception e) {
                LOG.error("\"" + e.getCause() + "\"" + "-" + "Exception while running ldapUpdater", e);
                return CommandResult.createFailureResult("\"" + e.getCause() + "\"" + "-" + "Exception while running ldapUpdater");

            } finally {
                UPDATER.setBusy(false);
            }
        } else{
            return CommandResult.createMessageResult("ldapUpdater is busy for max. 150 s (was probably started by timer). Try later!");
        }
        return CommandResult.createSuccessResult();
    }

    /**
     * Scans the IOC files on /applic and checks whether contained records are already listed in LDAP.
     * If not so, these entries are added to LDAP.
     * @throws OsgiServiceUnavailableException
     */
    public void updateLdapFromIOCFiles() throws LdapFacadeException {


        final TimeInstant startTime = UPDATER.logHeader(UPDATE_ACTION_NAME);

        try {
            final Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocs = _service.retrieveIOCs();

            if (iocs.isEmpty()) {
                LOG.info("No IOCs found in LDAP.");
                return;
            }

            updateIocsInLdap(iocs);

        } finally {
            UPDATER.setBusy(false);
            UPDATER.logFooter(UPDATE_ACTION_NAME, startTime);
        }
    }

    private void updateIocsInLdap(@Nonnull final Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocs)
                                  throws LdapFacadeException {

            final HistoryFileAccess histFileReader = new HistoryFileAccess();
            final HistoryFileContentModel historyFileModel = histFileReader.readFile();

            validateHistoryFileEntriesVsLDAPEntries(iocs, historyFileModel);

            final File value = IOC_DBL_DUMP_PATH.getValue();
            try {
                final RecordsFileTimeStampParser parser = new RecordsFileTimeStampParser(value, 1);
                final Map<String, IOC> iocMapFromFS = parser.getIocFileMap();

                _service.updateLDAPFromIOCList(iocs, iocMapFromFS, historyFileModel);

            } catch (final FileNotFoundException e) {
                throw new RuntimeException("File dir " + value + " could not be parsed for IOC files!", e);
            }
    }


    private void validateHistoryFileEntriesVsLDAPEntries(@Nonnull final Map<String, ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdap,
                                                         @Nonnull final HistoryFileContentModel historyFileModel) {

        final Set<String> iocsFromHistFile = historyFileModel.getIOCNameKeys();

        final Predicate<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> predicate =
            new Predicate<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>>() {
                @Override
                public boolean apply(@Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> iocFromLdap) {
                    return iocsFromHistFile.contains(iocFromLdap.getName());
                }
            };

        final Collection<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdapNotInHistFile =
            Collections2.filter(iocsFromLdap.values(), predicate);


        handleIocEntriesInLdapNotPresentInHistoryFile(iocsFromLdapNotInHistFile);


        final Collection<String> iocNamesFromLdap =
            Collections2.transform(iocsFromLdap.values(),
                                   new Function<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>, String>() {
                                       @Override
                                       @Nonnull
                                       public String apply(@Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> input) {
                                           return input.getName();
                                       }
                                   });
        iocsFromHistFile.removeAll(iocNamesFromLdap);
        handleIocEntriesInHistoryFileNotPresentInLdap(iocsFromHistFile);

    }


    private void handleIocEntriesInHistoryFileNotPresentInLdap(@Nonnull final Set<String> iocsFromHistFileNotInLdap) {
        for (final String ioc : iocsFromHistFileNotInLdap) {
            LOG.warn("IOC " + ioc + " found in history file is not present in LDAP!");
        }
    }

    private void handleIocEntriesInLdapNotPresentInHistoryFile(@Nonnull final Collection<ISubtreeNodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdapNotInFile) {
        final Map<String, List<String>> missingIOCsPerPerson = new HashMap<String, List<String>>();

        for (final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> ioc : iocsFromLdapNotInFile) {
            LOG.warn("IOC " + ioc.getName() + " from LDAP is not present in history file!");
            getResponsiblePersonForIOC(ioc, missingIOCsPerPerson);
        }

        UPDATER.sendNotificationMails(missingIOCsPerPerson);
    }

    @Nonnull
    private Map<String, List<String>> getResponsiblePersonForIOC(@Nonnull final ISubtreeNodeComponent<LdapEpicsControlsConfiguration> ioc,
                                                                 @Nonnull final Map<String, List<String>> iocsPerPerson) {
        final Attribute personAttr = ioc.getAttribute(LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
        String person = LdapUpdaterUtil.DEFAULT_RESPONSIBLE_PERSON;
        try {
            if (personAttr != null && personAttr.get() != null) {
                person = (String) personAttr.get();
            }
            if (!iocsPerPerson.containsKey(person)) {
                iocsPerPerson.put(person, new ArrayList<String>());
            }
            iocsPerPerson.get(person).add(ioc.getName());
        } catch (final NamingException e) {
            LOG.error("Attribute for " + LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON +
                      " in IOC " + ioc.getName() + "could not be retrieved.");
        }
        return iocsPerPerson;
    }
}
