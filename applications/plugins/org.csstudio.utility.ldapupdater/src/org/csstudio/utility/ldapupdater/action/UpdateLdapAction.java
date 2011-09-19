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

package org.csstudio.utility.ldapupdater.action;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.platform.management.CommandParameters;
import org.csstudio.platform.management.CommandResult;
import org.csstudio.platform.management.IManagementCommand;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.LdapUpdaterActivator;
import org.csstudio.utility.ldapupdater.LdapUpdaterUtil;
import org.csstudio.utility.ldapupdater.files.HistoryFileContentModel;
import org.csstudio.utility.ldapupdater.mail.NotificationMailer;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.csstudio.utility.ldapupdater.preferences.LdapUpdaterPreferencesService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterFileService;
import org.csstudio.utility.ldapupdater.service.ILdapUpdaterService;
import org.csstudio.utility.ldapupdater.service.LdapUpdaterServiceException;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.INodeComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.internal.Maps;


/**
 * Starts the LDAP update from the context menu.
 *
 * @author bknerr
 * @since 09.04.2010
 */
public class UpdateLdapAction implements IManagementCommand {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateLdapAction.class);

    private static final String UPDATE_ACTION_NAME = "LDAP Update Action";

    /**
     * Singleton approach to make the service public to the action, a pain to test...
     */
    private static final LdapUpdaterUtil UPDATER = LdapUpdaterUtil.INSTANCE;

    /*
     * Singleton approach to make the service public to the action, a pain to test...
     */
    private ILdapUpdaterService _updaterService;
    private ILdapUpdaterFileService _fileService;
    private LdapUpdaterPreferencesService _prefsService;

    /**
     * Constructor.
     */
    public UpdateLdapAction() {
        try {
            _updaterService = LdapUpdaterActivator.getDefault().getLdapUpdaterService();
            _fileService = LdapUpdaterActivator.getDefault().getLdapUpdaterFileService();
            _prefsService = LdapUpdaterActivator.getDefault().getLdapUpdaterPreferencesService();
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
                LOG.error("Exception while running ldapUpdater", e);
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
    public void updateLdapFromIOCFiles() throws LdapUpdaterServiceException {


        final TimeInstant startTime = UPDATER.logHeader(UPDATE_ACTION_NAME);

        try {
            final ContentModel<LdapEpicsControlsConfiguration> model =
                _updaterService.retrieveIOCs();

            if (model.isEmpty()) {
                LOG.info("No IOCs found in LDAP.");
                return;
            }

            updateIocsInLdap(model.getChildrenByTypeAndSimpleName(IOC));

        } finally {
            UPDATER.setBusy(false);
            UPDATER.logFooter(UPDATE_ACTION_NAME, startTime);
        }
    }

    private void updateIocsInLdap(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdapBySimpleName)
                                  throws LdapUpdaterServiceException {

            final TimeInstant lastHeartBeat = _fileService.getAndUpdateLastHeartBeat();

//            final HistoryFileAccess histFileReader = new HistoryFileAccess(_prefsService);
//            final HistoryFileContentModel historyFileModel = histFileReader.readFile();
//
//            validateHistoryFileEntriesVsLDAPEntries(iocsFromLdapBySimpleName, historyFileModel);

            // find IOC files newer than lastHeartBeat
            final File bootDirectory = _prefsService.getIocDblDumpPath();
            final Map<String, IOC> iocsFromFSMap =
                    _fileService.retrieveIocInformationFromBootDirectory(bootDirectory);

            _updaterService.updateLDAPFromIOCList(iocsFromLdapBySimpleName, iocsFromFSMap, lastHeartBeat);
    }


    @SuppressWarnings("unused")
    private void validateHistoryFileEntriesVsLDAPEntries(@Nonnull final Map<String, INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdap,
                                                         @Nonnull final HistoryFileContentModel historyFileModel) {

        final Set<String> iocsFromHistFile = historyFileModel.getIOCNameKeys();

        final Predicate<INodeComponent<LdapEpicsControlsConfiguration>> predicate =
            new Predicate<INodeComponent<LdapEpicsControlsConfiguration>>() {
                @Override
                public boolean apply(@Nonnull final INodeComponent<LdapEpicsControlsConfiguration> iocFromLdap) {
                    return !iocsFromHistFile.contains(iocFromLdap.getName());
                }
            };

        final Collection<INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdapNotInHistFile =
            Collections2.filter(iocsFromLdap.values(), predicate);


        handleIocEntriesInLdapNotPresentInHistoryFile(iocsFromLdapNotInHistFile);


        final Collection<String> iocNamesFromLdap = iocsFromLdap.keySet();

        iocsFromHistFile.removeAll(iocNamesFromLdap);
        handleIocEntriesInHistoryFileNotPresentInLdap(iocsFromHistFile);

    }


    private void handleIocEntriesInHistoryFileNotPresentInLdap(@Nonnull final Set<String> iocsFromHistFileNotInLdap) {
        for (final String ioc : iocsFromHistFileNotInLdap) {
            LOG.warn("IOC {} found in history file is not present in LDAP!", ioc);
        }
    }

    private void handleIocEntriesInLdapNotPresentInHistoryFile(@Nonnull final Collection<INodeComponent<LdapEpicsControlsConfiguration>> iocsFromLdapNotInFile) {
        final Map<InternetAddress, List<String>> missingIOCsPerPerson = Maps.newHashMap();

        for (final INodeComponent<LdapEpicsControlsConfiguration> ioc : iocsFromLdapNotInFile) {
            LOG.warn("IOC {} from LDAP is not present in history file!", ioc.getName());
            putIocIntoMap(ioc, missingIOCsPerPerson);
        }

        NotificationMailer.sendMissingIOCsNotificationMails(missingIOCsPerPerson,
                                                            _prefsService.getSmtpHostAddress());
    }

    @Nonnull
    private Map<InternetAddress, List<String>> putIocIntoMap(@Nonnull final INodeComponent<LdapEpicsControlsConfiguration> ioc,
                                                             @Nonnull final Map<InternetAddress, List<String>> iocsPerPerson) {
        final Attribute personAttr = ioc.getAttribute(LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON);
        InternetAddress person = _prefsService.getDefaultResponsiblePerson();
        try {
            if (personAttr != null && personAttr.get() != null) {
                person = new InternetAddress((String) personAttr.get());
            }
            if (!iocsPerPerson.containsKey(person)) {
                iocsPerPerson.put(person, new ArrayList<String>());
            }
        } catch (final NamingException e) {
            LOG.error("Attribute for {} in IOC {} could not be retrieved.",
                      LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON,
                      ioc.getName());
        } catch (final AddressException e) {
            LOG.error("Attribute for {} in IOC {} could not be transformed in valid type {}",
                      new Object[] {
                          LdapEpicsControlsFieldsAndAttributes.ATTR_FIELD_RESPONSIBLE_PERSON,
                          ioc.getName(),
                          InternetAddress.class.getSimpleName(),
                      });
        }
        iocsPerPerson.get(person).add(ioc.getName());

        return iocsPerPerson;
    }
}
