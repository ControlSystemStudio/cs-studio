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
	 * @param root Root directory of KBLog (e.g. /vdata2/KEKBLog)
	 * @return Names of sub archives.
	 */
	public static String[] getSubArchives(String root) {
		String kblogRoot = root;
		File kblogRootFile = new File(kblogRoot);
		
		if (!kblogRootFile.isDirectory()) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, kblogRoot + " is not a directory.");
			return new String[]{};
		}
		
		if (!kblogRoot.endsWith("/")) {
			kblogRoot = root + "/";
		}
		
		ArrayList<String> subArchives = new ArrayList<String>();
		findSubArchivesRecursively(new File(kblogRoot), kblogRoot, subArchives);
		
		return subArchives.toArray(new String[]{});
	}
	
	/**
	 * Find LCF files recursively in the specified directory, and append their names (sub
	 * archive names) to the list.
	 * 
	 * @param dir Search directory.
	 * @param kblogRoot Root directory of KBLog (e.g. /vdata2/KEKBLog/). It must end with "/".
	 * @param list List of sub archive names.
	 */
	private static void findSubArchivesRecursively(File dir, String kblogRoot, ArrayList<String> list) {
		File[] fs = dir.listFiles();
		
		for (File f : fs) {
			if (f.isDirectory()) {
				findSubArchivesRecursively(f, kblogRoot, list);
			} else if (isLCFFile(f)) {
				String absPath = f.getAbsolutePath();
				if (absPath.startsWith(kblogRoot) && absPath.endsWith("." + lcfSuffix)) {
					// Sub archive name is the path to LCF file with the following things removed. 
					//  * the path to the root directory of KEKBLog 
					//  * suffix ".lcf"
					String subArchiveName = 
						absPath.substring(kblogRoot.length(),
										  absPath.length() - lcfSuffix.length() - 1);
					list.add(subArchiveName);
				}
			}
		}
	}
	
	/**
	 * Return the regular expression pattern that matches active LCF file.
	 * The returned pattern excludes old LCF file.
	 * 
	 * Here we assumes that active LCF file only contains alphabetical letters (A-Z, a-z) with
	 * suffix ".lcf" in its file name while old LCF file contains other characters (_, 0-9).  
	 * 
	 * @return regular expression that matches file name of active LCF.
	 */
	private static synchronized Pattern getLCFPattern() {
		// precompile the regular expression
		if (lcfPattern == null) {
			lcfPattern = Pattern.compile("^[A-Za-z][A-Za-z]*\\." + lcfSuffix + "$");
		}
		return lcfPattern;
	}

	/**
	 * Returns whether the specified file is LCF file or not.
	 * 
	 * @param f File object
	 * @return whether the specified file is LCF file or not.
	 */
	private static boolean isLCFFile(File f) {
		Pattern lcfPattern = getLCFPattern();
		return f.isFile() && lcfPattern.matcher(f.getName()).matches();
	}
	
	public static String[] getProcessVariableNames(String root, String subArchive, String regExp) {
		ArrayList<String> matchedNames = new ArrayList<String>();
		Pattern namePattern = Pattern.compile(regExp);
		
		// Open LCF file.
		File lcfFile = new File(root, subArchive + "." + lcfSuffix);
		if (!lcfFile.isFile()) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, lcfFile.getAbsolutePath() + " is not a file.");
			return new String[]{};
		}
		
		try {
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
		} catch (IOException ex) {
			Logger.getLogger(Activator.ID).log(Level.WARNING, "Error while reading " + lcfFile.getAbsolutePath() + ".", ex);
			return matchedNames.toArray(new String[]{});
		}
		
		return matchedNames.toArray(new String[]{});
	}
}
