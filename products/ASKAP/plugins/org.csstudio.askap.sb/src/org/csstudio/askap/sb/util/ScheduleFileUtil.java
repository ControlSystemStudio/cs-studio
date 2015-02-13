/*
 * Copyright (c) 2009 CSIRO Australia Telescope National Facility (ATNF) Commonwealth
 * Scientific and Industrial Research Organisation (CSIRO) PO Box 76, Epping NSW 1710,
 * Australia atnf-enquiries@csiro.au
 *
 * This file is part of the ASKAP software distribution.
 *
 * The ASKAP software distribution is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 */

package org.csstudio.askap.sb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.csstudio.askap.sb.Preferences;

/**
 * @author wu049
 * @created Jun 28, 2010
 * 
 * This class handles the loading and saving of scheduling blocks to and from file system
 *
 */
public class ScheduleFileUtil {

	/**
	 * 
	 */
	public static String getPythonFileName(String schemaFileName) throws Exception {
		File file = new File(schemaFileName);
		if (!file.exists())
			return schemaFileName.replaceFirst(".sch", ".py");

		String pythonScriptName = loadOperatorDisplaySetting(schemaFileName);
		if (pythonScriptName==null || pythonScriptName.length()==0)
			pythonScriptName = schemaFileName.replaceFirst(".sch", ".py");
		
		return pythonScriptName;
	}

	public static String getSchemaFileName(String sbFileName) throws Exception {		
		String schemaFileName = loadOperatorDisplaySetting(sbFileName);
		if (schemaFileName==null || schemaFileName.length()==0)
			schemaFileName = sbFileName.replaceFirst(".sb", ".sch");
		
		return schemaFileName;
	}
	/**
	 * 
	 */
	public static String loadPythonScript(String pythonScriptName) throws Exception {
        FileReader fis = new FileReader(pythonScriptName);
        BufferedReader reader = new BufferedReader(fis);

        String script = "";
        String record = null;
		while ( (record = reader.readLine()) != null ) {
			script = script + System.getProperty("line.separator") 
					+ record;
		}
		
		return script;
	}
	
	// this essentially loads the first non empty line of the given file which should in format of :
	// #filename=<givenfilename>
	private static String loadOperatorDisplaySetting(String fileName) throws Exception {
		InputStream is = new FileInputStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String name = null;
		
		String str = reader.readLine();
		while (str!=null) {
			if (str.trim().length()>0) {
				// get rid of #
				str = str.trim().substring(1).trim();
				if (str.startsWith(Preferences.OD_SCHEDULE_BLOCK_NAME)) {
					int index = str.indexOf("=");
					if (index>0) {
						name = str.substring(index+1).trim();
						break;
					}
				}
				
				// so we got a none empty line and it was not a operator display line
				break;
			}
			str = reader.readLine();
		}
		
		// file is all empty lines
		is.close();
		return name;
	}

}
