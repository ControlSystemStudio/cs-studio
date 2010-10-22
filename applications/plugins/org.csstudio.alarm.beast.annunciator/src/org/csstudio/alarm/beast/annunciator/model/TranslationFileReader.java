/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.csstudio.utility.speech.Translation;

/** Reads translations from translations_file (translations.txt)
 * 
 * If no translation file name provided: program works OK, just translations are not used
 * If translation file name provided and it parses OK: programs works OK, and translations are used
 * If translation file name provided, but can't open that file: Error message
 * If translation file name provided, but error when parsing it: Error message
 * 
 * translations_file = "/home/ek5/workspace/org.csstudio.sns.jms2speech/translations.txt";
 * 
 * @author Katia Danilova
 * @author Kay Kasemir
 * 
 *      reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
class TranslationFileReader 
{
	/** Read translations from file
	 *  @param filename File to parse
     *  @return Array of translations
	 *  @throws Exception on error (file not found, parse error)
	 */
    public static Translation[] getTranslations(
	                        final String translations_file) throws Exception
	{
		final ArrayList<Translation> trans_arr = new ArrayList<Translation>();
		
		// Open the file 
		final FileInputStream fstream = new FileInputStream(translations_file);
		try {
		    // Convert file into buffered reader which can read line-by-line
			final DataInputStream in = new DataInputStream(fstream);
			final BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			int countLines = 0;
			
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) 
			{
			    // Count lines read from the file
				countLines++;
				
				// Remove spaces
				strLine = strLine.trim();
				
				// Skip comments
				if (strLine.length() <= 0  ||
				    strLine.startsWith("#"))
					continue;
				
				// Expect some_regular_expression_pattern = translation
				final int separator = strLine.indexOf("=");
				if (separator < 0)
					throw new Exception("Missing separator in line "
							+ countLines);
				// Add pattern & replacement to array of translations
				final String pattern = strLine.substring(0, separator);
				final String replacement = strLine.substring(separator + 1,
						strLine.length());
				trans_arr.add(new Translation(pattern, replacement));
			}
			br.close();
		}
		finally
		{
			// Close the input stream
			fstream.close();
		}
		
		// Convert array list into plain array
		return trans_arr.toArray(new Translation[trans_arr.size()]);
	}
}
