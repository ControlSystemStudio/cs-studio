package org.csstudio.utility.ldapUpdater;
/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id$
 * 
 * Modified for settable recursion depth by Klaus Valett - DESY Hamburg
 */

import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_DBL_DUMP_PATH;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferenceKey.IOC_LIST_FILE;
import static org.csstudio.utility.ldapUpdater.preferences.LdapUpdaterPreferences.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

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
	private static PrintWriter _zielDatei = null;
	
	public static Logger LOG = CentralLogger.getInstance().getLogger(IOCFilesDirTree.class);
	
	public static List<IOC> findIOCFiles(int recursiveDepth) {
		
		List<IOC> iocList = new ArrayList<IOC>();
		
		try {
			String iocListFile = getValueFromPreferences(IOC_LIST_FILE);
			_zielDatei =  new PrintWriter(iocListFile);
			IOCFilesDirTree dt = new IOCFilesDirTree();
			String iocDblDumpPath = getValueFromPreferences(IOC_DBL_DUMP_PATH); 
				
			dt.doDir(recursiveDepth, iocDblDumpPath, iocList);

		} catch (FileNotFoundException e) {
			// TODO : Loggen
			e.printStackTrace();
		}
		finally {
			_zielDatei.close();
		}
		return iocList;
	}

	/** doDir - handle one filesystem object by name 
	 * @param iocList the list of iocs to be filled by this recursive method 
	 * */
	@SuppressWarnings("static-access")
	private void doDir(final int recDepth, final String s, List<IOC> iocList) {
		
		File f = new File(s);
		if (!f.exists()) {
			System.out.println(f.getName() + " does not exist");
			return;
		}
		if (f.isFile()) {
			doFile(f, iocList);
		}
		else if (f.isDirectory()) {
			if (_currentDepth >= recDepth) {
				return;
			}
			_currentDepth++;
			
			String filePaths[] = f.list();
			for (String filePath : filePaths) {
				doDir(recDepth, s + f.separator + filePath, iocList);
			}
		} else {
			System.out.println("is unknown: " + f.getName());
		}
	}
		
	
	/** 
	 * doFile - process one regular file. 
	 * @param f the current file to be analysed
	 * @param iocList 
	 * */
	private static void doFile(final File f, List<IOC> iocList) {
		String fileName = splitFileNameFromCanonicalPath(f);
		
		if (!filterFileName(fileName, ".")) {
			GregorianCalendar dateTime = findLastModifiedDateForFile(fileName);

			iocList.add(new IOC(fileName, dateTime));
			LOG.debug("File found for IOC: " + fileName);
			
			// TODO (kvalett) : do this with a log file on a dedicated logging location !
			_zielDatei.println(fileName);
		}
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

	private static GregorianCalendar findLastModifiedDateForFile(final String iocName) {
		GregorianCalendar dateTime = new GregorianCalendar();
		String prefFileName = getValueFromPreferences(IOC_DBL_DUMP_PATH); 
		File filePath = new File(prefFileName);
		dateTime.setTimeInMillis(new File(filePath,iocName).lastModified());
		return dateTime;
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
}