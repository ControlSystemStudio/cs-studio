/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Helper to convert PV name entered in auto completed field.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class AutoCompleteHelper {

	private static String SINGLE_REPLACE_CHAR = "\\?";
	private static String MULTI_REPLACE_CHAR = "\\*";

	public static Pattern convertToPattern(String name) {
		String regex = name.replaceAll(MULTI_REPLACE_CHAR, ".*");
		regex = regex.replaceAll(SINGLE_REPLACE_CHAR, ".");
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			return null;
		}
	}

	public static String convertToSQL(String name) {
		String sql = name.replaceAll(MULTI_REPLACE_CHAR, "%");
		sql = sql.replaceAll(SINGLE_REPLACE_CHAR, "_");
		sql = sql.replaceAll("'", "''"); // prevent SQL injection
		return sql;
	}

}
