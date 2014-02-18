/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbdparser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.csstudio.utility.dbdparser.Preferences;
import org.csstudio.utility.dbdparser.antlr.DbdFileLexer;
import org.csstudio.utility.dbdparser.data.Template;
import org.csstudio.utility.dbdparser.exception.DbdParsingException;
import org.eclipse.core.runtime.CoreException;

/**
 * DBD parser helper.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class DbdUtil {

	private static Pattern comment_pattern = Pattern.compile("\\#.*$");
	private static Pattern c_dec_pattern = Pattern.compile("\\%.*$");

	public static Template parseDb(String dbFile) throws RecognitionException,
			DbdParsingException {
		CharStream cs = new ANTLRStringStream(dbFile);
		DbdFileLexer lexer = new DbdFileLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		DbdParser parser = new DbdParser();
		parser.parse(tokens);
		parser.transform();
		return parser.getCurrentTemplate();
	}

	public static String readFile(File file) throws IOException, CoreException {
		if (file == null)
			return null;
		Matcher matcher = null;
		StringBuilder out = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			String newLine = line;
			matcher = comment_pattern.matcher(line);
			if (matcher.find())
				newLine = line.substring(0, matcher.start());
			matcher = c_dec_pattern.matcher(line);
			if (matcher.find())
				newLine = line.substring(0, matcher.start());
			out.append(newLine + "\n");
		}
		br.close();
		return out.toString();
	}

	public static Template generateTemplate(String basePath) throws Exception {
		if (basePath == null)
			return null;
		String dbFile = new DbdReader(basePath).getCompleteDbdFile();
		return DbdUtil.parseDb(dbFile);
	}

	public static Template generateTemplate() throws Exception {
		return generateTemplate(Preferences.getEpicsDBDBasePath());
	}

}
