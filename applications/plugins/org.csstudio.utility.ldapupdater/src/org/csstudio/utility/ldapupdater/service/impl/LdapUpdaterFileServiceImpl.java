/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldapUpdater.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldapUpdater.UpdaterLdapConstants;
import org.csstudio.utility.ldapUpdater.files.BootFileContentParser;
import org.csstudio.utility.ldapUpdater.files.FileBySuffixCollector;
import org.csstudio.utility.ldapUpdater.mail.NotificationMailer;
import org.csstudio.utility.ldapUpdater.model.IOC;
import org.csstudio.utility.ldapUpdater.model.Record;
import org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference;
import org.csstudio.utility.ldapUpdater.service.ILdapUpdaterFileService;
import org.csstudio.utility.ldapUpdater.service.LdapUpdaterServiceException;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 29.04.2011
 */
public class LdapUpdaterFileServiceImpl implements ILdapUpdaterFileService {

    private final Map<File, Map<String, IOC>> _cache = Maps.newConcurrentMap();

    @Override
    @Nonnull
    public Map<String, IOC> retrieveIocInformationFromBootDirectory(@Nonnull final File bootDirectory)
                                                                    throws LdapUpdaterServiceException {

        if (_cache.containsKey(bootDirectory)) {
            return _cache.get(bootDirectory);
        }

        Map<String, File> recordFileMap;
        try {
            recordFileMap = new FileBySuffixCollector(bootDirectory, UpdaterLdapConstants.RECORDS_FILE_SUFFIX).getFileMap();
            final Map<String, File> bootFileMap =
                new FileBySuffixCollector(bootDirectory, UpdaterLdapConstants.BOOT_FILE_SUFFIX).getFileMap();

            validateBootFilesVsRecordFiles(recordFileMap, bootFileMap);

            final Map<String, IOC> iocsFromFSMap =
                createIntersectionIocMapFromFiles(bootDirectory,
                                                  recordFileMap,
                                                  bootFileMap);

            _cache.put(bootDirectory, iocsFromFSMap);
            return iocsFromFSMap;

        } catch (final FileNotFoundException e) {
            throw new LdapUpdaterServiceException("", e);
        }
    }


    private boolean validateBootFilesVsRecordFiles(@Nonnull final Map<String, File> recordFileMap,
                                                   @Nonnull final Map<String, File> bootFileMap) {

        final List<String> missingBootFiles = findMissingMapEntries(recordFileMap, bootFileMap);
        final List<String> missingRecordFiles = findMissingMapEntries(bootFileMap, recordFileMap);

        if (missingBootFiles.isEmpty() && missingRecordFiles.isEmpty()) {
            return true;
        }
        final String recordJoin = Joiner.on(", ").join(missingRecordFiles);
        final String bootJoin = Joiner.on(", ").join(missingBootFiles);

        final StringBuilder builder = new StringBuilder();
        if (!Strings.isNullOrEmpty(recordJoin)) {
            builder.append("\n\nMissing record files:\n").append(recordJoin);
        }
        if (!Strings.isNullOrEmpty(bootJoin)) {
            builder.append("\n\nMissing boot files:\n").append(bootJoin);
        }
        NotificationMailer.sendMissingFilesNotification(builder.toString());
        return false;
    }

    @Nonnull
    private List<String> findMissingMapEntries(@Nonnull final Map<String, File> sourceFileMap,
                                               @Nonnull final Map<String, File> targetFileMap) {
        final List<String> missingEntries = Lists.newLinkedList();
        for (final String sourceKey : sourceFileMap.keySet()) {
            if (!targetFileMap.containsKey(sourceKey)) {
                missingEntries.add(sourceKey);
            }
        }
        return missingEntries;
    }

    @Nonnull
    private Map<String, IOC> createIntersectionIocMapFromFiles(@Nonnull final File bootDirectory,
                                                               @Nonnull final Map<String, File> recordFileMap,
                                                               @Nonnull final Map<String, File> bootFileMap)
                                                               throws LdapUpdaterServiceException {
        final Set<String> intersectionKeySet = Sets.newHashSet(bootFileMap.keySet());
        intersectionKeySet.retainAll(recordFileMap.keySet());

        return createIocMapFromIocNameSet(bootDirectory, intersectionKeySet);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<Record> getBootRecordsFromIocFile(@Nonnull final String iocName) throws LdapUpdaterServiceException {
        final File bootDirectory = LdapUpdaterPreference.IOC_DBL_DUMP_PATH.getValue();

        if (notExistsInCache(bootDirectory)) {
            final Map<String, IOC> newMap = createIocMapFromIocNameSet(bootDirectory, Sets.newHashSet(iocName));
            _cache.put(bootDirectory, newMap);
            return newMap.get(iocName).getRecordSet();
        }

        final Map<String, IOC> cacheMap = _cache.get(bootDirectory);
        final IOC ioc = cacheMap.get(iocName);
        if (ioc != null) {
            return ioc.getRecordSet();
        }
        final Map<String, IOC> newMap = createIocMapFromIocNameSet(bootDirectory, Sets.newHashSet(iocName));
        final IOC newIocEntry = newMap.get(iocName);
        cacheMap.put(iocName, newIocEntry);

        return newIocEntry.getRecordSet();

    }

    private boolean notExistsInCache(@Nonnull final File bootDirectory) {
        return !_cache.containsKey(bootDirectory);
    }

    @Nonnull
    private Map<String, IOC> createIocMapFromIocNameSet(@Nonnull final File bootDirectory,
                                                        @Nonnull final Set<String> iocNames) throws LdapUpdaterServiceException {
        try {
            final BootFileContentParser parser = new BootFileContentParser(bootDirectory, iocNames);
            return parser.getIocMap();
        } catch (final IOException e) {
            throw new LdapUpdaterServiceException("", e);
        } catch (final ParseException e) {
            throw new LdapUpdaterServiceException("", e);
        }
    }
}
