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

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldapUpdater.model.IOC;


/**
 * DirTree - directory lister, like UNIX ls or DOS/VMS dir
 * 
 * @author Ian Darwin, http://www.darwinsys.com/
 * @version $Id$
 * modified by kv (Klaus Valett) - DESY - for recursion depth
 * 
 */
public class IOCFilesDirTree {
    private static int _currentDepth = 0;
    
    public static Logger LOG = CentralLogger.getInstance().getLogger(IOCFilesDirTree.class);
    
    /**
     * doFile - process one regulr file.
     * 
     * @param f
     *            the current file to be analysed
     * @param iocMap
     * */
    private static void doFile(final File f, Map<String, IOC> iocMap) {
        String fileName = splitFileNameFromCanonicalPath(f);
        
        if (!filterFileName(fileName, ".")) {
            GregorianCalendar dateTime = findLastModifiedDateForFile(fileName);
            
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
    private static boolean filterFileName(String fileName, String...filterStrings) {
        
        for (String filter : filterStrings) {
            if (fileName.contains(filter)) {
                return true;
            }
        }
        return false;
    }
    
    
    public static Map<String, IOC> findIOCFiles(int recursiveDepth) {
        
        Map<String, IOC> iocMap = new HashMap<String, IOC>();
        
        IOCFilesDirTree dt = new IOCFilesDirTree();
        String iocDblDumpPath = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        
        dt.doDir(recursiveDepth, iocDblDumpPath, iocMap);
        
        return iocMap;
    }
    
    private static GregorianCalendar findLastModifiedDateForFile(final String iocName) {
        GregorianCalendar dateTime = new GregorianCalendar();
        String prefFileName = getValueFromPreferences(IOC_DBL_DUMP_PATH);
        File filePath = new File(prefFileName);
        dateTime.setTimeInMillis(new File(filePath,iocName).lastModified());
        return dateTime;
    }
    
    private static String splitFileNameFromCanonicalPath(final File f) {
        String fileName = "";
        try {
            String cP = f.getCanonicalPath();
            String[] cA = cP.split("\\\\");
            fileName = cA[cA.length - 1];
        } catch (IOException e) {
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
    private void doDir(final int recDepth, final String s, Map<String, IOC> iocMap) {
        
        File f = new File(s);
        if (!f.exists()) {
            System.out.println(f.getName() + " does not exist");
            return;
        }
        if (f.isFile()) {
            doFile(f, iocMap);
        }
        else if (f.isDirectory()) {
            if (_currentDepth >= recDepth) {
                return;
            }
            _currentDepth++;
            
            String filePaths[] = f.list();
            for (String filePath : filePaths) {
                doDir(recDepth, s + f.separator + filePath, iocMap);
            }
        } else {
            System.out.println("is unknown: " + f.getName());
        }
    }
}