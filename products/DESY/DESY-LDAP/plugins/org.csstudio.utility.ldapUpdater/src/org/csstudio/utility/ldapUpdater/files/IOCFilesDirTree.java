package org.csstudio.utility.ldapUpdater.files;
/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id$
 *
 * Modified for settable recursion depth by Klaus Valett - DESY Hamburg
 */

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.getValueFromPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldapUpdater.LdapAccess;


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

    /**
     * Don't instantiate.
     */
    private IOCFilesDirTree() {
        // Empty
    }

    /**
     * doFile - process one regulr file.
     *
     * @param f
     *            the current file to be analysed
     * @param iocMap
     * */
    private static void doFile(@Nonnull final File f, @Nonnull final Map<String, IOC> iocMap) {
        final String fileName = splitFileNameFromCanonicalPath(f);

        if (!filterFileName(fileName, ".")) {
            final GregorianCalendar dateTime = findLastModifiedDateForFile(fileName);

            iocMap.put(fileName, new IOC(fileName, dateTime));
            LOG.debug("File found for IOC: " + fileName);
        }
    }

    /**
     * Filters the given string whether the filterString is contained.
     * @param fileName the name to filter for
     * @param filterStrings the array of filters
     * @return true, if the name contains any of the given filterStrings
     */
    private static boolean filterFileName(@Nonnull final String fileName, @Nonnull final String...filterStrings) {

        for (final String filter : filterStrings) {
            if (fileName.contains(filter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all IOC files under in the directory traversing up to a given tree level depth.
     * @param iocDblDumpPath the directory under which to look
     * @param recursiveDepth .
     * @return the map of identified files
     */
    @Nonnull
    public static Map<String, IOC> findIOCFiles(@Nonnull final String iocDblDumpPath, final int recursiveDepth) {
        final int currentDepth = 0;

        final Map<String, IOC> iocMap = new HashMap<String, IOC>();

        final IOCFilesDirTree dt = new IOCFilesDirTree();

        dt.doDir(currentDepth, recursiveDepth, iocDblDumpPath, iocMap);

        return iocMap;
    }

    @Nonnull
    private static GregorianCalendar findLastModifiedDateForFile(@Nonnull final String iocName) {
        final GregorianCalendar dateTime = new GregorianCalendar(TimeZone.getTimeZone("ETC"));
        final String prefFileName = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        final File filePath = new File(prefFileName);
        dateTime.setTimeInMillis(new File(filePath,iocName).lastModified());
        return dateTime;
    }

    @Nonnull
    private static String splitFileNameFromCanonicalPath(@Nonnull final File f) {
        String fileName = "";
        try {
            final String cP = f.getCanonicalPath();
            final String[] cA = cP.split("\\\\");
            fileName = cA[cA.length - 1];
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * doDir - handle one filesystem object by name
     *
     * @param iocMap
     *            the list of iocs to be filled by this recursive method
     * */
    private void doDir(final int currentDepth,
                       final int recDepth,
                       @Nonnull final String s,
                       @Nonnull final Map<String, IOC> iocMap) {

        int depth = currentDepth;

        final File f = new File(s);
        if (!f.exists()) {
            System.out.println(f.getName() + " does not exist");
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
                doDir(depth, recDepth, s + File.separator + filePath, iocMap);
            }
        } else {
            System.out.println("is unknown: " + f.getName());
        }
    }

    /**
     * TODO (bknerr) : should be encapsulated in a file access class - does not belong here.
     * @param pathToFile the file with records
     */
    @Nonnull
    public
    static Set<Record> getRecordsFromFile(@Nonnull final String pathToFile) {
        final Set<Record> records = new HashSet<Record>();
        try {
            final BufferedReader br = new BufferedReader(new FileReader(pathToFile));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                records.add(new Record(strLine));
            }
            br.close();
            return records;
        } catch (final FileNotFoundException e) {
            LdapAccess.LOGGER.error("Could not find file: " + pathToFile + "\n" + e.getMessage());
        } catch (final IOException e) {
            LdapAccess.LOGGER.error("Error while reading from file: " + e.getMessage());
        }
        return Collections.emptySet();
    }
}
