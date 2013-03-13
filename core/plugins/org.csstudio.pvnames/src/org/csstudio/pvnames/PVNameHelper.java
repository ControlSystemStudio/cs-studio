/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.pvnames;

import java.util.regex.Pattern;

/**
 * Helper to convert PV name entered in auto completed field.
 * 
 * @author Fred Arnaud (Sopra Group)
 * 
 */
public class PVNameHelper {

	private static String SINGLE_REPLACE_CHAR = "?";
	private static String MULTI_REPLACE_CHAR = "*";

	public static Pattern convertToPattern(String name) {
		String regex = name.replace(MULTI_REPLACE_CHAR, ".+");
		regex = regex.replace(SINGLE_REPLACE_CHAR, ".");
		return Pattern.compile(regex);
	}

	public static String convertToSQL(String name) {
		String sql = name.replace(MULTI_REPLACE_CHAR, "%");
		sql = sql.replace(SINGLE_REPLACE_CHAR, "_");
		sql = sql.replace("'", "''"); // prevent SQL injection
		return sql;
	}

}
