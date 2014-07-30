/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.utility.dbparser.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.csstudio.utility.dbparser.antlr.DbRecordLexer;
import org.csstudio.utility.dbparser.data.Record;
import org.csstudio.utility.dbparser.data.Template;
import org.csstudio.utility.dbparser.exception.DbParsingException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class DbUtil {

	private static Pattern comment_pattern = Pattern.compile("#.*");

	public static List<Record> parseDb(String dbFile)
			throws RecognitionException, DbParsingException {
		if (dbFile == null)
			return Collections.emptyList();
		CharStream cs = new ANTLRStringStream(dbFile);
		DbRecordLexer lexer = new DbRecordLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		RecordDbParser parser = new RecordDbParser();
		parser.parse(tokens);
		parser.transform();
		Template t = parser.getEpicsDb();
		return t.getEPICSRecords();
		// return parseTokens(tokens);
	}

	public static String readFile(IFile file) throws IOException, CoreException {
		if (file == null)
			return null;
		Matcher matcher = null;
		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			String newLine = line;
			matcher = comment_pattern.matcher(line);
			if (matcher.find()) {
				if (line.contains("\"")) {
					int index = 0;
					boolean quoted = false;
					while (index < line.length()) {
						char c = line.charAt(index);
						if (c == '"')
							quoted = !quoted;
						if (c == '#' && !quoted) {
							newLine = line.substring(0, index);
							break;
						}
						index++;
					}
				} else {
					newLine = line.substring(0, matcher.start());
				}
			}
			out.append(newLine + "\n");
		}
		br.close();
		return out.toString();
	}

}
