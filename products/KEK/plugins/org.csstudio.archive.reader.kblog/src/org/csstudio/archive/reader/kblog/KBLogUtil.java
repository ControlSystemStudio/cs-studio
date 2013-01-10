package org.csstudio.archive.reader.kblog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Collections of utility functions to access KBLog.
 * 
 * Note that these utility functions support only Linux platform as they greatly rely
 * on system specific commands and file separator.
 * 
 * @author Takashi Nakamoto
 */
public class KBLogUtil {
	static Pattern lcfPattern = null;
	final static String lcfSuffix = "lcf";

	/**
	 * Returns the names of sub archives.
	 * 
	 * @param root Root directory of KBLog (e.g. /KEKBLog)
	 * @return Names of sub archives.
	 */
	public static String[] getSubArchives(String root, String relPathToSubarchiveList) {
		File kblogArchiveListFile = new File(root, relPathToSubarchiveList);
		ArrayList<String> subArchives = new ArrayList<String>();
		
		if (!(kblogArchiveListFile.isFile() && kblogArchiveListFile.canRead()))
			Logger.getLogger(Activator.ID).log(Level.SEVERE, kblogArchiveListFile.getAbsolutePath() + " does not exist, or cannot be opened in read mode. This file is required to obtain sub archive names.");
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(kblogArchiveListFile));
			String line;
			
			while ((line = br.readLine()) != null) {
				if (line.isEmpty())
					continue;
				
				String subArchiveName = line.replace(' ', '/');
				subArchives.add(subArchiveName);
			}
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE, "Error while reading " + kblogArchiveListFile.getAbsolutePath() + ".", ex);
		}

		return subArchives.toArray(new String[]{});
	}
	
	public static String[] getProcessVariableNames(String root, String relPathToLCFDir, String subArchive, String regExp) {
		ArrayList<String> matchedNames = new ArrayList<String>();
		Pattern namePattern = Pattern.compile(regExp);

		// Locate LCF file.
		int lastSlashIndex = subArchive.lastIndexOf('/');
		String lcfFileName = subArchive.substring(lastSlashIndex + 1);
		
		// Open LCF file.
		File lcfFile = new File(root, relPathToLCFDir + "/" + lcfFileName + "." + lcfSuffix);
		if (!(lcfFile.isFile() && lcfFile.canRead())) {
			Logger.getLogger(Activator.ID).log(Level.SEVERE, lcfFile.getAbsolutePath() + " does not exist, or cannot be opened in read mode.");
			return new String[]{};
		}
		
		try {
			Logger.getLogger(Activator.ID).log(Level.FINEST, "Start to read " + lcfFile.getAbsolutePath());
			
			BufferedReader br = new BufferedReader(new FileReader(lcfFile));
			
			String line;
			String name;
			while ((line = br.readLine()) != null) {
				// Extract PV name from each line
				int spaceIndex = line.indexOf(" ");
				if (spaceIndex != -1) {
					name = line.substring(0, spaceIndex);
				} else {
					name = line;
				}
				
				if (regExp.isEmpty()) {
					matchedNames.add(name);
				} else {
					// Pattern matching
					if (namePattern.matcher(name).matches()) {
						matchedNames.add(name);
					}
				}
			}
			
			br.close();
			
			Logger.getLogger(Activator.ID).log(Level.FINEST, "End of reading " + lcfFile.getAbsolutePath());
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, "Error while reading " + lcfFile.getAbsolutePath() + ".", ex);
			return matchedNames.toArray(new String[]{});
		}
		
		return matchedNames.toArray(new String[]{});
	}
}
