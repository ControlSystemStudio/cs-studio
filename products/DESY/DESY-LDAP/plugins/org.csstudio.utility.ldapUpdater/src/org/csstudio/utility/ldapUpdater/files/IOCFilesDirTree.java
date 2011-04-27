/**
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id$
 *
 * Modified for settable recursion depth by Klaus Valett - DESY Hamburg
 */
package org.csstudio.utility.ldapUpdater.files;

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreference.IOC_DBL_DUMP_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldapUpdater.service.impl.LdapUpdaterServiceImpl;



/**
 * DirTree - directory lister, like UNIX ls or DOS/VMS dir
 *
 * TODO (valett) : extract generic dir tree scanner
 *
 * @author Ian Darwin, http://www.darwinsys.com/
 * @version $Id$
 * modified by kv (Klaus Valett) - DESY - for recursion depth
 *
 */
public final class IOCFilesDirTree {

    private static Logger LOG = CentralLogger.getInstance().getLogger(IOCFilesDirTree.class.getName());

    private static final String RECORDS_FILE_SUFFIX = ".records";

    /**
     * Don't instantiate.
     */
    private IOCFilesDirTree() {
        // Empty
    }

    /**
     * doFile - process one regular file.
     *
     * @param f
     *            the current file to be analysed
     * @param iocMap
     * */
    private static void doFile(@Nonnull final File f, @Nonnull final Map<String, IOC> iocMap) {
        final String fileName = f.getName();

        if (fileName.endsWith(RECORDS_FILE_SUFFIX)) {
            final TimeInstant dateTime = findTimeOfLastFileModification(fileName);

            final String iocName = fileName.replace(RECORDS_FILE_SUFFIX, "");
            iocMap.put(iocName, new IOC(iocName, dateTime));
            LOG.debug("File found for IOC: " + iocName);
        }
    }

    /**
     * Finds all IOC files under in the directory traversing up to a given tree level depth.
     * @param dumpPath the directory under which to look
     * @param recursiveDepth .
     * @return the map of identified files
     */
    @Nonnull
    public static Map<String, IOC> findIOCFiles(@Nonnull final File dir, final int recursiveDepth) {
        final int currentDepth = 0;

        final Map<String, IOC> iocMap = new HashMap<String, IOC>();

        final IOCFilesDirTree dt = new IOCFilesDirTree();

        dt.doDir(currentDepth, recursiveDepth, dir, iocMap);

        return iocMap;
    }

    @Nonnull
    private static TimeInstant findTimeOfLastFileModification(@Nonnull final String iocFileName) {
        final File filePath = IOC_DBL_DUMP_PATH.getValue();
        final TimeInstant timestamp = TimeInstantBuilder.fromMillis(new File(filePath, iocFileName).lastModified());
        return timestamp;
    }

    /**
     * doDir - handle one filesystem object by name
     *
     * @param iocMap
     *            the list of iocs to be filled by this recursive method
     * */
    private void doDir(final int currentDepth,
                       final int recDepth,
                       @Nonnull final File f,
                       @Nonnull final Map<String, IOC> iocMap) {

        int depth = currentDepth;

        if (!f.exists()) {
            LOG.warn(f.getName() + " does not exist.");
            return;
        }
        if (f.isFile()) {
            doFile(f, iocMap);
        } else if (f.isDirectory()) {
            if (depth >= recDepth) {
                return;
            }
            depth++;

            final String[] filePaths = f.list();
            for (final String filePath : filePaths) {
                doDir(depth, recDepth, new File (f, filePath), iocMap);
            }
        } else {
            LOG.warn(f.getAbsolutePath() + " is neither file nor directory.");
        }
    }

    /**
     * TODO (bknerr) : should be encapsulated in a file access class - does not belong here.
     * @param pathToFile the file with records
     */
    @Nonnull
    public static Set<Record> getRecordsFromFile(@Nonnull final File pathToFile) {
        final Set<Record> records = new HashSet<Record>();
        try {
            final BufferedReader br = new BufferedReader(new FileReader(pathToFile.toString() + RECORDS_FILE_SUFFIX));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                final String[] fields = strLine.split(",");
                if (fields != null && fields.length > 0) {
                    final String name = fields[0];
                    final String desc = fields.length >= 2 ? fields[1] : "";
                    records.add(new Record(name, desc));
                }
            }
            br.close();
            return records;
        } catch (final FileNotFoundException e) {
            LdapUpdaterServiceImpl.LOG.error("Could not find file: " + pathToFile + "\n" + e.getMessage());
        } catch (final IOException e) {
            LdapUpdaterServiceImpl.LOG.error("Error while reading from file: " + e.getMessage());
        }
        return Collections.emptySet();
    }
}
