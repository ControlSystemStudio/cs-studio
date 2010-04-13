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

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.IOC;


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
    private static int CURRENTDEPTH = 0;

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

        final Map<String, IOC> iocMap = new HashMap<String, IOC>();

        final IOCFilesDirTree dt = new IOCFilesDirTree();

        dt.doDir(recursiveDepth, iocDblDumpPath, iocMap);

        return iocMap;
    }

    @Nonnull
    private static GregorianCalendar findLastModifiedDateForFile(@Nonnull final String iocName) {
        final GregorianCalendar dateTime = new GregorianCalendar();
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
    @SuppressWarnings("static-access")
    private void doDir(final int recDepth, @Nonnull final String s, @Nonnull final Map<String, IOC> iocMap) {

        final File f = new File(s);
        if (!f.exists()) {
            System.out.println(f.getName() + " does not exist");
            return;
        }
        if (f.isFile()) {
            doFile(f, iocMap);
        } else if (f.isDirectory()) {
            if (CURRENTDEPTH >= recDepth) {
                return;
            }
            CURRENTDEPTH++;

            final String[] filePaths = f.list();
            for (final String filePath : filePaths) {
                doDir(recDepth, s + f.separator + filePath, iocMap);
            }
        } else {
            System.out.println("is unknown: " + f.getName());
        }
    }
}
