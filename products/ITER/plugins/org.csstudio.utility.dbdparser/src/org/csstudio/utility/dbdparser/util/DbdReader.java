/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;

/**
 * Recursively replaces all file inclusion by the corresponding file content.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbdReader {

	public static final String DBD_BASE_FILENAME = "base.dbd";
	public static final String ENV_EPICS_DB_INCLUDE_PATH = "EPICS_DB_INCLUDE_PATH";

	private static Pattern path_pattern = Pattern
			.compile("(path|addpath)\\s*\"([^\"]+)\"");
	private static Pattern include_pattern = Pattern
			.compile("include\\s*\"([^\"]+)\"");

	private final String completeDbdFile;
	// Map used as cache, filename => file content with replaced includes
	private Map<String, String> dbdfiles;

	public DbdReader(String basePath) throws Exception {
		try {
			dbdfiles = new HashMap<String, String>();
			File baseFile = new File(basePath + (basePath.endsWith("/") ? "" : "/") + DBD_BASE_FILENAME);
			this.completeDbdFile = replaceAllIncludes(baseFile);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		dbdfiles.clear();
	}

	private String replaceAllIncludes(File currentFile) throws IOException,
			CoreException {
		String dbFile = DbdUtil.readFile(currentFile);
		StringBuffer sb = new StringBuffer(dbFile);
		String currentFileDir = currentFile.getParentFile().getAbsolutePath();
		List<String> paths = readPaths(sb, currentFileDir);

		int offset = 0;
		Matcher matcher = include_pattern.matcher(new String(sb.toString()));
		while (matcher.find()) {
			String filename = matcher.group(1);

			// read cache
			String includedDbFile = dbdfiles.get(filename);
			if (includedDbFile == null) {
				File includedFile = null;
				if (isPath(filename)) { // if filename is a path, use it
					if (filename.startsWith("."))
						filename = currentFileDir
								+ (currentFileDir.endsWith("/") ? "" : "/")
								+ filename.substring(1);
					includedFile = new File(filename);
				} else if (!paths.isEmpty()) { // search in defined paths
					int index = 0;
					while (index < paths.size()) {
						includedFile = new File(paths.get(index) + "/" + filename);
						if (includedFile.exists() && includedFile.isFile())
							break;
						index++;
					}
				} else { // use the environment variable if defined, otherwise
							// use current directory
					String path = System.getenv(ENV_EPICS_DB_INCLUDE_PATH);
					if (path == null || path.isEmpty())
						path = currentFileDir;
					includedFile = new File(path
							+ (path.endsWith("/") ? "" : "/") + filename);
				}
				includedDbFile = replaceAllIncludes(includedFile);
				dbdfiles.put(filename, includedDbFile);
			}
			// replace content
			sb.replace(matcher.start() + offset, matcher.end() + offset, includedDbFile);
			offset += includedDbFile.length() - (matcher.end() - matcher.start());
		}
		return sb.toString();
	}

	private List<String> readPaths(StringBuffer sb, String fileDir) {
		List<String> paths = new LinkedList<String>();
		int offset = 0;
		Matcher matcher = path_pattern.matcher(new String(sb.toString()));
		while (matcher.find()) {
			String[] dirs = matcher.group(2).split(isWindows() ? ";" : ":");
			String path = "/";
			for (String dir : dirs) {
				if (dir.isEmpty()) { // current path
					path = fileDir;
				} else {
					path += "/" + dir;
				}
			}
			paths.add(path);
			sb.replace(matcher.start() + offset, matcher.end() + offset, "");
			offset -= matcher.end() - matcher.start();
		}
		return paths;
	}

	private boolean isPath(String filename) {
		if (filename.contains("/") || filename.contains("\\"))
			return true;
		return false;
	}

	private boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	public String getCompleteDbdFile() {
		return completeDbdFile;
	}

}
