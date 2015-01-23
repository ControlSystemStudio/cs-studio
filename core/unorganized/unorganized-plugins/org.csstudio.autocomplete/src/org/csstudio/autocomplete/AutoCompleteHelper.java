/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Helper to handle wildcards.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteHelper {

	private static String SINGLE_REPLACE_CHAR = "\\?";
	private static String MULTI_REPLACE_CHAR = "\\*";

	/**
	 * Quote the name and return a pattern which handles only wildcards.
	 */
	public static Pattern convertToPattern(String name) {
		String regex = Pattern.quote(name);
		regex = regex.replaceAll(MULTI_REPLACE_CHAR, "\\\\E.*\\\\Q");
		regex = regex.replaceAll(SINGLE_REPLACE_CHAR, "\\\\E.\\\\Q");
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

	/**
	 * Remove all begining/ending wildcards.
	 */
	public static String trimWildcards(String name) {
		String cleaned = name.replaceAll("^[\\*\\?]+", "");
		cleaned = cleaned.replaceAll("[\\*\\?]+$", "");
		return cleaned;
	}
	
	public static Set<String> retrieveUtilityPVSupported() {
		Set<String> items = new HashSet<String>();
		try {
			Class<?> clazz = Class.forName("org.csstudio.utility.pv.PVFactory");
			String[] parameters = (String[]) clazz.getMethod("getSupportedPrefixes").invoke(null);
			items.addAll(Arrays.asList(parameters));
			AutoCompletePlugin.getLogger().config("Loading utility.pv supported types: " + items);
			return items;
		} catch (Exception ex) {
			AutoCompletePlugin.getLogger().config("utility.pv not found: " + ex.getMessage());
			return Collections.emptySet();
		}
	}

	public static Set<String> retrievePVManagerSupported() {
		Set<String> items = new HashSet<String>();
		try {
			Class<?> clazz = Class.forName("org.csstudio.utility.pvmanager.ConfigurationHelper");
			@SuppressWarnings("unchecked")
			Map<String, Object> parameters = (Map<String, Object>) clazz.getMethod("configuredDataSources").invoke(null);
			items.addAll(parameters.keySet());
			AutoCompletePlugin.getLogger().config("Loading PVManager supported types: " + items);
			return items;
		} catch (Exception ex) {
			AutoCompletePlugin.getLogger().config("PVManager not found: " + ex.getMessage());
			return Collections.emptySet();
		}
	}

}
